<template>
    <div class="space-y-6">
        <!-- Header với nút quay lại và tiêu đề -->
        <div class="flex items-center justify-between">
            <button @click="router.push( `/projects/${ projectId }` )"
                class="btn btn-soft">
                Quay lại dự án
            </button>
            <div class="w-24"></div> <!-- Cân bằng layout -->
        </div>
        <h2 class="text-2xl font-semibold text-gray-800">Thống kê dự án</h2>

        <!-- Loading state -->
        <div v-if=" loading " class="flex justify-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>

        <!-- Error state -->
        <div v-else-if=" error " class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {{ error }}
        </div>

        <!-- Nội dung thống kê -->
        <div v-else class="space-y-8">
            <!-- Cards tổng quan -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
                <!-- Tổng số task -->
                <div class="bg-blue-700 text-white rounded-xl shadow-lg p-5">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-sm opacity-90">Tổng số task</p>
                            <p class="text-3xl font-bold">{{ stats.total || 0 }}</p>
                        </div>
                        <div class="bg-white/20 p-3 rounded-full">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2">
                                </path>
                            </svg>
                        </div>
                    </div>
                </div>

                <!-- Cần làm -->
                <div class="bg-yellow-500 text-white rounded-xl shadow-lg p-5">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-sm opacity-90">Cần làm</p>
                            <p class="text-3xl font-bold">{{ stats.todo || 0 }}</p>
                            <p class="text-xs mt-1 opacity-80">{{ percentage( stats.todo ) }}%</p>
                        </div>
                        <div class="bg-white/20 p-3 rounded-full">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                            </svg>
                        </div>
                    </div>
                </div>

                <!-- Đang làm -->
                <div class="bg-blue-500 text-white rounded-xl shadow-lg p-5">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-sm opacity-90">Đang làm</p>
                            <p class="text-3xl font-bold">{{ stats.inProgress || 0 }}</p>
                            <p class="text-xs mt-1 opacity-80">{{ percentage( stats.inProgress ) }}%</p>
                        </div>
                        <div class="bg-white/20 p-3 rounded-full">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                            </svg>
                        </div>
                    </div>
                </div>

                <!-- Chờ duyệt -->
                <div class="bg-purple-500 text-white rounded-xl shadow-lg p-5">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-sm opacity-90">Chờ duyệt</p>
                            <p class="text-3xl font-bold">{{ stats.inReview || 0 }}</p>
                            <p class="text-xs mt-1 opacity-80">{{ percentage( stats.inReview ) }}%</p>
                        </div>
                        <div class="bg-white/20 p-3 rounded-full">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                            </svg>
                        </div>
                    </div>
                </div>

                <!-- Hoàn thành -->
                <div class="bg-green-500 text-white rounded-xl shadow-lg p-5">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-sm opacity-90">Hoàn thành</p>
                            <p class="text-3xl font-bold">{{ stats.done || 0 }}</p>
                            <p class="text-xs mt-1 opacity-80">{{ percentage( stats.done ) }}%</p>
                        </div>
                        <div class="bg-white/20 p-3 rounded-full">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M5 13l4 4L19 7"></path>
                            </svg>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Biểu đồ tròn đơn giản (dùng CSS) -->
            <div class="bg-white rounded-xl shadow-lg p-6">
                <h3 class="text-lg font-semibold mb-4">Phân bố trạng thái</h3>
                <div class="flex flex-col md:flex-row items-center justify-around gap-8">
                    <!-- Doughnut chart với conic-gradient -->
                    <div class="relative w-48 h-48">
                        <div class="absolute inset-0 rounded-full" :style=" conicGradientStyle "></div>
                        <div class="absolute inset-2 bg-white rounded-full flex items-center justify-center">
                            <span class="text-2xl font-bold">{{ stats.total || 0 }}</span>
                        </div>
                    </div>

                    <!-- Chú thích -->
                    <div class="space-y-2">
                        <div v-for=" item in chartLegend " :key=" item.label " class="flex items-center gap-2">
                            <span class="w-3 h-3 rounded-full" :style=" { backgroundColor: item.color } "></span>
                            <span class="text-sm">{{ item.label }}: {{ item.value }} ({{ percentage( item.value )
                                }}%)</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getProjectStatistics } from '../services/statisticsService';
import { authState } from "../composables/useAuth";

const route = useRoute();
const router = useRouter();
const projectId = route.params.id;
const loading = ref(true);
const error = ref(null);
const stats = ref({
    total: 0,
    todo: 0,
    inProgress: 0,
    inReview: 0,
    done: 0
});

// Tính phần trăm
const percentage = (value) => {
    if (!stats.value.total) return 0;
    return Math.round((value / stats.value.total) * 100);
};

// Dữ liệu cho biểu đồ tròn
const chartLegend = computed(() => [
    { label: 'Cần làm', value: stats.value.todo, color: '#EAB308' },
    { label: 'Đang làm', value: stats.value.inProgress, color: '#3B82F6' },
    { label: 'Chờ duyệt', value: stats.value.inReview, color: '#8B5CF6' },
    { label: 'Hoàn thành', value: stats.value.done, color: '#22C55E' }
]);

// Tạo conic-gradient dựa trên tỷ lệ
const conicGradientStyle = computed(() => {
    const total = stats.value.total || 1;
    const todo = (stats.value.todo / total) * 100;
    const inProgress = (stats.value.inProgress / total) * 100;
    const inReview = (stats.value.inReview / total) * 100;
    const done = (stats.value.done / total) * 100;

    let gradient = `conic-gradient(
    #EAB308 0% ${todo}%,
    #3B82F6 ${todo}% ${todo + inProgress}%,
    #8B5CF6 ${todo + inProgress}% ${todo + inProgress + inReview}%,
    #22C55E ${todo + inProgress + inReview}% 100%
  )`;
    return { background: gradient };
});

onMounted(async () => {
    if (!authState.isAuthenticated.value) {
        error.value = 'Bạn cần đăng nhập để xem thống kê !';
        loading.value = false;
        return;
    }
    try {
        stats.value = await getProjectStatistics(projectId);
    } catch (err) {
        error.value = 'Không thể tải thống kê. Vui lòng thử lại sau !';
    } finally {
        loading.value = false;
    }
});
</script>