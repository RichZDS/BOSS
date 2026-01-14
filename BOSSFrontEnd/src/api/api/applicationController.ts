// @ts-ignore
/* eslint-disable */
import request from '@/libs/request'

/** 此处后端没有提供注释 POST /application/add */
export async function addApplication(
  body: API.ApplicationAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>('/application/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /application/delete */
export async function deleteApplication(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/application/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /application/get */
export async function getApplicationById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getApplicationByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseApplication>('/application/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /application/get/vo */
export async function getApplicationVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getApplicationVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseApplicationVO>('/application/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /application/list/page/vo */
export async function listApplicationVoByPage(
  body: API.ApplicationQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageApplicationVO>('/application/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /application/update */
export async function updateApplication(
  body: API.ApplicationUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/application/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
