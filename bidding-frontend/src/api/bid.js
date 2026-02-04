import request from '@/utils/request'

export function submitBid(data) {
    return request({
        url: '/bids',
        method: 'post',
        data
    })
}

export function getMyBids(params) {
    return request({
        url: '/bids/my',
        method: 'get',
        params
    })
}

export function getBidsByAnnouncement(announcementId) {
    return request({
        url: `/bids/announcement/${announcementId}`,
        method: 'get'
    })
}

export function checkHasBid(announcementId) {
    return request({
        url: `/bids/check/${announcementId}`,
        method: 'get'
    })
}

export const getMyBidDetail = (announcementId) => {
    return request.get(`/bids/detail/${announcementId}`)
}

export function auditBid(id, data) {
    return request({
        url: `/bids/${id}/audit`,
        method: 'put',
        data
    })
}
