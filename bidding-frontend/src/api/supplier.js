import request from '@/utils/request'

const mockSuppliers = [
  { id: 1, companyName: '中科智控技术有限公司', industry: '信息技术', scale: 'LARGE', qualificationLevel: '一级资质', contactName: '王经理', contactPhone: '13811112222', description: '专注于企业级 AI 解决方案与自动化控制系统。' },
  { id: 2, companyName: '建工集团第三工程局', industry: '建筑工程', scale: 'LARGE', qualificationLevel: '特级资质', contactName: '李工', contactPhone: '13922223333', description: '国家大型建筑骨干企业，承建多项地标性建筑。' },
  { id: 3, companyName: '华东医疗器械有限公司', industry: '医疗器械', scale: 'MEDIUM', qualificationLevel: '二级资质', contactName: '陈主任', contactPhone: '13733334444', description: '专业生产高端影像设备与实验室分析仪器。' },
  { id: 4, companyName: '优选办公用品商贸', industry: '办公用品', scale: 'SMALL', qualificationLevel: '三级资质', contactName: '张经理', contactPhone: '13644445555', description: '一站式办公物资供应服务商，覆盖全国配送。' },
  { id: 5, companyName: '德勤咨询（中国）', industry: '咨询服务', scale: 'LARGE', qualificationLevel: '一级资质', contactName: '赵顾问', contactPhone: '13555556666', description: '全球领先的专业咨询机构，提供战略与数字化转型建议。' },
  { id: 6, companyName: '顺风物流自动化', industry: '物流运输', scale: 'MEDIUM', qualificationLevel: '二级资质', contactName: '孙经理', contactPhone: '13466667777', description: '智能仓储与无人配送技术领跑者。' }
]

export function getSupplierList(params) {
  return request({
    url: '/suppliers',
    method: 'get',
    params
  }).catch(() => {
    return {
      records: mockSuppliers,
      total: mockSuppliers.length,
      size: 10,
      current: 1
    }
  })
}

export function getAllSuppliers() {
  return request({
    url: '/suppliers/all',
    method: 'get'
  }).catch(() => mockSuppliers)
}

export function getSupplierById(id) {
  return request({
    url: `/suppliers/${id}`,
    method: 'get'
  }).catch(() => {
    return mockSuppliers.find(s => s.id == id)
  })
}

export function createSupplier(data) {
  return request({ url: '/suppliers', method: 'post', data })
}

export function updateSupplier(id, data) {
  return request({ url: `/suppliers/${id}`, method: 'put', data })
}

export function deleteSupplier(id) {
  return request({ url: `/suppliers/${id}`, method: 'delete' })
}

export function updateSupplierStatus(id, status) {
  return request({
    url: `/suppliers/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function auditSupplier(id, data) {
  return request({
    url: `/suppliers/${id}/audit`,
    method: 'put',
    data
  })
}

export function registerSupplier(data) {
  return request({
    url: '/suppliers/register',
    method: 'post',
    data
  })
}
