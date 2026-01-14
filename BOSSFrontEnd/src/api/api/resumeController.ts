// @ts-ignore
/* eslint-disable */
import request from '@/libs/request'

/** 此处后端没有提供注释 POST /resume/add */
export async function addResume(body: API.ResumeAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/resume/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /resume/delete */
export async function deleteResume(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/resume/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /resume/get/vo */
export async function getResumeVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResumeVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseResumeVO>('/resume/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /resume/list/page/vo */
export async function listResumeVoByPage(
  body: API.ResumeQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResumeVO>('/resume/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /resume/update */
export async function updateResume(
  body: API.ResumeUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/resume/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /resume/upload */
export async function uploadFile(body: {}, options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/resume/upload', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
