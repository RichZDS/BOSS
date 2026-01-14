<template>
  <NavBar />
  <div class="my-applications-page">
    <a-card title="我的投递记录" :bordered="false">
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
          <template v-else-if="column.key === 'jobTitle'">
            {{ record.jobTitle }}
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
import { getJobVoById } from '@/api/api/jobPostingController';
import { message } from 'ant-design-vue';
import { useUserStore } from '@/stores/user';
import type { TableProps } from 'ant-design-vue';

const userStore = useUserStore();

const columns = [
  {
    title: '投递职位',
    dataIndex: 'jobTitle',
    key: 'jobTitle',
  },
  {
    title: '投递时间',
    dataIndex: 'appliedAt',
    key: 'appliedAt',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
];

const applications = ref<any[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

const fetchApplications = async () => {
  loading.value = true;
  try {
    const res = await listApplicationVoByPage({
      current: pagination.current,
      pageSize: pagination.pageSize,
      userId: userStore.loginUser.id,
    });
    if (res.code === 0 && res.data) {
      const records = res.data.records || [];
      const ids = Array.from(new Set(records.map((r: any) => r.jobId).filter(Boolean)));
      const map: Record<number, string> = {};
      for (const id of ids) {
        const jobRes = await getJobVoById({ id });
        if (jobRes.code === 0 && jobRes.data) {
          map[id] = jobRes.data.title;
        }
      }
      applications.value = records.map((r: any) => ({ ...r, jobTitle: map[r.jobId] || '-' }));
      pagination.total = Number(res.data.total) || 0;
    } else {
      message.error(res.message || '获取投递记录失败');
    }
  } catch (error) {
    message.error('获取投递记录失败');
  } finally {
    loading.value = false;
  }
};

const handleTableChange: TableProps['onChange'] = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
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
.my-applications-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
