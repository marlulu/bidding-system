<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">我的投标记录</span>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="announcementTitle" label="招标项目" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
               <el-link type="primary" :underline="false" @click="$router.push(`/announcements/detail/${row.announcementId}`)">
                  {{ row.announcementTitle || row.announcementId }}
               </el-link>
            </template>
        </el-table-column>
        <el-table-column label="投标金额" width="150">
           <template #default="{ row }">
              ￥{{ row.bidAmount.toLocaleString() }}
           </template>
        </el-table-column>
        <el-table-column prop="createTime" label="投标时间" width="180">
            <template #default="{ row }">
               {{ formatTime(row.createTime) }}
            </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
           <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ formatStatus(row.status) }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120">
           <template #default="{ row }">
              <el-button link type="primary" @click="$router.push(`/announcements/detail/${row.announcementId}`)">查看公告</el-button>
           </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyBids } from '@/api/bid'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyBids({
       page: pagination.page,
       size: pagination.size
    })
    tableData.value = res.records
    pagination.total = res.total
  } catch (error) {
    console.error('加载我的投标失败', error)
  } finally {
    loading.value = false
  }
}

const formatStatus = (status) => {
   const map = { SUBMITTED: '已提交', WIN: '中标', LOSE: '未中标' }
   return map[status] || status
}

const getStatusType = (status) => {
   const map = { SUBMITTED: 'info', WIN: 'success', LOSE: 'danger' }
   return map[status] || ''
}

const formatTime = (time) => {
   if (!time) return ''
   return time.replace('T', ' ')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.title {
  font-size: 18px;
  font-weight: bold;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
