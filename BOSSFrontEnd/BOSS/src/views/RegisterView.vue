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
        <div class="register-title">注册</div>
        
        <a-tabs v-model:activeKey="activeKey" centered>
          <a-tab-pane key="user" tab="用户注册">
            <a-form
              :model="formState"
              @finish="handleSubmit"
            >
              <a-form-item
                name="usrAccount"
                :rules="[{ required: true, message: '请输入账号!' }, { min: 4, message: '账号长度不能小于4位' }]"
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
                :rules="[{ required: true, message: '请输入密码!' }, { min: 8, message: '密码长度不能小于8位' }]"
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

              <a-form-item
                name="checkPassword"
                :rules="[{ required: true, message: '请再次输入密码!' }, { validator: validatePass2 }]"
              >
                <a-input-password
                  v-model:value="formState.checkPassword"
                  size="large"
                  placeholder="请确认密码"
                >
                  <template #prefix>
                    <LockOutlined class="prefixIcon" />
                  </template>
                </a-input-password>
              </a-form-item>

              <a-form-item>
                <a-button type="primary" html-type="submit" size="large" block>
                  注册
                </a-button>
              </a-form-item>
            </a-form>
          </a-tab-pane>

          <a-tab-pane key="boss" tab="Boss注册">
            <a-form
              :model="bossFormState"
              @finish="handleBossSubmit"
            >
              <a-form-item
                name="username"
                :rules="[{ required: true, message: '请输入用户名!' }]"
              >
                <a-input
                  v-model:value="bossFormState.username"
                  size="large"
                  placeholder="用户名"
                >
                  <template #prefix>
                    <UserOutlined class="prefixIcon" />
                  </template>
                </a-input>
              </a-form-item>

              <a-form-item
                name="passwordHash"
                :rules="[{ required: true, message: '请输入密码!' }, { min: 8, message: '密码长度不能小于8位' }]"
              >
                <a-input-password
                  v-model:value="bossFormState.passwordHash"
                  size="large"
                  placeholder="请输入密码"
                >
                  <template #prefix>
                    <LockOutlined class="prefixIcon" />
                  </template>
                </a-input-password>
              </a-form-item>

              <a-form-item
                name="checkPassword"
                :rules="[{ required: true, message: '请再次输入密码!' }, { validator: validateBossPass2 }]"
              >
                <a-input-password
                  v-model:value="bossFormState.checkPassword"
                  size="large"
                  placeholder="请确认密码"
                >
                  <template #prefix>
                    <LockOutlined class="prefixIcon" />
                  </template>
                </a-input-password>
              </a-form-item>

              <a-form-item
                name="bossName"
                :rules="[{ required: true, message: '请输入真实姓名!' }]"
              >
                <a-input
                  v-model:value="bossFormState.bossName"
                  size="large"
                  placeholder="真实姓名"
                >
                  <template #prefix>
                    <CrownOutlined class="prefixIcon" />
                  </template>
                </a-input>
              </a-form-item>

              <a-form-item name="phone">
                <a-input
                  v-model:value="bossFormState.phone"
                  size="large"
                  placeholder="联系电话 (选填)"
                />
              </a-form-item>

              <a-form-item name="companyName">
                <a-input
                  v-model:value="bossFormState.companyName"
                  size="large"
                  placeholder="公司名称 (选填)"
                />
              </a-form-item>

              <a-form-item>
                <a-button type="primary" html-type="submit" size="large" block>
                  Boss注册
                </a-button>
              </a-form-item>
            </a-form>
          </a-tab-pane>
        </a-tabs>

        <div class="other">
          <a-button type="link" @click="router.push('/user/login')">使用已有账户登录</a-button>
        </div>
      </div>
    </div>
    <div class="footer">BOSS招聘 Experience Technology Department</div>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { reactive, ref } from 'vue';
import { UserOutlined, LockOutlined, CrownOutlined } from '@ant-design/icons-vue';
import { userRegister } from '@/api/api/userController';
import { message } from 'ant-design-vue';
import { useRouter } from 'vue-router';
import type { Rule } from 'ant-design-vue/es/form';

const router = useRouter();
const activeKey = ref('user');

const formState = reactive({
  usrAccount: '',
  userPassword: '',
  checkPassword: '',
});

const bossFormState = reactive({
  username: '',
  passwordHash: '',
  checkPassword: '',
  bossName: '',
  phone: '',
  companyName: '',
});

const validatePass2 = async (_rule: Rule, value: string) => {
  if (value === '') {
    return Promise.reject('请再次输入密码');
  } else if (value !== formState.userPassword) {
    return Promise.reject("两次输入密码不一致!");
  }
  return Promise.resolve();
};

const validateBossPass2 = async (_rule: Rule, value: string) => {
  if (value === '') {
    return Promise.reject('请再次输入密码');
  } else if (value !== bossFormState.passwordHash) {
    return Promise.reject("两次输入密码不一致!");
  }
  return Promise.resolve();
};

const handleSubmit = async (values: any) => {
  try {
    const res = await userRegister(values);
    if (res.code === 0 && res.data) {
      message.success('注册成功');
      router.push('/user/login');
    } else {
      message.error(res.message || '注册失败');
    }
  } catch (error) {
    message.error('注册失败，请重试');
  }
};

const handleBossSubmit = async (values: any) => {
  try {
    const res = await registerBoss(values);
    if (res.code === 0 && res.data) {
      message.success('Boss注册成功');
      router.push('/user/login');
    } else {
      message.error(res.message || '注册失败');
    }
  } catch (error) {
    message.error('注册失败，请重试');
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

.register-title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 18px;
  font-weight: 500;
}

.footer {
  margin: 48px 0 24px;
  padding: 0 16px;
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}
</style>
