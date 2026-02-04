<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header class="clearfix">
        <span>专家库管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" :icon="Plus" @click="handleAdd">新增专家</el-button>
      </template>
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="专家姓名">
          <el-input v-model="searchForm.name" placeholder="请输入专家姓名"></el-input>
        </el-form-item>
        <el-form-item label="专业领域">
          <el-input v-model="searchForm.specialty" placeholder="请输入专业领域"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="listLoading"
        :data="list"
        element-loading-text="Loading"
        border
        fit
        highlight-current-row
      >
        <el-table-column align="center" label="ID" width="95">
          <template #default="{ row }">
            {{ row.id }}
          </template>
        </el-table-column>
        <el-table-column label="专家姓名" prop="name"></el-table-column>
        <el-table-column label="专业领域" prop="specialty"></el-table-column>
        <el-table-column label="职称" prop="title"></el-table-column>
        <el-table-column label="联系电话" prop="phone"></el-table-column>
        <el-table-column label="邮箱" prop="email"></el-table-column>
        <el-table-column label="简介" prop="description" show-overflow-tooltip></el-table-column>
        <el-table-column label="证书照片" align="center" width="100">
          <template #default="{ row }">
             <div v-if="isValidPhoto(row.certificatePhoto)" class="table-photo-cell">
               <el-image 
                 :src="getFirstPhoto(row.certificatePhoto)" 
                 :preview-src-list="getAllPhotos(row.certificatePhoto)"
                 fit="cover"
                 style="width: 50px; height: 50px"
                 preview-teleported
               />
               <span v-if="getPhotoCount(row.certificatePhoto) > 1" class="photo-count">
                 +{{ getPhotoCount(row.certificatePhoto) - 1 }}
               </span>
             </div>
             <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160">
          <template #default="{ row }">
            {{ row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-show="total>0"
        :total="total"
        v-model:current-page="listQuery.page"
        v-model:page-size="listQuery.size"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog :title="textMap[dialogStatus]" v-model="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="专家姓名" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="证件号码" prop="idCardNumber">
          <el-input v-model="temp.idCardNumber" placeholder="请输入身份证号或专家证号" />
        </el-form-item>
        <el-form-item label="专业领域" prop="specialty">
          <el-input v-model="temp.specialty" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="temp.title" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="temp.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="temp.email" />
        </el-form-item>
        <el-form-item label="专家简介" prop="description">
          <el-input v-model="temp.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="证书照片" prop="certificatePhoto">
          <div class="photo-list">
            <div v-for="(url, index) in certificateList" :key="index" class="photo-item">
              <el-image 
                :src="getPreviewUrl(url)" 
                :preview-src-list="getAllPreviewUrls()"
                fit="cover"
                class="photo-img"
              />
              <div class="photo-delete" @click="handleRemovePhoto(index)">
                <el-icon><Delete /></el-icon>
              </div>
            </div>
            
            <el-upload
              class="photo-uploader"
              action="/api/common/upload"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <div class="photo-add">
                <el-icon><Plus /></el-icon>
                <div class="add-text">新增</div>
              </div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExpertList, addExpert, updateExpert, deleteExpert } from '@/api/expert'
import { Plus, Search, Refresh, Delete } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'




const listLoading = ref(true)
const list = ref([])
const total = ref(0)
const listQuery = reactive({
  page: 1,
  size: 10
})
const searchForm = reactive({
  name: 
null,
  specialty: 
null
})

const dialogFormVisible = ref(false)
const dialogStatus = ref('')
const textMap = reactive({
  update: '编辑专家',
  create: '新增专家'
})
const temp = reactive({
  id: undefined,
  name: '',
  specialty: '',
  title: '',
  phone: '',
  email: '',
  description: '',
  idCardNumber: '',
  certificatePhoto: ''
})

const certificateList = ref([])

const uploadHeaders = {
  Authorization: `Bearer ${getToken()}`
}

const rules = reactive({
  name: [{ required: true, message: '专家姓名不能为空', trigger: 'blur' }],
  idCardNumber: [{ required: true, message: '证件号码不能为空', trigger: 'blur' }],
  specialty: [{ required: true, message: '专业领域不能为空', trigger: 'blur' }],
  phone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }]
})

const getList = async () => {
  listLoading.value = true
  try {
    const response = await getExpertList({ ...listQuery, ...searchForm })
    list.value = response.records
    total.value = response.total
  } catch (error) {
    console.error('加载专家列表失败', error)
    ElMessage.error('加载专家列表失败')
  } finally {
    listLoading.value = false
  }
}

const handleSearch = () => {
  listQuery.page = 1
  getList()
}

