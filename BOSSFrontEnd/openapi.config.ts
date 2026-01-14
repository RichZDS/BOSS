import { generateService } from '@umijs/openapi';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

// 获取当前文件目录
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// 生成 API 服务代码
generateService({
  requestLibPath: "import request from '@/libs/request'",
  schemaPath: 'http://localhost:8082/v3/api-docs',
  serversPath: join(__dirname, 'src/api'),
  mock: false,
}).catch((error) => {
  console.error('API生成失败:', error);
  process.exit(1);
});
