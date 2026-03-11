<template>
    <div v-if=" project " class="space-y-6">
        <div class="flex justify-between items-center">
            <button @click="router.push( '/projects' )"
                class="text-blue-600 hover:underline text-lg cursor-pointer">Quay
                lại</button>
            <h2 class="text-2xl font-bold">{{ project.name }}</h2>
            <div class="space-x-2">
                <button @click=" showMemberList " class="bg-gray-200 px-4 py-2 rounded hover:bg-gray-300">Thành
                    viên</button>
                <button @click="showAddTask = true" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">+
                    Thêm task</button>
            </div>
        </div>
        <p class="text-gray-600">{{ project.description }}</p>

        <!-- Kanban board -->
        <div class="flex gap-4 overflow-x-auto pb-4">
            <div v-for=" status in statuses " :key=" status " class="bg-gray-200 rounded-lg p-4 min-w-75"
                :data-status=" status ">
                <h3 class="font-semibold mb-3">{{ statusLabels[ status ] }}</h3>
                <draggable :list=" tasksByStatus[ status ] " group="tasks" itemKey="id" @end=" onDragEnd "
                    class="space-y-3 min-h-50">
                    <template #item=" { element } ">
                        <TaskCard :task=" element " @click="goToTask( element.id )" />
                    </template>
                </draggable>
            </div>
        </div>

        <!-- Modal thêm task -->
        <div v-if=" showAddTask " class="fixed inset-0 backdrop-blur-xs bg-opacity-50 flex items-center justify-center">
            <div class="bg-white rounded-lg p-6 w-96">
                <h3 class="text-lg font-bold mb-4">Thêm task mới</h3>
                <TaskForm :members=" members " @submit=" addTask " @cancel="showAddTask = false" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import draggable from 'vuedraggable';
import TaskCard from '../components/TaskCard.vue';
import TaskForm from '../components/TaskForm.vue';
import { getProject, getMembers as apiGetMembers } from '../services/projectService';
import { getTasksByProject, createTask, updateTask } from '../services/taskService';
import { push } from 'notivue';
import { authState } from '../composables/useAuth';

const route = useRoute();
const router = useRouter();
const projectId = Number(route.params.id);

const project = ref(null);
const tasks = ref([]);
const members = ref([]);
const showAddTask = ref(false);

const statuses = ['TODO', 'IN_PROGRESS', 'DONE', 'CANCELLED'];
const statusLabels = {
    TODO: 'Cần làm',
    IN_PROGRESS: 'Đang làm',
    DONE: 'Hoàn thành',
    CANCELLED: 'Đã hủy'
};

const tasksByStatus = computed(() => {
    const grouped = {};
    statuses.forEach(s => grouped[s] = []);
    tasks.value.forEach(t => {
        if (grouped[t.status]) grouped[t.status].push(t);
    });
    return grouped;
});

onMounted(async () => {
    await loadProject();
    await loadTasks();
    await loadMembers();
});

const loadProject = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    project.value = await getProject(projectId);
};

const loadTasks = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    tasks.value = await getTasksByProject(projectId);
};

const loadMembers = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    members.value = await apiGetMembers(projectId);
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

const onDragEnd = async (evt) => {
    const task = evt.item.__draggable_context.element;
    if (!task) return;
    const newStatus = evt.to.getAttribute('data-status');
    if (!newStatus || task.status === newStatus) return;

    const oldStatus = task.status;
    task.status = newStatus;

    try {
        await updateTask(task.id, { ...task, status: newStatus });
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