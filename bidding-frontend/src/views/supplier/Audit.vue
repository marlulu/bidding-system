<template>
  <div class="supplier-audit-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">供应商入驻审核</span>
          <el-radio-group v-model="filterStatus" @change="loadData">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button :label="0">待审核</el-radio-button>
            <el-radio-button :label="1">已认证</el-radio-button>
            <el-radio-button :label="2">已拒绝</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="companyName" label="公司名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <template v-if="row.status === 0">
              <el-button link type="success" @click="handleAudit(row, 1)">通过</el-button>
              <el-button link type="danger" @click="handleAudit(row, 2)">拒绝</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="供应商申请详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公司名称" :span="2">{{ currentItem.companyName }}</el-descriptions-item>
        <el-descriptions-item label="信用代码">{{ currentItem.companyCode }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ currentItem.industry }}</el-descriptions-item>
        <el-descriptions-item label="法人代表">{{ currentItem.legalPerson }}</el-descriptions-item>
        <el-descriptions-item label="企业规模">{{ currentItem.scale }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentItem.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentItem.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱" :span="2">{{ currentItem.contactEmail }}</el-descriptions-item>
        <el-descriptions-item label="公司地址" :span="2">{{ currentItem.address }}</el-descriptions-item>
        <el-descriptions-item label="公司简介" :span="2">{{ currentItem.description }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <template v-if="currentItem.status === 0">
          <el-button type="success" @click="handleAudit(currentItem, 1)">审核通过</el-button>
          <el-button type="danger" @click="handleAudit(currentItem, 2)">拒绝申请</el-button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSupplierList, updateSupplierStatus } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const filterStatus = ref('')

const detailVisible = ref(false)
const currentItem = ref({})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSupplierList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusLabel = (status) => {
  const map = { 0: '待审核', 1: '已认证', 2: '已拒绝' }
  return map[status] || '未知'
}

const viewDetail = (row) => {
  currentItem.value = row
  detailVisible.value = true
}

const handleAudit = (row, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  ElMessageBox.confirm(`确定要${action}该供应商的入驻申请吗？`, '审核确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: status === 1 ? 'success' : 'warning'
  }).then(async () => {
    try {
      await updateSupplierStatus(row.id, status)
      ElMessage.success(`已${action}`)
      detailVisible.value = false
      loadData()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(loadData)
</script>

<style scoped>
.supplier-audit-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #003366;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
