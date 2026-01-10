<template>
  <NavBar />
  <div class="job-edit-page">
    <a-card :title="isEdit ? '编辑职位' : '发布新职位'" :bordered="false">
      <a-form
        :model="formState"
        name="jobForm"
        @finish="onFinish"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item
          label="职位名称"
          name="title"
          :rules="[{ required: true, message: '请输入职位名称' }]"
        >
          <a-input v-model:value="formState.title" placeholder="例如：高级Java开发工程师" />
        </a-form-item>

        <a-form-item label="工作地点" name="location">
          <a-input v-model:value="formState.location" placeholder="例如：北京-海淀区" />
        </a-form-item>

        <a-form-item label="职位类型" name="jobType">
          <a-select v-model:value="formState.jobType">
            <a-select-option value="全职">全职</a-select-option>
            <a-select-option value="实习">实习</a-select-option>
            <a-select-option value="兼职">兼职</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="薪资范围">
          <a-input-group compact>
            <a-input-number v-model:value="formState.salaryMin" :min="0" placeholder="最低薪资" style="width: 120px" />
            <span style="padding: 0 8px; line-height: 32px"> - </span>
            <a-input-number v-model:value="formState.salaryMax" :min="0" placeholder="最高薪资" style="width: 120px" />
          </a-input-group>
        </a-form-item>

        <a-form-item label="职位描述" name="description">
          <a-textarea v-model:value="formState.description" :rows="6" placeholder="详细描述职位职责、工作内容等" />
        </a-form-item>

        <a-form-item label="任职要求" name="requirement">
          <a-textarea v-model:value="formState.requirement" :rows="6" placeholder="学历、经验、技能要求等" />
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
          <a-button type="primary" html-type="submit" :loading="loading">保存并发布</a-button>
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
import { addJob, updateJob, getJobVoById } from '@/api/api/jobPostingController';
import { message } from 'ant-design-vue';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isEdit = computed(() => !!route.params.id);
const loading = ref(false);

const formState = reactive({
  title: '',
  location: '',
  jobType: '全职',
  salaryMin: undefined,
  salaryMax: undefined,
  description: '',
  requirement: '',
});

const loadData = async () => {
  if (!isEdit.value) return;
  const id = Number(route.params.id);
  try {
    const res = await getJobVoById({ id });
    if (res.code === 0 && res.data) {
      Object.assign(formState, res.data);
    } else {
      message.error(res.message || '加载职位信息失败');
    }
  } catch (error) {
    message.error('加载职位信息失败');
  }
};

const onFinish = async (values: any) => {
  // Check if boss has company
  if (!userStore.loginUser.companyId) {
    message.warning('请先完善公司信息后再发布职位');
    router.push('/boss/company');
    return;
  }

  loading.value = true;
  try {
    let res;
    if (isEdit.value) {
      res = await updateJob({ ...values, id: route.params.id });
    } else {
      res = await addJob(values);
    }

    if (res.code === 0) {
      message.success('发布成功');
      router.push('/jobs'); // Or back to boss job list if implemented
    } else {
      message.error(res.message || '发布失败');
    }
  } catch (error) {
    message.error('发布失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.job-edit-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
