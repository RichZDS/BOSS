import { extend } from 'umi-request';

// 根据环境变量设置基础URL
const baseURL = import.meta.env.VITE_API_BASE_URL || '/api';

// 创建请求实例
const request = extend({
  prefix: baseURL, // 设置统一的请求前缀
  credentials: 'include', // 默认携带cookie
  headers: {
    'Content-Type': 'application/json',
  },
  errorHandler: (error: any) => {
    // 统一错误处理
    if (error.errno === 'ENOTFOUND') {
      console.error('网络请求失败，请检查网络连接');
    } else if (error.response) {
      // 服务端返回错误状态码
      const { status, data } = error.response;
      console.error(`请求失败: ${status}`, data);
    } else {
      // 其他错误
      console.error('请求异常', error);
    }
    throw error;
  },
});

// 请求拦截器
request.interceptors.request.use((url, options) => {
  // 如果data是FormData，删除Content-Type，让浏览器自动设置multipart/form-data和boundary
  if (options.data instanceof FormData) {
    if (options.headers) {
      delete options.headers['Content-Type'];
    }
  }
  return {
    url,
    options,
  };
});

// 响应拦截器
request.interceptors.response.use(async (response) => {
  try {
    const data = await response.clone().json();
    if (data && data.code !== 0) {
      // 可以在这里处理业务错误
      console.error(data.message || '请求失败');
    }
  } catch (e) {
    // 响应不是JSON，忽略
  }
  return response;
});

export default request;
