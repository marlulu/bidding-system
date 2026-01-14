<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑系统通知' : '新建系统通知' }}</span>
      </template>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择通知类型">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="政策变动" value="POLICY" />
            <el-option label="公告提醒" value="ANNOUNCEMENT" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="目标类型" prop="targetType">
          <el-radio-group v-model="form.targetType">
            <el-radio label="ALL">全部用户</el-radio>
            <el-radio label="SUPPLIER">指定供应商</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item
          v-if="form.targetType === 'SUPPLIER'"
          label="目标供应商"
          prop="targetSupplierIds"
        >
          <el-select
            v-model="form.targetSupplierIds"
            multiple
            placeholder="请选择目标供应商"
            style="width: 100%;"
          >
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.companyName"
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入通知内容"
          />
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
import { getNoticeById, createNotice, updateNotice } from '@/api/notice'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const suppliers = ref([])

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  type: 'SYSTEM',
  targetType: 'ALL',
  targetSupplierIds: [],
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const loadSuppliers = async () => {
  try {
    suppliers.value = await getAllSuppliers()
  } catch (error) {
    console.error('加载供应商列表失败', error)
  }
}

const loadData = async () => {
  if (isEdit.value) {
    try {
      const data = await getNoticeById(route.params.id)
      if (data.targetSupplierIds) {
        try {
          data.targetSupplierIds = JSON.parse(data.targetSupplierIds)
        } catch (e) {
          data.targetSupplierIds = []
        }
      } else {
        data.targetSupplierIds = []
      }
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
    
    const submitData = { ...form }
    if (submitData.targetSupplierIds && submitData.targetSupplierIds.length > 0) {
      submitData.targetSupplierIds = JSON.stringify(submitData.targetSupplierIds)
    } else {
      submitData.targetSupplierIds = null
    }
    
    if (isEdit.value) {
      await updateNotice(route.params.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createNotice(submitData)
      ElMessage.success('创建成功')
    }
    router.push('/notices')
  } catch (error) {
    console.error('提交失败', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}

onMounted(async () => {
  await loadSuppliers()
  await loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
</style>
