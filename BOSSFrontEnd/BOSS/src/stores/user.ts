import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getLoginUser, userLogout } from '@/api/api/userController';
import { message } from 'ant-design-vue';
import { useRouter } from 'vue-router';

export const useUserStore = defineStore('user', () => {
  const loginUser = ref<any>({
    userName: '未登录',
    userRole: 'guest',
  });

  const router = useRouter();

  const fetchLoginUser = async () => {
    try {
      const resUser = await getLoginUser();
      if (resUser.code === 0 && resUser.data) {
        loginUser.value = resUser.data;
        return;
      }
      loginUser.value = { userName: '未登录', userRole: 'guest' };
    } catch (error) {
      loginUser.value = { userName: '未登录', userRole: 'guest' };
    }
  };

  const logout = async () => {
    try {
      await userLogout();
      loginUser.value = { userName: '未登录', userRole: 'guest' };
      message.success('退出登录成功');
      router.push('/user/login');
    } catch (error) {
      message.error('退出登录失败');
    }
  };

  return {
    loginUser,
    fetchLoginUser,
    logout,
  };
});