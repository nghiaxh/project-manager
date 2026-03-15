<template>
    <div v-if=" project " class="space-y-6">
        <div class="flex flex-wrap items-center justify-between gap-4">
            <div class="flex items-center gap-4">
                <button @click="router.push( '/projects' )" class="btn btn-soft">
                    Quay lại
                </button>
            </div>
            <div class="flex items-center gap-3">
                <button @click=" showMemberList " class="btn btn-soft">
                    Thành viên
                </button>
                <button @click="router.push( `/projects/${ projectId }/statistics` )" class="btn btn-primary">Thống
                    kê</button>
                <button @click="showAddTask = true" class="btn btn-primary">
                    Thêm công việc
                </button>
            </div>
        </div>
        <h2 class="text-2xl font-bold">{{ project.name }}</h2>
        <p class="text-gray-600">{{ project.description }}</p>

        <div class="bg-white p-4 rounded-lg shadow-sm space-y-3">
            <!-- Tìm kiếm -->
            <div class="flex items-center gap-2">
                <input v-model=" searchQuery " type="text" placeholder="Tìm kiếm task theo tiêu đề hoặc mô tả..."
                    class="input flex-1 outline-none" />
            </div>

            <div class="flex flex-wrap items-center gap-3">
                <div class="flex items-center gap-1">
                    <button @click="filterStatus = 'ALL'" class="btn btn-soft"
                        :class=" filterStatus === 'ALL' ? 'bg-blue-600 text-white' : 'bg-gray-100 hover:bg-gray-200' ">
                        Tất cả <span class="ml-1 text-xs opacity-75">({{ totalCount }})</span>
                    </button>
                    <button v-for=" status in statuses " :key=" status " @click="filterStatus = status"
                        class="btn btn-soft"
                        :class=" filterStatus === status ? statusActiveClass( status ) : 'bg-gray-100 hover:bg-gray-200' ">
                        {{ statusLabels[ status ] }} <span class="ml-1 text-xs opacity-75">({{ statusCounts[ status ]
                        }})</span>
                    </button>
                </div>

                <select v-model=" filterAssignee " class="select w-auto">
                    <option value="ALL">Tất cả người được gán</option>
                    <option v-for=" member in members " :key=" member.userId " :value=" member.userId ">
                        {{ member.username }}
                    </option>
                </select>

                <select v-model=" sortBy " class="select w-auto">
                    <option value="deadline_asc">Hạn chót (gần nhất)</option>
                    <option value="deadline_desc">Hạn chót (xa nhất)</option>
                    <option value="title_asc">Tiêu đề (A-Z)</option>
                    <option value="title_desc">Tiêu đề (Z-A)</option>
                    <option value="status">Trạng thái</option>
                </select>
            </div>
        </div>

        <div v-if=" loading " class="text-center py-10">Đang tải...</div>
        <div v-else-if=" filteredAndSortedTasks.length === 0 " class="text-center py-10 text-gray-500">
            Không tìm thấy công việc.
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <TaskCard v-for=" task in filteredAndSortedTasks " :key=" task.id " :task=" task "
                @click="goToTask( task.id )" @status-change=" handleStatusChange " />
        </div>

        <div v-if=" showAddTask "
            class="fixed inset-0 backdrop-blur-xs bg-opacity-50 flex items-center justify-center z-50">
            <div class="bg-white rounded-lg p-6 w-full max-w-md">
                <h3 class="text-lg font-bold mb-4">Thêm task mới</h3>
                <TaskForm :members=" members " @submit=" addTask " @cancel="showAddTask = false" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TaskCard from '../components/TaskCard.vue';
import TaskForm from '../components/TaskForm.vue';
import { getProject, getMembers as apiGetMembers } from '../services/projectService';
import { getTasksByProject, createTask, updateTask } from '../services/taskService';
import { push } from 'notivue';
import { authState } from '../composables/useAuth';
import { MoveLeft } from "lucide-vue-next";

const route = useRoute();
const router = useRouter();
const projectId = Number(route.params.id);

const project = ref(null);
const tasks = ref([]);
const members = ref([]);
const showAddTask = ref(false);
const loading = ref(true);

const searchQuery = ref('');
const filterStatus = ref('ALL');
const filterAssignee = ref('ALL');
const sortBy = ref('deadline_asc');

