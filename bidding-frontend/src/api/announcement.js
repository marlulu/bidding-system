import request from '@/utils/request'

export function getAnnouncementList(params) {
  return request({
    url: '/announcements',
    method: 'get',
    params
  })
}

export function getAnnouncementById(id) {
  return request({
    url: `/announcements/${id}`,
    method: 'get'
  })
}

export function createAnnouncement(data) {
  return request({
    url: '/announcements',
    method: 'post',
    data
  })
}

export function updateAnnouncement(id, data) {
  return request({
    url: `/announcements/${id}`,
    method: 'put',
    data
  })
}

export function deleteAnnouncement(id) {
  return request({
    url: `/announcements/${id}`,
    method: 'delete'
  })
}

export function publishAnnouncement(id) {
  return request({
    url: `/announcements/${id}/publish`,
    method: 'put'
  })
}
