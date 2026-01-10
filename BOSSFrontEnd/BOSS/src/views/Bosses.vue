<template>
  <NavBar />
  <div class="bosses-page">
    <a-card title="Boss列表" :bordered="false" class="bosses-card">
      <template #extra>
        <a-button type="primary" @click="fetchBosses" :loading="loading">刷新</a-button>
      </template>

      <a-alert v-if="error" :message="error" type="error" show-icon style="margin-bottom: 16px" />

      <a-table
        :columns="userStore.loginUser.userRole === 'admin' ? adminColumns : columns"
        :data-source="bosses"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'createdAt'">
            {{ record.createdAt ? formatDate(record.createdAt) : '-' }}
          </template>
          <template v-else-if="column.key === 'action' && userStore.loginUser.userRole === 'admin'">
            <a-space v-if="canEdit(record)">
              <a @click="openEditModal(record)">编辑</a>
              <a-popconfirm title="确定要删除该Boss吗？" @confirm="handleDelete(record.id)">
                <a style="color: red">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- Edit Modal -->
    <a-modal
      v-model:visible="editModalVisible"
      title="编辑Boss信息"
      @ok="handleEditSubmit"
      :confirmLoading="editLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model:value="editForm.userName" />
        </a-form-item>
        <a-form-item label="简介">
          <a-input v-model:value="editForm.userProfile" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import { ref, onMounted, reactive } from 'vue';
import { useUserStore } from '@/stores/user';
import { message } from 'ant-design-vue';
import type { TableProps } from 'ant-design-vue';
import { listBoss1 } from '@/api/api/bossController';
import { deleteUser, updateUser } from '@/api/api/userController';
import { getCompanyVoById } from '@/api/api/companyController';

const userStore = useUserStore();

// 时间格式化函数
const formatDate = (dateString: string) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '老板', dataIndex: 'userName', key: 'userName' },
  { title: '公司名称', dataIndex: 'companyName', key: 'companyName' },
  { title: '电话', dataIndex: 'phone', key: 'phone' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' }
];
const adminColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '老板', dataIndex: 'userName', key: 'userName' },
  { title: '公司名称', dataIndex: 'companyName', key: 'companyName' },
  { title: '电话', dataIndex: 'phone', key: 'phone' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action' },
];

type BossRow = API.UserVO & { companyName?: string };

const bosses = ref<BossRow[]>([]);
const loading = ref(false);
const error = ref('');

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
});

const fetchBosses = async () => {
  loading.value = true;
  error.value = '';
  try {
    const response = await listBoss1();
    if (response.code === 0 && response.data) {
      const records = (response.data || []).filter((u: API.UserVO) => u.userRole === 'boss');
      // 获取公司名称
      const rows: BossRow[] = await Promise.all(records.map(async (u: API.UserVO) => {
        let companyName: string | undefined = undefined;
        if (u.companyId) {
          try {
            const c = await getCompanyVoById({ id: u.companyId });
            companyName = c?.data?.name || undefined;
          } catch {}
        }
        return { ...u, companyName };
      }));
      bosses.value = rows;
      pagination.total = rows.length;
    } else {
      error.value = response.message || '获取Boss列表失败';
    }
  } catch (err: any) {
    error.value = err.message || '获取Boss列表失败';
  } finally {
    loading.value = false;
  }
};

const handleTableChange: TableProps['onChange'] = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchBosses();
};

const canEdit = (record: API.UserVO) => {
  const { id, userRole } = userStore.loginUser;
  if (!id) return false;
  if (userRole === 'admin') return true;
  if (userRole === 'boss' && String(id) === String(record.id)) return true;
  return false;
};

const handleDelete = async (id: number) => {
  try {
    const res = await deleteUser({ id });
    if (res.code === 0) {
      message.success('删除成功');
      fetchBosses();
    } else {
      message.error(res.message || '删除失败');
    }
  } catch (error) {
    message.error('删除失败');
    console.error(error);
  }
};

// Edit
const editModalVisible = ref(false);
const editLoading = ref(false);
const editForm = reactive({
  id: 0,
  userName: '',
  userProfile: '',
});

const openEditModal = (record: API.UserVO) => {
  editForm.id = record.id as number;
  editForm.userName = record.userName || '';
  editForm.userProfile = record.userProfile || '';
  editModalVisible.value = true;
};

const handleEditSubmit = async () => {
  editLoading.value = true;
  try {
    const res = await updateUser(editForm);
    if (res.code === 0) {
      message.success('更新成功');
      editModalVisible.value = false;
      fetchBosses();
    } else {
      message.error(res.message || '更新失败');
    }
  } catch (error) {
    message.error('更新失败');
  } finally {
    editLoading.value = false;
  }
};

onMounted(() => {
  fetchBosses();
});
</script>

<style scoped>
.bosses-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.bosses-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
}
</style>