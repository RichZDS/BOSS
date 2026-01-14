// @ts-ignore
/* eslint-disable */
import request from '@/libs/request'

/** 此处后端没有提供注释 POST /interview/add */
export async function addInterview(
  body: API.InterviewAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>('/interview/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /interview/delete */
export async function deleteInterview(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/interview/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /interview/get */
export async function getInterviewById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInterviewByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInterview>('/interview/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /interview/get/vo */
export async function getInterviewVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInterviewVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInterviewVO>('/interview/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /interview/list/page/vo */
export async function listInterviewVoByPage(
  body: API.InterviewQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageInterviewVO>('/interview/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /interview/update */
export async function updateInterview(
  body: API.InterviewUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/interview/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
