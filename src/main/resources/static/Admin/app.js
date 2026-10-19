"use strict";
const $ = (id) => document.getElementById(id),
  esc = (x) =>
    String(x ?? "").replace(
      /[&<>"']/g,
      (c) =>
        ({
          "&": "&amp;",
          "<": "&lt;",
          ">": "&gt;",
          '"': "&quot;",
          "'": "&#39;",
        })[c],
    );
const money = (x) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(Number(x || 0));
function parseDate(x) {
  const value = String(x);
  return new Date(
    /[zZ]$|[+-]\d{2}:?\d{2}$/.test(value)
      ? value
      : value.length === 10
        ? value + "T00:00:00+07:00"
        : value + "+07:00",
  );
}
const time = (x) =>
  x
    ? parseDate(x).toLocaleString("vi-VN", { timeZone: "Asia/Ho_Chi_Minh" })
    : "—";
const on = (x) => x === true || x === 1;
const STATUS = {
  CHO_THANH_TOAN: "Chờ thanh toán",
  DA_THANH_TOAN: "Đã thanh toán",
  CHO_CHE_BIEN: "Chờ chế biến",
  DANG_CHE_BIEN: "Đang chế biến",
  HOAN_THANH: "Chờ giao món",
  DA_GIAO: "Đã giao món",
  DA_HUY: "Đã hủy",
};
const PAY = {
  CHO_THANH_TOAN: "Chưa trả tiền",
  THANH_CONG: "Đã trả tiền",
  THAT_BAI: "Thanh toán lỗi",
  HOAN_TIEN: "Đã hoàn tiền",
};
const NEXT = {
  CHO_THANH_TOAN: ["DA_HUY", "Hủy đơn"],
  DA_THANH_TOAN: ["CHO_CHE_BIEN", "Chuyển vào bếp"],
  CHO_CHE_BIEN: ["DANG_CHE_BIEN", "Bắt đầu chế biến"],
  DANG_CHE_BIEN: ["HOAN_THANH", "Hoàn tất chế biến"],
  HOAN_THANH: ["DA_GIAO", "Xác nhận giao món"],
};
const pages = {
  system: ["⚙", "Quản lý hệ thống"],
  dashboard: ["◫", "Dashboard Tổng Quan"],
  orders: ["▤", "Đơn hàng"],
  kitchen: ["◷", "Trạng thái Bếp (Live)"],
  categories: ["▦", "Danh mục món"],
  dishes: ["◉", "Quản lý Món ăn"],
  combos: ["▣", "Quản lý Combo"],
  promotions: ["◇", "Khuyến mãi / Voucher"],
  users: ["♙", "Tài khoản"],
  reports: ["▥", "Báo cáo"],
  audit: ["≡", "Nhật ký thao tác"],
};
const ids = {
  categories: "MaDanhMuc",
  dishes: "MaMonAn",
  combos: "MaCombo",
  promotions: "MaKhuyenMai",
  users: "MaTaiKhoan",
};
const names = {
  categories: "TenDanhMuc",
  dishes: "TenMon",
  combos: "TenCombo",
  promotions: "TenKhuyenMai",
  users: "HoTen",
};
let csrf,
  me,
  page = document.body.dataset.page || "dashboard",
  cache = {},
  rows = [],
  reportFrom,
  reportTo,
  loadVersion = 0,
  ws,
  retryTimer;
function toast(message, error = false) {
  $("toast").textContent = message;
  $("toast").className = error ? "error" : "";
  $("toast").hidden = false;
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => ($("toast").hidden = true), 5000);
}
async function api(path, options = {}) {
  const headers = { ...(options.headers || {}) };
  if (options.body) {
    headers["Content-Type"] = "application/json";
    options.body = JSON.stringify(options.body);
  }
  if (options.method && options.method !== "GET")
    headers[csrf.headerName] = csrf.token;
  const r = await fetch("/api/admin/" + path, { ...options, headers });
  if (r.status === 401) {
    location.href = "/login.html";
    throw new Error("Phiên đã hết hạn");
  }
  if (!r.ok) {
    let e;
    try {
      e = await r.json();
    } catch {}
    throw new Error(e?.message || `Yêu cầu thất bại (${r.status}).`);
  }
  return r.status === 204 ? null : r.json();
}
function badge(value, payment = false) {
  const good = ["THANH_CONG", "DA_GIAO"],
    bad = ["DA_HUY", "THAT_BAI", "HOAN_TIEN"],
    blue = ["DANG_CHE_BIEN", "DA_THANH_TOAN"];
  return `<span class="pill ${good.includes(value) ? "good" : bad.includes(value) ? "bad" : blue.includes(value) ? "blue" : "warn"}">${esc((payment ? PAY : STATUS)[value] || value || "Chưa thanh toán")}</span>`;
}
function activeBadge(value) {
  return `<span class="pill ${on(value) ? "good" : ""}">${on(value) ? "Đang sử dụng" : "Ngừng sử dụng"}</span>`;
}
function source(s) {
  return `<span class="source ${s === "KIOSK" ? "kiosk" : ""}">${s === "KIOSK" ? "KIOSK" : "QUẦY"}</span>`;
}
function intro(title, desc, action = "") {
  return `<div class="intro"><div><h1>${title}</h1><p>${desc}</p></div>${action}</div>`;
}
function panel(title, desc, body, action = "") {
  return `<section class="panel"><div class="panel-head"><div><h2>${title}</h2>${desc ? `<p>${desc}</p>` : ""}</div>${action}</div>${body}</section>`;
}
function table(headers, body) {
  return `<div class="table-wrap"><table><thead><tr>${headers.map((x) => `<th>${x}</th>`).join("")}</tr></thead><tbody>${body || `<tr><td colspan="${headers.length}" class="empty">Chưa có dữ liệu phù hợp</td></tr>`}</tbody></table></div>`;
}
function metrics(report, queue, ready) {
  const s = report.summary;
  return `<div class="metrics"><div class="metric"><small>DOANH THU ĐÃ THU</small><strong>${money(s.DoanhThu)}</strong><span>Theo giao dịch thanh toán thành công</span></div><div class="metric"><small>ĐƠN ĐÃ THANH TOÁN</small><strong>${s.SoDon}</strong><span>Kiosk và quầy thu ngân</span></div><div class="metric"><small>${queue === undefined ? "GIÁ TRỊ TRUNG BÌNH" : "ĐƠN ĐANG CHẾ BIẾN / CHỜ BẾP"}</small><strong>${queue ?? money(Number(s.DoanhThu) / (Number(s.SoDon) || 1))}</strong><span>${queue === undefined ? "Doanh thu / số đơn" : "Bao gồm đơn đã trả tiền chờ vào bếp"}</span></div><div class="metric"><small>${ready === undefined ? "TỔNG GIẢM GIÁ" : "HOÀN TẤT, CHỜ GIAO"}</small><strong>${ready ?? money(s.TienGiam)}</strong><span>${ready === undefined ? "Giảm giá trên đơn thanh toán thành công" : "Chưa xác nhận giao cho khách"}</span></div></div>`;
}
function orderRows(list) {
  return list
    .map(
      (o) =>
        `<tr><td><strong>#${o.MaDonHang}</strong><small>${time(o.ThoiGianDat)}</small></td><td>${source(o.NguonDon)}</td><td>${badge(o.TrangThaiThanhToan, true)}</td><td>${badge(o.TrangThaiDon)}</td><td><strong>${money(o.ThanhTien)}</strong></td><td><button class="secondary small" data-detail="${o.MaDonHang}">Chi tiết ↗</button></td></tr>`,
    )
    .join("");
}
function bars(items, label, value, format = money) {
  const max = Math.max(1, ...items.map((x) => Number(x[value])));
  return `<div class="panel-body bars">${items.length ? items.map((x, i) => `<div><div class="bar-label"><span>${esc(x[label])}</span><strong>${format(x[value])}</strong></div><div class="bar-track"><div class="bar-fill ${i % 2 ? "light" : ""}" style="width:${(Number(x[value]) / max) * 100}%"></div></div></div>`).join("") : '<p class="empty">Chưa có giao dịch thành công</p>'}</div>`;
}
async function navigate(next, quiet = false) {
  page = next;
  if (!quiet && location.pathname !== "/admin/" + page)
    history.pushState({ page }, "", "/admin/" + page);
  const version = ++loadVersion;
  const filters = quiet
    ? Object.fromEntries(
        [
          "search",
          "source-filter",
          "order-filter",
          "pay-filter",
          "active-filter",
          "category-filter",
        ].map((id) => [id, $(id)?.value || ""]),
      )
    : {};
  $("title").textContent = pages[page][1];
  document
    .querySelectorAll("nav [data-page]")
    .forEach((b) => b.classList.toggle("active", b.dataset.page === page));
  if (!quiet) $("content").innerHTML = '<p class="empty">Đang tải dữ liệu…</p>';
  try {
    if (page === "dashboard") {
      const date = cache.dashboardDate || localDate();
      const [d, o] = await Promise.all([
        api("dashboard?date=" + date),
        api("orders"),
      ]);
      if (version !== loadVersion) return;
      cache.orders = o;
      $("content").innerHTML =
        `<div class="metrics reference-metrics"><div class="metric"><small>DOANH THU TRONG NGÀY <span class="metric-icon green">$</span></small><strong>${money(d.summary.DoanhThu)}</strong><span>${d.summary.SoDon} đơn thanh toán thành công</span></div><div class="metric"><small>ĐƠN HOÀN TẤT <span class="metric-icon blue">${svg("orders")}</span></small><strong>${d.completed} Đơn</strong><span>Bếp hoàn tất / đã giao trong ngày thanh toán</span></div><div class="metric"><small>MÓN ĐANG KINH DOANH <span class="metric-icon amber">${svg("dishes")}</span></small><strong>${d.activeDishes} Món</strong><span>${d.categoryCount} danh mục</span></div><div class="metric"><small>ĐƠN CHỜ / ĐANG LÀM <span class="metric-icon red">${svg("kitchen")}</span></small><strong class="text-red">${d.queue} Đơn</strong><span>${d.ready} đơn hoàn tất chờ giao</span></div></div>
  <div class="dashboard-columns">${panel("Biểu Đồ Doanh Thu Trong Ngày", "Thống kê theo giờ thanh toán", hourlyChart(d.hourly), `<input type="date" id="dashboard-date" value="${date}" aria-label="Ngày doanh thu">`)}${panel(
    "Cảnh Báo Cửa Hàng",
    "",
    `<div class="panel-body store-alerts">${
      d.stockAlerts.length
        ? d.stockAlerts
            .slice(0, 3)
            .map(
              (x) =>
                `<div class="store-alert amber"><strong>${esc(x.TenNguyenLieu)}</strong><span>Còn ${esc(x.TonKhoHienTai)} ${esc(x.DonViTinh)} · Ngưỡng ${esc(x.NguongCanhBao)}</span></div>`,
            )
            .join("")
        : '<div class="store-alert blue"><strong>Nguyên liệu trong ngưỡng</strong><span>Chưa có nguyên liệu hoạt động chạm ngưỡng cảnh báo.</span></div>'
    }<div class="store-alert ${d.queue > 0 ? "red" : "blue"}"><strong>${d.queue > 0 ? "Đang có " + d.queue + " đơn cần chế biến" : "Hàng đợi bếp trống"}</strong><span>${d.ready} đơn đang chờ giao cho khách.</span></div><p class="system-version">FastPOS MVC <strong>2.0.0</strong></p></div>`,
  )}</div>` +
        panel(
          "Đơn Hàng Gần Đây",
          "500 đơn mới nhất có thể tra cứu ở trang đơn hàng",
          table(
            ["Đơn hàng", "Nguồn", "Thanh toán", "Tiến độ", "Thành tiền", ""],
            orderRows(o.slice(0, 6)),
          ),
          '<button class="text-link" data-page="orders">Xem tất cả</button>',
        );
    } else if (page === "system") {
      const d = await api("system/status");
      if (version !== loadVersion) return;
      renderSystem(d);
    } else if (["orders", "kitchen"].includes(page)) {
      rows = await api(page === "kitchen" ? "kitchen" : "orders");
      if (version !== loadVersion) return;
      cache.orders = rows;
      if (page === "orders") {
        $("content").innerHTML =
          intro(
            "Quản lý đơn hàng",
            "Theo dõi riêng thanh toán và tiến độ phục vụ.",
          ) +
          `<div class="toolbar"><input id="search" placeholder="Tìm theo số đơn…" aria-label="Tìm số đơn"><select id="source-filter"><option value="">Tất cả nguồn</option><option value="KIOSK">Kiosk</option><option value="COUNTER">Quầy</option></select><select id="order-filter"><option value="">Tất cả trạng thái</option>${Object.entries(
            STATUS,
          )
            .map(([k, v]) => `<option value="${k}">${v}</option>`)
            .join(
              "",
            )}</select><select id="pay-filter"><option value="">Tất cả thanh toán</option>${Object.entries(
            PAY,
          )
            .map(([k, v]) => `<option value="${k}">${v}</option>`)
            .join("")}</select></div><div id="results"></div>`;
        renderOrders();
      } else renderKitchen();
    } else if (page === "reports") {
      reportFrom ??= localDate();
      reportTo ??= localDate();
      const r = await api(`reports?from=${reportFrom}&to=${reportTo}`);
      if (version !== loadVersion) return;
      $("content").innerHTML =
        intro(
          "Doanh thu và báo cáo",
          "Tính theo ngày thanh toán, múi giờ Việt Nam.",
          `<a class="secondary" href="/api/admin/reports.csv?from=${reportFrom}&to=${reportTo}">↓ Xuất CSV</a>`,
        ) +
        `<form id="report-form" class="toolbar"><label>Từ ngày<input name="from" type="date" value="${reportFrom}" required></label><label>Đến ngày<input name="to" type="date" value="${reportTo}" required></label><button class="primary">Xem báo cáo</button></form>` +
        metrics(r) +
        `<div class="columns">${panel("Doanh thu theo ngày", "Không tính giao dịch chờ, lỗi hoặc hoàn tiền", table(["Ngày", "Số đơn", "Doanh thu"], r.daily.map((x) => `<tr><td>${esc(x.Ngay)}</td><td>${x.SoDon}</td><td>${money(x.DoanhThu)}</td></tr>`).join("")))}${panel("Theo nguồn đơn", "Kiosk và quầy thu ngân", bars(r.sources, "NguonDon", "DoanhThu"))}</div>` +
        panel(
          "Sản phẩm bán nhiều",
          "Tiền dòng hàng trước giảm giá, không phải doanh thu thuần",
          table(
            ["Sản phẩm", "Loại", "Số lượng", "Tiền trước giảm"],
            r.top
              .map(
                (x) =>
                  `<tr><td>${esc(x.TenSanPham)}</td><td>${x.Loai}</td><td>${x.SoLuong}</td><td>${money(x.TienTruocGiam)}</td></tr>`,
              )
              .join(""),
          ),
        );
    } else if (page === "audit") {
      rows = await api("audit");
      if (version !== loadVersion) return;
      $("content").innerHTML =
        intro(
          "Nhật ký thao tác",
          "200 thao tác mới nhất để đối chiếu vận hành.",
        ) +
        panel(
          "Lịch sử quản trị",
          "",
          table(
            [
              "Thời gian",
              "Người thực hiện",
              "Hành động",
              "Đối tượng",
              "Nội dung",
            ],
            rows
              .map(
                (x) =>
                  `<tr><td>${time(x.ThoiGian)}</td><td>${esc(x.TenDangNhap)}</td><td>${esc(x.HanhDong)}</td><td>${esc(x.DoiTuong)}</td><td>${esc(x.NoiDung)}</td></tr>`,
              )
              .join(""),
          ),
        );
    } else {
      rows = await api(page);
      if (version !== loadVersion) return;
      cache[page] = rows;
      if (page === "dishes") cache.categories = await api("categories");
      if (page === "combos") cache.dishes = await api("dishes");
      if (page === "users") cache.roles = await api("roles");
      if (version !== loadVersion) return;
      $("content").innerHTML =
        intro(
          pages[page][1],
          {
            categories: "Nhóm món giúp khách chọn nhanh trên kiosk.",
            dishes: "Giá bán, hình ảnh và trạng thái hiển thị thực đơn.",
            combos: "Quản lý giá combo và số lượng từng món thành phần.",
            promotions: "Thiết lập thời gian, điều kiện và mức giảm giá.",
            users: "Tài khoản nội bộ và quyền truy cập theo vai trò.",
          }[page],
          '<button class="primary" data-add>+ Thêm mới</button>',
        ) +
        `<div class="toolbar"><input id="search" placeholder="Tìm tên hoặc mã…" aria-label="Tìm kiếm"><select id="active-filter"><option value="">Tất cả trạng thái</option><option value="1">Đang sử dụng</option><option value="0">Ngừng sử dụng</option></select>${page === "dishes" ? `<select id="category-filter"><option value="">Tất cả danh mục</option>${cache.categories.map((x) => `<option value="${x.MaDanhMuc}">${esc(x.TenDanhMuc)}</option>`).join("")}</select>` : ""}<span class="counter">${rows.length} bản ghi</span></div><div id="results"></div>`;
      renderCatalog();
    }
    if (quiet && version === loadVersion) {
      for (const [id, value] of Object.entries(filters))
        if ($(id)) $(id).value = value;
      if (page === "orders") renderOrders();
      else if (ids[page]) renderCatalog();
    }
  } catch (e) {
    if (version === loadVersion) {
      if (!quiet)
        $("content").innerHTML =
          `<div class="notice">${esc(e.message)} <button class="secondary small" data-refresh>Thử lại</button></div>`;
      toast(e.message, true);
    }
  }
}
function localDate() {
  const d = new Date();
  return new Intl.DateTimeFormat("en-CA", {
    timeZone: "Asia/Ho_Chi_Minh",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).format(d);
}
function filtered() {
  const q = ($("search")?.value || "").toLowerCase().trim(),
    state = $("active-filter")?.value;
  return rows.filter(
    (x) =>
      (!q ||
        String(x[names[page]] || "")
          .toLowerCase()
          .includes(q) ||
        String(x.MaCode || x.TenDangNhap || "")
          .toLowerCase()
          .includes(q)) &&
      (!state || on(x.TrangThai) === (state === "1")) &&
      (!$("category-filter")?.value ||
        String(x.MaDanhMuc) === $("category-filter").value),
  );
}
function controls(x) {
  return `<div class="actions"><button class="secondary small" data-edit="${x[ids[page]]}">Sửa</button>${on(x.TrangThai) ? `<button class="danger small" data-archive="${x[ids[page]]}">${page === "users" ? "Khóa" : "Ngừng"}</button>` : ""}</div>`;
}
function renderCatalog() {
  const list = filtered();
  if (page === "combos") {
    $("results").innerHTML =
      `<div class="notice">Combo chỉ có thể bán khi tất cả món thành phần còn phục vụ và danh mục đang sử dụng. Kiểm tra cảnh báo trước khi mở bán.</div><div class="combo-grid">${list.map((x) => `<article class="combo-card"><div class="combo-art">${x.HinhAnh ? `<img src="${esc(x.HinhAnh)}" alt="${esc(x.TenCombo)}" referrerpolicy="no-referrer">` : '<img src="/Admin/assetsassets/combo.svg" alt="Combo món ăn và nước uống">'}${activeBadge(x.TrangThai)}</div><div class="panel-body">${on(x.CoTheBan) ? "" : '<span class="pill warn">Chưa thể bán combo</span>'}<h2>${esc(x.TenCombo)}</h2><p>${(x.items || []).map((i) => `${i.SoLuong} × ${esc(i.TenMon)}${on(i.TrangThai) ? "" : " (ngừng bán)"}`).join(" · ")}</p><div class="combo-price">${money(x.GiaBan)}</div>${controls(x)}</div></article>`).join("") || '<p class="empty">Không có combo phù hợp</p>'}</div>`;
    return;
  }
  const head = {
    categories: ["Danh mục", "Mô tả", "Trạng thái", ""],
    dishes: ["Món ăn", "Danh mục", "Giá bán", "Trạng thái", ""],
    promotions: ["Chương trình", "Mức giảm", "Điều kiện", "Hiệu lực", ""],
    users: ["Tài khoản", "Vai trò", "Liên hệ", "Trạng thái", ""],
  }[page];
  const body = list
    .map((x) => {
      let cells = "";
      if (page === "categories")
        cells = `<td><strong>${esc(x.TenDanhMuc)}</strong><small>#${x.MaDanhMuc}</small></td><td>${esc(x.MoTa)}</td><td>${activeBadge(x.TrangThai)}</td>`;
      if (page === "dishes") {
        const cat = cache.categories.find((c) => c.MaDanhMuc === x.MaDanhMuc);
        cells = `<td><div class="dish">${x.HinhAnh ? `<img src="${esc(x.HinhAnh)}" alt="${esc(x.TenMon)}" referrerpolicy="no-referrer">` : '<img src="/Admin/assetsassets/dish.svg" alt="Hình minh họa món ăn">'}<div><strong>${esc(x.TenMon)}</strong><small>#${x.MaMonAn}</small></div></div></td><td>${esc(cat?.TenDanhMuc || "—")}${cat && !on(cat.TrangThai) ? "<small>Danh mục đã ngừng</small>" : ""}</td><td><strong>${money(x.GiaBan)}</strong></td><td>${activeBadge(x.TrangThai)}${on(x.ConHang) ? "" : "<small>Hết món tạm thời</small>"}${on(x.CoTheBan) ? "" : "<small>Không bán trên kiosk</small>"}</td>`;
      }
      if (page === "promotions") {
        const now = Date.now(),
          start = parseDate(x.NgayBatDau),
          end = parseDate(x.NgayKetThuc),
          label = !on(x.TrangThai)
            ? "Tạm ngừng"
            : now < start
              ? "Chưa bắt đầu"
              : now > end
                ? "Đã hết hạn"
                : "Đang hiệu lực";
        cells = `<td><strong>${esc(x.TenKhuyenMai)}</strong><small>${esc(x.MaCode)}</small></td><td><strong>${x.LoaiGiam === "PHAN_TRAM" ? esc(x.GiaTriGiam) + "%" : money(x.GiaTriGiam)}</strong><small>Tối đa ${x.GiamToiDa == null ? "không giới hạn" : money(x.GiamToiDa)}</small></td><td>Từ ${money(x.GiaTriDonToiThieu)}</td><td><span class="pill ${label === "Đang hiệu lực" ? "good" : ""}">${label}</span><small>${time(x.NgayBatDau)}<br>→ ${time(x.NgayKetThuc)}</small></td>`;
      }
      if (page === "users")
        cells = `<td><strong>${esc(x.HoTen)}</strong><small>${esc(x.TenDangNhap)}</small></td><td><span class="pill">${esc(x.TenVaiTro)}</span></td><td>${esc(x.Email) || "—"}<small>${esc(x.SoDienThoai)}</small></td><td>${activeBadge(x.TrangThai)}</td>`;
      return `<tr>${cells}<td>${controls(x)}</td></tr>`;
    })
    .join("");
  $("results").innerHTML = panel(
    "Danh sách " + pages[page][1].toLowerCase(),
    "Ngừng sử dụng để bảo toàn lịch sử đơn hàng",
    table(head, body),
  );
}
function renderOrders() {
  const q = $("search").value.trim();
  const list = rows.filter(
    (x) =>
      (!q || String(x.MaDonHang).includes(q)) &&
      (!$("source-filter").value || x.NguonDon === $("source-filter").value) &&
      (!$("order-filter").value ||
        x.TrangThaiDon === $("order-filter").value) &&
      (!$("pay-filter").value ||
        x.TrangThaiThanhToan === $("pay-filter").value),
  );
  $("results").innerHTML = panel(
    "Danh sách đơn hàng",
    `${list.length} / ${rows.length} đơn mới nhất (tối đa 500). Tra cứu mã cũ qua nút bên dưới.`,
    table(
      ["Đơn hàng", "Nguồn", "Thanh toán", "Tiến độ", "Thành tiền", ""],
      orderRows(list),
    ),
    `<button class="secondary small" id="lookup">Tra cứu mã đơn</button>`,
  );
}
function renderKitchen() {
  const list = rows.filter(
    (x) =>
      x.TrangThaiThanhToan === "THANH_CONG" &&
      ["DA_THANH_TOAN", "CHO_CHE_BIEN", "DANG_CHE_BIEN", "HOAN_THANH"].includes(
        x.TrangThaiDon,
      ),
  );
  $("content").innerHTML =
    intro(
      "Theo dõi khu vực bếp",
      "Đơn đã trả tiền, ưu tiên xử lý theo thời gian chờ.",
    ) +
    `<div class="notice">Đánh dấu vàng sau 3 phút, đỏ sau 7 phút kể từ lúc thanh toán. Hoàn tất chế biến giữ lại đơn để giao khách và báo cáo.</div><div class="kitchen-grid">${
      list
        .sort((a, b) => a.MaDonHang - b.MaDonHang)
        .map((o) => {
          const minutes = Math.max(
              0,
              Math.floor(
                (Date.now() - parseDate(o.ThoiGianThanhToan || o.ThoiGianDat)) /
                  60000,
              ),
            ),
            style =
              o.TrangThaiDon === "HOAN_THANH"
                ? ""
                : minutes >= 7
                  ? "late"
                  : minutes >= 3
                    ? "warn"
                    : "";
          return `<article class="ticket ${style}"><div class="ticket-head"><h2>#${o.MaDonHang}</h2>${source(o.NguonDon)}</div><p>${badge(o.TrangThaiDon)} · ${minutes} phút</p><p>${esc(o.GhiChu) || "Không có ghi chú"}</p><button class="secondary" data-detail="${o.MaDonHang}">Xem món và cập nhật →</button></article>`;
        })
        .join("") || '<p class="empty">Không có đơn đang chờ phục vụ</p>'
    }</div>`;
}
function openModal(title, body) {
  $("modal-title").textContent = title;
  $("modal-body").innerHTML = body;
  if (!$("modal").open) $("modal").showModal();
}
async function orderDetail(id) {
  try {
    const o = await api("orders/" + id),
      next = NEXT[o.TrangThaiDon];
    let can =
      !!next &&
      (next[0] === "DA_HUY"
        ? o.TrangThaiThanhToan !== "THANH_CONG" &&
          o.TrangThaiThanhToan !== "HOAN_TIEN"
        : o.TrangThaiThanhToan === "THANH_CONG");
    openModal(
      "Chi tiết đơn #" + id,
      `<div class="detail-grid"><div><small>Nguồn đơn</small>${source(o.NguonDon)}</div><div><small>Thời gian đặt</small>${time(o.ThoiGianDat)}</div><div><small>Thanh toán</small>${badge(o.TrangThaiThanhToan, true)}<small>${esc(o.PhuongThucThanhToan) || "—"} · ${esc(o.MaGiaoDich) || "—"}</small></div><div><small>Tiến độ phục vụ</small>${badge(o.TrangThaiDon)}</div></div>${table(["Món / Combo", "SL", "Đơn giá", "Thành tiền"], o.items.map((x) => `<tr><td>${esc(x.TenSanPham)}<small>${x.Loai}</small></td><td>${x.SoLuong}</td><td>${money(x.DonGia)}</td><td>${money(x.ThanhTien)}</td></tr>`).join(""))}<div class="total-line"><span>Tạm tính</span>${money(o.TongTien)}</div><div class="total-line"><span>Giảm giá</span>${money(o.TienGiam)}</div><div class="total-line"><strong>Khách thanh toán</strong><strong>${money(o.ThanhTien)}</strong></div><p class="muted">Ghi chú: ${esc(o.GhiChu) || "Không có"}</p>${o.ThoiGianHoanThanh ? `<p class="muted">Bếp hoàn tất: ${time(o.ThoiGianHoanThanh)}</p>` : ""}${can ? `<div class="form-footer"><button class="primary" id="transition">${next[1]}</button></div>` : '<div class="notice">Không có thao tác chuyển trạng thái phù hợp. Hoàn tiền cần quy trình xác nhận từ cổng thanh toán.</div>'}`,
    );
    if (can)
      $("transition").onclick = async () => {
        let reason = "";
        if (next[0] === "DA_HUY") {
          reason = prompt("Lý do hủy đơn chưa thanh toán:");
          if (!reason?.trim()) return;
        } else if (!confirm(`Đơn #${id}: ${next[1]}?`)) return;
        const b = $("transition");
        b.disabled = true;
        try {
          await api("orders/" + id + "/status", {
            method: "POST",
            body: { from: o.TrangThaiDon, to: next[0], reason },
          });
          $("modal").close();
          toast("Đã cập nhật đơn hàng");
          await navigate(page, true);
        } catch (e) {
          toast(e.message, true);
          b.disabled = false;
        }
      };
  } catch (e) {
    toast(e.message, true);
  }
}
const field = (name, label, value = "", type = "text", attrs = "") =>
  `<label>${label}<input name="${name}" type="${type}" value="${esc(value)}" ${attrs}></label>`;
