<template>
  <div class="reset-container">
    <div class="reset-box">
      <h2 class="reset-title">重置密码</h2>
      <el-form ref="formRef" :model="resetForm" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="resetForm.username" placeholder="请输入您的用户名" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="resetForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>

        <el-button type="primary" :loading="loading" @click="handleReset" style="width: 100%; margin-top: 20px">
          重置密码
        </el-button>
        <div class="login-link">
          记起密码了？<el-link type="primary" @click="router.push('/')">返回登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { resetPassword } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const resetForm = reactive({
  username: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== resetForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

const handleReset = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    await resetPassword(resetForm)
    ElMessage.success('密码重置成功，请使用新密码登录')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '重置失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reset-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}

.reset-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.reset-title {
  text-align: center;
  margin-bottom: 30px;
  color: #003366;
}

.login-link {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}
</style>
