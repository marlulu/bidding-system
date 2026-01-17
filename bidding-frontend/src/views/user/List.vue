<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.username"
          placeholder="搜索用户名"
          style="width: 200px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.role"
          placeholder="角色"
          style="width: 120px; margin-right: 10px;"
          clearable
        >
          <el-option label="管理员" value="ADMIN" />
          <el-option label="供应商" value="SUPPLIER" />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          style="width: 120px; margin-right: 10px;"
          clearable
        >
          <el-option label="待审核" :value="0" />
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="handleCreate" style="float: right;">新建用户</el-button>
      </div>
      
      <el-table :data="tableData" style="width: 100%; margin-top: 20px;" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'ADMIN'" type="danger">管理员</el-tag>
            <el-tag v-else-if="row.role === 'SUPPLIER'" type="primary">供应商</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="supplierName" label="关联供应商" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleUpdate(row)">编辑</el-button>
            <el-button link type="primary" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-dropdown style="margin-left: 10px;">
              <el-button link type="primary">
                更多操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleModifyRole(row, 'ADMIN')" v-if="row.role !== 'ADMIN'">设为管理员</el-dropdown-item>
                  <el-dropdown-item @click="handleModifyRole(row, 'SUPPLIER')" v-if="row.role !== 'SUPPLIER'">设为供应商</el-dropdown-item>
                  <el-dropdown-item @click="handleDelete(row.id)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
    
    <!-- 新建/编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%;">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="供应商" value="SUPPLIER" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.role === 'SUPPLIER'" label="关联供应商" prop="supplierId">
          <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%;">
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.companyName"
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="待审核" :value="0" />
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getUserList, createUser, updateUser, deleteUser, updateUserStatus, updateUserRole } from '@/api/user'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const suppliers = ref([])
const isEdit = ref(false)
const dialogTitle = ref('新建用户')

const searchForm = reactive({
  username: '',
  role: '',
  status: undefined
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const defaultForm = {
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'SUPPLIER',
  supplierId: null,
  status: 0
}

const form = reactive({ ...defaultForm })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.page,
      size: pagination.size,
      username: searchForm.username,
      role: searchForm.role,
      status: searchForm.status
    })
    tableData.value = res.records
    pagination.total = res.total
  } catch (error) {
    console.error('加载数据失败', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const loadSuppliers = async () => {
  try {
    const res = await getAllSuppliers()
    suppliers.value = res
  } catch (error) {
    console.error('加载供应商列表失败', error)
    ElMessage.error('加载供应商列表失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.role = ''
  searchForm.status = undefined
  handleSearch()
}

const handleCreate = () => {
  Object.assign(form, defaultForm)
  isEdit.value = false
  dialogTitle.value = '新建用户'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleUpdate = (row) => {
  Object.assign(form, row)
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateUser(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createUser(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败', error)
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 2 : 1 // 正常 <-> 禁用
    await updateUserStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    loadData()
  } catch (error) {
    console.error('更新状态失败', error)
    ElMessage.error('更新状态失败')
  }
}

const handleModifyRole = async (row, role) => {
  try {
    await ElMessageBox.confirm(`确认将用户 ${row.username} 设为 ${role === 'ADMIN' ? '管理员' : '供应商'} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateUserRole(row.id, role)
    ElMessage.success('角色更新成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新角色失败', error)
      ElMessage.error('更新角色失败')
    }
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteUser(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
  loadSuppliers()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
</style>
