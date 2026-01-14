<template>
  <NavBar />
  <div class="my-resumes-page">
    <a-card title="我的简历" :bordered="false">
      <template #extra>
        <a-button type="primary" @click="router.push('/user/resume/edit')">创建新简历</a-button>
      </template>

      <a-table
        :columns="columns"
        :data-source="resumes"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'isDefault'">
            <a-tag :color="record.isDefault === 1 ? 'green' : 'default'">
              {{ record.isDefault === 1 ? '默认' : '否' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'createdAt'">
            {{ new Date(record.createdAt).toLocaleString() }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="router.push(`/user/resume/edit/${record.id}`)">编辑</a>
              <a-popconfirm title="确定要删除这份简历吗？" @confirm="handleDelete(record.id)">
                <a style="color: red">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, onMounted, reactive } from 'vue';
import { listResumeVoByPage, deleteResume } from '@/api/api/resumeController';
import {  } from '@/api/api/resumeController';
import { message } from 'ant-design-vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import type { TableProps } from 'ant-design-vue';
import { Upload } from 'ant-design-vue';

const router = useRouter();
const userStore = useUserStore();

const columns = [
  {
    title: '简历标题',
    dataIndex: 'resumeTitle',
    key: 'resumeTitle',
  },
  {
    title: '是否默认',
    dataIndex: 'isDefault',
    key: 'isDefault',
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
  },
  {
    title: '操作',
    key: 'action',
  },
];

const resumes = ref<API.ResumeVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

const fetchResumes = async () => {
  loading.value = true;
  try {
    const res = await listResumeVoByPage({
      current: pagination.current,
      pageSize: pagination.pageSize,
      userId: userStore.loginUser.id, // Only my resumes
    });
    if (res.code === 0 && res.data) {
      resumes.value = res.data.records || [];
      pagination.total = Number(res.data.total) || 0;
    } else {
      message.error(res.message || '获取简历列表失败');
    }
  } catch (error) {
    message.error('获取简历列表失败');
  } finally {
    loading.value = false;
  }
};

const handleTableChange: TableProps['onChange'] = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchResumes();
};

const addressLoading = ref(false);
const myAddress = ref<any>(null);

const fetchAddress = async () => {
  addressLoading.value = true;
  try {
    const res = await getMyResumeAddress();
    myAddress.value = res.data || null;
  } catch (e) {
    myAddress.value = null;
  } finally {
    addressLoading.value = false;
  }
};

const beforeUpload = (file: File) => {
  const isPdf = file.type === 'application/pdf';
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isPdf) {
    message.error('仅支持PDF文件');
    return Upload.LIST_IGNORE;
  }
  if (!isLt5M) {
    message.error('文件需小于5MB');
    return Upload.LIST_IGNORE;
  }
  return true;
};

const doUpload = async (file: File) => {
  const form = new FormData();
  form.append('file', file);
  const res = await uploadResumePdf(form);
  if (res.code === 0) {
    message.success('上传成功');
    fetchAddress();
  } else {
    message.error(res.message || '上传失败');
  }
};

const deleteAddress = async () => {
  if (!myAddress.value?.id) {
    return;
  }
  const res = await deleteMyResumeAddress({ id: myAddress.value.id });
  if (res.code === 0) {
    message.success('删除成功');
    fetchAddress();
  } else {
    message.error(res.message || '删除失败');
  }
};

const handleDelete = async (id: number) => {
  try {
    const res = await deleteResume({ id });
    if (res.code === 0) {
      message.success('删除成功');
      fetchResumes();
    } else {
      message.error(res.message || '删除失败');
    }
  } catch (error) {
    message.error('删除失败');
  }
};

onMounted(() => {
  fetchResumes();
  fetchAddress();
});
</script>

<style scoped>
.my-resumes-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
