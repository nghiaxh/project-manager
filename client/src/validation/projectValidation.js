import * as yup from 'yup';

export const projectSchema = yup.object({
    name: yup.string().required('Tên dự án không được để trống'),
    description: yup.string().nullable(),
    deadline: yup.date().min(new Date(), 'Deadline phải lớn hơn ngày hiện tại').typeError('Deadline không hợp lệ')
});