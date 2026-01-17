import request from '@/utils/request'

export function getFavorites(params) {
  return request({
    url: '/favorites/list',
    method: 'get',
    params
  })
}

export function addFavorite(data) {
  return request({
    url: '/favorites',
    method: 'post',
    data
  })
}

export function removeFavorite(targetId, targetType) {
  return request({
    url: '/favorites',
    method: 'delete',
    params: { targetId, targetType }
  })
}

export function checkFavoriteStatus(targetId, targetType) {
  return request({
    url: `/favorites/check`,
    method: 'get',
    params: { targetId, targetType }
  })
}
