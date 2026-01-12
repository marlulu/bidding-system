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

    <!-- 列表展示 - 统一卡片模式 -->
    <div v-loading="loading">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="item in tableData" :key="item.id">
          <el-card class="card-item announcement-card" shadow="hover">
            <!-- 管理员操作菜单 -->
            <template v-if="userStore.isAdmin()" #header>
              <div class="card-header">
                <span class="card-title">{{ item.title }}</span>
                <el-dropdown trigger="click" @command="handleCommand($event, item.id)">
                  <el-button type="primary" link size="small"><el-icon><MoreFilled /></el-icon></el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="edit">编辑</el-dropdown-item>
                      <el-dropdown-item v-if="item.status === 'DRAFT'" command="publish">发布</el-dropdown-item>
                      <el-dropdown-item v-if="item.status === 'PUBLISHED'" command="close">关闭</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>

            <!-- 非管理员模式下的标题 -->
            <div v-if="!userStore.isAdmin() && item.isTop" class="card-tag">置顶</div>
            <h3 v-if="!userStore.isAdmin()" class="title" @click="handleDetail(item.id)">{{ item.title }}</h3>

            <!-- 公告类型标签 -->
            <div class="type-badge">
              <el-tag :type="getTypeTag(item.type)" size="small">{{ formatType(item.type) }}</el-tag>
              <el-tag v-if="userStore.isAdmin()" :type="getStatusTag(item.status)" size="small" style="margin-left: 5px">
                {{ formatStatus(item.status) }}
              </el-tag>
            </div>

            <!-- 公告信息 -->
            <div class="info">
              <p><el-icon><Location /></el-icon> 地区：{{ item.region || '全国' }}</p>
              <p><el-icon><Money /></el-icon> 预算：<span class="price">￥{{ formatMoney(item.projectBudget) }}</span></p>
              <p><el-icon><Calendar /></el-icon> 截止：{{ formatDate(item.bidDeadline) }}</p>
            </div>

            <!-- 底部操作 -->
            <div class="card-footer">
              <el-button type="primary" link @click="handleDetail(item.id)">
                {{ userStore.isAdmin() ? '查看详情' : '立即投标' }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

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
import { Search, Location, Money, Calendar, MoreFilled } from '@element-plus/icons-vue'
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
  size: 9,
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

const handleCommand = async (command, id) => {
  switch (command) {
    case 'edit':
      router.push(`/announcements/edit/${id}`)
      break
    case 'publish':
      await handlePublish(id)
      break
    case 'close':
      await handleClose(id)
      break
    case 'delete':
      await handleDelete(id)
      break
  }
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

const formatStatus = (status) => {
  const map = { DRAFT: '草稿', PUBLISHED: '已发布', CLOSED: '已关闭' }
  return map[status] || status
}

const getTypeTag = (type) => {
  const map = { BID: '', CHANGE: 'warning', TERMINATE: 'danger', RESULT: 'success' }
  return map[type] || ''
}

const getStatusTag = (status) => {
  const map = { DRAFT: 'info', PUBLISHED: 'success', CLOSED: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.filter-card {
  margin-bottom: 30px;
  background-color: #fff;
  border-radius: 8px;
}

.announcement-card {
  position: relative;
  height: 280px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.announcement-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
}

.card-title {
  flex: 1;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.card-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #FF6600;
  color: #fff;
  padding: 2px 10px;
  font-size: 12px;
  border-radius: 4px;
  z-index: 1;
}

.title {
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

.title:hover {
  color: #003366;
}

.type-badge {
  margin-bottom: 15px;
}

.info {
  flex: 1;
  margin-bottom: 15px;
}

.info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.info .el-icon {
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
  justify-content: flex-end;
}

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
