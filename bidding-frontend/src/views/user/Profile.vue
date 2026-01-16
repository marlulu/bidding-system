<template>
  <div class="profile container">
    <el-row :gutter="30">
      <!-- 左侧菜单 -->
      <el-col :span="6">
        <el-card class="menu-card" shadow="never">
          <div class="user-brief">
            <el-avatar :size="80" :src="userStore.userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <h3>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</h3>
            <el-tag size="small">{{ userStore.userInfo?.role === 'ADMIN' ? '系统管理员' : '认证供应商' }}</el-tag>
          </div>
          <el-menu :default-active="activeTab" class="profile-menu" @select="handleSelect">
            <el-menu-item index="info">
              <el-icon><User /></el-icon><span>个人资料</span>
            </el-menu-item>
            <el-menu-item index="favorites">
              <el-icon><Star /></el-icon><span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="bids" v-if="userStore.isSupplier()">
              <el-icon><Document /></el-icon><span>投标记录</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon><span>安全设置</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="18">
        <el-card class="content-card" shadow="never">
          <div v-if="activeTab === 'info'">
            <h2 class="section-title">个人资料</h2>
            <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px" style="max-width: 500px">
              <el-form-item label="头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/files/upload"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar">
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="userForm.realName" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userForm.phone" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="userForm.email" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateUserInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>

          <div v-if="activeTab === 'favorites'">
            <h2 class="section-title">我的收藏</h2>
            <el-tabs v-model="favTab" @tab-change="loadFavorites">
              <el-tab-pane label="招标公告" name="announcement">
                <el-table :data="favoriteAnnouncements" v-loading="favoriteLoading" style="width: 100%">
                  <el-table-column prop="title" label="公告标题" />
                  <el-table-column prop="publishTime" label="发布时间" width="180" />
                  <el-table-column label="操作" width="150">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="viewAnnouncementDetail(row.targetId)">查看</el-button>
                      <el-button link type="danger" @click="handleRemoveFavorite(row.id)">取消收藏</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-empty v-if="favoriteAnnouncements.length === 0 && !favoriteLoading" description="暂无收藏的公告" />
              </el-tab-pane>
              <el-tab-pane label="供应商" name="supplier">
                <el-table :data="favoriteSuppliers" v-loading="favoriteLoading" style="width: 100%">
                  <el-table-column prop="companyName" label="公司名称" />
                  <el-table-column prop="contactName" label="联系人" width="120" />
                  <el-table-column prop="contactPhone" label="联系电话" width="150" />
                  <el-table-column label="操作" width="150">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="viewSupplierDetail(row.targetId)">查看</el-button>
                      <el-button link type="danger" @click="handleRemoveFavorite(row.id)">取消收藏</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-empty v-if="favoriteSuppliers.length === 0 && !favoriteLoading" description="暂无收藏的供应商" />
              </el-tab-pane>
            </el-tabs>
          </div>

          <div v-if="activeTab === 'bids'">
            <h2 class="section-title">投标记录</h2>
            <el-table :data="bidRecords" style="width: 100%">
              <el-table-column prop="projectName" label="项目名称" />
              <el-table-column prop="bidAmount" label="投标金额">
                <template #default="{ row }">￥{{ row.bidAmount.toLocaleString() }}</template>
              </el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'WIN' ? 'success' : 'info'">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="投标时间" />
            </el-table>
          </div>

          <div v-if="activeTab === 'security'">
            <h2 class="section-title">安全设置</h2>
            <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" style="max-width: 500px">
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Star, Document, Lock, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { updateUserInfo as apiUpdateUserInfo, changePassword as apiChangePassword } from '@/api/user'
import { getFavorites, removeFavorite } from '@/api/favorite'
import { getAnnouncementDetail } from '@/api/announcement'
import { getSupplierDetail } from '@/api/supplier'
import { getToken } from '@/utils/auth'

const userStore = useUserStore()
const route = useRoute()
const activeTab = ref(route.query.tab || 'info')
const favTab = ref('announcement')

const userFormRef = ref(null)
const passwordFormRef = ref(null)

