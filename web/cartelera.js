const $ = selector => document.querySelector(selector);
const $$ = selector => Array.from(document.querySelectorAll(selector));

const state = {
  cartelera: null,
  seleccion: null,
  codigoActivo: null
};

function esc(value) {
  return String(value ?? '').replace(/[&<>"']/g, character => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[character]));
}

function money(value) {
  return 'Bs ' + Number(value || 0).toFixed(2);
}

function fechaBonita(fecha) {
  if (!fecha) return '';
  const [year, month, day] = fecha.split('-').map(Number);
  const date = new Date(year, month - 1, day);
  const dias = ['dom', 'lun', 'mar', 'mié', 'jue', 'vie', 'sáb'];
  const meses = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];
  return `${dias[date.getDay()]} ${day} ${meses[month - 1]}`;
}

async function api(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
  });
  if (response.status === 204) {
    return null;
  }
  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(body.error || 'No se pudo completar la operación.');
  }
  return body;
}

function toast(message, type = 'success') {
  const element = document.createElement('div');
  element.className = `toast ${type === 'error' ? 'error' : ''}`;
  element.textContent = message;
  $('#toasts').appendChild(element);
  setTimeout(() => {
    element.classList.add('out');
    setTimeout(() => element.remove(), 260);
  }, 3400);
}

function aplicarPromos() {
  const promos = state.cartelera?.promociones || [];
  if (!promos.length) {
    $('#hero-promos').innerHTML = '';
    return;
  }
  $('#hero-promos').innerHTML = promos.map(promo => {
    const texto = promo.tipo === '2x1'
      ? 'Dos entradas al precio de una'
      : promo.tipo === 'Descuento %'
        ? `${promo.valor}% de descuento`
        : `Función benéfica · ${esc(promo.beneficiario || '')}`;
    return `<div class="promo-card"><strong>${esc(promo.nombre)}</strong><span>${texto}</span></div>`;
  }).join('');
}

function renderCartelera() {
  const peliculas = state.cartelera?.peliculas || [];
  const contenedor = $('#cartelera-lista');
  if (!peliculas.length) {
    contenedor.innerHTML = '<div class="empty">No hay funciones programadas por el momento.</div>';
  } else {
    contenedor.innerHTML = peliculas.map((pelicula, index) => {
      const funciones = pelicula.funciones || [];
      return `
        <article class="movie-card">
          <div class="movie-poster" data-gen="${esc(pelicula.genero)}">
            <span class="movie-initials">${esc((pelicula.titulo || '').slice(0, 2).toUpperCase())}</span>
            <span class="movie-rating">${esc(pelicula.clasificacion)}</span>
          </div>
          <div class="movie-info">
            <h3>${esc(pelicula.titulo)}</h3>
            <p class="movie-meta">${esc(pelicula.genero)} · ${pelicula.duracion} min</p>
            <p class="movie-sinopsis">${esc(pelicula.sinopsis || 'Sin sinopsis registrada.')}</p>
            <div class="funciones-lista">
              ${funciones.map(funcion => `
                <button class="funcion-chip" data-funcion="${funcion.id}">
                  <span class="funcion-hora">${esc(funcion.hora)}</span>
                  <span class="funcion-dato">${fechaBonita(funcion.fecha)} · ${esc(funcion.salaNombre)}</span>
                  <span class="funcion-dato small">${esc(funcion.tipoSalaNombre)} · ${money(funcion.precio)}</span>
                </button>
              `).join('')}
            </div>
          </div>
        </article>`;
    }).join('');
  }

  const proximos = state.cartelera?.proximos || [];
  if (proximos.length) {
    $('#proximos-wrap').hidden = false;
    $('#proximos-lista').innerHTML = proximos.map(pelicula => `
      <article class="movie-mini">
        <span class="movie-initials">${esc((pelicula.titulo || '').slice(0, 2).toUpperCase())}</span>
        <div>
          <strong>${esc(pelicula.titulo)}</strong>
          <span>${esc(pelicula.genero)} · ${pelicula.duracion} min</span>
        </div>
      </article>`).join('');
  } else {
    $('#proximos-wrap').hidden = true;
  }

  const confiteria = state.cartelera?.confiteria || [];
  $('#confiteria-lista').innerHTML = confiteria.length
    ? confiteria.map(producto => `
      <div class="product-card">
        <div class="product-icon">${esc((producto.categoria || '').slice(0, 1))}</div>
        <div class="product-info">
          <strong>${esc(producto.nombre)}</strong>
          <span>${esc(producto.categoria)}</span>
        </div>
        <span class="product-price">${money(producto.precio)}</span>
      </div>`).join('')
    : '<div class="empty">La dulcería no tiene productos disponibles.</div>';
}

