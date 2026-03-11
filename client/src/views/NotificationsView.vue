<template>
    <div>
        <h2 class="text-2xl font-semibold mb-6">Thông báo</h2>
        <div v-if=" loading ">Đang tải...</div>
        <div v-else-if=" notifications.length === 0 " class="text-center text-gray-500">
            Không có thông báo nào.
        </div>
        <div v-else class="space-y-2">
            <div v-for=" notif in notifications " :key=" notif.id "
                class="bg-white p-4 rounded shadow flex justify-between items-start">
                <div>
                    <p :class=" { 'font-semibold': !notif.read } ">{{ notif.message }}</p>
                    <span class="text-xs text-gray-500">{{ new Date( notif.createdAt ).toLocaleString() }}</span>
                </div>
                <button v-if=" !notif.read " @click="markAsRead( notif.id )" class="text-blue-500 text-sm">Đánh dấu đã
                    đọc</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getUserNotifications, markAsRead as apiMarkAsRead } from '../services/notificationService';
import { authState } from '../composables/useAuth';

const loading = ref(true);
const notifications = ref([]);

onMounted(async () => {
    await loadNotifications();
    loading.value = false;
});

const loadNotifications = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    notifications.value = await getUserNotifications();
};

const markAsRead = async (id) => {
    await apiMarkAsRead(id);
    await loadNotifications();
};
</script>