const userForm = reactive({
  id: userStore.userInfo?.id,
  username: userStore.userInfo?.username || '',
  realName: userStore.userInfo?.realName || '',
  phone: userStore.userInfo?.phone || '',
  email: userStore.userInfo?.email || '',
  avatar: userStore.userInfo?.avatar || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const uploadHeaders = reactive({
  Authorization: 'Bearer ' + getToken()
})

const userRules = {
  realName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '旧密码不能为空', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

const bidRecords = ref([
  { projectName: '2026年度企业数字化转型软件采购项目', bidAmount: 4800000, status: '已提交', createTime: '2026-01-08' }
])

const favoriteAnnouncements = ref([])
const favoriteSuppliers = ref([])
const favoriteLoading = ref(false)

const handleSelect = (index) => {
  activeTab.value = index
}

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    userForm.avatar = res.data // 假设后端返回的是文件URL
    userStore.setUserInfo({ ...userStore.userInfo, avatar: res.data })
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败: ' + res.message)
  }
}

const beforeAvatarUpload = (file) => {
  const isJPGPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGPNG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPGPNG && isLt2M
}

const handleUpdateUserInfo = async () => {
  try {
    await userFormRef.value.validate()
    await apiUpdateUserInfo(userForm.id, userForm)
    userStore.setUserInfo({ ...userStore.userInfo, ...userForm }) // 更新 store 中的用户信息
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    console.error('更新个人信息失败', error)
    ElMessage.error('更新个人信息失败: ' + (error.message || '未知错误'))
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    await apiChangePassword(passwordForm)
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout() // 修改密码成功后强制退出登录
  } catch (error) {
    console.error('修改密码失败', error)
    ElMessage.error('修改密码失败: ' + (error.message || '未知错误'))
  }
}

// 监听 URL 中的 tab 参数变化，同步到 activeTab
watch(() => route.query.tab, (newTab) => {
  activeTab.value = newTab || 'info'
}, { immediate: true })

// 监听 userStore.userInfo 变化，同步到 userForm
watch(() => userStore.userInfo, (newVal) => {
  if (newVal) {
    Object.assign(userForm, newVal)
  }
}, { immediate: true, deep: true })

// 确保在组件挂载时初始化数据
const loadFavorites = async () => {
  favoriteLoading.value = true
  try {
    const res = await getFavorites({ targetType: favTab.value.toUpperCase() })
    if (favTab.value === 'announcement') {
      // 批量获取公告详情
      const announcementDetails = await Promise.all(res.data.records.map(async (fav) => {
        const detail = await getAnnouncementDetail(fav.targetId)
        return { ...fav, ...detail.data }
      }))
      favoriteAnnouncements.value = announcementDetails
    } else if (favTab.value === 'supplier') {
      // 批量获取供应商详情
      const supplierDetails = await Promise.all(res.data.records.map(async (fav) => {
        const detail = await getSupplierDetail(fav.targetId)
        return { ...fav, ...detail.data }
      }))
      favoriteSuppliers.value = supplierDetails
    }
  } catch (error) {
    console.error('加载收藏列表失败', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    favoriteLoading.value = false
  }
}

const handleRemoveFavorite = async (id) => {
  try {
    await ElMessageBox.confirm('确认取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFavorite(id)
    ElMessage.success('取消收藏成功')
    loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

const viewAnnouncementDetail = (id) => {
  // 跳转到公告详情页
  router.push({ name: 'AnnouncementDetail', params: { id } })
}

const viewSupplierDetail = (id) => {
  // 跳转到供应商详情页
  router.push({ name: 'SupplierDetail', params: { id } })
}

onMounted(() => {
  // 确保user对象与store中的userInfo同步
  Object.assign(userForm, userStore.userInfo)
  loadFavorites()
})

watch(activeTab, (newVal) => {
  if (newVal === 'favorites') {
    loadFavorites()
  }
})

watch(favTab, () => {
  loadFavorites()
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.menu-card {
  padding: 20px 0;
}

.user-brief {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.user-brief h3 {
  margin: 15px 0 10px;
  color: #333;
}

.profile-menu {
  border-right: none;
}

.section-title {
  font-size: 20px;
  color: #003366;
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.content-card {
  min-height: 500px;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  border-radius: 50%;
  line-height: 100px;
}
</style>
