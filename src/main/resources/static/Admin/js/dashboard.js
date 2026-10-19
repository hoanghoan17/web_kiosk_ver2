function hourlyChart(rows) {
  const values = Array.from({ length: 24 }, (_, i) => ({
    hour: i,
    value: Number(rows.find((r) => Number(r.Gio) === i)?.DoanhThu || 0),
  }));
  const max = Math.max(1, ...values.map((x) => x.value));
  const x = (i) => 62 + i * 30.3,
    y = (v) => 260 - (v / max) * 208;
  const points = values.map((p, i) => `${x(i)},${y(p.value)}`).join(" ");
  return `<div class="chart-box"><svg viewBox="0 0 800 305" role="img" aria-label="Doanh thu từng giờ trong ngày"><g font-family="Arial,sans-serif" font-size="10" fill="#77808f">${[0, 0.25, 0.5, 0.75, 1].map((f) => `<line x1="62" y1="${y(f * max)}" x2="760" y2="${y(f * max)}" stroke="#e3e6eb"/><text x="54" y="${y(f * max) + 4}" text-anchor="end">${new Intl.NumberFormat("vi-VN").format(Math.round(max * f))}</text>`).join("")}${values
    .filter((p) => p.hour % 3 === 0)
    .map(
      (p) =>
        `<text x="${x(p.hour)}" y="285" text-anchor="middle">${String(p.hour).padStart(2, "0")}:00</text>`,
    )
    .join(
      "",
    )}</g><polygon points="62,260 ${points} 760,260" fill="#fcebea"/><polyline points="${points}" stroke="#ef3434" stroke-width="3" fill="none"/>${values.map((p) => `<circle cx="${x(p.hour)}" cy="${y(p.value)}" r="3" fill="white" stroke="#ef3434"><title>${p.hour}:00 · ${money(p.value)}</title></circle>`).join("")}</svg><p class="chart-legend"><i></i> Doanh thu (VNĐ)</p></div>`;
}
