<template>
    <div v-if=" loading " class="text-center py-10">Đang tải...</div>
    <div v-else class="max-w-2xl mx-auto">
        <!-- Hiển thị thông tin -->
        <div class="bg-white p-6 rounded shadow">
            <div class="flex justify-between items-start mb-6">
                <h2 class="text-2xl font-semibold">Hồ sơ của tôi</h2>
                <button @click=" startEdit " class="btn btn-primary">Chỉnh sửa</button>
            </div>

            <div class="avatar avatar-placeholder mb-8">
                <div class="bg-indigo-600 text-neutral-content w-24 rounded-full">
                    <span class="text-3xl">{{ user.name?.charAt( 0 ).toUpperCase() }}</span>
                </div>
            </div>

            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-600">Họ tên</label>
                    <div class="mt-1 text-lg">{{ user.name }}</div>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-600">Tên đăng nhập</label>
                    <div class="mt-1 text-lg">{{ user.username }}</div>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-600">Email</label>
                    <div class="mt-1 text-lg">{{ user.email }}</div>
                </div>
            </div>
        </div>

        <!-- Modal chỉnh sửa -->
        <div v-if=" showEditModal "
            class="fixed inset-0 backdrop-blur-xs bg-opacity-50 flex items-center justify-center z-50">
            <div class="bg-white p-6 rounded w-96">
                <h3 class="text-xl font-semibold mb-4">Chỉnh sửa hồ sơ</h3>
                <form @submit.prevent=" onSubmit ">
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Họ tên</label>
                        <input v-model=" name " type="text" required class="input w-full border rounded px-3 py-2" />
                        <span class="text-red-500 text-sm">{{ errors.name }}</span>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Email</label>
                        <input v-model=" email " type="email" required class="input w-full border rounded px-3 py-2" />
                        <span class="text-red-500 text-sm">{{ errors.email }}</span>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Mật khẩu hiện tại</label>
                        <input v-model=" currentPassword " type="password" class="input w-full border rounded px-3 py-2"
                            placeholder="Nhập nếu muốn đổi mật khẩu" />
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Mật khẩu mới</label>
                        <input v-model=" newPassword " type="password" class="input w-full border rounded px-3 py-2"
                            placeholder="Để trống nếu không đổi" />
                    </div>
                    <div class="flex justify-end space-x-2">
                        <button type="button" @click="showEditModal = false" class="btn btn-soft">Hủy</button>
                        <button type="submit" class="btn btn-primary" :disabled=" isSubmitting ">
                            {{ isSubmitting ? 'Đang lưu...' : 'Lưu' }}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { authState } from '../composables/useAuth';
import { getUserById, updateCurrentUser } from '../services/userService';
import { push } from 'notivue';
import { userUpdateSchema } from '../validation/userValidation';
import { useForm, useField } from 'vee-validate';

const loading = ref(true);
const user = ref({});
const showEditModal = ref(false);
const isSubmitting = ref(false);

const { handleSubmit, errors, setValues } = useForm({
    validationSchema: userUpdateSchema,
    initialValues: {
        name: '',
        email: ''
    }
});

const { value: name } = useField('name');
const { value: email } = useField('email');
const currentPassword = ref('');
const newPassword = ref('');

onMounted(async () => {
    try {
        if (!authState.isAuthenticated.value) return;
        user.value = await getUserById(authState.user.value.id);
    } catch (error) {
        push.error('Không thể tải thông tin người dùng');
    } finally {
        loading.value = false;
    }
});

const startEdit = () => {
    setValues({
        name: user.value.name,
        email: user.value.email
    });
    currentPassword.value = '';
    newPassword.value = '';
    showEditModal.value = true;
};

const onSubmit = handleSubmit(async (values) => {
    isSubmitting.value = true;
    try {
        const payload = {
            name: values.name,
            email: values.email,
            currentPassword: currentPassword.value || undefined,
            newPassword: newPassword.value || undefined,
        };
        if (!payload.currentPassword) delete payload.currentPassword;
        if (!payload.newPassword) delete payload.newPassword;

        const updatedUser = await updateCurrentUser(payload);
        user.value = updatedUser;
        authState.user.value = updatedUser;
        sessionStorage.setItem('user', JSON.stringify(updatedUser));

        showEditModal.value = false;
        push.success('Cập nhật hồ sơ thành công');
    } catch (error) {
        push.error(error.response?.data?.message || 'Cập nhật thất bại');
    } finally {
        isSubmitting.value = false;
    }
});
</script>