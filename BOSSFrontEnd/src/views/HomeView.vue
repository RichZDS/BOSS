<template>
  <div class="home">
    <NavBar />
    <div class="content">
      <div class="banner">
        <h1>BOSS 招聘</h1>
        <p>连接人才与机会，成就无限可能</p>
      </div>

      <a-card title="最新职位" :bordered="false" class="jobs-card">
        <template #extra>
          <router-link to="/jobs">查看更多</router-link>
        </template>
        
        <a-list 
          :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 4, xxl: 4 }" 
          :data-source="jobs"
          :pagination="pagination"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <a-card :title="item.title" hoverable @click="viewJob(item)">
                <p><strong>薪资：</strong>{{ item.salaryMin }} - {{ item.salaryMax }}</p>
                <p><strong>地点：</strong>{{ item.location }}</p>
                <p><strong>类型：</strong><a-tag>{{ item.jobType }}</a-tag></p>
                <div class="card-footer">
                  <span>{{ new Date(item.publishAt).toLocaleDateString() }}</span>
                </div>
              </a-card>
            </a-list-item>
          </template>
        </a-list>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, reactive, onMounted } from 'vue';
import { listJobVoByPage } from '@/api/api/jobPostingController';
import { useRouter } from 'vue-router';

const router = useRouter();
const jobs = ref<API.JobPostingVO[]>([]);

const pagination = reactive({
  onChange: (page: number) => {
    fetchJobs(page);
  },
  pageSize: 20,
  total: 0,
  current: 1,
});

const fetchJobs = async (page = 1) => {
  try {
    const res = await listJobVoByPage({
      current: page,
      pageSize: pagination.pageSize,
      status: 1, // Published
    });
    if (res.code === 0 && res.data) {
      jobs.value = res.data.records || [];
      pagination.total = Number(res.data.total) || 0;
      pagination.current = page;
    }
  } catch (error) {
    console.error(error);
  }
};

const viewJob = (job: API.JobPostingVO) => {
  // Currently we just go to jobs list, ideally detail page
  router.push('/jobs'); 
};

onMounted(() => {
  fetchJobs();
});
</script>

<style scoped>
.content {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.banner {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  margin-bottom: 24px;
  border-radius: 4px;
}

.banner h1 {
  font-size: 48px;
  color: #1890ff;
  margin-bottom: 16px;
}

.banner p {
  font-size: 20px;
  color: #666;
}

.jobs-card {
  border-radius: 8px;
}
</style>
