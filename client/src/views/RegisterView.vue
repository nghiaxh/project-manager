<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
        <div class="bg-white p-8 rounded shadow-md w-96">
            <h2 class="text-2xl font-bold mb-6 text-center">Đăng ký</h2>
            <form @submit.prevent=" register ">
                <div class="mb-4">
                    <label class="block text-sm font-medium mb-1">Họ tên</label>
                    <input v-model=" name " type="text" required class="w-full border rounded px-3 py-2" />
                </div>
                <div class="mb-4">
                    <label class="block text-sm font-medium mb-1">Tên đăng nhập</label>
                    <input v-model=" username " type="text" required class="w-full border rounded px-3 py-2" />
                </div>
                <div class="mb-4">
                    <label class="block text-sm font-medium mb-1">Email</label>
                    <input v-model=" email " type="email" required class="w-full border rounded px-3 py-2" />
                </div>
                <div class="mb-6">
                    <label class="block text-sm font-medium mb-1">Mật khẩu</label>
                    <input v-model=" password " type="password" required class="w-full border rounded px-3 py-2" />
                </div>
                <button type="submit" class="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600">Đăng
                    ký</button>
                <p class="mt-4 text-center text-sm">
                    Đã có tài khoản? <router-link to="/" class="text-blue-500">Đăng nhập</router-link>
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
const name = ref('');
const username = ref('');
const email = ref('');
const password = ref('');

const register = async () => {
    try {
        await authState.register({ name: name.value, username: username.value, email: email.value, password: password.value });
        router.push('/');
    } catch (error) {
        alert('Đăng ký thất bại');
    }
};
</script>