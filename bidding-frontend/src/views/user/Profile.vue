<template>
  <div class="profile container">
    <el-row :gutter="30">
      <!-- 左侧菜单 -->
      <el-col :span="6">
        <el-card class="menu-card" shadow="never">
          <div class="user-brief">
            <el-avatar :size="80" :src="userStore.userInfo?.avatar" />
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
            <el-form :model="userForm" label-width="100px" style="max-width: 500px">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="userForm.realName" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdate">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>

          <div v-if="activeTab === 'favorites'">
            <h2 class="section-title">我的收藏</h2>
            <el-tabs v-model="favTab">
              <el-tab-pane label="招标公告" name="announcement">
                <el-empty description="暂无收藏的公告" />
              </el-tab-pane>
              <el-tab-pane label="供应商" name="supplier">
                <el-empty description="暂无收藏的供应商" />
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
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { User, Star, Document, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('info')
const favTab = ref('announcement')

const userForm = reactive({
  username: userStore.userInfo?.username || '',
  realName: userStore.userInfo?.realName || '',
  phone: userStore.userInfo?.phone || '',
  email: userStore.userInfo?.email || ''
})

const bidRecords = ref([
  { projectName: '2026年度企业数字化转型软件采购项目', bidAmount: 4800000, status: '已提交', createTime: '2026-01-08' }
])

const handleSelect = (index) => {
  activeTab.value = index
}

const handleUpdate = () => {
  ElMessage.success('资料更新成功')
}
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
</style>
