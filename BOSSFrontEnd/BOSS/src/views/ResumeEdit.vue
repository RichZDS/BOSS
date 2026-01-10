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
              accept=".pdf,.doc,.docx"
            >
              <a-button type="primary">
                <UploadOutlined />
                上传简历附件（PDF/Word）
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
                  <FileTextOutlined />
                  查看附件
                </a>
                <span style="color: #999">{{ uploadFileName }}</span>
                <a @click="clearAttachment" style="color: #ff4d4f">删除</a>
              </a-space>
            </div>
            <div v-if="!formState.attachmentUrl && !uploading" style="color: #999; font-size: 12px">
              支持PDF和Word格式，最大10MB
            </div>
          </a-space>
        </a-form-item>

        <a-form-item label="设为默认" name="isDefault">
          <a-switch v-model:checked="isDefaultChecked" />
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
import { addResume, updateResume, getResumeVoById } from '@/api/api/resumeController';
import { message, Upload } from 'ant-design-vue';
import { UploadOutlined, FileTextOutlined } from '@ant-design/icons-vue';

const route = useRoute();
const router = useRouter();

const isEdit = computed(() => !!route.params.id);
const loading = ref(false);
const uploading = ref(false);
const uploadProgress = ref(0);
const uploadFileName = ref('');

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
  const isWord = file.type === 'application/msword' || 
                 file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
  const extension = file.name.split('.').pop()?.toLowerCase();
  const isAllowedExtension = extension === 'pdf' || extension === 'doc' || extension === 'docx';
  const isLt10M = file.size / 1024 / 1024 < 10;
  
  if (!isPdf && !isWord && !isAllowedExtension) {
    message.error('仅支持PDF和Word文档格式');
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

const clearAttachment = () => {
  formState.attachmentUrl = '';
  uploadFileName.value = '';
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
</style>
