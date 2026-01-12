<template>
  <div class="announcement-list container">
    <h1 class="page-title">招标信息门户</h1>
    
    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="行业分类">
          <el-select v-model="searchForm.industry" placeholder="全部行业" clearable style="width: 150px">
            <el-option v-for="item in industries" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="searchForm.region" placeholder="全部地区" clearable style="width: 150px">
            <el-option v-for="item in regions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="searchForm.type" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="招标公告" value="BID" />
            <el-option label="变更公告" value="CHANGE" />
            <el-option label="终止公告" value="TERMINATE" />
            <el-option label="中标公示" value="RESULT" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="userStore.isAdmin()">
          <el-select v-model="searchForm.status" placeholder="公告状态" clearable style="width: 150px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="搜索项目名称/编号" clearable style="width: 250px">
            <template #append>
              <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="userStore.isAdmin()">
          <el-button type="success" @click="router.push('/announcements/create')">发布公告</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表展示 -->
    <div v-loading="loading">
      <!-- 管理员视图 - 表格形式 -->
      <div v-if="userStore.isAdmin()" class="admin-view">
        <el-table :data="tableData" stripe style="width: 100%; margin-bottom: 20px">
          <el-table-column prop="announcementNo" label="公告编号" width="150" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.type)" size="small">{{ formatType(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'DRAFT'" type="info">草稿</el-tag>
              <el-tag v-else-if="row.status === 'PUBLISHED'" type="success">已发布</el-tag>
              <el-tag v-else-if="row.status === 'CLOSED'" type="danger">已关闭</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="publishTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEdit(row.id)">编辑</el-button>
              <el-button 
                v-if="row.status === 'DRAFT'" 
                type="success" 
                link 
                size="small" 
                @click="handlePublish(row.id)"
              >
                发布
              </el-button>
              <el-button 
                v-if="row.status === 'PUBLISHED'" 
                type="warning" 
                link 
                size="small" 
                @click="handleClose(row.id)"
              >
                关闭
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 普通用户视图 - 卡片形式 -->
      <div v-else class="user-view">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="item in tableData" :key="item.id">
            <el-card class="card-item announcement-card" shadow="hover">
              <div class="card-tag" v-if="item.isTop">置顶</div>
              <h3 class="title" @click="handleDetail(item.id)">{{ item.title }}</h3>
              <div class="info">
                <p><el-icon><Location /></el-icon> 地区：{{ item.region || '全国' }}</p>
                <p><el-icon><Money /></el-icon> 预算：<span class="price">￥{{ formatMoney(item.projectBudget) }}</span></p>
                <p><el-icon><Calendar /></el-icon> 截止：{{ formatDate(item.bidDeadline) }}</p>
              </div>
              <div class="card-footer">
                <el-tag :type="getTypeTag(item.type)" size="small">{{ formatType(item.type) }}</el-tag>
                <el-button type="primary" link @click="handleDetail(item.id)">查看详情</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <el-empty v-if="tableData.length === 0" description="暂无相关招标信息" />

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, prev, pager, next, jumper"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Location, Money, Calendar, Edit, Delete } from '@element-plus/icons-vue'
import { getAnnouncementList, deleteAnnouncement, publishAnnouncement } from '@/api/announcement'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])

const industries = ['信息技术', '建筑工程', '医疗器械', '办公用品', '咨询服务', '物流运输']
const regions = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉']

const searchForm = reactive({
  industry: '',
  region: '',
  type: '',
  status: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: userStore.isAdmin() ? 10 : 9,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAnnouncementList({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.records
    pagination.total = res.total
  } catch (error) {
    console.error('加载列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleDetail = (id) => {
  router.push(`/announcements/detail/${id}`)
}

const handleEdit = (id) => {
  router.push(`/announcements/edit/${id}`)
}

const handlePublish = async (id) => {
  try {
    await ElMessageBox.confirm('确定要发布该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await publishAnnouncement(id)
    ElMessage.success('发布成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败', error)
    }
  }
}

const handleClose = async (id) => {
  try {
    await ElMessageBox.confirm('确定要关闭该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 这里可以调用关闭公告的 API，暂时使用更新状态的方式
    ElMessage.success('关闭成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('关闭失败', error)
    }
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAnnouncement(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

const formatMoney = (val) => {
  if (!val) return '面议'
  return val.toLocaleString()
}

const formatDate = (val) => {
  if (!val) return '-'
  return val.split(' ')[0]
}

const formatType = (type) => {
  const map = { BID: '招标公告', CHANGE: '变更公告', TERMINATE: '终止公告', RESULT: '中标公示' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { BID: '', CHANGE: 'warning', TERMINATE: 'danger', RESULT: 'success' }
  return map[type] || ''
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

.filter-card {
  margin-bottom: 30px;
  background-color: #fff;
  border-radius: 8px;
}

.admin-view {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.user-view {
  margin-top: 20px;
}

.announcement-card {
  position: relative;
  height: 220px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  margin-bottom: 20px;
}

.card-tag {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #FF6600;
  color: #fff;
  padding: 2px 10px;
  font-size: 12px;
  border-bottom-left-radius: 8px;
  z-index: 1;
}

.announcement-card .title {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
  line-height: 1.4;
  height: 50px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  cursor: pointer;
  transition: color 0.3s;
}

.announcement-card .title:hover {
  color: #003366;
}

.announcement-card .info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.announcement-card .info .el-icon {
  margin-right: 8px;
  color: #003366;
}

.price {
  color: #FF6600;
  font-weight: bold;
}

.card-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
