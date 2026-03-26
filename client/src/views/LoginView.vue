<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
        <div class="bg-white p-8 rounded shadow-md w-96">
            <h2 class="text-2xl font-bold mb-6 text-center">Đăng nhập</h2>
            <form @submit.prevent="onSubmit">
                <div class="mb-4">
                    <label class="label block text-sm font-medium mb-1">Tên đăng nhập</label>
                    <input v-model="username" type="text" class="input w-full border rounded px-3 py-2"
                        placeholder="Nhập tên đăng nhập" />
                    <span class="text-red-500 text-sm">{{ errors.username }}</span>
                </div>
                <div class="mb-6">
                    <label class="label block text-sm font-medium mb-1">Mật khẩu</label>
                    <input v-model="password" type="password" class="input w-full border rounded px-3 py-2"
                        placeholder="Nhập mật khẩu" />
                    <span class="text-red-500 text-sm">{{ errors.password }}</span>
                </div>
                <button type="submit" class="btn btn-primary w-full">Đăng nhập</button>
                <p class="mt-4 text-center text-sm">
                    Chưa có tài khoản? <router-link to="/register" class="text-blue-500 hover:underline">Đăng
                        ký</router-link>
                </p>
            </form>
        </div>
    </div>
</template>

<script setup>
import { useForm, useField } from 'vee-validate';
import { useRouter } from 'vue-router';
import { authState } from '../composables/useAuth';
import { push } from 'notivue';
import { loginSchema } from '../validation/authValidation';

const { handleSubmit, errors } = useForm({
    validationSchema: loginSchema,
});

const { value: username } = useField('username');
const { value: password } = useField('password');

const router = useRouter();

const onSubmit = handleSubmit(async (values) => {
    try {
        await authState.login(values.username, values.password);
        if (authState.user.value?.role === 'ADMIN') {
            router.push('/admin');
        } else {
            router.push('/projects');
        }
        push.success("Đăng nhập thành công");
    } catch (error) {
        console.error(error)
        push.error('Đăng nhập thất bại');
    }
});
</script>