<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑供应商' : '新建供应商' }}</span>
      </template>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入公司名称" />
        </el-form-item>
        
        <el-form-item label="统一社会信用代码" prop="companyCode">
          <el-input v-model="form.companyCode" placeholder="请输入统一社会信用代码" />
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
          <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
        </el-form-item>
        
        <el-form-item label="公司地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入公司地址" />
        </el-form-item>
        
        <el-form-item label="行业分类" prop="industry">
          <el-input v-model="form.industry" placeholder="请输入行业分类" />
        </el-form-item>
        
        <el-form-item label="企业规模" prop="scale">
          <el-select v-model="form.scale" placeholder="请选择企业规模">
            <el-option label="小型" value="SMALL" />
            <el-option label="中型" value="MEDIUM" />
            <el-option label="大型" value="LARGE" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">待审核</el-radio>
            <el-radio :label="1">已认证</el-radio>
            <el-radio :label="2">已拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
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
  status: 0
})

const rules = {
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  companyCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const loadData = async () => {
  if (isEdit.value) {
    try {
      const data = await getSupplierById(route.params.id)
      Object.assign(form, data)
    } catch (error) {
      console.error('加载数据失败', error)
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
    console.error('提交失败', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
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
</style>
