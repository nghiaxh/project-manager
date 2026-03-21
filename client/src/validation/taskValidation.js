import * as yup from 'yup';

export const taskSchema = yup.object({
    title: yup.string().required('Tiêu đề không được để trống'),
    description: yup.string().nullable(),
    deadline: yup.date().nullable(),
    assigneeId: yup.number().nullable()
});