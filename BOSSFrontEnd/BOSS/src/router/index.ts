import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    {
      path: '/home',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/user/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/user/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('../views/Users.vue'),
    },
    {
      path: '/bosses',
      name: 'bosses',
      component: () => import('../views/Bosses.vue'),
    },
    {
      path: '/user/resumes',
      name: 'my-resumes',
      component: () => import('../views/MyResumes.vue'),
    },
    {
      path: '/user/resume/edit/:id?',
      name: 'resume-edit',
      component: () => import('../views/ResumeEdit.vue'),
    },
    {
      path: '/jobs',
      name: 'jobs',
      component: () => import('../views/Jobs.vue'),
    },
    {
      path: '/user/applications',
      name: 'my-applications',
      component: () => import('../views/MyApplications.vue'),
    },
    {
      path: '/boss/applications',
      name: 'boss-applications',
      component: () => import('../views/BossApplications.vue'),
    },
    {
      path: '/boss/company',
      name: 'boss-company',
      component: () => import('../views/BossCompany.vue'),
    },
    {
      path: '/boss/job/edit/:id?',
      name: 'job-edit',
      component: () => import('../views/JobEdit.vue'),
    },
    {
      path: '/admin/applications',
      name: 'admin-applications',
      component: () => import('../views/AdminApplications.vue'),
    },
    {
      path: '/user/profile',
      name: 'user-profile',
      component: () => import('../views/Profile.vue'),
    },
  ],
})

// 全局路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  const publicPaths = ['/user/login', '/user/register'];

  // 如果访问的是公开路径，直接放行
  if (publicPaths.includes(to.path)) {
    next();
    return;
  }

  // 如果没有用户信息（id不存在），尝试获取用户信息
  if (!userStore.loginUser.id) {
    await userStore.fetchLoginUser();
  }

  // 再次检查，如果还是没有登录，重定向到登录页
  if (!userStore.loginUser.id) {
    next('/user/login');
  } else {
    next();
  }
});

export default router