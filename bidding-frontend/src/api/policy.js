import request from '@/utils/request'

export function getPolicyList(params) {
  return request({
    url: '/policies',
    method: 'get',
    params
  })
}

export function getPolicyById(id) {
  return request({
    url: `/policies/${id}`,
    method: 'get'
  })
}

export function createPolicy(data) {
  return request({
    url: '/policies',
    method: 'post',
    data
  })
}

export function updatePolicy(id, data) {
  return request({
    url: `/policies/${id}`,
    method: 'put',
    data
  })
}

export function deletePolicy(id) {
  return request({
    url: `/policies/${id}`,
    method: 'delete'
  })
}

export function publishPolicy(id) {
  return request({
    url: `/policies/${id}/publish`,
    method: 'put'
  })
}
