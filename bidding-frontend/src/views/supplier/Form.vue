<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑供应商信息' : '新增供应商' }}</span>
          <el-tag v-if="isEdit && supplierData.status !== undefined" :type="getStatusTagType(supplierData.status)">
            {{ formatStatus(supplierData.status) }}
          </el-tag>
        </div>
      </template>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入公司名称" />
        </el-form-item>
        
        <el-form-item label="统一社会信用代码" prop="companyCode">
          <el-input v-model="form.companyCode" placeholder="请输入统一社会信用代码" :disabled="isEdit" />
        </el-form-item>
        
        <el-form-item label="法人代表" prop="legalPerson">
          <el-input v-model="form.legalPerson" placeholder="请输入法人代表" />
        </el-form-item>
        
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" type="email" />
        </el-form-item>
        
        <el-form-item label="公司地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入公司地址" />
        </el-form-item>
        
        <el-form-item label="行业分类" prop="industry">
          <el-select v-model="form.industry" placeholder="请选择行业分类">
            <el-option label="信息技术" value="信息技术" />
            <el-option label="建筑工程" value="建筑工程" />
            <el-option label="医疗器械" value="医疗器械" />
            <el-option label="办公用品" value="办公用品" />
            <el-option label="咨询服务" value="咨询服务" />
            <el-option label="物流运输" value="物流运输" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="企业规模" prop="scale">
          <el-select v-model="form.scale" placeholder="请选择企业规模">
            <el-option label="小型企业" value="SMALL" />
            <el-option label="中型企业" value="MEDIUM" />
            <el-option label="大型企业" value="LARGE" />
          </el-select>
        </el-form-item>

        <el-form-item label="资质等级" prop="qualificationLevel">
          <el-select v-model="form.qualificationLevel" placeholder="请选择资质等级" clearable>
            <el-option label="一级资质" value="一级资质" />
            <el-option label="二级资质" value="二级资质" />
            <el-option label="三级资质" value="三级资质" />
          </el-select>
        </el-form-item>

        <el-form-item label="公司描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入公司简介或经营范围等信息"
          />
        </el-form-item>
        
        <el-form-item label="认证状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">待审核</el-radio>
            <el-radio :label="1">已认证</el-radio>
            <el-radio :label="2">已驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSupplierById, createSupplier, updateSupplier } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const supplierData = ref({})

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  companyName: '',
  companyCode: '',
  legalPerson: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  industry: '',
  scale: 'MEDIUM',
  qualificationLevel: '',
  description: '',
  status: 0
})

const rules = {
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  companyCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业分类', trigger: 'change' }]
}

const loadData = async () => {
  if (isEdit.value) {
    try {
      const data = await getSupplierById(route.params.id)
      supplierData.value = data
      Object.assign(form, data)
    } catch (error) {
      console.error('加载数据失败', error)
      ElMessage.error('加载供应商信息失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    if (isEdit.value) {
      await updateSupplier(route.params.id, form)
      ElMessage.success('更新成功')
    } else {
      await createSupplier(form)
      ElMessage.success('创建成功')
    }
    
    router.push('/suppliers')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
    console.error('提交失败', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}

const formatStatus = (status) => {
  const map = { 0: '待审核', 1: '已认证', 2: '已驳回' }
  return map[status] || '未知'
}

const getStatusTagType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
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
</style>