const handleReset = () => {
  searchForm.name = null
  searchForm.specialty = null
  listQuery.page = 1
  getList()
}

const resetTemp = () => {
  temp.id = undefined
  temp.name = ''
  temp.specialty = ''
  temp.title = ''
  temp.phone = ''
  temp.email = ''
  temp.description = ''
  temp.idCardNumber = ''
  temp.certificatePhoto = ''
  certificateList.value = []
}

// Helper functions for image display
const getPreviewUrl = (url) => {
  if (!url) return ''
  return `/api/common/view?url=${encodeURIComponent(url)}`
}

const getAllPreviewUrls = () => {
  return certificateList.value.map(url => getPreviewUrl(url))
}

const isValidPhoto = (jsonStr) => {
  if (!jsonStr) return false
  try {
    const photos = JSON.parse(jsonStr)
    return Array.isArray(photos) ? photos.length > 0 : !!jsonStr
  } catch (e) {
    return !!jsonStr
  }
}

const getFirstPhoto = (jsonStr) => {
  if (!jsonStr) return ''
  try {
    const photos = JSON.parse(jsonStr)
    return Array.isArray(photos) && photos.length > 0 ? getPreviewUrl(photos[0]) : ''
  } catch (e) {
    return getPreviewUrl(jsonStr)
  }
}

const getAllPhotos = (jsonStr) => {
  if (!jsonStr) return []
  try {
    const photos = JSON.parse(jsonStr)
    return Array.isArray(photos) ? photos.map(url => getPreviewUrl(url)) : [getPreviewUrl(jsonStr)]
  } catch (e) {
    return [getPreviewUrl(jsonStr)]
  }
}

const getPhotoCount = (jsonStr) => {
   if (!jsonStr) return 0
   try {
     const photos = JSON.parse(jsonStr)
     return Array.isArray(photos) ? photos.length : 1
   } catch (e) {
     return 1
   }
}

const handleRemovePhoto = (index) => {
  certificateList.value.splice(index, 1)
  temp.certificatePhoto = JSON.stringify(certificateList.value)
}

const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 200) {
    certificateList.value.push(response.data.url)
    temp.certificatePhoto = JSON.stringify(certificateList.value)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeAvatarUpload = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAdd = () => {
  resetTemp()
  dialogStatus.value = 'create'
  dialogFormVisible.value = true
}

const createData = () => {
  // Ensure certificatePhoto is synced (though it should be)
  temp.certificatePhoto = JSON.stringify(certificateList.value)
  
  // eslint-disable-next-line no-unused-vars
  const { id, ...data } = temp
  addExpert(data).then(() => {
    getList()
    dialogFormVisible.value = false
    ElMessage.success('新增成功')
  })
}

const handleEdit = (row) => {
  Object.assign(temp, row)
  
  if (temp.certificatePhoto) {
    try {
      const parsed = JSON.parse(temp.certificatePhoto)
      certificateList.value = Array.isArray(parsed) ? parsed : [temp.certificatePhoto]
    } catch (e) {
      certificateList.value = [temp.certificatePhoto]
    }
  } else {
    certificateList.value = []
  }
  
  dialogStatus.value = 'update'
  dialogFormVisible.value = true
}

const updateData = () => {
  temp.certificatePhoto = JSON.stringify(certificateList.value)
  
  updateExpert(temp.id, temp).then(() => {
    getList()
    dialogFormVisible.value = false
    ElMessage.success('更新成功')
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该专家吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteExpert(row.id).then(() => {
      getList()
      ElMessage.success('删除成功')
    })
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

onMounted(() => {
  getList()
})

</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}
.pagination-container {
  padding: 32px 16px;
}
.photo-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.photo-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #d9d9d9;
}
.photo-img {
  width: 100%;
  height: 100%;
}
.photo-delete {
  position: absolute;
  top: 0;
  right: 0;
  width: 20px;
  height: 20px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-bottom-left-radius: 4px;
  transition: background-color 0.3s;
}
.photo-delete:hover {
  background-color: rgba(255, 0, 0, 0.7);
}
.photo-uploader :deep(.el-upload) {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--el-transition-duration-fast);
}
.photo-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}
.photo-add {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.add-text {
  font-size: 12px;
  color: #8c939d;
  margin-top: 5px;
}
.table-photo-cell {
  position: relative;
  display: inline-block;
  line-height: 1;
}
.photo-count {
  position: absolute;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  color: #fff;
  border-radius: 8px;
  padding: 1px 5px;
  font-size: 10px;
  transform: translate(30%, 30%);
}
</style>
