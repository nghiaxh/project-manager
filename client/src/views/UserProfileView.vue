<template>
    <div v-if=" loading ">Đang tải...</div>
    <div v-else class="max-w-2xl mx-auto">
        <div class="bg-white p-6 rounded shadow">
            <h2 class="text-2xl font-semibold mb-6">Hồ sơ của tôi</h2>
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
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { authState } from '../composables/useAuth';
import { getUserById } from '../services/userService';

const loading = ref(true);
const user = ref({});

onMounted(async () => {
    try {
        if (!authState.isAuthenticated.value) {
            return;
        }
        user.value = await getUserById(authState.user.value.id);
    } catch (error) {
        alert('Không thể tải thông tin người dùng');
    } finally {
        loading.value = false;
    }
});
</script>