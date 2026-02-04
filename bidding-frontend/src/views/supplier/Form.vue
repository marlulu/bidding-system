<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑供应商信息' : '新增供应商' }}</span>
          <el-tag v-if="isEdit && form.status !== undefined" :type="getStatusTagType(form.status)">
            {{ formatStatus(form.status) }}
          </el-tag>
        </div>
      </template>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="150px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公司名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="请输入公司名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一社会信用代码" prop="companyCode">
              <el-input v-model="form.companyCode" placeholder="请输入统一社会信用代码" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-select v-model="form.industry" placeholder="请选择所属行业" style="width: 100%;">
                <el-option label="信息技术" value="信息技术" />
                <el-option label="建筑工程" value="建筑工程" />
                <el-option label="医疗器械" value="医疗器械" />
                <el-option label="办公用品" value="办公用品" />
                <el-option label="咨询服务" value="咨询服务" />
                <el-option label="物流运输" value="物流运输" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司规模" prop="scale">
              <el-select v-model="form.scale" placeholder="请选择公司规模" style="width: 100%;">
                <el-option label="小型" value="SMALL" />
                <el-option label="中型" value="MEDIUM" />
                <el-option label="大型" value="LARGE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资质等级" prop="qualificationLevel">
              <el-input v-model="form.qualificationLevel" placeholder="请输入资质等级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法人代表" prop="legalPerson">
              <el-input v-model="form.legalPerson" placeholder="请输入法人代表" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法人电话" prop="legalPersonPhone">
              <el-input v-model="form.legalPersonPhone" placeholder="请输入法人联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="公司地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入公司地址" />
        </el-form-item>

        <el-form-item label="公司简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入公司简介" />
        </el-form-item>

        <el-form-item label="资质文件" prop="qualificationFiles">
          <el-upload
            class="upload-demo"
            action="#"
            :http-request="handleFileUpload"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            :limit="3"
            :on-exceed="handleExceed"
            multiple
            :auto-upload="true"
          >
            <el-button type="primary">点击上传文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持上传图片、PDF等文件，单个文件不超过5MB</div>
            </template>
          </el-upload>
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
import { getSupplierDetail, createSupplier, updateSupplier, uploadFile } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const fileList = ref([])

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  companyName: '',
  companyCode: '',
  industry: '',
  scale: 'SMALL',
  qualificationLevel: '',
  legalPerson: '',
  legalPersonPhone: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  description: '',
  qualificationFiles: '[]',
  status: 0 // 新增时默认为待审核
})

const rules = {
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  companyCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
  legalPerson: [{ required: true, message: '请输入法人代表', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
}

const loadData = async () => {
  if (isEdit.value) {
    try {
      const data = await getSupplierDetail(route.params.id)
      Object.assign(form, data)
      if (data.qualificationFiles) {
        fileList.value = JSON.parse(data.qualificationFiles).map(file => ({
          name: file.name,
          url: file.url,
          response: { data: file.url } // 模拟上传成功响应
        }))
      }
    } catch (error) {
      console.error('加载数据失败', error)
      ElMessage.error('加载供应商信息失败')
    }
  }
}

const handleFileUpload = async (options) => {
  const file = options.file
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await uploadFile(formData)
    const newFile = { name: file.name, url: res }
    fileList.value.push(newFile)
    form.qualificationFiles = JSON.stringify(fileList.value.map(f => ({ name: f.name, url: f.url })))
    ElMessage.success('文件上传成功')
  } catch (error) {
    ElMessage.error('文件上传失败')
  }
}

const handleFileRemove = (file) => {
  fileList.value = fileList.value.filter(f => f.url !== file.url)
  form.qualificationFiles = JSON.stringify(fileList.value.map(f => ({ name: f.name, url: f.url })))
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传3个文件')
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
    ElMessage.error(error.message || '提交失败')
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
