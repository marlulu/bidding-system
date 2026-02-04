<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <div>
            <span class="title">{{ detail.title }}</span>
            <el-tag :type="getTypeTagType(detail.type)" style="margin-left: 10px">
              {{ formatType(detail.type) }}
            </el-tag>
          </div>
          <div>
             <el-button 
                v-if="canBid" 
                type="primary" 
                @click="handleBid" 
                :disabled="hasBid"
             >
                {{ hasBid ? '已投标' : '立即投标' }}
             </el-button>
             <el-button 
                v-if="userStore.role === 'ADMIN' && detail.type === 'BID'"
                type="primary"
                @click="router.push(`/announcements/${detail.id}/bids`)"
             >
                查看投标情况
             </el-button>
             <el-button @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>
      
      <!-- 基本信息 -->
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告编号">{{ detail.announcementNo }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          {{ formatType(detail.type) }}
        </el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ detail.projectName }}</el-descriptions-item>
        <el-descriptions-item label="项目预算">
          <span class="price">￥{{ formatMoney(detail.projectBudget) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="地区">{{ detail.region || '全国' }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ detail.industry || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 根据公告类型显示不同的信息 -->
      <el-divider />
      
      <!-- 招标公告特定字段 -->
      <div v-if="detail.type === 'BID'" class="type-specific-section">
        <h3>招标信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="投标截止时间">
            <el-tag type="danger">{{ detail.bidDeadline }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 变更公告特定字段 -->
      <div v-else-if="detail.type === 'CHANGE'" class="type-specific-section">
        <h3>变更信息</h3>
        <el-alert
          title="这是一份变更公告"
          type="warning"
          description="请仔细阅读变更内容，了解招标项目的最新变化"
          :closable="false"
          style="margin-bottom: 20px"
        />
        <el-descriptions :column="2" border>
          <el-descriptions-item label="联系人">{{ detail.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 终止公告特定字段 -->
      <div v-else-if="detail.type === 'TERMINATE'" class="type-specific-section">
        <h3>终止信息</h3>
        <el-alert
          title="招标项目已终止"
          type="error"
          description="该招标项目已被终止，不再接受投标"
          :closable="false"
          style="margin-bottom: 20px"
        />
        <el-descriptions :column="2" border>
          <el-descriptions-item label="联系人">{{ detail.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 中标结果特定字段 -->
      <div v-else-if="detail.type === 'RESULT'" class="type-specific-section">
        <h3>中标结果</h3>
        <el-alert
          title="中标结果公示"
          type="success"
          description="以下是本次招标的中标结果，请仔细核对"
          :closable="false"
          style="margin-bottom: 20px"
        />
        <el-descriptions :column="2" border>
          <el-descriptions-item label="联系人">{{ detail.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <el-divider />
      
      <!-- 公告内容 -->
      <div class="content-section">
        <h3>{{ getContentTitle(detail.type) }}</h3>
        <div class="content-text">{{ detail.content }}</div>
      </div>

      <!-- 附件信息 -->
      <div v-if="attachments.length > 0" class="content-section">
        <h3>相关附件</h3>
        <div class="attachment-list">
          <div v-for="(file, index) in attachments" :key="index" class="attachment-item">
             <el-link type="primary" :href="`/api/common/download?url=${encodeURIComponent(file.url)}`" target="_blank" :underline="false">
               <el-icon style="margin-right: 5px"><Document /></el-icon>
               {{ file.name }}
             </el-link>
          </div>
        </div>
      </div>

      <el-divider />

      <!-- 发布信息 -->
      <el-descriptions :column="2" border>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === 'DRAFT'" type="info">草稿</el-tag>
          <el-tag v-else-if="detail.status === 'PUBLISHED'" type="success">已发布</el-tag>
          <el-tag v-else-if="detail.status === 'CLOSED'" type="danger">已关闭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detail.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAnnouncementDetail } from '@/api/announcement'
import { checkHasBid, submitBid, getMyBidDetail } from '@/api/bid'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const detail = ref({})
const bidRecord = ref(null)

// 判断是否可以投标：用户是供应商，公告类型是BID，状态是PUBLISHED
const canBid = computed(() => {
   return userStore.role === 'SUPPLIER' && 
          detail.value.type === 'BID' && 
          detail.value.status === 'PUBLISHED'
})

const attachments = computed(() => {
  if (!detail.value.attachmentFiles) return []
  try {
    return JSON.parse(detail.value.attachmentFiles)
  } catch (e) {
    return []
  }
})

const loadData = async () => {
  loading.value = true
  try {
    detail.value = await getAnnouncementDetail(route.params.id)
    if (userStore.role === 'SUPPLIER' && detail.value.type === 'BID') {
       hasBid.value = await checkHasBid(route.params.id)
       if (hasBid.value) {
          bidRecord.value = await getMyBidDetail(route.params.id)
       }
       // 如果是从列表页点击"去投标"进来的，且未投标，则自动触发投标
       if (route.query.action === 'bid' && !hasBid.value && detail.value.status === 'PUBLISHED') {
          handleBid()
       }
    }
  } catch (error) {
    console.error('加载详情失败', error)
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.back()
}

const handleBid = () => {
   ElMessageBox.prompt('请输入投标金额(元)', '确认投标', {
      confirmButtonText: '确定提交',
      cancelButtonText: '取消',
      inputPattern: /^\d+(\.\d{1,2})?$/,
      inputErrorMessage: '金额格式不正确'
   }).then(async ({ value }) => {
      try {
         await submitBid({
            announcementId: detail.value.id,
            bidAmount: value
         })
         
         ElMessageBox.confirm('投标成功！您可以前往"我的投标"查看记录，或留在当前页面。', '提示', {
            confirmButtonText: '查看我的投标',
            cancelButtonText: '留在本页',
            type: 'success'
         }).then(() => {
            router.push('/bids/my')
         }).catch(async () => {
            hasBid.value = true
            bidRecord.value = await getMyBidDetail(detail.value.id)
         })
         
      } catch (error) {
         ElMessage.error('投标失败')
      }
   }).catch(() => {})
}

const formatType = (type) => {
  const map = { BID: '招标公告', CHANGE: '变更公告', TERMINATE: '终止公告', RESULT: '中标结果' }
  return map[type] || type
}

const getTypeTagType = (type) => {
  const map = { BID: 'primary', CHANGE: 'warning', TERMINATE: 'danger', RESULT: 'success' }
  return map[type] || 'info'
}

const getContentTitle = (type) => {
  const map = {
    BID: '招标内容',
    CHANGE: '变更内容',
    TERMINATE: '终止原因及说明',
    RESULT: '中标结果详情'
  }
  return map[type] || '公告内容'
}

const formatMoney = (val) => {
  if (!val) return '面议'
  return val.toLocaleString()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-header .title {
  font-size: 18px;
  font-weight: bold;
  color: #003366;
}

.type-specific-section {
  margin: 20px 0;
}

.type-specific-section h3 {
  margin-bottom: 15px;
  color: #003366;
  font-size: 16px;
  font-weight: bold;
}

.content-section {
  margin: 20px 0;
}

.content-section h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.content-text {
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.attachment-item {
  display: flex;
  align-items: center;
}

.price {
  color: #FF6600;
  font-weight: bold;
  font-size: 16px;
}
</style>
