<template>
    <form @submit.prevent=" handleSubmit " class="space-y-4">
        <div>
            <label class="block text-sm font-medium">Tiêu đề</label>
            <input v-model=" form.title " type="text" required class="w-full border rounded px-3 py-2" />
        </div>
        <div>
            <label class="block text-sm font-medium">Mô tả</label>
            <textarea v-model=" form.description " rows="3" class="w-full border rounded px-3 py-2"></textarea>
        </div>
        <div>
            <label class="block text-sm font-medium">Trạng thái</label>
            <select v-model=" form.status " class="w-full border rounded px-3 py-2">
                <option value="TODO">Cần làm</option>
                <option value="IN_PROGRESS">Đang làm</option>
                <option value="DONE">Hoàn thành</option>
                <option value="CANCELLED">Đã hủy</option>
            </select>
        </div>
        <div>
            <label class="block text-sm font-medium">Hạn chót</label>
            <input v-model=" form.deadline " type="date" class="w-full border rounded px-3 py-2" />
        </div>
        <div>
            <label class="block text-sm font-medium">Người được gán</label>
            <select v-model=" form.assigneeId " class="w-full border rounded px-3 py-2">
                <option :value=" null ">Chưa gán</option>
                <option v-for=" member in members " :key=" member.userId " :value=" member.userId ">
                    {{ member.username }}
                </option>
            </select>
        </div>
        <div class="flex justify-end space-x-2">
            <button type="button" @click="$emit( 'cancel' )"
                class="px-4 py-2 border rounded hover:bg-gray-100">Hủy</button>
            <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">Lưu</button>
        </div>
    </form>
</template>

<script setup>
import { reactive } from 'vue';

const props = defineProps({
    initialData: Object,
    members: Array
});

const emit = defineEmits(['submit', 'cancel']);

const form = reactive({
    title: props.initialData?.title || '',
    description: props.initialData?.description || '',
    status: props.initialData?.status || 'TODO',
    deadline: props.initialData?.deadline || '',
    assigneeId: props.initialData?.assigneeId || null
});

const handleSubmit = () => {
    emit('submit', { ...form });
};
</script>