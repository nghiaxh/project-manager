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
                        <th class="px-4 py-2 text-left">Vai trò</th>
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
                            <span v-if="user.role === 'ADMIN'" class="badge badge-primary">Quản trị viên</span>
                            <span v-else class="badge badge-ghost">Người dùng</span>
                        </td>
                        <td class="px-4 py-2 text-center flex flex-row justify-evenly">
                            <button @click="openEditModal(user)" class="btn btn-soft btn-primary">Sửa</button>
                            <button @click="confirmDelete(user)" class="btn btn-soft btn-error">Xóa</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div v-if="modalVisible" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div class="bg-white p-6 rounded w-96">
                <h3 class="text-xl font-semibold mb-4">{{ isEdit ? 'Chỉnh sửa' : 'Thêm người dùng' }}</h3>
                <form @submit.prevent="onSubmit">
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Tên đăng nhập</label>
                        <input v-model="username" type="text" class="input w-full border rounded px-3 py-2"
                            :disabled="isEdit" placeholder="Tên đăng nhập" />
                        <span class="text-red-500 text-sm">{{ errors.username }}</span>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Họ tên</label>
                        <input v-model="name" type="text" class="input w-full border rounded px-3 py-2"
                            placeholder="Họ tên" />
                        <span class="text-red-500 text-sm">{{ errors.name }}</span>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Email</label>
                        <input v-model="email" type="email" class="input w-full border rounded px-3 py-2"
                            placeholder="Email" />
                        <span class="text-red-500 text-sm">{{ errors.email }}</span>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Mật khẩu</label>
                        <input v-model="password" type="password" class="input w-full border rounded px-3 py-2"
                            placeholder="Mật khẩu" />
                        <span class="text-red-500 text-sm">{{ errors.password }}</span>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Vai trò</label>
                        <select v-model="role" class="select w-full border rounded px-3 py-2">
                            <option value="USER">Người dùng</option>
                            <option value="ADMIN">Quản trị viên</option>
                        </select>
                    </div>
                    <div class="flex justify-end gap-2">
                        <button type="button" @click="modalVisible = false" class="btn btn-soft">Hủy</button>
                        <button type="submit" class="btn btn-primary" :disabled="submitting">
                            {{ submitting ? 'Đang lưu...' : 'Lưu' }}
                        </button>
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
import { useForm, useField } from 'vee-validate';
import { registerSchema } from '../validation/authValidation';

const users = ref([]);
const modalVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);

const { handleSubmit, errors, resetForm, setValues } = useForm({
    validationSchema: registerSchema,
    context: { isEdit: false }
});

const { value: username } = useField('username');
const { value: name } = useField('name');
const { value: email } = useField('email');
const { value: password } = useField('password');
const { value: role } = useField('role');

onMounted(async () => {
    await loadUsers();
});

const loadUsers = async () => {
    try {
        if (!authState.isAuthenticated.value) return
        const currentUserId = authState.user.value?.id;
        users.value = await getAllUsers();
        users.value = users.value.filter(user => user.id !== currentUserId);
    } catch (error) {
        push.error('Không thể tải danh sách người dùng');
    }
};

const openAddModal = () => {
    isEdit.value = false;
    resetForm({
        values: {
            username: '',
            name: '',
            email: '',
            password: '',
            role: 'USER'
        },
        context: { isEdit: false }
    });
    modalVisible.value = true;
};

const openEditModal = (user) => {
    isEdit.value = true;
    setValues({
        username: user.username,
        name: user.name,
        email: user.email,
        password: '',
        role: user.role
    });
    modalVisible.value = true;
};

const onSubmit = handleSubmit(async (values) => {
    submitting.value = true;
    try {
        if (isEdit.value) {
            await updateUser(values.username, {
                name: values.name,
                email: values.email,
                role: values.role,
                newPassword: values.password || undefined
            });
            push.success('Cập nhật người dùng thành công');
        } else {
            await createUser({
                username: values.username,
                name: values.name,
                email: values.email,
                password: values.password,
                role: values.role
            });
            push.success('Thêm người dùng thành công');
        }
        modalVisible.value = false;
        await loadUsers();
    } catch (error) {
        push.error(error.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
        submitting.value = false;
    }
});

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