import request from '@/utils/request'

export function getExpertList(params) {
  return request({
    url: '/experts',
    method: 'get',
    params
  })
}

export function getExpertById(id) {
  return request({
    url: `/experts/${id}`,
    method: 'get'
  })
}

export function addExpert(data) {
  return request({
    url: '/experts',
    method: 'post',
    data
  })
}

export function updateExpert(id, data) {
  return request({
    url: `/experts/${id}`,
    method: 'put',
    data
  })
}

export function deleteExpert(id) {
  return request({
    url: `/experts/${id}`,
    method: 'delete'
  })
}

export function extractExperts(announcementId, extractCount) {
  return request({
    url: '/expert-extractions/extract',
    method: 'post',
    params: { announcementId, extractCount }
  })
}

export function getExtractedExperts(announcementId) {
  return request({
    url: `/expert-extractions/announcement/${announcementId}`,
    method: 'get'
  })
}

export function checkExtractionStatus(announcementId) {
  return request({
    url: `/expert-extractions/announcement/${announcementId}/status`,
    method: 'get'
  })
}
