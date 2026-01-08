<template>
  <div class="supplier-list container">
    <h1 class="page-title">优质供应商库</h1>

    <!-- 分类导航与标签云 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-section">
        <span class="label">行业分类：</span>
        <div class="tag-cloud">
          <el-tag
            v-for="tag in industries"
            :key="tag"
            :effect="searchForm.industry === tag ? 'dark' : 'plain'"
            class="filter-tag"
            @click="handleIndustryClick(tag)"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
      <el-divider border-style="dashed" />
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="企业规模">
          <el-select v-model="searchForm.scale" placeholder="全部规模" clearable style="width: 150px">
            <el-option label="大型企业" value="LARGE" />
            <el-option label="中型企业" value="MEDIUM" />
            <el-option label="小型企业" value="SMALL" />
          </el-select>
        </el-form-item>
        <el-form-item label="资质等级">
          <el-select v-model="searchForm.qualificationLevel" placeholder="全部等级" clearable style="width: 150px">
            <el-option label="一级资质" value="一级资质" />
            <el-option label="二级资质" value="二级资质" />
            <el-option label="三级资质" value="三级资质" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="搜索供应商名称" clearable style="width: 250px">
            <template #append>
              <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="userStore.isAdmin()">
          <el-button type="success" @click="router.push('/suppliers/create')">新增供应商</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 供应商列表 -->
    <div v-loading="loading">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="item in tableData" :key="item.id">
          <el-card class="card-item supplier-card" shadow="hover" @click="handleDetail(item.id)">
            <div class="supplier-logo">
              <el-icon :size="40" color="#003366"><OfficeBuilding /></el-icon>
            </div>
            <h3 class="name">{{ item.companyName }}</h3>
            <p class="industry">{{ item.industry }}</p>
            <div class="tags">
              <el-tag size="small" type="info">{{ formatScale(item.scale) }}</el-tag>
              <el-tag size="small" type="success" style="margin-left: 5px">{{ item.qualificationLevel || '认证供应商' }}</el-tag>
            </div>
            <div class="card-footer">
              <el-button type="primary" plain size="small" @click.stop="handleContact(item)">联系供应商</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="tableData.length === 0" description="暂无相关供应商信息" />

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
import { useRouter, useRoute } from 'vue-router'
import { Search, OfficeBuilding } from '@element-plus/icons-vue'
import { getSupplierList } from '@/api/supplier'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])

const industries = ['全部', '信息技术', '建筑工程', '医疗器械', '办公用品', '咨询服务', '物流运输']

const searchForm = reactive({
  industry: route.query.industry || '',
  scale: '',
  qualificationLevel: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm }
    if (params.industry === '全部') params.industry = ''
    const res = await getSupplierList({
      page: pagination.page,
      size: pagination.size,
      ...params
    })
    tableData.value = res.records
    pagination.total = res.total
  } catch (error) {
    console.error('加载供应商列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleIndustryClick = (tag) => {
  searchForm.industry = tag
  handleSearch()
}

const handleDetail = (id) => {
  router.push(`/suppliers/detail/${id}`)
}

const handleContact = (supplier) => {
  ElMessage.success(`已向 ${supplier.companyName} 发送联系请求，请等待回复。`)
}

const formatScale = (scale) => {
  const map = { LARGE: '大型企业', MEDIUM: '中型企业', SMALL: '小型企业' }
  return map[scale] || scale
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
}

.filter-section {
  display: flex;
  align-items: center;
}

.filter-section .label {
  font-weight: bold;
  color: #666;
  width: 80px;
}

.tag-cloud {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.supplier-card {
  text-align: center;
  padding: 20px 10px;
  cursor: pointer;
  margin-bottom: 20px;
  height: 280px;
  display: flex;
  flex-direction: column;
}

.supplier-logo {
  width: 70px;
  height: 70px;
  background-color: #f0f2f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
}

.supplier-card .name {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
  height: 50px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.supplier-card .industry {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.supplier-card .tags {
  margin-bottom: 15px;
}

.card-footer {
  margin-top: auto;
}

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
