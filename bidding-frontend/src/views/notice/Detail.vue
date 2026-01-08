<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <span>{{ detail.title }}</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题" :span="2">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag v-if="detail.type === 'SYSTEM'">系统通知</el-tag>
          <el-tag v-else-if="detail.type === 'POLICY'" type="warning">政策变动</el-tag>
          <el-tag v-else-if="detail.type === 'ANNOUNCEMENT'" type="success">公告提醒</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === 'DRAFT'" type="info">草稿</el-tag>
          <el-tag v-else-if="detail.status === 'PUBLISHED'" type="success">已发布</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detail.publishTime }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <div class="content-section">
        <h3>通知内容</h3>
        <div class="content-text">{{ detail.content }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNoticeById, markAsRead } from '@/api/notice'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({})

const loadData = async () => {
  loading.value = true
  try {
    detail.value = await getNoticeById(route.params.id)
    // 标记为已读
    await markAsRead(route.params.id)
  } catch (error) {
    console.error('加载详情失败', error)
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.back()
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

.content-section {
  margin-top: 20px;
}

.content-section h3 {
  margin-bottom: 15px;
  color: #303133;
}

.content-text {
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}
</style>
