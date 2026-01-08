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
        <el-descriptions-item label="公告编号">{{ detail.announcementNo }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag v-if="detail.type === 'BID'" type="primary">招标公告</el-tag>
          <el-tag v-else-if="detail.type === 'CHANGE'" type="warning">变更公告</el-tag>
          <el-tag v-else-if="detail.type === 'TERMINATE'" type="danger">终止公告</el-tag>
          <el-tag v-else-if="detail.type === 'RESULT'" type="success">中标结果</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ detail.projectName }}</el-descriptions-item>
        <el-descriptions-item label="项目预算">{{ detail.projectBudget }} 元</el-descriptions-item>
        <el-descriptions-item label="投标截止时间">{{ detail.bidDeadline }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detail.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === 'DRAFT'" type="info">草稿</el-tag>
          <el-tag v-else-if="detail.status === 'PUBLISHED'" type="success">已发布</el-tag>
          <el-tag v-else-if="detail.status === 'CLOSED'" type="danger">已关闭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detail.publishTime }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <div class="content-section">
        <h3>公告内容</h3>
        <div class="content-text">{{ detail.content }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAnnouncementById } from '@/api/announcement'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({})

const loadData = async () => {
  loading.value = true
  try {
    detail.value = await getAnnouncementById(route.params.id)
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
