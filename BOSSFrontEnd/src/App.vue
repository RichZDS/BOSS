<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onMounted } from 'vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

onMounted(() => {
  userStore.fetchLoginUser();
});
</script>

<template>
  <RouterView />
</template>

<style>
/* 全局样式 */
body {
  margin: 0;
  padding: 0;
}
</style>
