// @ts-ignore
/* eslint-disable */
import request from '@/libs/request'

/** 此处后端没有提供注释 GET /boss/list */
export async function listBoss1(options?: { [key: string]: any }) {
  return request<API.BaseResponseListUserVO>('/boss/list', {
    method: 'GET',
    ...(options || {}),
  })
}
