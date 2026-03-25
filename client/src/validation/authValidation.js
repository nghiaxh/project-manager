import * as yup from 'yup';

export const loginSchema = yup.object({
    username: yup.string().required('Tên đăng nhập không được để trống').min(5, "Tên đăng nhập tối thiểu 5 kí tự"),
    password: yup.string().required('Mật khẩu không được để trống').min(5, "Mật khẩu tối thiểu là 5 kí tự")
});

export const registerSchema = yup.object({
    name: yup.string().required('Họ tên không được để trống').min(5, "Họ tên tối thiểu 5 kí tự"),
    username: yup.string().required('Tên đăng nhập không được để trống').min(5, "Tên đăng nhập tối thiểu 5 kí tự"),
    email: yup.string().email('Email không hợp lệ').required('Email không được để trống').min(5, "Email tối thiểu là 5 kí tự"),
    password: yup.string().min(5, 'Mật khẩu phải có ít nhất 5 ký tự').required('Mật khẩu không được để trống')
});