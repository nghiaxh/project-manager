<template>
  <div
    :data-task-id="task.id"
    class="bg-white p-3 rounded shadow mb-2 cursor-pointer hover:shadow-md transition"
    :class="{ 'border-l-4 border-green-500': task.status === 'DONE' }"
  >
    <div class="flex justify-between items-start">
      <h4 class="font-medium">{{ task.title }}</h4>

      <select
        v-model="localStatus"
        @click.stop
        @change="onStatusChange"
        class="text-sm border rounded px-2 py-1 bg-gray-50 focus:outline-none cursor-pointer"
      >
        <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>
    </div>

    <p class="text-sm text-gray-600 truncate mt-1">{{ task.description }}</p>

    <div class="flex justify-between items-center mt-2 text-xs text-gray-500">
      <span>{{ formattedDeadline }}</span>
      <span v-if="assignee" class="bg-blue-100 text-blue-800 px-2 py-1 rounded">
        {{ assignee.name }}
      </span>
    </div>

    <div class="mt-2 flex justify-end">
      <router-link
        :to="`/tasks/${task.id}`"
        class="text-blue-500 hover:underline text-sm"
        @click.stop
      >
        Xem chi tiết
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getUserById } from '../services/userService';

const props = defineProps({
  task: Object,
});

const emit = defineEmits(['status-change']);

const assignee = ref(null);

const statusOptions = [
  { value: 'TODO', label: 'Cần làm' },
  { value: 'IN_PROGRESS', label: 'Đang làm' },
  { value: 'IN_REVIEW', label: 'Chờ duyệt' },
  { value: 'DONE', label: 'Hoàn thành' },
];

const localStatus = ref(props.task.status);

const formattedDeadline = computed(() => {
  if (!props.task.deadline) return 'Chưa có hạn';
  return new Date(props.task.deadline).toLocaleDateString('vi-VN');
});

const onStatusChange = () => {
  if (localStatus.value !== props.task.status) {
    emit('status-change', {
      taskId: props.task.id,
      newStatus: localStatus.value,
    });
  }
};

onMounted(async () => {
  if (props.task.assigneeId) {
    try {
      assignee.value = await getUserById(props.task.assigneeId);
    } catch (error) {
      console.error('Không thể tải thông tin người được gán');
    }
  }
});
</script>