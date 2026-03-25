<template>
    <div class="p-6">
        <div class="flex justify-between items-center mb-6">
            <h1 class="text-2xl font-bold">Quản lý người dùng</h1>
            <button @click="openAddModal" class="btn btn-primary">Thêm người dùng</button>
        </div>

        <div class="overflow-x-auto bg-white rounded shadow">
            <table class="min-w-full">
                <thead>
                    <tr class="bg-gray-100">
                        <th class="px-4 py-2 text-left">ID</th>
                        <th class="px-4 py-2 text-left">Tên đăng nhập</th>
                        <th class="px-4 py-2 text-left">Họ tên</th>
                        <th class="px-4 py-2 text-left">Email</th>
                        <th class="px-4 py-2 text-left">Role</th>
                        <th class="px-4 py-2 text-center">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="user in users" :key="user.id" class="border-b hover:bg-gray-50">
                        <td class="px-4 py-2">{{ user.id }}</td>
                        <td class="px-4 py-2">{{ user.username }}</td>
                        <td class="px-4 py-2">{{ user.name }}</td>
                        <td class="px-4 py-2">{{ user.email }}</td>
                        <td class="px-4 py-2">
                            <span class="badge" :class="user.role === 'ADMIN' ? 'badge-primary' : 'badge-ghost'">
                                {{ user.role || 'Không xác định' }}
                            </span>
                        </td>
                        <td class="px-4 py-2 text-center flex flex-row justify-evenly">
                            <button @click="openEditModal(user)" class="btn btn-soft btn-primary">Sửa</button>
                            <button @click="confirmDelete(user)" class="btn btn-soft btn-error">Xóa</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Modal thêm/sửa -->
        <div v-if="modalVisible" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div class="bg-white p-6 rounded w-96">
                <h3 class="text-xl font-semibold mb-4">{{ isEdit ? 'Chỉnh sửa' : 'Thêm người dùng' }}</h3>
                <form @submit.prevent="onSubmit">
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Tên đăng nhập</label>
                        <input v-model="form.username" type="text" required
                            class="input w-full border rounded px-3 py-2" :disabled="isEdit" />
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Họ tên</label>
                        <input v-model="form.name" type="text" required class="input w-full border rounded px-3 py-2" />
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Email</label>
                        <input v-model="form.email" type="email" required
                            class="input w-full border rounded px-3 py-2" />
                    </div>
                    <div v-if="!isEdit" class="mb-4">
                        <label class="block text-sm font-medium mb-1">Mật khẩu</label>
                        <input v-model="form.password" type="password" required
                            class="input w-full border rounded px-3 py-2" />
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Role</label>
                        <select v-model="form.role" class="select w-full border rounded px-3 py-2">
                            <option value="USER">USER</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                    </div>
                    <div class="flex justify-end gap-2">
                        <button type="button" @click="modalVisible = false" class="btn btn-soft">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { push } from 'notivue';
import { getAllUsers, createUser, updateUser, deleteUser } from '../services/adminService';
import { authState } from '../composables/useAuth';

const users = ref([]);
const modalVisible = ref(false);
const isEdit = ref(false);
const form = ref({
    id: null,
    username: '',
    name: '',
    email: '',
    password: '',
    role: 'USER',
});

onMounted(async () => {
    await loadUsers();
});

const loadUsers = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    try {
        const currentUserId = authState.user.value?.id;
        users.value = await getAllUsers();
        users.value = users.value.filter(user => user.id !== currentUserId)
    } catch (error) {
        push.error('Không thể tải danh sách người dùng');
    }
};

const openAddModal = () => {
    isEdit.value = false;
    form.value = { id: null, username: '', name: '', email: '', password: '', role: 'USER' };
    modalVisible.value = true;
};

const openEditModal = (user) => {
    isEdit.value = true;
    form.value = { ...user, password: '' };
    modalVisible.value = true;
};

const onSubmit = async () => {
    try {
        if (isEdit.value) {
            await updateUser(form.value.id, {
                name: form.value.name,
                email: form.value.email,
                role: form.value.role,
                newPassword: form.value.password || undefined
            });
            push.success('Cập nhật người dùng thành công');
        } else {
            await createUser({
                username: form.value.username,
                name: form.value.name,
                email: form.value.email,
                password: form.value.password,
                role: form.value.role
            });
            push.success('Thêm người dùng thành công');
        }
        modalVisible.value = false;
        await loadUsers();
    } catch (error) {
        push.error(error.response?.data?.message || 'Có lỗi xảy ra');
    }
};

const confirmDelete = (user) => {
    if (confirm(`Xóa người dùng ${user.username}?`)) {
        handleDelete(user.id);
    }
};

const handleDelete = async (id) => {
    try {
        await deleteUser(id);
        push.success('Xóa người dùng thành công');
        await loadUsers();
    } catch (error) {
        push.error('Không thể xóa người dùng');
    }
};
</script>