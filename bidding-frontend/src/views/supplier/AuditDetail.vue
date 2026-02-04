<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <span class="title">供应商审核 - {{ detail.companyName }}</span>
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
        <el-descriptions-item label="当前状态">
          <el-tag v-if="detail.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="detail.status === 1" type="success">已认证</el-tag>
          <el-tag v-else-if="detail.status === 2" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detail.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱">{{ detail.contactEmail }}</el-descriptions-item>
        <el-descriptions-item label="公司地址" :span="2">{{ detail.address }}</el-descriptions-item>
        <el-descriptions-item label="公司简介" :span="2">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="申请附件" :span="2">
           <!-- 暂时预留附件展示位置 -->
           <span v-if="!detail.attachment">暂无附件</span>
           <el-link v-else type="primary" :href="detail.attachment" target="_blank">查看附件</el-link>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 审核操作区 -->
      <div class="audit-actions" v-if="detail.status === 0">
         <el-divider content-position="center">审核操作</el-divider>
         <div class="action-buttons">
            <el-button type="success" size="large" @click="handleAudit(1)">审核通过</el-button>
            <el-button type="danger" size="large" @click="handleAudit(2)">驳回申请</el-button>
         </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSupplierDetail, auditSupplier } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({})

const loadData = async () => {
  loading.value = true
  try {
    const id = route.params.id
    if (!id) {
       ElMessage.error('缺少参数')
       return
    }
    detail.value = await getSupplierDetail(id)
  } catch (error) {
    console.error('加载详情失败', error)
    ElMessage.error('加载供应商详情失败')
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.back()
}

const handleAudit = (status) => {
  const action = status === 1 ? '通过' : '驳回'
  const isPass = status === 1
  
  ElMessageBox.prompt(`请输入${action}原因/备注`, '审核确认', {
    confirmButtonText: '确定提交',
    cancelButtonText: '取消',
    inputPlaceholder: isPass ? '符合入驻条件（选填）' : '请填写驳回原因（必填）',
    inputType: 'textarea',
    inputPattern: isPass ? /.*/ : /^.+$/,
    inputErrorMessage: '驳回时必须填写原因',
    type: isPass ? 'success' : 'warning'
  }).then(async ({ value }) => {
    try {
      loading.value = true
      await auditSupplier(detail.value.id, {
        status: status,
        auditRemark: value || (isPass ? '审核通过' : '审核驳回')
      })
      ElMessage.success(`已${action}该供应商申请`)
      router.push('/suppliers')
    } catch (error) {
      console.error('审核失败', error)
      ElMessage.error('操作失败，请重试')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消操作
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.audit-actions {
  margin-top: 40px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 20px;
  margin-bottom: 20px;
}
</style>
