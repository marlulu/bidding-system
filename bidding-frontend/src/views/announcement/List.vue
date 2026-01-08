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
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="item in tableData" :key="item.id">
          <el-card class="card-item announcement-card" shadow="hover" @click="handleDetail(item.id)">
            <div class="card-tag" v-if="item.isTop">置顶</div>
            <h3 class="title">{{ item.title }}</h3>
            <div class="info">
              <p><el-icon><Location /></el-icon> 地区：{{ item.region || '全国' }}</p>
              <p><el-icon><Money /></el-icon> 预算：<span class="price">￥{{ formatMoney(item.projectBudget) }}</span></p>
              <p><el-icon><Calendar /></el-icon> 截止：{{ formatDate(item.bidDeadline) }}</p>
            </div>
            <div class="card-footer">
              <el-tag :type="getTypeTag(item.type)" size="small">{{ formatType(item.type) }}</el-tag>
              <el-button type="primary" link>立即投标</el-button>
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
import { Search, Location, Money, Calendar } from '@element-plus/icons-vue'
import { getAnnouncementList } from '@/api/announcement'
import { useUserStore } from '@/stores/user'

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
