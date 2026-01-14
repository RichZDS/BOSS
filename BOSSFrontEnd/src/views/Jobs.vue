<template>
  <NavBar />
  <div class="jobs-page">
    <a-card title="职位列表" :bordered="false">
      <a-form layout="inline" :model="searchParams" @finish="fetchJobs" style="margin-bottom: 24px">
        <a-form-item label="职位名称">
          <a-input v-model:value="searchParams.title" placeholder="搜索职位" />
        </a-form-item>
        <a-form-item label="地点">
          <a-input v-model:value="searchParams.location" placeholder="工作地点" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button style="margin-left: 8px" @click="resetSearch">重置</a-button>
        </a-form-item>
      </a-form>

      <a-table
        :columns="columns"
        :data-source="jobs"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'salary'">
            {{ record.salaryMin }} - {{ record.salaryMax }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" @click="openApplyModal(record)">投递简历</a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="detailVisible"
      title="职位详情"
      width="800px"
      :footer="null"
    >
      <div v-if="currentJob">
        <a-descriptions title="职位信息" bordered>
          <a-descriptions-item label="职位名称">{{ currentJob.title }}</a-descriptions-item>
          <a-descriptions-item label="薪资范围">{{ currentJob.salaryMin }} - {{ currentJob.salaryMax }}</a-descriptions-item>
          <a-descriptions-item label="工作地点">{{ currentJob.location }}</a-descriptions-item>
          <a-descriptions-item label="工作类型">{{ currentJob.jobType }}</a-descriptions-item>
          <a-descriptions-item label="发布时间" :span="2">{{ currentJob.publishAt ? new Date(currentJob.publishAt).toLocaleString() : '-' }}</a-descriptions-item>
          <a-descriptions-item label="职位描述" :span="3">
            <div style="white-space: pre-wrap;">{{ currentJob.description }}</div>
          </a-descriptions-item>
        </a-descriptions>

        <a-divider />

        <div v-if="currentCompany">
          <a-descriptions title="公司信息" bordered>
            <a-descriptions-item label="公司名称">{{ currentCompany.name }}</a-descriptions-item>
            <a-descriptions-item label="行业">{{ currentCompany.industry }}</a-descriptions-item>
            <a-descriptions-item label="规模">{{ currentCompany.sizeRange }}</a-descriptions-item>
            <a-descriptions-item label="地址" :span="2">{{ currentCompany.address }}</a-descriptions-item>
            <a-descriptions-item label="公司简介" :span="3">
              <div style="white-space: pre-wrap;">{{ currentCompany.intro }}</div>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </div>
    </a-modal>

    <a-modal
      v-model:visible="applyModalVisible"
      title="投递简历"
      @ok="handleApply"
      :confirmLoading="applyLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="选择简历" required>
          <a-select v-model:value="selectedResumeId" placeholder="请选择一份简历">
            <a-select-option v-for="resume in myResumes" :key="resume.id" :value="resume.id">
              {{ resume.resumeTitle }} ({{ resume.isDefault ? '默认' : '普通' }})
            </a-select-option>
          </a-select>
          <div v-if="myResumes.length === 0" style="margin-top: 8px; color: red">
            暂无简历，请先<a @click="router.push('/user/resume/edit')">创建简历</a>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, reactive, onMounted } from 'vue';
import { listJobVoByPage } from '@/api/api/jobPostingController';
import { addApplication } from '@/api/api/applicationController';
import { listResumeVoByPage } from '@/api/api/resumeController';
import { getCompanyVoById } from '@/api/api/companyController';
import { message } from 'ant-design-vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import type { TableProps } from 'ant-design-vue';

// 定义 JobRow 类型，扩展 JobPostingVO 以包含公司名称
type JobRow = API.JobPostingVO & { companyName?: string };

const userStore = useUserStore();
const router = useRouter();

