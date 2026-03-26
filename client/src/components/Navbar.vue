<template>
    <header class="bg-white">
        <div class="px-4 py-3 flex justify-end shadow-2xl">
            <div class="flex items-center space-x-4">
                <router-link to="/notifications" class="text-gray-600 hover:text-gray-900">
                    <span class="relative">
                        <Bell />
                    </span>
                </router-link>
                <router-link to="/profile" class="text-gray-600 hover:text-gray-900">
                    Xin chào, {{ authState.user.value?.name }}
                </router-link>
            </div>
        </div>
    </header>
</template>

<script setup>
import { Bell } from "lucide-vue-next";
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { authState } from '../composables/useAuth';
import { getUserNotifications } from '../services/notificationService';

const unreadCount = ref(0);

const fetchUnreadCount = async () => {
    if (!authState.isAuthenticated.value) return;
    try {
        const notifs = await getUserNotifications();
        unreadCount.value = notifs.filter(n => !n.read).length;
    } catch (error) {
        console.error('Không thể lấy thông báo');
    }
};

onMounted(() => {
    fetchUnreadCount();
});
</script>