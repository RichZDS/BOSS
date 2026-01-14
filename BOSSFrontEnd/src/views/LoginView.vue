<template>
  <NavBar />
  <div class="container">
    <div class="content">
      <div class="top">
        <div class="header">
          <span class="title">BOSS招聘</span>
        </div>
        <div class="desc">最为专业的互联网招聘平台</div>
      </div>
      <div class="main">
        <a-form
          :model="formState"
          @finish="handleSubmit"
        >
          <a-tabs v-model:activeKey="type" size="large" centered :tabBarStyle="{ textAlign: 'center' }">
            <a-tab-pane key="user" tab="用户登录" />
          </a-tabs>

          <div v-if="type === 'user' || type === 'boss' || type === 'admin'">
            <a-form-item
              name="usrAccount"
              :rules="[{ required: true, message: '请输入账号!' }]"
            >
              <a-input
                v-model:value="formState.usrAccount"
                size="large"
                placeholder="请输入账号"
              >
                <template #prefix>
                  <UserOutlined class="prefixIcon" />
                </template>
              </a-input>
            </a-form-item>
            <a-form-item
              name="userPassword"
              :rules="[{ required: true, message: '请输入密码!' }]"
            >
              <a-input-password
                v-model:value="formState.userPassword"
                size="large"
                placeholder="请输入密码"
              >
                <template #prefix>
                  <LockOutlined class="prefixIcon" />
                </template>
              </a-input-password>
            </a-form-item>
          </div>

          <div style="margin-bottom: 24px">
            <a-checkbox v-model:checked="autoLogin">自动登录</a-checkbox>
            <a style="float: right">忘记密码</a>
          </div>

          <a-form-item>
            <a-button type="primary" html-type="submit" size="large" block>
              登录
            </a-button>
          </a-form-item>
          <div class="other">
            <router-link to="/user/register">注册账户</router-link>
          </div>
        </a-form>
      </div>
    </div>
    <div class="footer">BOSS招聘 Experience Technology Department</div>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { reactive, ref } from 'vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { userLogin } from '@/api/api/userController';
import { useUserStore } from '@/stores/user';
import { message } from 'ant-design-vue';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

const formState = reactive({
  usrAccount: '',
  userPassword: '',
});
const autoLogin = ref(true);
const type = ref('user');

const handleSubmit = async (values: any) => {
  try {
    let res;
    if (type.value === 'user') {
      res = await userLogin(values);
    } else if (type.value === 'admin') {
      res = await adminLogin({
        userAccount: values.usrAccount,
        userPassword: values.userPassword
      });
    } else if (type.value === 'boss') {
      // 手动调用 Boss 登录接口，假设路径为 /boss/login
      res = await request('/boss/login', {
        method: 'POST',
        data: {
          userAccount: values.usrAccount,
          userPassword: values.userPassword
        }
      });
    }

    if (res && res.code === 0 && res.data) {
      message.success('登录成功');
      await userStore.fetchLoginUser();
      
      // 检查是否有重定向参数
      const redirect = router.currentRoute.value.query.redirect as string;
      if (redirect) {
        router.push(redirect);
      } else {
        router.push('/');
      }
    } else {
      message.error(res.message || '登录失败');
    }
  } catch (error) {
    message.error('登录失败，请重试');
  }
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: auto;
  background: #f0f2f5;
  background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg');
  background-repeat: no-repeat;
  background-position: center 110px;
  background-size: 100%;
}

.content {
  flex: 1;
  padding: 32px 0;
}

.top {
  text-align: center;
}

.header {
  height: 44px;
  line-height: 44px;
}

.logo {
  height: 44px;
  margin-right: 16px;
  vertical-align: top;
}

.title {
  position: relative;
  top: 2px;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 600;
  font-size: 33px;
  font-family: Avenir, 'Helvetica Neue', Arial, Helvetica, sans-serif;
}

.desc {
  margin-top: 12px;
  margin-bottom: 40px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}

.main {
  width: 328px;
  margin: 0 auto;
}

.footer {
  margin: 48px 0 24px;
  padding: 0 16px;
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}
</style>