const statuses = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
const statusLabels = {
    TODO: 'Cần làm',
    IN_PROGRESS: 'Đang làm',
    IN_REVIEW: 'Chờ duyệt',
    DONE: 'Hoàn thành'
};

const statusCounts = computed(() => {
    const counts = { TODO: 0, IN_PROGRESS: 0, IN_REVIEW: 0, DONE: 0 };
    tasks.value.forEach(t => {
        if (counts.hasOwnProperty(t.status)) counts[t.status]++;
    });
    return counts;
});

const totalCount = computed(() => tasks.value.length);

const statusActiveClass = (status) => {
    const classes = {
        TODO: 'bg-yellow-500 text-white',
        IN_PROGRESS: 'bg-blue-500 text-white',
        IN_REVIEW: 'bg-purple-500 text-white',
        DONE: 'bg-green-500 text-white',
    };
    return classes[status] || 'bg-blue-600 text-white';
};

const filteredAndSortedTasks = computed(() => {
    let result = tasks.value;

    if (searchQuery.value.trim()) {
        const query = searchQuery.value.toLowerCase().trim();
        result = result.filter(t =>
            t.title.toLowerCase().includes(query) ||
            (t.description && t.description.toLowerCase().includes(query))
        );
    }

    if (filterStatus.value !== 'ALL') {
        result = result.filter(t => t.status === filterStatus.value);
    }

    if (filterAssignee.value === 'UNASSIGNED') {
        result = result.filter(t => !t.assigneeId);
    } else if (filterAssignee.value !== 'ALL') {
        const assigneeId = Number(filterAssignee.value);
        result = result.filter(t => t.assigneeId === assigneeId);
    }

    const [sortField, sortOrder] = sortBy.value.split('_');
    if (sortField === 'deadline') {
        result = [...result].sort((a, b) => {
            if (!a.deadline) return 1;
            if (!b.deadline) return -1;
            const dateA = new Date(a.deadline);
            const dateB = new Date(b.deadline);
            return sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
        });
    } else if (sortField === 'title') {
        result = [...result].sort((a, b) => {
            const titleA = a.title.toLowerCase();
            const titleB = b.title.toLowerCase();
            if (sortOrder === 'asc') {
                return titleA.localeCompare(titleB);
            } else {
                return titleB.localeCompare(titleA);
            }
        });
    } else if (sortBy.value === 'status') {
        const statusOrder = { TODO: 1, IN_PROGRESS: 2, IN_REVIEW: 3, DONE: 4 };
        result = [...result].sort((a, b) => statusOrder[a.status] - statusOrder[b.status]);
    }

    return result;
});

onMounted(async () => {
    if (isNaN(projectId)) {
        router.push('/projects');
        return;
    }
    await Promise.all([loadProject(), loadTasks(), loadMembers()]);
    loading.value = false;
});

const loadProject = async () => {
    if (!authState.isAuthenticated.value) return;
    try {
        project.value = await getProject(projectId);
    } catch (error) {
        push.error('Không thể tải dự án');
    }
};

const loadTasks = async () => {
    if (!authState.isAuthenticated.value) return;
    try {
        tasks.value = await getTasksByProject(projectId);
    } catch (error) {
        push.error('Không thể tải danh sách task');
    }
};

const loadMembers = async () => {
    if (!authState.isAuthenticated.value) return;
    try {
        members.value = await apiGetMembers(projectId);
    } catch (error) {
        push.error('Không thể tải thành viên');
    }
};

const addTask = async (formData) => {
    try {
        await createTask(projectId, formData);
        showAddTask.value = false;
        await loadTasks();
        push.success('Thêm task thành công');
    } catch (error) {
        push.error('Thêm task thất bại');
    }
};

const handleStatusChange = async ({ taskId, newStatus }) => {
    const task = tasks.value.find(t => t.id === taskId);
    if (!task) return;

    const oldStatus = task.status;
    task.status = newStatus;

    try {
        await updateTask(taskId, { ...task, status: newStatus });
        push.success('Cập nhật trạng thái thành công');
    } catch (error) {
        task.status = oldStatus;
        push.error('Cập nhật trạng thái thất bại');
        await loadTasks();
    }
};

const goToTask = (taskId) => {
    router.push(`/tasks/${taskId}`);
};

const showMemberList = () => {
    router.push(`/projects/${projectId}/members`);
};
</script>