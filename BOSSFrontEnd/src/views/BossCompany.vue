<template>
  <NavBar />
  <div class="boss-company-page">
    <a-card title="公司管理" :bordered="false">
      <div v-if="loading" class="loading-container">
        <a-spin />
      </div>
      
      <div v-else-if="!hasCompany">
        <a-result
          title="您还没有创建公司"
          sub-title="创建公司后，您可以发布职位并招募人才"
        >
          <template #extra>
            <a-button type="primary" @click="showCreateModal">立即创建</a-button>
          </template>
        </a-result>
      </div>

      <div v-else class="company-info">
        <a-descriptions title="公司信息" bordered>
          <template #extra>
            <a-button type="primary" @click="showEditModal">编辑</a-button>
          </template>
          <a-descriptions-item label="公司名称">{{ companyInfo.name }}</a-descriptions-item>
          <a-descriptions-item label="行业">{{ companyInfo.industry || '-' }}</a-descriptions-item>
          <a-descriptions-item label="规模">{{ companyInfo.sizeRange || '-' }}</a-descriptions-item>
          <a-descriptions-item label="官网" :span="3">
             <a v-if="companyInfo.website" :href="companyInfo.website" target="_blank">{{ companyInfo.website }}</a>
             <span v-else>-</span>
          </a-descriptions-item>
          <a-descriptions-item label="地址" :span="3">{{ companyInfo.address || '-' }}</a-descriptions-item>
          <a-descriptions-item label="介绍" :span="3">
            <div style="white-space: pre-wrap">{{ companyInfo.intro || '-' }}</div>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-card>

    <!-- Create/Edit Modal -->
    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑公司信息' : '创建公司'"
      @ok="handleSubmit"
      :confirmLoading="submitLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="公司名称" required>
          <a-input v-model:value="formState.name" placeholder="请输入公司名称" />
        </a-form-item>
        <a-form-item label="行业">
          <a-input v-model:value="formState.industry" placeholder="例如：互联网/软件" />
        </a-form-item>
        <a-form-item label="规模">
          <a-select v-model:value="formState.sizeRange">
            <a-select-option value="0-20人">0-20人</a-select-option>
            <a-select-option value="20-99人">20-99人</a-select-option>
            <a-select-option value="100-499人">100-499人</a-select-option>
            <a-select-option value="500-999人">500-999人</a-select-option>
            <a-select-option value="1000-9999人">1000-9999人</a-select-option>
            <a-select-option value="10000人以上">10000人以上</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="官网">
          <a-input v-model:value="formState.website" placeholder="http://..." />
        </a-form-item>
        <a-form-item label="地址">
          <a-input v-model:value="formState.address" placeholder="详细地址" />
        </a-form-item>
        <a-form-item label="介绍">
          <a-textarea v-model:value="formState.intro" :rows="4" placeholder="公司简介" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, reactive, onMounted, computed } from 'vue';
import { addCompany, updateCompany, getCompanyVoById } from '@/api/api/companyController';
import { useUserStore } from '@/stores/user';
import { message } from 'ant-design-vue';

const userStore = useUserStore();
const loading = ref(false);
const hasCompany = ref(false);
const companyInfo = ref<API.CompanyVO>({});

const modalVisible = ref(false);
const submitLoading = ref(false);
const isEdit = ref(false);

const formState = reactive({
  id: 0,
  name: '',
  industry: '',
  sizeRange: '',
  website: '',
  address: '',
  intro: '',
});

const fetchCompanyInfo = async () => {
  // Boss info contains companyId
  if (!userStore.loginUser.companyId) {
    hasCompany.value = false;
    return;
  }
  
  loading.value = true;
  try {
    const res = await getCompanyVoById({ id: userStore.loginUser.companyId });
    if (res.code === 0 && res.data) {
      companyInfo.value = res.data;
      hasCompany.value = true;
    } else {
      hasCompany.value = false;
    }
  } catch (error) {
    message.error('获取公司信息失败');
  } finally {
    loading.value = false;
  }
};

const showCreateModal = () => {
  isEdit.value = false;
  Object.assign(formState, {
    name: '',
    industry: '',
    sizeRange: '',
    website: '',
    address: '',
    intro: '',
  });
  modalVisible.value = true;
};

const showEditModal = () => {
  isEdit.value = true;
  Object.assign(formState, companyInfo.value);
  modalVisible.value = true;
};

const handleSubmit = async () => {
  if (!formState.name) {
    message.error('请输入公司名称');
    return;
  }
  
  submitLoading.value = true;
  try {
    let res;
    if (isEdit.value) {
      res = await updateCompany(formState);
    } else {
      res = await addCompany(formState);
    }
    
    if (res.code === 0) {
      message.success(isEdit.value ? '更新成功' : '创建成功');
      modalVisible.value = false;
      // Refresh user info to get new companyId if created
      await userStore.fetchLoginUser();
      fetchCompanyInfo();
    } else {
      message.error(res.message || '操作失败');
    }
  } catch (error) {
    message.error('操作失败');
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  fetchCompanyInfo();
});
</script>

<style scoped>
.boss-company-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
.loading-container {
  text-align: center;
  padding: 50px;
}
</style>
