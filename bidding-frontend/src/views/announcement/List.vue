<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索标题、公告编号、项目名称"
          style="width: 300px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.type"
          placeholder="公告类型"
          style="width: 150px; margin-right: 10px;"
          clearable
        >
          <el-option label="招标公告" value="BID" />
          <el-option label="变更公告" value="CHANGE" />
          <el-option label="终止公告" value="TERMINATE" />
          <el-option label="中标结果" value="RESULT" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button
          v-if="userStore.isAdmin()"
          type="success"
          @click="handleCreate"
          style="float: right;"
        >
          新建公告
        </el-button>
      </div>
      
      <el-table :data="tableData" style="width: 100%; margin-top: 20px;" v-loading="loading">
        <el-table-column prop="announcementNo" label="公告编号" width="150" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'BID'" type="primary">招标公告</el-tag>
            <el-tag v-else-if="row.type === 'CHANGE'" type="warning">变更公告</el-tag>
            <el-tag v-else-if="row.type === 'TERMINATE'" type="danger">终止公告</el-tag>
            <el-tag v-else-if="row.type === 'RESULT'" type="success">中标结果</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'DRAFT'" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 'PUBLISHED'" type="success">已发布</el-tag>
            <el-tag v-else-if="row.status === 'CLOSED'" type="danger">已关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row.id)">查看</el-button>
            <template v-if="userStore.isAdmin()">
              <el-button link type="primary" @click="handleEdit(row.id)">编辑</el-button>
              <el-button
                v-if="row.status === 'DRAFT'"
                link
                type="success"
                @click="handlePublish(row.id)"
              >
                发布
              </el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAnnouncementList, deleteAnnouncement, publishAnnouncement } from '@/api/announcement'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  keyword: '',
  type: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await getAnnouncementList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      type: searchForm.type
    })
    tableData.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  handleSearch()
}

const handleCreate = () => {
  router.push('/announcements/create')
}

const handleView = (id) => {
  router.push(`/announcements/detail/${id}`)
}

const handleEdit = (id) => {
  router.push(`/announcements/edit/${id}`)
}

const handlePublish = async (id) => {
  try {
    await ElMessageBox.confirm('确认发布该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await publishAnnouncement(id)
    ElMessage.success('发布成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败', error)
    }
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteAnnouncement(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>
