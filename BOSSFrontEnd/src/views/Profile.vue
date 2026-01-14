<template>
  <NavBar />
  <div class="profile-page">
    <a-card title="个人信息设置" :bordered="false">
      <a-tabs v-model:activeKey="activeKey">
        <a-tab-pane key="basic" tab="基本信息">
          <a-form
            :model="formState"
            @finish="onFinish"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 12 }"
          >

            <a-form-item label="用户名/账号">
              <a-input v-model:value="formState.username" disabled />
            </a-form-item>
            
            <a-form-item label="昵称/姓名">
              <a-input v-model:value="formState.userAccount" />
            </a-form-item>

            <a-form-item label="个人简介">
              <a-textarea v-model:value="formState.profile" :rows="4" />
            </a-form-item>

            <a-form-item label="电话">
              <a-input v-model:value="formState.phone"  placeholder="156xxxx" />
            </a-form-item>

            <a-form-item label="邮箱">
              <a-input v-model:value="formState.email" placeholder="xxx@cc.com" />
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 4, span: 12 }">
              <a-button type="primary" html-type="submit" :loading="loading">保存修改</a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, reactive, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { updateUser } from '@/api/api/userController';
import { message } from 'ant-design-vue';

const userStore = useUserStore();
const activeKey = ref('basic');
const loading = ref(false);

const formState = reactive<any>({});

const initData = () => {
  const user = userStore.loginUser;
  // 通用字段映射
  formState.id = user.id;
  formState.username = user.userName || user.userAccount; 
  formState.userAccount = user.userAccount;
  formState.phone = user.phone;
  formState.email = user.email;
  formState.profile = user.userProfile || user.profile; // 兼容不同字段名
  
  // Boss 特有字段映射（如果有）
  if (user.userRole === 'boss') {
    formState.bossName = user.userName; // Boss 姓名复用 userName
  }
};

const onFinish = async () => {
  loading.value = true;
  try {
    // 统一调用 updateUser
    // 注意：后端 UserUpdateRequest 目前可能仅支持部分字段，phone/email 可能无法更新
    const res = await updateUser({
        id: formState.id,
        userName: formState.userAccount, // 昵称/姓名
        userProfile: formState.profile,
        userRole: userStore.loginUser.userRole,
        // phone: formState.phone, // 后端暂不支持
        // email: formState.email, // 后端暂不支持
    });

    if (res && res.code === 0) {
      message.success('更新成功');
      await userStore.fetchLoginUser(); // Refresh store
    } else {
      message.error(res?.message || '更新失败');
    }
  } catch (error) {
    message.error('更新失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  initData();
});
</script>

<style scoped>
.profile-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