async function abrirFuncion(funcionId) {
  const drawer = $('#drawer');
  drawer.innerHTML = '<div class="drawer-loading">Cargando asientos…</div>';
  $('#overlay').hidden = false;
  try {
    const data = await api(`/api/public/funciones/${funcionId}/asientos`);
    state.seleccion = {
      funcion: data.funcion,
      asientos: data.asientos,
      seleccionados: new Set(),
      productos: {},
      cliente: '',
      email: '',
      medioPago: 'Tarjeta'
    };
    renderDrawer();
  } catch (error) {
    drawer.innerHTML = `<div class="drawer-loading">${esc(error.message)}</div>`;
  }
}

function capturarDatos() {
  const sel = state.seleccion;
  if (!sel) return;
  const nombre = $('#cliente-nombre');
  const email = $('#cliente-email');
  const pago = $('#cliente-pago');
  if (nombre) sel.cliente = nombre.value;
  if (email) sel.email = email.value;
  if (pago) sel.medioPago = pago.value;
}

function renderDrawer() {
  const sel = state.seleccion;
  if (!sel) return;
  const funcion = sel.funcion;
  const confiteria = state.cartelera.confiteria || [];

  const byRow = new Map();
  sel.asientos.forEach(asiento => {
    if (!byRow.has(asiento.fila)) byRow.set(asiento.fila, []);
    byRow.get(asiento.fila).push(asiento);
  });
  const mapa = Array.from(byRow.entries())
    .sort((a, b) => a[0].localeCompare(b[0]))
    .map(([fila, lista]) => `
      <div class="seat-row">
        <span class="row-label">${esc(fila)}</span>
        ${lista.sort((a, b) => a.numero - b.numero).map(asiento => {
          const seleccionado = sel.seleccionados.has(asiento.id);
          const clase = asiento.estado === 'Ocupado'
            ? 'ocupado'
            : asiento.estado === 'Mantenimiento'
              ? 'mantenimiento'
              : seleccionado ? 'seleccionado' : '';
          return `<button class="seat ${clase}" data-asiento="${asiento.id}" ${asiento.estado === 'Disponible' ? '' : 'disabled'}>${asiento.numero}</button>`;
        }).join('')}
      </div>`).join('');

  const entradas = sel.seleccionados.size;
  const precio = funcion.precio || 0;
  const subtotalEntradas = precio * entradas;
  const descuento = calcularDescuento(subtotalEntradas, entradas);
  const subtotalConfiteria = Object.entries(sel.productos).reduce((acc, [id, cantidad]) => {
    const producto = confiteria.find(p => String(p.id) === String(id));
    return acc + (producto ? producto.precio * cantidad : 0);
  }, 0);
  const total = Math.max(0, subtotalEntradas - descuento) + subtotalConfiteria;

  $('#drawer').innerHTML = `
    <div class="drawer-head">
      <div>
        <p class="eyebrow">${esc(funcion.tipoSalaNombre)} · ${esc(funcion.salaNombre)}</p>
        <h2>${esc(funcion.peliculaTitulo)}</h2>
        <p class="drawer-sub">${fechaBonita(funcion.fecha)} · ${esc(funcion.hora)} a ${esc(funcion.fin)} · ${esc(funcion.clasificacion)}</p>
      </div>
      <button class="drawer-close" id="drawer-close" aria-label="Cerrar">&times;</button>
    </div>

    <div class="drawer-body">
      <div class="drawer-main">
        <div class="pantalla">PANTALLA</div>
        <div class="seat-map">${mapa}</div>
        <div class="leyenda">
          <span><i class="dot libre"></i>Libre</span>
          <span><i class="dot seleccionado"></i>Seleccionado</span>
          <span><i class="dot ocupado"></i>Ocupado</span>
          <span><i class="dot mantenimiento"></i>Mantenimiento</span>
        </div>

        <h3 class="drawer-title">Agrega a tu compra</h3>
        <div class="confiteria-select">
          ${confiteria.map(producto => `
            <div class="product-row">
              <div class="product-info">
                <strong>${esc(producto.nombre)}</strong>
                <span>${esc(producto.categoria)} · ${money(producto.precio)}</span>
              </div>
              <div class="stepper">
                <button data-stepper="menos" data-producto="${producto.id}">−</button>
                <span>${sel.productos[producto.id] || 0}</span>
                <button data-stepper="mas" data-producto="${producto.id}">+</button>
              </div>
            </div>`).join('') || '<div class="empty small">Sin productos disponibles.</div>'}
        </div>

        <h3 class="drawer-title">Tus datos</h3>
        <div class="datos-grid">
          <label class="campo"><span>Nombre completo *</span>
            <input id="cliente-nombre" type="text" placeholder="Ej. María Fernández" value="${esc(sel.cliente)}">
          </label>
          <label class="campo"><span>Correo electrónico</span>
            <input id="cliente-email" type="email" placeholder="tucorreo@ejemplo.com" value="${esc(sel.email || '')}">
          </label>
          <label class="campo"><span>Medio de pago</span>
            <select id="cliente-pago">
              ${['Tarjeta', 'Efectivo', 'QR'].map(opcion => `<option ${sel.medioPago === opcion ? 'selected' : ''}>${opcion}</option>`).join('')}
            </select>
          </label>
        </div>
      </div>

      <aside class="drawer-side">
        <h3 class="drawer-title">Resumen</h3>
        <div class="resumen-linea"><span>Entradas (${entradas})</span><strong>${money(subtotalEntradas)}</strong></div>
        ${descuento ? `<div class="resumen-linea desc"><span>Descuento</span><strong>−${money(descuento)}</strong></div>` : ''}
        <div class="resumen-linea"><span>Confitería</span><strong>${money(subtotalConfiteria)}</strong></div>
        <div class="resumen-linea total"><span>Total</span><strong>${money(total)}</strong></div>
        <div class="asientos-elegidos" id="asientos-elegidos"></div>
        <button class="btn primary block" id="confirmar" ${entradas ? '' : 'disabled'}>
          ${entradas ? 'Confirmar reserva' : 'Selecciona tus asientos'}
        </button>
      </aside>
    </div>`;

  pintarAsientosElegidos();
}