const area = (name, label, value = "") =>
  `<label class="full">${label}<textarea name="${name}" maxlength="500">${esc(value)}</textarea></label>`;
const select = (name, label, options, value) =>
  `<label>${label}<select name="${name}">${options.map(([k, v]) => `<option value="${k}" ${String(k) === String(value) ? "selected" : ""}>${esc(v)}</option>`).join("")}</select></label>`;
function dateInput(x) {
  if (!x) return "";
  return String(x).slice(0, 16);
}
async function edit(id) {
  const type = page,
    x = id ? rows.find((r) => r[ids[type]] === Number(id)) : {};
  let fields = "";
  if (type === "categories")
    fields =
      field(
        "TenDanhMuc",
        "Tên danh mục",
        x.TenDanhMuc,
        "text",
        'required maxlength="100"',
      ) + area("MoTa", "Mô tả", x.MoTa);
  if (["dishes", "combos"].includes(type)) {
    fields =
      field(
        type === "dishes" ? "TenMon" : "TenCombo",
        "Tên " + (type === "dishes" ? "món" : "combo"),
        x[names[type]],
        "text",
        'required maxlength="150"',
      ) +
      field(
        "GiaBan",
        "Giá bán (đ)",
        x.GiaBan,
        "number",
        'min="0" step="0.01" required',
      );
    if (type === "dishes")
      fields +=
        select(
          "MaDanhMuc",
          "Danh mục",
          cache.categories.map((c) => [c.MaDanhMuc, c.TenDanhMuc]),
          x.MaDanhMuc,
        ) +
        select(
          "ConHang",
          "Tình trạng phục vụ",
          [
            [1, "Còn món"],
            [0, "Hết món tạm thời"],
          ],
          id ? (on(x.ConHang) ? 1 : 0) : 1,
        );
    fields +=
      field(
        "HinhAnh",
        "URL hình ảnh HTTPS",
        x.HinhAnh,
        "url",
        'maxlength="500" placeholder="https://…"',
      ) + area("MoTa", "Mô tả", x.MoTa);
  }
  if (type === "users")
    fields =
      field(
        "TenDangNhap",
        "Tên đăng nhập",
        x.TenDangNhap,
        "text",
        `required maxlength="100" ${id ? "readonly" : ""}`,
      ) +
      field("HoTen", "Họ tên", x.HoTen, "text", 'required maxlength="100"') +
      select(
        "MaVaiTro",
        "Vai trò",
        cache.roles.map((r) => [r.MaVaiTro, r.TenVaiTro]),
        x.MaVaiTro,
      ) +
      field(
        "MatKhau",
        id ? "Mật khẩu mới (để trống nếu giữ nguyên)" : "Mật khẩu",
        "",
        "password",
        `minlength="8" maxlength="72" autocomplete="new-password" ${id ? "" : "required"}`,
      ) +
      field("Email", "Email", x.Email, "email", 'maxlength="150"') +
      field(
        "SoDienThoai",
        "Số điện thoại",
        x.SoDienThoai,
        "tel",
        'maxlength="20"',
      );
  if (type === "promotions")
    fields =
      field(
        "MaCode",
        "Mã giảm giá",
        x.MaCode,
        "text",
        'required maxlength="50"',
      ) +
      field(
        "TenKhuyenMai",
        "Tên chương trình",
        x.TenKhuyenMai,
        "text",
        'required maxlength="150"',
      ) +
      select(
        "LoaiGiam",
        "Loại giảm",
        [
          ["PHAN_TRAM", "Phần trăm"],
          ["SO_TIEN", "Số tiền"],
        ],
        x.LoaiGiam,
      ) +
      field(
        "GiaTriGiam",
        "Giá trị giảm",
        x.GiaTriGiam,
        "number",
        'min="0.01" step="0.01" required',
      ) +
      field(
        "GiaTriDonToiThieu",
        "Đơn tối thiểu (đ)",
        x.GiaTriDonToiThieu,
        "number",
        'min="0" step="0.01"',
      ) +
      field(
        "GiamToiDa",
        "Giảm tối đa (đ, trống = không giới hạn)",
        x.GiamToiDa,
        "number",
        'min="0" step="0.01"',
      ) +
      field(
        "NgayBatDau",
        "Bắt đầu (giờ Việt Nam)",
        dateInput(x.NgayBatDau),
        "datetime-local",
        "required",
      ) +
      field(
        "NgayKetThuc",
        "Kết thúc (giờ Việt Nam)",
        dateInput(x.NgayKetThuc),
        "datetime-local",
        "required",
      ) +
      area("MoTa", "Mô tả", x.MoTa);
  fields += select(
    "TrangThai",
    "Trạng thái",
    [
      [1, "Đang sử dụng"],
      [0, "Ngừng sử dụng"],
    ],
    id ? (on(x.TrangThai) ? 1 : 0) : 1,
  );
  openModal(
    (id ? "Cập nhật " : "Thêm ") + pages[type][1].toLowerCase(),
    `<form id="edit-form"><div class="form-grid">${fields}</div>${type === "combos" ? '<h3 style="margin-top:24px">Món thành phần</h3><div id="combo-items"></div><button type="button" class="secondary small" id="add-item">+ Thêm món</button>' : ""}<p id="save-error" class="form-error" role="alert"></p><div class="form-footer"><button type="button" class="secondary" id="cancel-edit">Hủy</button><button class="primary" type="submit">Lưu thông tin</button></div></form>`,
  );
  $("cancel-edit").onclick = () => $("modal").close();
  if (type === "combos") {
    (x.items || [{ SoLuong: 1 }]).forEach(addComboItem);
    $("add-item").onclick = () => addComboItem({ SoLuong: 1 });
  }
  $("edit-form").onsubmit = async (e) => {
    e.preventDefault();
    const button = e.target.querySelector("[type=submit]");
    button.disabled = true;
    $("save-error").textContent = "";
    try {
      const data = Object.fromEntries(new FormData(e.target));
      data.TrangThai = data.TrangThai === "1";
      if (type === "dishes") data.ConHang = data.ConHang === "1";
      if (type === "combos")
        data.items = [...document.querySelectorAll(".combo-item")].map(
          (el) => ({
            MaMonAn: el.querySelector("select").value,
            SoLuong: el.querySelector("input").value,
          }),
        );
      await api(type + (id ? "/" + id : ""), {
        method: id ? "PUT" : "POST",
        body: data,
      });
      $("modal").close();
      toast("Đã lưu thông tin");
      await navigate(type, true);
    } catch (error) {
      $("save-error").textContent = error.message;
      button.disabled = false;
    }
  };
}
function addComboItem(x) {
  const el = document.createElement("div");
  el.className = "combo-item";
  el.innerHTML = `<select aria-label="Món thành phần" required>${cache.dishes.map((d) => `<option value="${d.MaMonAn}" ${d.MaMonAn === x.MaMonAn ? "selected" : ""}>${esc(d.TenMon)}${on(d.TrangThai) ? "" : " · ngừng bán"}</option>`).join("")}</select><input type="number" aria-label="Số lượng món" min="1" max="100" value="${esc(x.SoLuong || 1)}" required><button type="button" class="icon-button" aria-label="Xóa thành phần">×</button>`;
  el.querySelector("button").onclick = () => el.remove();
  $("combo-items").append(el);
}
const navGroups = [
  ["TỔNG QUAN", ["dashboard"]],
  ["THỰC ĐƠN & BÁN HÀNG", ["categories", "dishes", "combos", "promotions"]],
  ["VẬN HÀNH & ĐƠN HÀNG", ["orders", "kitchen"]],
  ["HỆ THỐNG", ["users", "reports", "system", "audit"]],
];
$("nav").innerHTML = navGroups
  .map(
    ([label, keys]) =>
      `<p class="nav-caption">${label}</p>${keys.map((k) => `<a href="/admin/${k}" data-page="${k}">${svg(k)}<span>${pages[k][1].replace(" Tổng Quan", "")}</span>${k === "kitchen" ? '<i class="live-dot"></i>' : ""}</a>`).join("")}`,
  )
  .join("");
