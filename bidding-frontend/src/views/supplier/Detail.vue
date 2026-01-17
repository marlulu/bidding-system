<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <span>{{ detail.companyName }}</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公司名称">{{ detail.companyName }}</el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码">{{ detail.companyCode }}</el-descriptions-item>
        <el-descriptions-item label="法人代表">{{ detail.legalPerson }}</el-descriptions-item>
        <el-descriptions-item label="行业分类">{{ detail.industry }}</el-descriptions-item>
        <el-descriptions-item label="企业规模">
          <span v-if="detail.scale === 'SMALL'">小型</span>
          <span v-else-if="detail.scale === 'MEDIUM'">中型</span>
          <span v-else-if="detail.scale === 'LARGE'">大型</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="detail.status === 1" type="success">已认证</el-tag>
          <el-tag v-else-if="detail.status === 2" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detail.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱">{{ detail.contactEmail }}</el-descriptions-item>
        <el-descriptions-item label="公司地址" :span="2">{{ detail.address }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSupplierDetail } from '@/api/supplier'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({})

const loadData = async () => {
  loading.value = true
  try {
    detail.value = await getSupplierDetail(route.params.id)
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
</style>
