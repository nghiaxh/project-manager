import * as yup from 'yup';

export const userUpdateSchema = yup.object({
    name: yup.string().required('Họ tên không được để trống'),
    email: yup.string().email('Email không hợp lệ').required('Email không được để trống')
});