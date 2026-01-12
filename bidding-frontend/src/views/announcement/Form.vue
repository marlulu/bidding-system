<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑招标公告' : '新建招标公告' }}</span>
          <el-tag v-if="isEdit && announcementData.status" :type="getStatusTagType(announcementData.status)">
            {{ formatStatus(announcementData.status) }}
          </el-tag>
        </div>
      </template>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="公告编号" prop="announcementNo">
          <el-input v-model="form.announcementNo" placeholder="请输入公告编号" :disabled="isEdit" />
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型" :disabled="isEdit">
            <el-option label="招标公告" value="BID" />
            <el-option label="变更公告" value="CHANGE" />
            <el-option label="终止公告" value="TERMINATE" />
            <el-option label="中标结果" value="RESULT" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        
        <el-form-item label="项目预算" prop="projectBudget">
          <el-input-number v-model="form.projectBudget" :min="0" :precision="2" />
        </el-form-item>

        <el-form-item label="地区" prop="region">
          <el-input v-model="form.region" placeholder="请输入地区" />
        </el-form-item>

        <el-form-item label="行业" prop="industry">
          <el-input v-model="form.industry" placeholder="请输入行业" />
        </el-form-item>
        
        <el-form-item label="投标截止时间" prop="bidDeadline">
          <el-date-picker
            v-model="form.bidDeadline"
            type="datetime"
            placeholder="选择日期时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="可见级别" prop="visibilityLevel">
          <el-radio-group v-model="form.visibilityLevel">
            <el-radio label="PUBLIC">公开</el-radio>
            <el-radio label="RESTRICTED">受限</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item
          v-if="form.visibilityLevel === 'RESTRICTED'"
          label="可见供应商"
          prop="visibleSupplierIds"
        >
          <el-select
            v-model="form.visibleSupplierIds"
            multiple
            placeholder="请选择可见供应商"
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

        <el-form-item label="是否置顶" prop="isTop">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
          <el-button v-if="isEdit && announcementData.status === 'DRAFT'" 
            type="success" 
            @click="handlePublish" 
            :loading="loading"
          >
            发布
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
import { getAnnouncementById, createAnnouncement, updateAnnouncement, publishAnnouncement } from '@/api/announcement'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const suppliers = ref([])
const announcementData = ref({})

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  announcementNo: '',
  title: '',
  type: 'BID',
  projectName: '',
  projectBudget: 0,
  region: '',
  industry: '',
  bidDeadline: '',
  contactPerson: '',
  contactPhone: '',
  visibilityLevel: 'PUBLIC',
  visibleSupplierIds: [],
  isTop: 0,
  content: ''
})

const rules = {
  announcementNo: [{ required: true, message: '请输入公告编号', trigger: 'blur' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
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
      const data = await getAnnouncementById(route.params.id)
      announcementData.value = data
      Object.assign(form, data)
      if (data.visibleSupplierIds) {
        form.visibleSupplierIds = JSON.parse(data.visibleSupplierIds)
      }
    } catch (error) {
      console.error('加载数据失败', error)
      ElMessage.error('加载公告数据失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const submitData = { ...form }
    if (submitData.visibleSupplierIds && submitData.visibleSupplierIds.length > 0) {
      submitData.visibleSupplierIds = JSON.stringify(submitData.visibleSupplierIds)
    } else {
      submitData.visibleSupplierIds = null
    }
    
    if (isEdit.value) {
      await updateAnnouncement(route.params.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createAnnouncement(submitData)
      ElMessage.success('创建成功')
    }
    
    router.push('/announcements')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
    console.error('提交失败', error)
  } finally {
    loading.value = false
  }
}

const handlePublish = async () => {
  try {
    loading.value = true
    await publishAnnouncement(route.params.id)
    ElMessage.success('发布成功')
    router.push('/announcements')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
    console.error('发布失败', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}

const formatStatus = (status) => {
  const map = { DRAFT: '草稿', PUBLISHED: '已发布', CLOSED: '已关闭' }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = { DRAFT: 'info', PUBLISHED: 'success', CLOSED: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  loadSuppliers()
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
