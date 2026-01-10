// @ts-ignore
/* eslint-disable */
import request from '@/libs/request'

/** 此处后端没有提供注释 POST /decision/add */
export async function addDecision(
  body: API.BossApplicationDecisionAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>('/decision/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /decision/delete */
export async function deleteDecision(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/decision/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /decision/get */
export async function getDecisionById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDecisionByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBossApplicationDecision>('/decision/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /decision/get/vo */
export async function getDecisionVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDecisionVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBossApplicationDecisionVO>('/decision/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /decision/list/page/vo */
export async function listDecisionVoByPage(
  body: API.BossApplicationDecisionQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageBossApplicationDecisionVO>('/decision/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /decision/update */
export async function updateDecision(
  body: API.BossApplicationDecisionUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/decision/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
