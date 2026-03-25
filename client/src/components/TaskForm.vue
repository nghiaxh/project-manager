<template>
    <form @submit.prevent="onSubmit" class="space-y-4">
        <div>
            <label class="label block text-sm font-medium">Tiêu đề</label>
            <input v-model="title" type="text" class="input w-full border rounded px-3 py-2"
                placeholder="Tiêu đề" />
            <span class="text-red-500 text-sm">{{ errors.title }}</span>
        </div>
        <div>
            <label class="label block text-sm font-medium">Mô tả</label>
            <input v-model="description" rows="3" class="input w-full border rounded px-3 py-2" placeholder="Mô tả" />
            <span class="text-red-500 text-sm">{{ errors.description }}</span>
        </div>
        <div>
            <label class="label block text-sm font-medium">Trạng thái công việc</label>
            <select v-model="status" class="select w-full border rounded px-3 py-2">
                <option value="TODO">Cần làm</option>
                <option value="IN_PROGRESS">Đang làm</option>
                <option value="IN_REVIEW">Chờ duyệt</option>
                <option value="DONE">Hoàn thành</option>
            </select>
            <span class="text-red-500 text-sm">{{ errors.status }}</span>
        </div>
        <div>
            <label class="label block text-sm font-medium">Thời hạn</label>
            <input v-model="deadline" type="date" class="input w-full border rounded px-3 py-2" />
            <span class="text-red-500 text-sm">{{ errors.deadline }}</span>
        </div>
        <div>
            <label class="label block text-sm font-medium">Người được giao việc</label>
            <select v-model="assigneeId" class="input w-full border rounded px-3 py-2">
                <option :value="null">Chưa gán</option>
                <option v-for="member in members" :key="member.userId" :value="member.userId">
                    {{ member.username }}
                </option>
            </select>
            <span class="text-red-500 text-sm">{{ errors.assigneeId }}</span>
        </div>
        <div class="flex justify-end space-x-2">
            <button type="button" @click="$emit('cancel')" class="btn btn-soft">Hủy</button>
            <button type="submit" class="btn btn-primary">Lưu</button>
        </div>
    </form>
</template>

<script setup>
import { useForm, useField } from 'vee-validate';
import { taskSchema } from '../validation/taskValidation';

const props = defineProps({
    initialData: Object,
    members: Array,
});

const emit = defineEmits(['submit', 'cancel']);

const { handleSubmit, errors } = useForm({
    validationSchema: taskSchema,
    initialValues: {
        title: props.initialData?.title || '',
        description: props.initialData?.description || '',
        status: props.initialData?.status || 'TODO',
        deadline: props.initialData?.deadline || '',
        assigneeId: props.initialData?.assigneeId || null,
    }
});

const { value: title } = useField('title');
const { value: description } = useField('description');
const { value: status } = useField('status');
const { value: deadline } = useField('deadline');
const { value: assigneeId } = useField('assigneeId');

const onSubmit = handleSubmit((values) => {
    emit('submit', values);
});
</script>