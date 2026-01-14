<template>
  <NavBar />
  <div class="resume-edit-page">
    <a-card :title="isEdit ? '编辑简历' : '创建简历'" :bordered="false">
      <a-form
        :model="formState"
        name="resumeForm"
        @finish="onFinish"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item
          label="简历标题"
          name="resumeTitle"
          :rules="[{ required: true, message: '请输入简历标题' }]"
        >
          <a-input v-model:value="formState.resumeTitle" placeholder="例如：Java后端开发工程师-张三" />
        </a-form-item>

        <a-form-item label="个人摘要" name="summary">
          <a-textarea v-model:value="formState.summary" :rows="3" placeholder="简要介绍自己的技能和优势" />
        </a-form-item>

        <a-form-item label="详细内容" name="content">
          <a-textarea v-model:value="formState.content" :rows="10" placeholder="详细的工作经历、项目经验、教育背景等" />
        </a-form-item>

        <a-form-item label="简历附件" name="attachmentUrl">
          <a-space direction="vertical" style="width: 100%">
            <a-upload
              :before-upload="beforeUpload"
              :show-upload-list="false"
              :custom-request="handleUpload"
              accept=".pdf"
            >
              <a-button type="primary">
                <UploadOutlined />
                上传简历PDF
              </a-button>
            </a-upload>
            <div v-if="uploading" style="color: #1890ff">
              <a-spin size="small" /> 上传中...
              <a-progress
                v-if="uploadProgress > 0"
                :percent="uploadProgress"
                size="small"
                style="margin-top: 8px"
              />
            </div>
            <div v-if="formState.attachmentUrl" style="margin-top: 8px">
              <a-space>
                <a :href="formState.attachmentUrl" target="_blank" style="color: #1890ff">
                  <FilePdfOutlined />
                  查看PDF简历
                </a>
                <span style="color: #999">{{ uploadFileName }}</span>
                <a-popconfirm title="确定要删除此PDF文件吗？" @confirm="clearAttachment">
                  <a style="color: #ff4d4f">删除</a>
                </a-popconfirm>
              </a-space>
            </div>
            <div v-if="!formState.attachmentUrl && !uploading" style="color: #999; font-size: 12px">
              仅支持PDF格式，最大10MB（文件将上传至腾讯云COS）
            </div>
          </a-space>
        </a-form-item>

        <a-form-item label="设为默认" name="isDefault">
          <a-switch v-model:checked="isDefaultChecked" />
        </a-form-item>

        <!-- AI优化区域 -->
        <a-form-item label="AI优化" :wrapper-col="{ span: 16 }">
          <a-space direction="vertical" style="width: 100%">
            <a-button
              type="default"
              :loading="aiOptimizing"
              @click="handleAiOptimize"
              :disabled="aiOptimizing"
              class="ai-optimize-btn"
            >
              <template #icon>
                <RobotOutlined />
              </template>
              {{ aiOptimizing ? 'AI优化中...' : 'DeepSeek AI 智能优化' }}
            </a-button>
            <a-progress
              v-if="aiOptimizing"
              :percent="Math.round(aiProgress)"
              :status="aiProgress >= 100 ? 'success' : 'active'"
              :stroke-color="{
                '0%': '#108ee9',
                '100%': '#87d068',
              }"
              size="small"
            />
            <div v-if="!aiOptimizing" style="color: #999; font-size: 12px">
              使用 DeepSeek AI 智能优化您的简历内容，让简历更专业、更有吸引力
            </div>
          </a-space>
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
          <a-button type="primary" html-type="submit" :loading="loading">保存</a-button>
          <a-button style="margin-left: 10px" @click="router.back()">取消</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { reactive, ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { addResume, updateResume, getResumeVoById, deleteResumeFile, aiOptimizeResume } from '@/api/api/resumeController';
import { message, Upload } from 'ant-design-vue';
import { UploadOutlined, FilePdfOutlined, RobotOutlined } from '@ant-design/icons-vue';

const route = useRoute();
const router = useRouter();

const isEdit = computed(() => !!route.params.id);
const loading = ref(false);
const uploading = ref(false);
const uploadProgress = ref(0);
const uploadFileName = ref('');

// AI优化相关状态
const aiOptimizing = ref(false);
const aiProgress = ref(0);
let aiProgressTimer: ReturnType<typeof setInterval> | null = null;

const formState = reactive({
  resumeTitle: '',
  summary: '',
  content: '',
  attachmentUrl: '',
  isDefault: 0,
});

const isDefaultChecked = computed({
  get: () => formState.isDefault === 1,
  set: (val) => {
    formState.isDefault = val ? 1 : 0;
  },
});

const loadData = async () => {
  if (!isEdit.value) return;
  const id = Number(route.params.id);
  try {
    const res = await getResumeVoById({ id });
    if (res.code === 0 && res.data) {
      Object.assign(formState, res.data);
      uploadFileName.value = extractFileName(formState.attachmentUrl);
    } else {
      message.error(res.message || '加载简历失败');
    }
  } catch (error) {
    message.error('加载简历失败');
  }
};

const extractFileName = (url: string) => {
  if (!url) return '';
  const parts = url.split('/');
  return parts[parts.length - 1] || '';
};

const beforeUpload = (file: File) => {
  const isPdf = file.type === 'application/pdf';
  const extension = file.name.split('.').pop()?.toLowerCase();
  const isAllowedExtension = extension === 'pdf';
  const isLt10M = file.size / 1024 / 1024 < 10;
  
  if (!isPdf && !isAllowedExtension) {
    message.error('仅支持PDF格式');
    return Upload.LIST_IGNORE;
  }
  if (!isLt10M) {
    message.error('文件大小不能超过10MB');
    return Upload.LIST_IGNORE;
  }
  return true;
};

const handleUpload = async (options: any) => {
  const { file, onError, onSuccess } = options;
  uploading.value = true;
  uploadProgress.value = 0;
  try {
    const formData = new FormData();
    formData.append('file', file);
    
    // 如果是编辑模式，传入resumeId以便同时更新resume的attachment_url
    if (isEdit.value && route.params.id) {
      formData.append('resumeId', String(route.params.id));
    }

    const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api';
    const xhr = new XMLHttpRequest();
    xhr.open('POST', `${baseUrl}/resume/upload`, true);
    xhr.withCredentials = true;
    xhr.upload.onprogress = (event) => {
      if (event.lengthComputable) {
        uploadProgress.value = Math.round((event.loaded / event.total) * 100);
      }
    };
    xhr.onload = () => {
      try {
        const response = JSON.parse(xhr.responseText);
        if (response.code === 0 && response.data) {
          formState.attachmentUrl = response.data;
          uploadFileName.value = extractFileName(response.data);
          message.success('文件上传成功');
          onSuccess?.(response, file);
        } else {
          message.error(response.message || '文件上传失败');
          onError?.(response);
        }
      } catch (e) {
        message.error('文件上传失败');
        onError?.(e);
      } finally {
        uploadProgress.value = 0;
        uploading.value = false;
      }
    };
    xhr.onerror = () => {
      message.error('文件上传失败');
      onError?.(new Error('Upload failed'));
      uploadProgress.value = 0;
      uploading.value = false;
    };
    xhr.send(formData);
  } catch (error: any) {
    console.error('上传失败:', error);
    message.error(error.message || '文件上传失败');
    uploadProgress.value = 0;
    uploading.value = false;
  }
};

const clearAttachment = async () => {
  if (formState.attachmentUrl) {
    try {
      // 调用后端接口删除COS中的文件，如果是编辑模式传入resumeId
      const params: { fileUrl: string; resumeId?: number } = { fileUrl: formState.attachmentUrl };
      if (isEdit.value && route.params.id) {
        params.resumeId = Number(route.params.id);
      }
      const res = await deleteResumeFile(params);
      if (res.code === 0) {
        message.success('文件删除成功');
      } else {
        message.warning('文件删除可能失败，请手动确认');
      }
    } catch (error) {
      console.error('删除文件失败:', error);
      message.warning('文件删除失败');
    }
  }
  formState.attachmentUrl = '';
  uploadFileName.value = '';
};

// 开始伪进度条
const startFakeProgress = () => {
  aiProgress.value = 0;
  aiProgressTimer = setInterval(() => {
    // 伪进度条逻辑：快速增长到30%，然后慢慢增长到90%
    if (aiProgress.value < 30) {
      aiProgress.value += Math.random() * 8 + 2; // 2-10%
    } else if (aiProgress.value < 60) {
      aiProgress.value += Math.random() * 4 + 1; // 1-5%
    } else if (aiProgress.value < 90) {
      aiProgress.value += Math.random() * 2 + 0.5; // 0.5-2.5%
    }
    // 最多到90%，等待AI返回后才变成100%
    if (aiProgress.value > 90) {
      aiProgress.value = 90;
    }
  }, 300);
};

// 停止伪进度条
const stopFakeProgress = (success: boolean) => {
  if (aiProgressTimer) {
    clearInterval(aiProgressTimer);
    aiProgressTimer = null;
  }
  if (success) {
    aiProgress.value = 100;
    // 100%后延迟重置
    setTimeout(() => {
      aiProgress.value = 0;
      aiOptimizing.value = false;
    }, 500);
  } else {
    aiProgress.value = 0;
    aiOptimizing.value = false;
  }
};

// AI优化简历
const handleAiOptimize = async () => {
  // 检查是否有内容可优化
  if (!formState.resumeTitle?.trim() && !formState.summary?.trim() && !formState.content?.trim()) {
    message.warning('请至少填写一项内容后再进行AI优化');
    return;
  }

  aiOptimizing.value = true;
  startFakeProgress();

  try {
    const res = await aiOptimizeResume({
      resumeTitle: formState.resumeTitle,
      summary: formState.summary,
      content: formState.content,
    });

    if (res.code === 0 && res.data) {
      // 填充AI优化后的内容
      if (res.data.resumeTitle) {
        formState.resumeTitle = res.data.resumeTitle;
      }
      if (res.data.summary) {
        formState.summary = res.data.summary;
      }
      if (res.data.content) {
        formState.content = res.data.content;
      }
      stopFakeProgress(true);
      message.success('AI优化完成！');
    } else {
      stopFakeProgress(false);
      message.error(res.message || 'AI优化失败');
    }
  } catch (error: any) {
    console.error('AI优化失败:', error);
    stopFakeProgress(false);
    message.error(error.message || 'AI优化失败，请稍后重试');
  }
};

const onFinish = async (values: any) => {
  loading.value = true;
  try {
    // Merge switch value manually if not in values (usually it is, but to be safe)
    const payload = { ...values, isDefault: formState.isDefault };
    
    let res;
    if (isEdit.value) {
      res = await updateResume({ ...payload, id: route.params.id });
    } else {
      res = await addResume(payload);
    }

    if (res.code === 0) {
      message.success('保存成功');
      router.push('/user/resumes');
    } else {
      message.error(res.message || '保存失败');
    }
  } catch (error) {
    message.error('保存失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.resume-edit-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.ai-optimize-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.ai-optimize-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  color: white;
}

.ai-optimize-btn:disabled {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.7;
  color: white;
}
</style>
