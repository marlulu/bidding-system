<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409EFF;">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">招标公告</div>
              <div class="stat-value">{{ stats.announcementCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67C23A;">
              <el-icon :size="30"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">供应商数量</div>
              <div class="stat-value">{{ stats.supplierCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #E6A23C;">
              <el-icon :size="30"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">政策法规</div>
              <div class="stat-value">{{ stats.policyCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #F56C6C;">
              <el-icon :size="30"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">未读通知</div>
              <div class="stat-value">{{ stats.unreadCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>欢迎使用内部招标网站系统</span>
          </template>
          <div class="welcome-content">
            <h3>系统功能介绍</h3>
            <ul>
              <li><strong>门户管理：</strong>招标信息发布、信息权限控制、政策法规发布、通知公告系统</li>
              <li><strong>供应商库管理：</strong>供应商信息管理、供应商分类</li>
              <li><strong>用户管理与权限管理：</strong>用户管理、登录认证系统</li>
            </ul>
            <p style="margin-top: 20px; color: #909399;">
              当前登录用户：{{ userStore.userInfo?.realName || userStore.userInfo?.username }} 
              （{{ userStore.userInfo?.role === 'ADMIN' ? '管理员' : '供应商' }}）
            </p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAnnouncementList } from '@/api/announcement'
import { getSupplierList } from '@/api/supplier'
import { getPolicyList } from '@/api/policy'
import { getUnreadCount } from '@/api/notice'

const userStore = useUserStore()

const stats = ref({
  announcementCount: 0,
  supplierCount: 0,
  policyCount: 0,
  unreadCount: 0
})

const loadStats = async () => {
  try {
    const [announcements, suppliers, policies, unread] = await Promise.all([
      getAnnouncementList({ page: 1, size: 1 }),
      getSupplierList({ page: 1, size: 1 }),
      getPolicyList({ page: 1, size: 1 }),
      getUnreadCount()
    ])
    
    stats.value = {
      announcementCount: announcements.total || 0,
      supplierCount: suppliers.total || 0,
      policyCount: policies.total || 0,
      unreadCount: unread || 0
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  userStore.getUserInfo()
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.welcome-content h3 {
  margin-top: 0;
  color: #303133;
}

.welcome-content ul {
  line-height: 2;
  color: #606266;
}

.welcome-content li {
  margin-bottom: 10px;
}
</style>