const columns = [
  {
    title: '职位名称',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: `公司名称`,
    dataIndex: 'companyName',
    key: 'companyName',
  },
  {
    title: '地点',
    dataIndex: 'location',
    key: 'location',
  },
  {
    title: '薪资范围',
    key: 'salary',
  },
  {
    title: '类型',
    dataIndex: 'jobType',
    key: 'jobType',
  },
  {
    title: '发布时间',
    dataIndex: 'publishAt',
    key: 'publishAt',
    customRender: ({ text }: any) => text ? new Date(text).toLocaleDateString() : '-',
  },
  {
    title: '操作',
    key: 'action',
  },
];

const jobs = ref<API.JobPostingVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

const searchParams = reactive({
  title: '',
  location: '',
});

// 添加缺失的响应式变量
const detailVisible = ref(false);
const currentJob = ref<API.JobPostingVO | null>(null);
const currentCompany = ref<API.CompanyVO | null>(null);

const fetchJobs = async () => {
  loading.value = true;
  try {
    const res = await listJobVoByPage({
      current: pagination.current,
      pageSize: pagination.pageSize,
      title: searchParams.title,
      location: searchParams.location,
      status: 1, // Only published jobs
    });
    if (res.code === 0 && res.data) {
      const records = res.data.records || [];
      const rows: JobRow[] = await Promise.all(records.map(async (job) => {
        let companyName: string | undefined = undefined;
        if (job.companyId) {
          try {
            const cRes = await getCompanyVoById({ id: job.companyId });
            if (cRes.code === 0 && cRes.data) {
              companyName = cRes.data.name;
            }
          } catch (e) { /* ignore */ }
        }
        return { ...job, companyName };
      }));
      jobs.value = rows;
      pagination.total = Number(res.data.total) || 0;
    } else {
      message.error(res.message || '获取职位列表失败');
    }
  } catch (error) {
    message.error('获取职位列表失败');
  } finally {
    loading.value = false;
  }
};

const handleTableChange: TableProps['onChange'] = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchJobs();
};

const resetSearch = () => {
  searchParams.title = '';
  searchParams.location = '';
  fetchJobs();
};

// 添加打开职位详情的方法
const openDetailModal = async (job: API.JobPostingVO) => {
  currentJob.value = job;
  // 获取公司信息
  if (job.companyId) {
    try {
      const res = await getCompanyVoById({ id: job.companyId });
      if (res.code === 0 && res.data) {
        currentCompany.value = res.data;
      } else {
        currentCompany.value = null;
      }
    } catch (error) {
      console.error('获取公司信息失败:', error);
      currentCompany.value = null;
    }
  } else {
    currentCompany.value = null;
  }
  detailVisible.value = true;
};

// Apply Logic
const applyModalVisible = ref(false);
const applyLoading = ref(false);
const selectedJobId = ref<number | null>(null);
const selectedResumeId = ref<number | undefined>(undefined);
const myResumes = ref<API.ResumeVO[]>([]);

const openApplyModal = async (job: API.JobPostingVO) => {
  if (!userStore.loginUser.id) {
    message.warning('请先登录');
    router.push('/user/login');
    return;
  }
  if (userStore.loginUser.userRole !== 'user') {
    message.warning('只有求职者可以投递简历');
    return;
  }

  selectedJobId.value = job.id as number;
  selectedResumeId.value = undefined;
  applyModalVisible.value = true;

  // Load resumes
  const res = await listResumeVoByPage({
    userId: userStore.loginUser.id,
    pageSize: 5,
  });
  if (res.code === 0 && res.data) {
    myResumes.value = res.data.records || [];
    // Auto select default
    const defaultResume = myResumes.value.find(r => r.isDefault === 1);
    if (defaultResume) {
      selectedResumeId.value = defaultResume.id;
    }
  }
};

const handleApply = async () => {
  if (!selectedResumeId.value) {
    message.warning('请选择一份简历');
    return;
  }
  applyLoading.value = true;
  try {
    const res = await addApplication({
      jobId: selectedJobId.value as number,
      resumeId: selectedResumeId.value,
    });
    if (res.code === 0) {
      message.success('投递成功');
      applyModalVisible.value = false;
    } else {
      message.error(res.message || '投递失败');
    }
  } catch (error) {
    message.error('投递失败');
  } finally {
    applyLoading.value = false;
  }
};

onMounted(() => {
  fetchJobs();
});
</script>

<style scoped>
.jobs-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>