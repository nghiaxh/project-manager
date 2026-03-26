<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
        <div class="bg-white p-8 rounded shadow-md w-96">
            <h2 class="text-2xl font-bold mb-6 text-center">Đăng ký</h2>
            <form @submit.prevent=" onSubmit ">
                <div class="mb-4">
                    <label class="label block text-sm font-medium mb-1">Họ tên</label>
                    <input v-model=" name " type="text" class="input w-full border rounded px-3 py-2"
                        placeholder="Nhập họ tên" />
                    <span class="text-red-500 text-sm">{{ errors.name }}</span>
                </div>
                <div class="mb-4">
                    <label class="label block text-sm font-medium mb-1">Tên đăng nhập</label>
                    <input v-model=" username " type="text" class="input w-full border rounded px-3 py-2"
                        placeholder="Nhập tên đăng nhập" />
                    <span class="text-red-500 text-sm">{{ errors.username }}</span>
                </div>
                <div class="mb-4">
                    <label class="label block text-sm font-medium mb-1">Email</label>
                    <input v-model=" email " type="email" class="input w-full border rounded px-3 py-2"
                        placeholder="Nhập địa chỉ email" />
                    <span class="text-red-500 text-sm">{{ errors.email }}</span>
                </div>
                <div class="mb-6">
                    <label class="label block text-sm font-medium mb-1" placeholder="Nhập mật khẩu">Mật khẩu</label>
                    <input v-model=" password " type="password" class="input w-full border rounded px-3 py-2"
                        placeholder="Nhập mật khẩu" />
                    <span class="text-red-500 text-sm">{{ errors.password }}</span>
                </div>
                <button type="submit" class="btn btn-primary w-full">Đăng ký</button>
                <p class="mt-4 text-center text-sm">
                    Đã có tài khoản? <router-link to="/" class="text-blue-500 hover:underline">Đăng nhập</router-link>
                </p>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { authState } from '../composables/useAuth';
import { registerSchema } from '../validation/authValidation';
import { useForm, useField } from 'vee-validate';
import { push } from "notivue";

const router = useRouter();

const { handleSubmit, errors } = useForm({
    validationSchema: registerSchema,
});

const { value: name } = useField('name');
const { value: username } = useField('username');
const { value: email } = useField('email');
const { value: password } = useField('password');

const onSubmit = handleSubmit(async (values) => {
    try {
        await authState.register({ name: values.name, username: values.username, email: values.email, password: values.password });
        router.push('/');
        push.success('Đăng ký thành công');
    } catch (error) {
        push.error('Đăng ký thất bại');
    }
});
</script>