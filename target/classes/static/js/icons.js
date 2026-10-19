function svg(name) {
  const paths = {
    dashboard: "M3 3h7v7H3z M14 3h7v7h-7z M3 14h7v7H3z M14 14h7v7h-7z",
    categories: "M3 3h18v18H3z M3 9h18 M3 15h18 M9 3v18 M15 3v18",
    dishes: "M6 3v7 M3 3v7q0 3 6 0V3 M6 12v9 M17 3q-5 5 0 10V3z M17 13v8",
    combos: "M3 7l9-5 9 5v10l-9 5-9-5z M3 7l9 5 9-5 M12 12v10 M7 4l10 6",
    promotions: "M3 5h18v5q-4 2 0 4v5H3v-5q4-2 0-4z M9 15l6-6 M8 8h1 M15 16h1",
    orders: "M5 3h14l2 4v14H3V7z M3 7h18 M8 10q4 7 8 0",
    kitchen: "M7 14q-7-4-2-9 3-2 5 0 5-6 9 0 6 4-2 9v7H7z M7 17h10",
    users:
      "M8 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M2 21v-3q0-5 12-5v8 M16 4q6 3 0 7 M18 14q4 1 4 7",
    reports: "M3 2v20h19 M7 17V9 M12 17V5 M17 17v-6",
    system: "M4 5h16v14H4z M8 9h8 M8 13h5 M8 17h8",
    audit: "M5 3h14v18H5z M9 8h6 M9 12h6 M9 16h6",
  };
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" stroke-linecap="round" aria-hidden="true"><path d="${paths[name] || paths.system}"/></svg>`;
}
