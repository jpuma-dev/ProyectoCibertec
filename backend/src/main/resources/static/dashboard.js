const numberFormatter = new Intl.NumberFormat("es-PE");
const moneyFormatter = new Intl.NumberFormat("es-PE", {
  style: "currency",
  currency: "PEN",
  minimumFractionDigits: 2
});

const clamp = (value, min, max) => Math.min(Math.max(value, min), max);
const asNumber = (value) => Number(value || 0);

function setText(selector, value) {
  document.querySelectorAll(selector).forEach((node) => {
    node.textContent = value;
  });
}

function setStatus(state, label) {
  const pill = document.querySelector(".system-pill");
  const text = document.getElementById("connection-status");

  if (!pill || !text) {
    return;
  }

  pill.classList.remove("is-online", "is-offline");
  pill.classList.add(state);
  text.textContent = label;
}

function paintStats(stats) {
  document.querySelectorAll("[data-number]").forEach((node) => {
    const key = node.dataset.number;
    node.textContent = numberFormatter.format(asNumber(stats[key]));
  });

  document.querySelectorAll("[data-money]").forEach((node) => {
    const key = node.dataset.money;
    node.textContent = moneyFormatter.format(asNumber(stats[key]));
  });

  const totalPuestos = asNumber(stats.totalPuestos);
  const ocupados = asNumber(stats.puestosOcupados);
  const pendientes = asNumber(stats.cantidadDeudasPendientes);
  const vencidas = asNumber(stats.cantidadDeudasVencidas);
  const recaudacionHoy = asNumber(stats.recaudacionHoy);
  const montoPendiente = asNumber(stats.montoTotalPendiente);

  const occupancy = totalPuestos ? Math.round((ocupados / totalPuestos) * 100) : 0;
  const risk = pendientes ? Math.round((vencidas / pendientes) * 100) : 0;

  setText("#occupancy-value", `${clamp(occupancy, 0, 100)}%`);
  setText("#risk-percent", `${clamp(risk, 0, 100)}%`);

  const meter = document.querySelector(".risk-meter");
  if (meter) {
    meter.style.setProperty("--risk", `${clamp(risk, 0, 100)}%`);
  }

  const liquidityStatus = recaudacionHoy >= montoPendiente * 0.08
    ? "Ritmo saludable"
    : recaudacionHoy > 0
      ? "Ritmo moderado"
      : "Sin movimiento";

  setText("#liquidity-status", liquidityStatus);
}

function paintToday() {
  const date = new Intl.DateTimeFormat("es-PE", {
    weekday: "short",
    day: "2-digit",
    month: "short"
  }).format(new Date());

  setText("#today-label", date.replace(".", ""));
}

async function loadDashboard() {
  paintToday();

  try {
    const response = await fetch("/api/dashboard/stats", {
      headers: { Accept: "application/json" }
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const payload = await response.json();
    paintStats(payload.data || {});
    setStatus("is-online", "Datos en vivo");
  } catch (error) {
    setStatus("is-offline", "API no disponible");
  }
}

loadDashboard();
