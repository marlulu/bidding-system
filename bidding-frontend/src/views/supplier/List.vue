<template>
  <div class="supplier-list container">
    <h1 class="page-title">优质供应商库</h1>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <div v-if="!userStore.isAdmin()" class="filter-section">
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
      <el-divider v-if="!userStore.isAdmin()" border-style="dashed" />
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="企业规模">
          <el-select v-model="searchForm.scale" placeholder="全部规模" clearable style="width: 150px">
            <el-option label="大型企业" value="LARGE" />
            <el-option label="中型企业" value="MEDIUM" />
            <el-option label="小型企业" value="SMALL" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!userStore.isAdmin()" label="资质等级">
          <el-select v-model="searchForm.qualificationLevel" placeholder="全部等级" clearable style="width: 150px">
            <el-option label="一级资质" value="一级资质" />
            <el-option label="二级资质" value="二级资质" />
            <el-option label="三级资质" value="三级资质" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="userStore.isAdmin()" label="认证状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="待审核" :value="0" />
            <el-option label="已认证" :value="1" />
            <el-option label="已驳回" :value="2" />
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

    <!-- 供应商列表 - 管理员表格视图 -->
    <div v-if="userStore.isAdmin()" v-loading="loading" class="admin-view">
      <el-table :data="tableData" stripe style="width: 100%; margin-bottom: 20px">
        <el-table-column prop="companyName" label="公司名称" min-width="200" />
        <el-table-column prop="companyCode" label="统一社会信用代码" width="180" />
        <el-table-column prop="industry" label="行业" width="100" />
        <el-table-column prop="scale" label="企业规模" width="100">
          <template #default="{ row }">
            {{ formatScale(row.scale) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="认证状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已认证</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row.id)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleAudit(row.id)">审核</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 供应商列表 - 普通用户卡片视图 -->
    <div v-else v-loading="loading" class="user-view">
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
    </div>

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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, OfficeBuilding } from '@element-plus/icons-vue'
import { getSupplierList, deleteSupplier } from '@/api/supplier'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

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
  status: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: userStore.isAdmin() ? 10 : 12,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm }
    if (!userStore.isAdmin() && params.industry === '全部') {
      params.industry = ''
    }
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

const handleEdit = (id) => {
  router.push(`/suppliers/edit/${id}`)
}

const handleAudit = (id) => {
  router.push(`/suppliers/audit/${id}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该供应商吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteSupplier(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
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
  max-width: 1400px;
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

.admin-view {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.user-view {
  margin-top: 20px;
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
