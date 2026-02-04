<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">投标管理</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="supplierId" label="供应商ID" width="100" />
        <el-table-column label="投标金额" width="150" sortable prop="bidAmount">
           <template #default="{ row }">
              ￥{{ row.bidAmount.toLocaleString() }}
           </template>
        </el-table-column>
        <el-table-column prop="createTime" label="投标时间" width="180" sortable />
        <el-table-column prop="status" label="状态" width="120">
           <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ formatStatus(row.status) }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
           <template #default="{ row }">
              <template v-if="row.status === 'SUBMITTED'">
                 <el-button type="success" size="small" @click="handleAudit(row, 'WIN')">设为中标</el-button>
                 <el-button type="danger" size="small" @click="handleAudit(row, 'LOSE')">设为未中标</el-button>
              </template>
              <span v-else>已完成</span>
           </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getBidsByAnnouncement, auditBid } from '@/api/bid'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const announcementId = route.params.id
    if (!announcementId) return
    tableData.value = await getBidsByAnnouncement(announcementId)
  } catch (error) {
    console.error('加载投标记录失败', error)
  } finally {
    loading.value = false
  }
}

const handleAudit = (row, status) => {
   const action = status === 'WIN' ? '中标' : '未中标'
   ElMessageBox.confirm(`确定要将该供应商设为${action}吗？`, '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
   }).then(async () => {
      try {
         await auditBid(row.id, { status })
         ElMessage.success('操作成功')
         loadData()
      } catch (error) {
         ElMessage.error('操作失败')
      }
   }).catch(() => {})
}

const formatStatus = (status) => {
   const map = { SUBMITTED: '待定标', WIN: '中标', LOSE: '未中标' }
   return map[status] || status
}

const getStatusType = (status) => {
   const map = { SUBMITTED: 'warning', WIN: 'success', LOSE: 'info' }
   return map[status] || ''
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
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
}
</style>
