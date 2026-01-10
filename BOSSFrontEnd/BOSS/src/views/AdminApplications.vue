<template>
  <NavBar />
  <div class="admin-applications-page">
    <a-card title="全站投递记录管理" :bordered="false">
      <a-form layout="inline" :model="searchParams" @finish="fetchApplications" style="margin-bottom: 24px">
        <a-form-item label="用户ID">
          <a-input v-model:value="searchParams.userId" placeholder="申请人ID" />
        </a-form-item>
        <a-form-item label="BossID">
          <a-input v-model:value="searchParams.bossId" placeholder="招聘方ID" />
        </a-form-item>
        <a-form-item label="职位ID">
          <a-input v-model:value="searchParams.jobId" placeholder="职位ID" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button style="margin-left: 8px" @click="resetSearch">重置</a-button>
        </a-form-item>
      </a-form>

      <a-table
        :columns="columns"
        :data-source="applications"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'appliedAt'">
            {{ new Date(record.appliedAt).toLocaleString() }}
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, reactive, onMounted } from 'vue';
import { listApplicationVoByPage } from '@/api/api/applicationController';
import { message } from 'ant-design-vue';
import type { TableProps } from 'ant-design-vue';

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '职位ID', dataIndex: 'jobId', key: 'jobId' },
  { title: 'BossID', dataIndex: 'bossId', key: 'bossId' },
  { title: '申请人ID', dataIndex: 'userId', key: 'userId' },
  { title: '投递时间', dataIndex: 'appliedAt', key: 'appliedAt' },
  { title: '状态', dataIndex: 'status', key: 'status' },
];

const applications = ref<API.ApplicationVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

const searchParams = reactive({
  userId: '',
  bossId: '',
  jobId: '',
});

const fetchApplications = async () => {
  loading.value = true;
  try {
    const res = await listApplicationVoByPage({
      current: pagination.current,
      pageSize: pagination.pageSize,
      userId: searchParams.userId ? Number(searchParams.userId) : undefined,
      bossId: searchParams.bossId ? Number(searchParams.bossId) : undefined,
      jobId: searchParams.jobId ? Number(searchParams.jobId) : undefined,
    });
    if (res.code === 0 && res.data) {
      applications.value = res.data.records || [];
      pagination.total = Number(res.data.total) || 0;
    } else {
      message.error(res.message || '获取列表失败');
    }
  } catch (error) {
    message.error('获取列表失败');
  } finally {
    loading.value = false;
  }
};

const handleTableChange: TableProps['onChange'] = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchApplications();
};

const resetSearch = () => {
  searchParams.userId = '';
  searchParams.bossId = '';
  searchParams.jobId = '';
  fetchApplications();
};

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '已投递',
    1: '已撤回',
    2: '已过期',
    3: '面试中',
    4: '录用',
    5: '不合适'
  };
  return map[status] || '未知';
};

const getStatusColor = (status: number) => {
  if (status === 0) return 'blue';
  if (status === 4) return 'green';
  if (status === 5) return 'red';
  return 'default';
};

onMounted(() => {
  fetchApplications();
});
</script>

<style scoped>
.admin-applications-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
