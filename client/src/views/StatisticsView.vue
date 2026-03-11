<template>
    <div v-if=" loading ">Đang tải...</div>
    <div v-else>
        <h2 class="text-2xl font-semibold mb-6">Thống kê dự án</h2>
        <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
            <div class="bg-white p-4 rounded shadow text-center">
                <div class="text-3xl font-bold">{{ stats.total }}</div>
                <div class="text-gray-600">Tổng số task</div>
            </div>
            <div class="bg-white p-4 rounded shadow text-center">
                <div class="text-3xl font-bold text-yellow-500">{{ stats.todo }}</div>
                <div class="text-gray-600">Cần làm</div>
            </div>
            <div class="bg-white p-4 rounded shadow text-center">
                <div class="text-3xl font-bold text-blue-500">{{ stats.inProgress }}</div>
                <div class="text-gray-600">Đang làm</div>
            </div>
            <div class="bg-white p-4 rounded shadow text-center">
                <div class="text-3xl font-bold text-green-500">{{ stats.done }}</div>
                <div class="text-gray-600">Hoàn thành</div>
            </div>
            <div class="bg-white p-4 rounded shadow text-center">
                <div class="text-3xl font-bold text-gray-500">{{ stats.cancelled }}</div>
                <div class="text-gray-600">Đã hủy</div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getProjectStatistics } from '../services/statisticsService';

const route = useRoute();
const projectId = route.params.id;
const loading = ref(true);
const stats = ref({});

onMounted(async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    try {
        stats.value = await getProjectStatistics(projectId);
    } catch (error) {
        alert('Không thể tải thống kê');
    } finally {
        loading.value = false;
    }
});
</script>