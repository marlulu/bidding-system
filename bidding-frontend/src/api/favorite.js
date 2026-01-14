import request from '@/utils/request'

export function getFavorites(params) {
  return request({
    url: '/favorites',
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

export function removeFavorite(id) {
  return request({
    url: `/favorites/${id}`,
    method: 'delete'
  })
}

export function checkFavoriteStatus(targetId, targetType) {
  return request({
    url: `/favorites/status`,
    method: 'get',
    params: { targetId, targetType }
  })
}