window.addEventListener("popstate", () =>
  navigate(location.pathname.split("/").pop() || "dashboard", true),
);
document.addEventListener("click", async (e) => {
  const b = e.target.closest("button,[data-page]");
  if (!b) return;
  if (b.matches("a[data-page]")) e.preventDefault();
  if (b.dataset.page) return navigate(b.dataset.page);
  if (b.hasAttribute("data-refresh")) return navigate(page);
  if (b.dataset.detail) return orderDetail(b.dataset.detail);
  if (b.hasAttribute("data-add")) return edit();
  if (b.dataset.edit) return edit(b.dataset.edit);
  if (b.dataset.archive) {
    if (
      !confirm("Ngừng sử dụng bản ghi này? Lịch sử đơn hàng vẫn được giữ lại.")
    )
      return;
    b.disabled = true;
    try {
      await api(page + "/" + b.dataset.archive, { method: "DELETE" });
      toast("Đã ngừng sử dụng");
      await navigate(page, true);
    } catch (x) {
      toast(x.message, true);
      b.disabled = false;
    }
  }
  if (b.id === "lookup") {
    const id = prompt("Nhập mã đơn hàng:");
    if (id && /^\d+$/.test(id)) orderDetail(id);
  }
});
document.addEventListener("input", (e) => {
  if (e.target.id === "search") {
    if (page === "orders") renderOrders();
    else if (ids[page]) renderCatalog();
  }
});
document.addEventListener("change", (e) => {
  if (e.target.id === "dashboard-date") {
    cache.dashboardDate = e.target.value;
    navigate("dashboard", true);
    return;
  }
  if (e.target.id.endsWith("-filter")) {
    if (page === "orders") renderOrders();
    else if (ids[page]) renderCatalog();
  }
});
document.addEventListener("submit", (e) => {
  if (e.target.id === "report-form") {
    e.preventDefault();
    const d = new FormData(e.target);
    reportFrom = d.get("from");
    reportTo = d.get("to");
    navigate("reports");
  }
});
$("close-modal").onclick = () => $("modal").close();
const logout = async () => {
  try {
    if (ws) ws.close();
    await fetch("/logout", {
      method: "POST",
      headers: { [csrf.headerName]: csrf.token },
    });
    location.href = "/login.html";
  } catch (e) {
    toast("Không thể đăng xuất. Thử lại.", true);
  }
};
$("logout").onclick = logout;
$("logout-header").onclick = logout;
function connect() {
  ws = new WebSocket(
    `${location.protocol === "https:" ? "wss:" : "ws:"}//${location.host}/ws/admin`,
  );
  ws.onopen = () => {
    $("connection").textContent = "● Kết nối trực tiếp";
    $("connection").classList.remove("off");
  };
  ws.onmessage = () => {
    clearTimeout(retryTimer);
    retryTimer = setTimeout(() => {
      if (!$("modal").open) navigate(page, true);
    }, 300);
  };
  ws.onclose = () => {
    $("connection").textContent = "● Đang kết nối lại";
    $("connection").classList.add("off");
    setTimeout(connect, 5000);
  };
}
function clock() {
  $("clock").textContent = new Date().toLocaleString("vi-VN", {
    timeZone: "Asia/Ho_Chi_Minh",
  });
}
clock();
setInterval(clock, 1000);
setInterval(() => {
  if (!$("modal").open && ["dashboard", "orders", "kitchen"].includes(page))
    navigate(page, true);
}, 30000);
(async () => {
  try {
    csrf = await fetch("/api/csrf").then((r) => r.json());
    me = await api("me");
    $("username").textContent = me.username;
    $("demo-banner").hidden = !me.demo;
    await refreshMaintenance();
    await navigate(page);
    connect();
  } catch (e) {
    toast(e.message, true);
  }
})();
