"use strict";
document.getElementById("login-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const button = e.target.querySelector("button");
  button.disabled = true;
  const error = document.getElementById("error");
  error.textContent = "";
  try {
    const csrf = await fetch("/api/csrf").then((r) => r.json());
    const r = await fetch("/login", {
      method: "POST",
      headers: { [csrf.headerName]: csrf.token },
      body: new URLSearchParams(new FormData(e.target)),
    });
    if (!r.ok)
      throw new Error("Sai tài khoản, mật khẩu hoặc tài khoản đã bị khóa.");
    const me = await fetch("/api/admin/me");
    if (me.status === 403)
      throw new Error("Tài khoản này không có quyền Admin.");
    if (!me.ok) throw new Error("Không thể mở phiên quản trị.");
    location.href = "/";
  } catch (x) {
    error.textContent = x.message;
  } finally {
    button.disabled = false;
  }
});
