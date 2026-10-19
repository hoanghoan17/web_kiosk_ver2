async function refreshMaintenance() {
  try {
    const d = await api("system/status");
    $("maintenance-banner").hidden = !d.maintenance.enabled;
    $("maintenance-banner").textContent = "BẢO TRÌ · " + d.maintenance.message;
  } catch {}
}
function renderSystem(d) {
  cache.system = d;
  const enabled = d.backupEnabled && !d.busy;
  const backups = d.jobs.filter(
    (x) =>
      ["BACKUP", "SAFETY_BACKUP"].includes(x.kind) && x.status === "SUCCESS",
  );
  $("content").innerHTML =
    intro(
      "Quản Lý Hệ Thống",
      "Sao lưu, khôi phục dữ liệu và kiểm tra vận hành.",
      `<button class="secondary" data-refresh>Làm mới</button>`,
    ) +
    `<div class="system-grid">${panel("Bảo trì hệ thống", d.maintenance.enabled ? "Đang tạm dừng thao tác thay đổi dữ liệu" : "Đang mở hoạt động", `<div class="panel-body"><span class="pill ${d.maintenance.enabled ? "warn" : "good"}">${d.maintenance.enabled ? "Đang bảo trì" : "Hoạt động"}</span><p class="muted">Thông báo khi bảo trì: ${esc(d.maintenance.message)}</p><button class="${d.maintenance.enabled ? "secondary" : "primary"}" id="maintenance-toggle" ${d.busy ? "disabled" : ""}>${d.maintenance.enabled ? "Tắt bảo trì" : "Bật bảo trì"}</button></div>`)}${panel("Sao lưu cơ sở dữ liệu", esc(d.database), `<div class="panel-body"><p>Tạo bản sao lưu đầy đủ và kiểm tra tính đọc được của bản sao lưu.</p><button class="primary" id="backup-start" ${enabled ? "" : "disabled"}>Tạo bản sao lưu</button>${!d.backupEnabled ? '<p class="muted">Chưa bật cấu hình sao lưu SQL Server. Xem hướng dẫn thiết lập trong README.</p>' : ""}</div>`)}${panel("Kiểm tra và bảo trì dữ liệu", "Kiểm tra toàn vẹn, không tự sửa hoặc xóa dữ liệu", `<div class="panel-body"><p>Chạy kiểm tra toàn vẹn cơ sở dữ liệu khi cửa hàng đã bật bảo trì.</p><button class="secondary" id="checkdb-start" ${enabled && d.maintenance.enabled ? "" : "disabled"}>Kiểm tra toàn vẹn</button></div>`)}</div>` +
    panel(
      "Các bản sao lưu có thể khôi phục",
      "Thư mục bản sao lưu thuộc máy SQL Server; chỉ dùng các bản đã tạo thành công",
      table(
        ["Tạo lúc", "Người thực hiện", "Bản sao lưu", "Thao tác"],
        backups
          .map(
            (j) =>
              `<tr><td>${time(j.finished)}</td><td>${esc(j.actor)}</td><td><strong>${esc(j.filename)}</strong></td><td><div class="actions"><button class="secondary small" data-verify="${j.id}" ${enabled ? "" : "disabled"}>Kiểm tra bản sao</button><button class="danger small" data-restore="${j.id}" ${enabled && d.maintenance.enabled ? "" : "disabled"}>Khôi phục</button></div></td></tr>`,
          )
          .join(""),
      ),
    ) +
    panel(
      "Lịch sử tác vụ hệ thống",
      "Nhật ký giữ riêng với database được khôi phục",
      table(
        ["Tác vụ", "Trạng thái", "Người thực hiện", "Bắt đầu", "Kết quả"],
        d.jobs
          .slice(0, 50)
          .map(
            (j) =>
              `<tr><td>${esc(j.kind)}</td><td><span class="pill ${j.status === "SUCCESS" ? "good" : ["FAILED", "INTERRUPTED"].includes(j.status) ? "bad" : "warn"}">${esc(j.status)}</span></td><td>${esc(j.actor)}</td><td>${time(j.started)}</td><td>${esc(j.message)}</td></tr>`,
          )
          .join(""),
      ),
    );
  $("maintenance-toggle").onclick = () => systemDialog("MAINTENANCE");
  $("backup-start").onclick = () => systemDialog("BACKUP");
  $("checkdb-start").onclick = () => systemDialog("CHECKDB");
}
function systemDialog(kind, backupId = "") {
  const d = cache.system;
  const title = {
    MAINTENANCE: d.maintenance.enabled ? "Tắt bảo trì" : "Bật bảo trì",
    BACKUP: "Tạo bản sao lưu",
    VERIFY: "Kiểm tra bản sao lưu",
    RESTORE: "Khôi phục cơ sở dữ liệu",
    CHECKDB: "Kiểm tra toàn vẹn",
  }[kind];
  const restore = kind === "RESTORE";
  openModal(
    title,
    `<form id="system-form">${restore ? '<div class="notice danger">Khôi phục sẽ thay thế dữ liệu hiện tại bằng bản đã chọn. Chương trình tạo bản an toàn trước khi thực hiện. Sau khôi phục, cần đăng nhập lại và kiểm tra trước khi tắt bảo trì.</div>' : ""}<div class="form-grid">${kind === "MAINTENANCE" ? area("message", "Thông báo bảo trì", d.maintenance.message) : ""}${restore ? field("confirmation", "Nhập chính xác KHOI PHUC " + d.database, "", "text", "required") : ""}${field("password", "Mật khẩu Admin hiện tại", "", "password", 'required autocomplete="current-password" maxlength="72"')}</div><p class="form-error" id="system-error"></p><div class="form-footer"><button type="submit" class="${restore ? "danger" : "primary"}">${title}</button></div></form>`,
  );
  $("system-form").onsubmit = async (e) => {
    e.preventDefault();
    const button = e.target.querySelector("button");
    button.disabled = true;
    const f = new FormData(e.target);
    try {
      if (kind === "MAINTENANCE")
        await api("system/maintenance", {
          method: "POST",
          body: {
            enabled: !d.maintenance.enabled,
            message: f.get("message"),
            password: f.get("password"),
          },
        });
      else
        await api("system/jobs", {
          method: "POST",
          body: {
            kind,
            backupId,
            confirmation: f.get("confirmation") || "",
            password: f.get("password"),
          },
        });
      $("modal").close();
      toast("Đã tiếp nhận tác vụ");
      await refreshMaintenance();
      await navigate("system", true);
    } catch (error) {
      $("system-error").textContent = error.message;
      button.disabled = false;
    }
  };
}
document.addEventListener("click", (e) => {
  const b = e.target.closest("button");
  if (b?.dataset.verify) systemDialog("VERIFY", b.dataset.verify);
  if (b?.dataset.restore) systemDialog("RESTORE", b.dataset.restore);
});
setInterval(() => {
  if (page === "system" && !$("modal").open) navigate("system", true);
}, 3000);
