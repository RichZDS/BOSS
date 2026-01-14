// @ts-ignore
/* eslint-disable */
import request from '@/libs/request'

export async function uploadResumePdf(formData: FormData, options?: { [key: string]: any }) {
  return request<any>('/resume/address/upload', {
    method: 'POST',
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data: formData,
    ...(options || {}),
  })
}

export async function getMyResumeAddress(options?: { [key: string]: any }) {
  return request<any>('/resume/address/get', {
    method: 'GET',
    ...(options || {}),
  })
}

export async function deleteMyResumeAddress(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<any>('/resume/address/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
