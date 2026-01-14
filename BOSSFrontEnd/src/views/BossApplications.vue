<template>
  <NavBar />
  <div class="boss-applications-page">
    <a-card title="收到的投递" :bordered="false">
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
          <template v-else-if="column.key === 'action'">
            <a @click="viewResume(record.resumeId)">查看简历</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定接受该申请吗？" @confirm="handleDecision(record.id, 1)">
              <a style="color: green">接受</a>
            </a-popconfirm>
            <a-divider type="vertical" />
            <a-popconfirm title="确定拒绝该申请吗？" @confirm="handleDecision(record.id, 2)">
              <a style="color: red">拒绝</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="resumeVisible"
      title="简历详情"
      width="800px"
      :footer="null"
    >
      <div v-if="currentResume">
        <h3>{{ currentResume.resumeTitle }}</h3>
        <p><strong>摘要：</strong>{{ currentResume.summary }}</p>
        <div style="white-space: pre-wrap; background: #f5f5f5; padding: 16px; border-radius: 4px;">
          {{ currentResume.content }}
        </div>
        <div v-if="currentResume.attachmentUrl" style="margin-top: 16px;">
          <a :href="currentResume.attachmentUrl" target="_blank">查看附件</a>
        </div>
      </div>
      <div v-else class="loading-resume">
        <a-spin />
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, reactive, onMounted } from 'vue';
import { listApplicationVoByPage } from '@/api/api/applicationController';
import { addDecision } from '@/api/api/bossApplicationDecisionController';
import { getResumeVoById } from '@/api/api/resumeController';
import { message } from 'ant-design-vue';
import { useUserStore } from '@/stores/user';
import type { TableProps } from 'ant-design-vue';

const userStore = useUserStore();

const columns = [
  {
    title: '申请职位ID',
    dataIndex: 'jobId',
    key: 'jobId',
  },
  {
    title: '申请人ID',
    dataIndex: 'userId',
    key: 'userId',
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
  {
    title: '操作',
    key: 'action',
  },
];

const applications = ref<API.ApplicationVO[]>([]);
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
      bossId: userStore.loginUser.id,
    });
    if (res.code === 0 && res.data) {
      applications.value = res.data.records || [];
      pagination.total = Number(res.data.total) || 0;
    } else {
      message.error(res.message || '获取投递列表失败');
    }
  } catch (error) {
    message.error('获取投递列表失败');
  } finally {
    loading.value = false;
  }
};

const handleTableChange: TableProps['onChange'] = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchApplications();
};

// Resume View
const resumeVisible = ref(false);
const currentResume = ref<API.ResumeVO | null>(null);

const viewResume = async (resumeId: number) => {
  resumeVisible.value = true;
  currentResume.value = null;
  try {
    const res = await getResumeVoById({ id: resumeId });
    if (res.code === 0 && res.data) {
      currentResume.value = res.data;
    } else {
      message.error('获取简历详情失败');
    }
  } catch (error) {
    message.error('获取简历详情失败');
  }
};

// Decision
const handleDecision = async (applicationId: number, decision: number) => {
  try {
    const res = await addDecision({
      applicationId,
      decision, // 1: Accept, 2: Reject
      bossId: userStore.loginUser.id,
      stage: 1, // 添加阶段字段
      note: decision === 1 ? '接受申请' : '拒绝申请', // 添加备注
    });
    if (res.code === 0) {
      message.success('操作成功');
      fetchApplications();
    } else {
      message.error(res.message || '操作失败');
    }
  } catch (error) {
    message.error('操作失败');
  }
};

const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '待处理';
    case 1:
      return '已面试';
    case 2:
      return '已拒绝';
    default:
      return '未知状态';
  }
};

const getStatusColor = (status: number) => {
  switch (status) {
    case 1:
      return 'green';
    case 2:
      return 'red';
    default:
      return 'default';
  }
};

onMounted(() => {
  fetchApplications();
});
</script>

<style scoped>
.boss-applications-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
