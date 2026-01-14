<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>
      <el-form ref="formRef" :model="registerForm" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" show-password placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="注册角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="SUPPLIER">供应商</el-radio>
            <el-radio label="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <div v-if="registerForm.role === 'SUPPLIER'" class="supplier-info">
          <el-divider>供应商信息</el-divider>
          <el-form-item label="关联供应商" prop="supplierId">
            <el-select v-model="registerForm.supplierId" placeholder="请选择所属供应商" style="width: 100%">
              <el-option
                v-for="item in supplierOptions"
                :key="item.id"
                :label="item.companyName"
                :value="item.id"
              />
            </el-select>
            <div class="tips">如果没有您的公司，请先联系管理员录入供应商信息。</div>
          </el-form-item>
        </div>

        <el-button type="primary" :loading="loading" @click="handleRegister" style="width: 100%; margin-top: 20px">
          立即注册
        </el-button>
        <div class="login-link">
          已有账号？<el-link type="primary" @click="router.push('/')">返回登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const supplierOptions = ref([])

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  role: 'SUPPLIER',
  supplierId: null
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  supplierId: [{ required: true, message: '请选择所属供应商', trigger: 'change' }]
}

const loadSuppliers = async () => {
  try {
    supplierOptions.value = await getAllSuppliers()
  } catch (error) {
    console.error('加载供应商失败', error)
  }
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    await register(registerForm)
    ElMessage.success('注册成功，请等待管理员审核')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSuppliers()
})
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  padding: 40px 0;
}

.register-box {
  width: 500px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #003366;
}

.login-link {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.tips {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
