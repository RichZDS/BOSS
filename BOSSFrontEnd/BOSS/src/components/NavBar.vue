<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <div class="title-bar">
          <a-button type="link" class="back-btn" @click="router.back()">
            <LeftOutlined /> 返回
          </a-button>
          <img class="logo" src="../../public/favicon.svg" alt="logo" />
          <div class="title">BOSS招聘</div>
        </div>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUser.userAvatar" />
                {{ loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="profile" @click="router.push('/user/profile')">
                    <SettingOutlined /> 个人设置
                  </a-menu-item>
                  <a-menu-item key="logout" @click="doLogout">
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" @click="router.push('/user/login')">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { h, ref, computed } from 'vue';
import { HomeOutlined, UserOutlined, TeamOutlined, FileTextOutlined, SolutionOutlined, SendOutlined, SearchOutlined, BankOutlined, PlusCircleOutlined, AuditOutlined, SettingOutlined, LeftOutlined } from '@ant-design/icons-vue';
import { MenuProps } from 'ant-design-vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { storeToRefs } from 'pinia';

const router = useRouter();
const userStore = useUserStore();
const { loginUser } = storeToRefs(userStore);

// 路由列表
const items = computed<MenuProps['items']>(() => [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/users',
    icon: () => h(TeamOutlined),
    label: '用户列表',
    title: '用户列表',
  },
  {
    key: '/bosses',
    icon: () => h(UserOutlined),
    label: 'Boss列表',
    title: 'Boss列表',
  },
  ...(loginUser.value.userRole === 'user' || loginUser.value.userRole === 'admin' ? [
    {
      key: '/user/resumes',
      icon: () => h(FileTextOutlined),
      label: '我的简历',
      title: '我的简历',
    },
    {
      key: '/user/applications',
      icon: () => h(SendOutlined),
      label: '我的投递',
      title: '我的投递',
    }
  ] : []),
  ...(loginUser.value.userRole === 'admin' ? [
    {
      key: '/admin/applications',
      icon: () => h(AuditOutlined),
      label: '投递管理',
      title: '投递管理',
    }
  ] : []),
  ...(loginUser.value.userRole === 'boss' ? [
    {
      key: '/boss/applications',
      icon: () => h(SolutionOutlined),
      label: 'Boss工作台',
      title: 'Boss工作台',
    },
    {
      key: '/boss/company',
      icon: () => h(BankOutlined),
      label: '我的公司',
      title: '我的公司',
    },
    {
      key: '/boss/job/edit',
      icon: () => h(PlusCircleOutlined),
      label: '发布职位',
      title: '发布职位',
    }
  ] : []),
]);

const current = ref<string[]>(['/']);

// 路由跳转
const doMenuClick = ({ key }: { key: string }) => {
  router.push(key);
};

// 退出登录
const doLogout = () => {
  userStore.logout();
};
</script>

<style scoped>
#globalHeader {
  background-color: white;
  border-bottom: 1px solid #eee;
  padding: 0 24px;
}

.title-bar {
  display: flex;
  align-items: center;
}

.logo {
  height: 48px;
}

.title {
  color: #1890ff; /* 蓝白风格 */
  font-size: 20px;
  margin-left: 16px;
  font-weight: bold;
}

.back-btn {
  margin-right: 12px;
}
</style>
