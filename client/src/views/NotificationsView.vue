<template>
    <div>
        <div class="flex justify-between items-center mb-6">
            <button @click="markAllAsRead" class="btn btn-primary">
                Đánh dấu tất cả thông báo đã đọc
            </button>
            <button @click="confirmDeleteAllRead" class="btn btn-error text-white">
                Xóa thông báo đã đọc
            </button>
        </div>

        <div v-if="loading">Đang tải...</div>
        <div v-else-if="notifications.length === 0" class="text-center text-gray-500">
            Không có thông báo nào.
        </div>
        <div v-else class="space-y-2">
            <template v-for="notif in notifications" :key="notif.id">
                <router-link v-if="notif && notif.link" :to="notif.link"
                    class="bg-white p-4 rounded shadow flex justify-between items-start hover:underline">
                    <div class="flex-1">
                        <p :class="{ 'font-semibold': !notif.read }">{{ notif.message }}</p>
                        <span class="text-xs text-gray-500">{{ formatDate(notif.createdAt) }}</span>
                    </div>
                    <div class="flex items-center gap-2">
                        <button v-if="!notif.read" @click="markAsRead(notif.id)"
                            class="text-blue-500 text-sm hover:underline cursor-pointer">
                            Đánh dấu đã đọc
                        </button>
                        <button @click="confirmDelete(notif.id)"
                            class="text-gray-400 hover:text-red-600 transition-colors cursor-pointer"
                            title="Xóa thông báo">
                            <X />
                        </button>
                    </div>
                </router-link>

                <div v-else-if="notif" class="bg-white p-4 rounded shadow flex justify-between items-start">
                    <div class="flex-1">
                        <p :class="{ 'font-semibold': !notif.read }">{{ notif.message }}</p>
                        <span class="text-xs text-gray-500">{{ formatDate(notif.createdAt) }}</span>
                    </div>
                    <div class="flex items-center gap-2">
                        <button v-if="!notif.read" @click="markAsRead(notif.id)"
                            class="text-blue-500 text-sm hover:underline cursor-pointer">
                            Đánh dấu đã đọc
                        </button>
                        <button @click="confirmDelete(notif.id)"
                            class="text-gray-400 hover:text-red-600 transition-colors cursor-pointer"
                            title="Xóa thông báo">
                            <X />
                        </button>
                    </div>
                </div>
            </template>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import {
    getUserNotifications,
    markAsRead as apiMarkAsRead,
    markAllAsRead as markAllAsReadApi,
    deleteNotification,
    deleteReadNotifications,
} from "../services/notificationService";
import { authState } from "../composables/useAuth";
import { X } from "lucide-vue-next";
import { push } from "notivue";

const loading = ref(true);
const notifications = ref([]);

const loadNotifications = async () => {
    if (!authState.isAuthenticated.value) return;
    notifications.value = await getUserNotifications();
};

onMounted(async () => {
    await loadNotifications();
    loading.value = false;
});

const markAsRead = async (id) => {
    await apiMarkAsRead(id);
    await loadNotifications();
};

const markAllAsRead = async () => {
    try {
        await markAllAsReadApi();
        await loadNotifications();
    } catch (error) {
        push.error("Đánh dấu thất bại");
    }
};

const formatDate = (date) => {
    if (!date) return "";
    return new Date(date).toLocaleDateString("vi-VN");
};

const confirmDelete = (id) => {
    if (confirm("Bạn có chắc muốn xóa thông báo này?")) {
        handleDelete(id);
    }
};

const handleDelete = async (id) => {
    try {
        await deleteNotification(id);
        await loadNotifications();
    } catch (error) {
        push.error("Xóa thông báo thất bại");
    }
};

const confirmDeleteAllRead = () => {
    if (confirm("Bạn có chắc muốn xóa tất cả thông báo đã đọc?")) {
        handleDeleteAllRead();
    }
};

const handleDeleteAllRead = async () => {
    try {
        await deleteReadNotifications();
        await loadNotifications();
    } catch (error) {
        push.error("Xóa thông báo thất bại");
    }
};
</script>