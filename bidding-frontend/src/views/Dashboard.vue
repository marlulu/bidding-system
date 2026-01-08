<template>
  <div class="dashboard">
    <!-- 轮播横幅 -->
    <el-carousel height="400px" indicator-position="outside">
      <el-carousel-item v-for="item in banners" :key="item.title">
        <div class="banner-item" :style="{ backgroundImage: `linear-gradient(rgba(0,51,102,0.7), rgba(0,51,102,0.7)), url(${item.image})` }">
          <div class="banner-content">
            <h1>{{ item.title }}</h1>
            <p>{{ item.subtitle }}</p>
            <el-button type="warning" size="large" @click="router.push(item.link)">立即体验</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="container">
      <!-- 核心功能模块展示 -->
      <div class="section">
        <div class="section-header">
          <h2 class="section-title">最新招标公告</h2>
          <el-link type="primary" @click="router.push('/announcements')">查看更多 <el-icon><ArrowRight /></el-icon></el-link>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="item in latestAnnouncements" :key="item.id">
            <el-card class="card-item announcement-card" shadow="hover" @click="router.push(`/announcements/detail/${item.id}`)">
              <div class="card-tag" v-if="item.isTop">置顶</div>
              <h3 class="title">{{ item.title }}</h3>
              <div class="info">
                <p><el-icon><Location /></el-icon> 地区：{{ item.region || '全国' }}</p>
                <p><el-icon><Money /></el-icon> 预算：<span class="price">￥{{ formatMoney(item.projectBudget) }}</span></p>
                <p><el-icon><Calendar /></el-icon> 截止：{{ formatDate(item.bidDeadline) }}</p>
              </div>
              <div class="card-footer">
                <el-tag size="small">{{ item.type }}</el-tag>
                <el-button type="primary" link>查看详情</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 供应商推荐 -->
      <div class="section bg-white">
        <div class="section-header">
          <h2 class="section-title">优质供应商推荐</h2>
          <el-link type="primary" @click="router.push('/suppliers')">查看更多 <el-icon><ArrowRight /></el-icon></el-link>
        </div>
        <div class="tag-cloud">
          <el-tag v-for="tag in hotTags" :key="tag" class="hot-tag" effect="plain" @click="handleTagClick(tag)">
            {{ tag }}
          </el-tag>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" v-for="item in recommendedSuppliers" :key="item.id">
            <el-card class="card-item supplier-card" shadow="hover" @click="router.push(`/suppliers/detail/${item.id}`)">
              <div class="supplier-logo">
                <el-icon :size="40" color="#003366"><OfficeBuilding /></el-icon>
              </div>
              <h3 class="name">{{ item.companyName }}</h3>
              <p class="industry">{{ item.industry }} | {{ item.qualificationLevel || '认证供应商' }}</p>
              <el-button type="primary" plain size="small">联系供应商</el-button>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Location, Money, Calendar, OfficeBuilding } from '@element-plus/icons-vue'
import { getAnnouncementList } from '@/api/announcement'
import { getSupplierList } from '@/api/supplier'

const router = useRouter()

const banners = [
  { title: '精准锁定招标商机', subtitle: '汇聚全国优质招标项目，助您事业腾飞', image: 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=2070', link: '/announcements' },
  { title: '100W+ 供应商选择', subtitle: '专业、诚信、高效的供应商库管理系统', image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?q=80&w=2069', link: '/suppliers' }
]

const latestAnnouncements = ref([])
const recommendedSuppliers = ref([])
const hotTags = ['信息技术', '建筑工程', '医疗器械', '办公用品', '咨询服务', '物流运输']

const loadData = async () => {
  try {
    const annRes = await getAnnouncementList({ page: 1, size: 6 })
    latestAnnouncements.value = annRes.records
    
    const supRes = await getSupplierList({ page: 1, size: 4 })
    recommendedSuppliers.value = supRes.records
  } catch (error) {
    console.error('加载首页数据失败', error)
  }
}

const formatMoney = (val) => {
  if (!val) return '面议'
  return val.toLocaleString()
}

const formatDate = (val) => {
  if (!val) return '-'
  return val.split(' ')[0]
}

const handleTagClick = (tag) => {
  router.push({ path: '/suppliers', query: { industry: tag } })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  background-color: #f5f5f5;
}

.banner-item {
  height: 400px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #fff;
}

.banner-content h1 {
  font-size: 48px;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.banner-content p {
  font-size: 20px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section {
  margin-bottom: 60px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-title {
  font-size: 28px;
  color: #003366;
  margin: 0;
  position: relative;
  padding-left: 15px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 24px;
  background-color: #FF6600;
}

.announcement-card {
  position: relative;
  height: 220px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  margin-bottom: 20px;
}

.card-tag {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #FF6600;
  color: #fff;
  padding: 2px 10px;
  font-size: 12px;
  border-bottom-left-radius: 8px;
  z-index: 1;
}

.announcement-card .title {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
  line-height: 1.4;
  height: 50px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.announcement-card .info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.announcement-card .info .el-icon {
  margin-right: 8px;
  color: #003366;
}

.price {
  color: #FF6600;
  font-weight: bold;
}

.card-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tag-cloud {
  margin-bottom: 30px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.hot-tag:hover {
  background-color: #003366;
  color: #fff;
}

.supplier-card {
  text-align: center;
  padding: 20px 10px;
  cursor: pointer;
  margin-bottom: 20px;
}

.supplier-logo {
  width: 80px;
  height: 80px;
  background-color: #f0f2f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.supplier-card .name {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.supplier-card .industry {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .banner-content h1 {
    font-size: 32px;
  }
  .banner-content p {
    font-size: 16px;
  }
}
</style>
