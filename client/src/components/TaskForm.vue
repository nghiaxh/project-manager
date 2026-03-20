<template>
    <form @submit.prevent=" handleSubmit " class="space-y-4">
        <div>
            <label class="label block text-sm font-medium">Tiêu đề</label>
            <input v-model=" form.title " type="text" required class="input w-full border rounded px-3 py-2"
                placeholder="Tiêu đề" />
        </div>
        <div>
            <label class="label block text-sm font-medium">Mô tả</label>
            <input v-model=" form.description " rows="3" class="input w-full border rounded px-3 py-2"
                placeholder="Mô tả" />
        </div>
        <div>
            <label class="label block text-sm font-medium">Trạng thái công việc</label>
            <select v-model=" form.status " class="select w-full border rounded px-3 py-2">
                <option value="TODO">Cần làm</option>
                <option value="IN_PROGRESS">Đang làm</option>
                <option value="IN_REVIEW">Chờ duyệt</option>
                <option value="DONE">Hoàn thành</option>
            </select>
        </div>
        <div>
            <label class="label block text-sm font-medium">Thời hạn</label>
            <input v-model=" form.deadline " type="date" class="input w-full border rounded px-3 py-2" />
        </div>
        <div>
            <label class="label block text-sm font-medium">Người được giao việc</label>
            <select v-model=" form.assigneeId " class="input w-full border rounded px-3 py-2">
                <option :value=" null ">Chưa gán</option>
                <option v-for=" member in members " :key=" member.userId " :value=" member.userId ">
                    {{ member.username }}
                </option>
            </select>
        </div>
        <div class="flex justify-end space-x-2">
            <button type="button" @click="$emit( 'cancel' )" class="btn btn-soft">Hủy</button>
            <button type="submit" class="btn btn-primary">Lưu</button>
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