function pintarAsientosElegidos() {
  const sel = state.seleccion;
  const contenedor = $('#asientos-elegidos');
  if (!contenedor) return;
  const etiquetas = sel.asientos
    .filter(a => sel.seleccionados.has(a.id))
    .map(a => a.etiqueta);
  contenedor.innerHTML = etiquetas.length
    ? `<span class="elegidos-label">Asientos:</span> ${etiquetas.map(e => `<span class="chip">${esc(e)}</span>`).join('')}`
    : '';
}

function calcularDescuento(subtotal, cantidad) {
  if (cantidad <= 0) return 0;
  const promos = state.cartelera?.promociones || [];
  let mejor = 0;
  promos.forEach(promo => {
    if (promo.tipo === '2x1') {
      mejor = Math.max(mejor, Math.floor(cantidad / 2) * (subtotal / cantidad));
    } else if (promo.tipo === 'Descuento %') {
      mejor = Math.max(mejor, subtotal * promo.valor / 100);
    }
  });
  return Math.round(mejor * 100) / 100;
}

function toggleAsiento(id) {
  const sel = state.seleccion;
  capturarDatos();
  if (sel.seleccionados.has(id)) {
    sel.seleccionados.delete(id);
  } else {
    if (sel.seleccionados.size >= 6) {
      toast('Solo puedes reservar hasta 6 asientos.', 'error');
      return;
    }
    sel.seleccionados.add(id);
  }
  renderDrawer();
}

function cambiarProducto(id, delta) {
  const sel = state.seleccion;
  capturarDatos();
  const actual = sel.productos[id] || 0;
  const siguiente = Math.max(0, actual + delta);
  if (siguiente === 0) {
    delete sel.productos[id];
  } else {
    sel.productos[id] = siguiente;
  }
  renderDrawer();
}

async function confirmarReserva() {
  const sel = state.seleccion;
  capturarDatos();
  const nombre = (sel.cliente || '').trim();
  const email = (sel.email || '').trim();
  const medioPago = sel.medioPago || 'Tarjeta';
  if (!nombre) {
    toast('Ingresa tu nombre para confirmar la reserva.', 'error');
    return;
  }
  if (!sel.seleccionados.size) {
    toast('Selecciona al menos un asiento.', 'error');
    return;
  }
  const productos = Object.fromEntries(
    Object.entries(sel.productos).map(([productoId, cantidad]) => [productoId, cantidad])
  );
  const button = $('#confirmar');
  button.disabled = true;
  button.textContent = 'Procesando…';
  try {
    const reserva = await api('/api/public/reservas', {
      method: 'POST',
      body: JSON.stringify({
        funcionId: sel.funcion.id,
        asientos: Array.from(sel.seleccionados),
        productos,
        cliente: nombre,
        email,
        medioPago
      })
    });
    state.codigoActivo = reserva.codigo;
    cerrarDrawer();
    mostrarTicket(reserva);
    toast(`Reserva ${reserva.codigo} confirmada.`);
  } catch (error) {
    toast(error.message, 'error');
    button.disabled = false;
    button.textContent = 'Confirmar reserva';
  }
}

