<template>
  <NavBar />
  <div class="users-page">
    <a-card title="用户列表" :bordered="false" class="users-card">
      <template #extra>
        <a-button type="primary" @click="fetchUsers" :loading="loading">刷新</a-button>
      </template>

      <a-alert v-if="error" :message="error" type="error" show-icon style="margin-bottom: 16px" />
      <a-table v-if="userStore.loginUser.userRole===`admin`"
        :columns="adminColumns"
        :data-source="users"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'userRole'">
            <a-tag :color="record.userRole === 'admin' ? 'blue' : 'green'">
              {{ record.userRole === 'admin' ? '管理员' : '用户' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'createdAt'">
            {{ record.createdAt ? formatDate(record.createdAt) : '-' }}
          </template>

          <template v-else-if="column.key === 'action'">
            <template v-if="userStore.loginUser.userRole === 'admin'">

              <a @click="openEditModal(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定要删除该用户吗？" @confirm="handleDelete(record.id)">
                <a style="color: red">删除</a>
              </a-popconfirm>
            </template>
          </template>
        </template>
      </a-table>

      <a-table v-if="userStore.loginUser.userRole!=`admin`"
               :columns="columns"
               :data-source="users"
               :loading="loading"
               :pagination="pagination"
               row-key="id"
               @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'userRole'">
            <a-tag :color="record.userRole === 'admin' ? 'blue' : 'green'">
              {{ record.userRole === 'admin' ? '管理员' : '用户' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'createdAt'">
            {{ record.createdAt ? formatDate(record.createdAt) : '-' }}
          </template>

          <template v-else-if="column.key === 'action'">
            <template v-if="userStore.loginUser.userRole === 'admin'">

              <a @click="openEditModal(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定要删除该用户吗？" @confirm="handleDelete(record.id)">
                <a style="color: red">删除</a>
              </a-popconfirm>
            </template>
          </template>
        </template>
      </a-table>
    </a-card>


    <a-modal
      v-model:visible="editModalVisible"
      title="编辑用户信息"
      @ok="handleEditSubmit"
      :confirmLoading="editLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model:value="editForm.userName" />
        </a-form-item>
        <a-form-item label="角色">
          <a-select v-model:value="editForm.userRole">
            <a-select-option value="user">用户</a-select-option>
            <a-select-option value="boss">Boss</a-select-option>
            <a-select-option value="admin">管理员</a-select-option>
          </a-select>
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
import { listUserVoByPage, deleteUser, updateUser } from '@/api/api/userController';
import { useUserStore } from '@/stores/user';
import { message } from 'ant-design-vue';

const userStore = useUserStore();

import type { TableProps } from 'ant-design-vue';

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

// Define columns for Ant Design Table
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName',
  },
  {
    title: '角色',
    dataIndex: 'userRole',
    key: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
  },
];
const adminColumns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName',
  },
  {
    title: '角色',
    dataIndex: 'userRole',
    key: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
  },
  {
    title: '操作',
    key: 'action',
  },
];

const users = ref<API.UserVO[]>([]);
const loading = ref(false);
const error = ref('');

// Pagination state
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
});

const fetchUsers = async () => {
  loading.value = true;
  error.value = '';
  try {
    const response = await listUserVoByPage({
      current: pagination.current,
      pageSize: pagination.pageSize,
    });
    
    if (response.code === 0 && response.data) {
      users.value = response.data.records || [];
      pagination.total = Number(response.data.total) || 0;
    } else {
      error.value = response.message || '获取用户列表失败';
    }
  } catch (err: any) {
    error.value = err.message || '获取用户列表失败';
  } finally {
    loading.value = false;
  }
};

const handleTableChange: TableProps['onChange'] = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchUsers();
};

const handleDelete = async (id: number) => {
  try {
    const res = await deleteUser({ id });
    if (res.code === 0) {
      message.success('删除成功');
      fetchUsers();
    } else {
      message.error(res.message || '删除失败');
    }
  } catch (error) {
    message.error('删除失败');
  }
};

// Edit
const editModalVisible = ref(false);
const editLoading = ref(false);
const editForm = reactive({
  id: 0,
  userName: '',
  userProfile: '',
  userRole: '',
});

const openEditModal = (record: API.UserVO) => {
  editForm.id = record.id as number;
  editForm.userName = record.userName || '';
  editForm.userProfile = record.userProfile || '';
  editForm.userRole = record.userRole || 'user';
  editModalVisible.value = true;
};

const handleEditSubmit = async () => {
  editLoading.value = true;
  try {
    const res = await updateUser(editForm);
    if (res.code === 0) {
      message.success('更新成功');
      editModalVisible.value = false;
      fetchUsers();
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
  fetchUsers();
});
</script>

<style scoped>
.users-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.users-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
}
</style>