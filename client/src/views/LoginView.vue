<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
        <div class="bg-white p-8 rounded shadow-md w-96">
            <h2 class="text-2xl font-bold mb-6 text-center">Đăng nhập</h2>
            <form @submit.prevent=" login ">
                <div class="mb-4">
                    <label class="block text-sm font-medium mb-1">Tên đăng nhập</label>
                    <input v-model=" username " type="text" required class="w-full border rounded px-3 py-2" />
                </div>
                <div class="mb-6">
                    <label class="block text-sm font-medium mb-1">Mật khẩu</label>
                    <input v-model=" password " type="password" required class="w-full border rounded px-3 py-2" />
                </div>
                <button type="submit" class="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600">Đăng
                    nhập</button>
                <p class="mt-4 text-center text-sm">
                    Chưa có tài khoản? <router-link to="/register" class="text-blue-500">Đăng ký</router-link>
                </p>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { authState } from '../composables/useAuth';

const router = useRouter();
const username = ref('');
const password = ref('');

const login = async () => {
    try {
        await authState.login(username.value, password.value);
        router.push('/projects');
    } catch (error) {
        alert('Đăng nhập thất bại');
    }
};
</script>