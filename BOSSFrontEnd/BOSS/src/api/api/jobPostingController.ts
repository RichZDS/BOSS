// @ts-ignore
/* eslint-disable */
import request from '@/libs/request'

/** 此处后端没有提供注释 POST /job/add */
export async function addJob(body: API.JobPostingAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/job/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /job/delete */
export async function deleteJob(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/job/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /job/get */
export async function getJobById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getJobByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseJobPosting>('/job/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /job/get/vo */
export async function getJobVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getJobVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseJobPostingVO>('/job/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /job/list/page/vo */
export async function listJobVoByPage(
  body: API.JobPostingQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageJobPostingVO>('/job/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /job/update */
export async function updateJob(
  body: API.JobPostingUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/job/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
