import * as yup from "yup";

export const loginSchema = yup.object({
  username: yup
    .string()
    .required("Tên đăng nhập không được để trống")
    .min(6, "Tên đăng nhập tối thiểu 6 kí tự"),
  password: yup
    .string()
    .required("Mật khẩu không được để trống")
    .min(6, "Mật khẩu tối thiểu là 6 kí tự"),
});

export const registerSchema = yup.object({
  name: yup
    .string()
    .required("Họ tên không được để trống")
    .min(6, "Họ tên tối thiểu 6 kí tự"),
  username: yup
    .string()
    .required("Tên đăng nhập không được để trống")
    .min(6, "Tên đăng nhập tối thiểu 6 kí tự"),
  email: yup
    .string()
    .email("Email không hợp lệ")
    .required("Email không được để trống")
    .min(6, "Email tối thiểu là 6 kí tự"),
  password: yup
    .string()
    .min(6, "Mật khẩu phải có ít nhất 6 ký tự")
    .required("Mật khẩu không được để trống"),
});
