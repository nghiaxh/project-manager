import * as yup from 'yup';

export const loginSchema = yup.object({
    username: yup.string().required('Tên đăng nhập không được để trống'),
    password: yup.string().required('Mật khẩu không được để trống')
});

export const registerSchema = yup.object({
    name: yup.string().required('Họ tên không được để trống'),
    username: yup.string().required('Tên đăng nhập không được để trống'),
    email: yup.string().email('Email không hợp lệ').required('Email không được để trống'),
    password: yup.string().min(6, 'Mật khẩu phải có ít nhất 6 ký tự').required('Mật khẩu không được để trống')
});