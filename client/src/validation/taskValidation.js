import * as yup from 'yup';

export const taskSchema = yup.object({
    title: yup.string().required('Tiêu đề không được để trống'),
    description: yup.string().nullable(),
    status: yup.string().oneOf(['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE']).required(),
    deadline: yup.date().min(new Date(), 'Deadline phải lớn hơn ngày hiện tại').typeError('Deadline không hợp lệ'),
    assigneeId: yup.number().nullable()
});