function cerrarDrawer() {
  $('#overlay').hidden = true;
  $('#drawer').innerHTML = '';
  state.seleccion = null;
}

function crearTicket(reserva) {
  const template = $('#ticket-template').content.cloneNode(true);
  template.querySelector('#ticket-pelicula').textContent = reserva.peliculaTitulo || 'Función';
  template.querySelector('#ticket-funcion').textContent =
    `${fechaBonita(reserva.fecha)} · ${reserva.hora} · ${reserva.salaNombre}`;
  template.querySelector('#ticket-codigo').textContent = reserva.codigo;
  template.querySelector('#ticket-code-display').textContent = reserva.codigo;

  const asientos = (reserva.asientosDetalle || []).map(a => a.etiqueta).join(', ');
  const productos = (reserva.productosDetalle || []).length
    ? reserva.productosDetalle.map(p => `${esc(p.nombre)} x${p.cantidad}`).join('<br>')
    : 'Sin productos';
  template.querySelector('#ticket-detalle').innerHTML = `
    <div class="ticket-linea"><span>Cliente</span><strong>${esc(reserva.cliente)}</strong></div>
    <div class="ticket-linea"><span>Asientos</span><strong>${esc(asientos)}</strong></div>
    <div class="ticket-linea"><span>Entradas</span><strong>${money(reserva.subtotalEntradas)}</strong></div>
    ${reserva.descuento ? `<div class="ticket-linea"><span>Descuento</span><strong>−${money(reserva.descuento)}</strong></div>` : ''}
    <div class="ticket-linea"><span>Confitería</span><strong>${money(reserva.subtotalConfiteria)}</strong></div>
    <div class="ticket-linea"><span>Productos</span><strong>${productos}</strong></div>
    <div class="ticket-linea total"><span>Total pagado</span><strong>${money(reserva.total)}</strong></div>
    <div class="ticket-linea"><span>Medio de pago</span><strong>${esc(reserva.medioPago)}</strong></div>`;
  return template;
}

function mostrarTicket(reserva) {
  const drawer = $('#drawer');
  drawer.innerHTML = '';
  drawer.appendChild(crearTicket(reserva));
  $('#overlay').hidden = false;
}

function cambiarPanel(target) {
  $$('.nav-link[data-target]').forEach(link => link.classList.toggle('active', link.dataset.target === target));
  $$('.panel-view').forEach(view => view.classList.remove('active'));
  document.getElementById(`panel-${target}`).classList.add('active');
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function buscarReserva(codigo) {
  const contenedor = $('#buscar-resultado');
  contenedor.innerHTML = '<div class="drawer-loading">Buscando…</div>';
  try {
    const reserva = await api(`/api/public/reservas/${encodeURIComponent(codigo.trim())}`);
    state.codigoActivo = reserva.codigo;
    contenedor.innerHTML = '';
    const ticket = document.createElement('div');
    ticket.className = 'ticket-inline';
    ticket.appendChild(crearTicket(reserva));
    contenedor.appendChild(ticket);
  } catch (error) {
    contenedor.innerHTML = `<div class="empty">${esc(error.message)}</div>`;
  }
}

function bindEventos() {
  $$('.nav-link[data-target]').forEach(link => {
    link.addEventListener('click', () => cambiarPanel(link.dataset.target));
  });

  document.addEventListener('click', event => {
    const chip = event.target.closest('[data-funcion]');
    if (chip) {
      abrirFuncion(Number(chip.dataset.funcion));
      return;
    }
    const asiento = event.target.closest('[data-asiento]');
    if (asiento && !asiento.disabled) {
      toggleAsiento(Number(asiento.dataset.asiento));
      return;
    }
    const stepper = event.target.closest('[data-stepper]');
    if (stepper) {
      cambiarProducto(stepper.dataset.producto, stepper.dataset.stepper === 'mas' ? 1 : -1);
      return;
    }
    if (event.target.id === 'confirmar') {
      confirmarReserva();
      return;
    }
    if (event.target.id === 'drawer-close') {
      cerrarDrawer();
      return;
    }
    if (event.target.id === 'overlay') {
      cerrarDrawer();
    }
  });

  $('#buscar-form').addEventListener('submit', event => {
    event.preventDefault();
    buscarReserva($('#buscar-codigo').value);
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape' && !$('#overlay').hidden) {
      cerrarDrawer();
    }
  });
}

async function init() {
  bindEventos();
  try {
    state.cartelera = await api('/api/public/cartelera');
    aplicarPromos();
    renderCartelera();
  } catch (error) {
    $('#cartelera-lista').innerHTML = `<div class="empty">${esc(error.message)}</div>`;
  }
}

document.addEventListener('DOMContentLoaded', init);
