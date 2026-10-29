const state = {
  peliculas: [],
  tipos: [],
  salas: [],
  asientos: [],
  tarifas: [],
  funciones: [],
  productos: [],
  promociones: [],
  reservas: [],
  usuarios: [],
  auditoria: [],
  clientes: [],
  mediosPago: [],
  entradas: [],
  reembolsos: [],
  recaudacion: null,
  section: 'peliculas',
  search: '',
  salaSeleccionada: null,
  sesion: null,
  token: null,
  rolLogin: 'Administrador'
};

const sectionMeta = {
  peliculas: { title: 'Películas', eyebrow: 'HU-01 · Catálogo' },
  funciones: { title: 'Programación', eyebrow: 'HU-13 · Funciones' },
  tipos: { title: 'Tipos de sala', eyebrow: 'HU-03 · Formatos' },
  salas: { title: 'Salas', eyebrow: 'HU-02 · Infraestructura' },
  asientos: { title: 'Asientos', eyebrow: 'HU-04 · Distribución' },
  tarifas: { title: 'Tarifas', eyebrow: 'HU-31 · Precios' },
  productos: { title: 'Confitería', eyebrow: 'HU-11 · Inventario' },
  promociones: { title: 'Promociones', eyebrow: 'HU-14 · Beneficios' },
  clientes: { title: 'Clientes', eyebrow: 'HU-07 · Cartera' },
  'medios-pago': { title: 'Medios de pago', eyebrow: 'HU-33 · Pagos' },
  reservas: { title: 'Reservas', eyebrow: 'HU-08 · Ventas' },
  entradas: { title: 'Entradas', eyebrow: 'HU-09 · Emisión' },
  reembolsos: { title: 'Reembolsos', eyebrow: 'HU-35 · Post-venta' },
  recaudacion: { title: 'Recaudación', eyebrow: 'HU-27 · Control' },
  usuarios: { title: 'Usuarios', eyebrow: 'HU-30 · Seguridad' },
  auditoria: { title: 'Auditoría', eyebrow: 'HU-36 · Bitácora' }
};

const entities = {
  peliculas: {
    name: 'Película',
    singular: 'película',
    endpoint: '/api/peliculas',
    columns: [
      {
        key: 'titulo',
        label: 'Título',
        render: item => `<div class="cell-title"><strong>${esc(item.titulo)}</strong><span class="sub">${esc(item.sinopsis || 'Sin sinopsis registrada')}</span></div>`
      },
      { key: 'genero', label: 'Género' },
      { key: 'clasificacion', label: 'Clasificación', render: item => `<span class="badge outline">${esc(item.clasificacion)}</span>` },
      { key: 'duracion', label: 'Duración', render: item => `${item.duracion} min` },
      { key: 'estado', label: 'Estado', render: item => estadoBadge(item.estado) }
    ],
    fields: [
      { name: 'titulo', label: 'Título', type: 'text', required: true, placeholder: 'Ej. El último horizonte' },
      { name: 'genero', label: 'Género', type: 'text', required: true, placeholder: 'Ej. Ciencia ficción' },
      { name: 'clasificacion', label: 'Clasificación', type: 'select', required: true, placeholder: 'Selecciona una clasificación', options: ['TP', '+7', '+13', '+16', '+18'] },
      { name: 'duracion', label: 'Duración (minutos)', type: 'number', required: true, min: 1, max: 600, placeholder: '120' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activa', 'Inactiva'], default: 'Activa' },
      { name: 'sinopsis', label: 'Sinopsis', type: 'textarea', full: true, placeholder: 'Breve descripción de la película' }
    ]
  },
  tipos: {
    name: 'Tipo de sala',
    singular: 'tipo de sala',
    endpoint: '/api/tipos-sala',
    columns: [
      {
        key: 'nombre',
        label: 'Tipo',
        render: item => `<div class="cell-title"><strong>${esc(item.nombre)}</strong><span class="sub">${esc(item.descripcion || 'Sin descripción')}</span></div>`
      },
      { key: 'estado', label: 'Estado', render: item => estadoBadge(item.estado) }
    ],
    fields: [
      { name: 'nombre', label: 'Nombre', type: 'text', required: true, placeholder: 'Ej. 4DX' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activa', 'Inactiva'], default: 'Activa' },
      { name: 'descripcion', label: 'Descripción', type: 'textarea', full: true, placeholder: 'Características del formato de proyección' }
    ]
  },
  salas: {
    name: 'Sala',
    singular: 'sala',
    endpoint: '/api/salas',
    columns: [
      { key: 'nombre', label: 'Sala', render: item => `<div class="cell-title"><strong>${esc(item.nombre)}</strong><span class="sub">${esc(item.caracteristicas || 'Sin características registradas')}</span></div>` },
      { key: 'tipoSalaNombre', label: 'Tipo', render: item => `<span class="badge outline">${esc(item.tipoSalaNombre)}</span>` },
      { key: 'capacidad', label: 'Capacidad', render: item => `${item.capacidad} asientos` },
      { key: 'estado', label: 'Estado', render: item => estadoBadge(item.estado) }
    ],
    fields: [
      { name: 'nombre', label: 'Nombre', type: 'text', required: true, placeholder: 'Ej. Sala 4' },
      {
        name: 'tipoSalaId',
        label: 'Tipo de sala',
        type: 'select',
        required: true,
        placeholder: 'Selecciona un tipo',
        options: () => state.tipos.map(tipo => ({ value: tipo.id, label: tipo.nombre }))
      },
      { name: 'capacidad', label: 'Capacidad', type: 'number', required: true, min: 1, max: 500, placeholder: '60' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activa', 'Inactiva'], default: 'Activa' },
      { name: 'caracteristicas', label: 'Características', type: 'text', full: true, placeholder: 'Ubicación, pantalla, sonido' }
    ]
  },
  clientes: {
    name: 'Cliente',
    singular: 'cliente',
    endpoint: '/api/clientes',
    columns: [
      { key: 'nombre', label: 'Cliente', render: item => `<div class="cell-title"><strong>${esc(item.nombre)}</strong><span class="sub">Doc. ${esc(item.documento)}</span></div>` },
      { key: 'email', label: 'Correo', render: item => esc(item.email || '—') },
      { key: 'telefono', label: 'Teléfono', render: item => esc(item.telefono || '—') },
      { key: 'estado', label: 'Estado', render: item => estadoBadge(item.estado) }
    ],
    fields: [
      { name: 'nombre', label: 'Nombre completo', type: 'text', required: true, placeholder: 'Ej. María Fernández' },
      { name: 'documento', label: 'Documento', type: 'text', required: true, placeholder: 'Ej. 7712345' },
      { name: 'email', label: 'Correo electrónico', type: 'email', placeholder: 'cliente@ejemplo.com' },
      { name: 'telefono', label: 'Teléfono', type: 'text', placeholder: '71234567' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activa', 'Inactiva'], default: 'Activa' }
    ]
  },
  'medios-pago': {
    name: 'Medio de pago',
    singular: 'medio de pago',
    endpoint: '/api/medios-pago',
    columns: [
      { key: 'nombre', label: 'Medio', render: item => `<div class="cell-title"><strong>${esc(item.nombre)}</strong><span class="sub">${esc(item.descripcion || 'Sin descripción')}</span></div>` },
      { key: 'estado', label: 'Estado', render: item => estadoCatalogoBadge(item.estado) }
    ],
    fields: [
      { name: 'nombre', label: 'Nombre', type: 'text', required: true, placeholder: 'Ej. Transferencia' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activo', 'Inactivo'], default: 'Activo' },
      { name: 'descripcion', label: 'Descripción', type: 'textarea', full: true, placeholder: 'Detalle del medio de pago' }
    ]
  },
  funciones: {
    name: 'Función',
    singular: 'función',
    endpoint: '/api/funciones',
    columns: [
      { key: 'peliculaTitulo', label: 'Película', render: item => `<div class="cell-title"><strong>${esc(item.peliculaTitulo)}</strong><span class="sub">${esc(item.genero)} · ${esc(item.clasificacion)} · ${item.duracion} min</span></div>` },
      { key: 'salaNombre', label: 'Sala', render: item => `<span class="badge outline">${esc(item.salaNombre)}</span>` },
      { key: 'fecha', label: 'Fecha', render: item => `${esc(item.fecha)} <span class="sub">${esc(item.hora)}–${esc(item.fin)}</span>` },
      {
        key: 'disponibles',
        label: 'Ocupación',
        render: item => `<div class="cell-title"><strong>${item.disponibles} libres</strong><span class="sub">${item.ocupados}/${item.capacidad} ocupados</span></div>`
      },
      { key: 'precio', label: 'Precio', render: item => `<strong>${money(item.precio)}</strong>` },
      { key: 'estado', label: 'Estado', render: item => badgeFuncion(item.estado) }
    ],
    fields: [
      {
        name: 'peliculaId',
        label: 'Película',
        type: 'select',
        required: true,
        placeholder: 'Selecciona una película',
        options: () => state.peliculas.filter(p => p.estado === 'Activa').map(p => ({ value: p.id, label: p.titulo }))
      },
      {
        name: 'salaId',
        label: 'Sala',
        type: 'select',
        required: true,
        placeholder: 'Selecciona una sala',
        options: () => state.salas.filter(s => s.estado === 'Activa').map(s => ({ value: s.id, label: `${s.nombre} · ${s.tipoSalaNombre}` }))
      },
      { name: 'fecha', label: 'Fecha', type: 'date', required: true },
      { name: 'hora', label: 'Hora de inicio', type: 'time', required: true },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Programada', 'Cancelada'], default: 'Programada' }
    ]
  },
  productos: {
    name: 'Producto',
    singular: 'producto de confitería',
    endpoint: '/api/productos',
    columns: [
      { key: 'nombre', label: 'Producto', render: item => `<div class="cell-title"><strong>${esc(item.nombre)}</strong><span class="sub">${esc(item.categoria)}</span></div>` },
      { key: 'precio', label: 'Precio', render: item => `<strong>${money(item.precio)}</strong>` },
      { key: 'stock', label: 'Stock', render: item => item.bajoStock ? `<span class="badge danger">${item.stock} u.</span>` : `<span class="badge neutral">${item.stock} u.</span>` },
      { key: 'stockMinimo', label: 'Mínimo', render: item => `${item.stockMinimo} u.` },
      { key: 'estado', label: 'Estado', render: item => estadoBadge(item.estado) }
    ],
    fields: [
      { name: 'nombre', label: 'Nombre', type: 'text', required: true, placeholder: 'Ej. Combo Clásico' },
      { name: 'categoria', label: 'Categoría', type: 'select', required: true, options: ['Combos', 'Palomitas', 'Bebidas', 'Snacks', 'Dulces'] },
      { name: 'precio', label: 'Precio (Bs)', type: 'number', required: true, min: 0, max: 10000, step: '0.01', placeholder: '45.00' },
      { name: 'stock', label: 'Stock', type: 'number', required: true, min: 0, max: 100000, placeholder: '40' },
      { name: 'stockMinimo', label: 'Stock mínimo', type: 'number', required: true, min: 0, max: 100000, placeholder: '10' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activa', 'Inactiva'], default: 'Activa' }
    ]
  },
  promociones: {
    name: 'Promoción',
    singular: 'promoción',
    endpoint: '/api/promociones',
    columns: [
      { key: 'nombre', label: 'Promoción', render: item => `<div class="cell-title"><strong>${esc(item.nombre)}</strong><span class="sub">${esc(item.beneficiario || tipoTexto(item))}</span></div>` },
      { key: 'tipo', label: 'Tipo', render: item => `<span class="badge outline">${esc(item.tipo)}</span>` },
      { key: 'vigencia', label: 'Vigencia', render: item => `${esc(item.fechaInicio || '—')} → ${esc(item.fechaFin || '—')}` },
      { key: 'vigente', label: 'Vigente', render: item => item.vigente ? '<span class="badge success">Sí</span>' : '<span class="badge neutral">No</span>' },
      { key: 'estado', label: 'Estado', render: item => estadoBadge(item.estado) }
    ],
    fields: [
      { name: 'nombre', label: 'Nombre', type: 'text', required: true, placeholder: 'Ej. 2x1 Martes de Cine' },
      { name: 'tipo', label: 'Tipo', type: 'select', required: true, options: ['2x1', 'Descuento %', 'Función benéfica'] },
      { name: 'valor', label: 'Valor (% para descuento)', type: 'number', required: true, min: 0, max: 100, step: '0.01', default: 0, placeholder: '20' },
      { name: 'fechaInicio', label: 'Fecha de inicio', type: 'date' },
      { name: 'fechaFin', label: 'Fecha de fin', type: 'date' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activa', 'Inactiva'], default: 'Activa' },
      { name: 'beneficiario', label: 'Beneficiario (solo función benéfica)', type: 'text', full: true, placeholder: 'Ej. Hogar San José' }
    ]
  },
  tarifas: {
    name: 'Tarifa',
    singular: 'tarifa',
    endpoint: '/api/tarifas',
    columns: [
      { key: 'nombre', label: 'Tarifa', render: item => `<strong>${esc(item.nombre)}</strong>` },
      { key: 'tipoSalaNombre', label: 'Tipo de sala', render: item => `<span class="badge outline">${esc(item.tipoSalaNombre)}</span>` },
      { key: 'condicion', label: 'Condición' },
      { key: 'precio', label: 'Precio', render: item => `<strong>${money(item.precio)}</strong>` },
      { key: 'estado', label: 'Estado', render: item => estadoBadge(item.estado) }
    ],
    fields: [
      { name: 'nombre', label: 'Nombre', type: 'text', required: true, placeholder: 'Ej. General 2D' },
      {
        name: 'tipoSalaId',
        label: 'Tipo de sala',
        type: 'select',
        required: true,
        options: () => [{ value: 0, label: 'Todas las salas' }, ...state.tipos.map(tipo => ({ value: tipo.id, label: tipo.nombre }))],
        default: 0
      },
      { name: 'condicion', label: 'Condición', type: 'select', required: true, placeholder: 'Selecciona una condición', options: ['General', 'Estudiante', 'Niño', 'Adulto Mayor', 'Socio'] },
      { name: 'precio', label: 'Precio (Bs)', type: 'number', required: true, min: 0, max: 10000, step: '0.01', placeholder: '25.00' },
      { name: 'estado', label: 'Estado', type: 'select', required: true, options: ['Activa', 'Inactiva'], default: 'Activa' }
    ]
  }
};

const $ = selector => document.querySelector(selector);
const $$ = selector => Array.from(document.querySelectorAll(selector));

let modalSubmitHandler = null;

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

function estadoBadge(estado) {
  const clase = estado === 'Activa' || estado === 'Disponible'
    ? 'success'
    : estado === 'Mantenimiento'
      ? 'warning'
      : 'neutral';
  return `<span class="badge ${clase}">${esc(estado)}</span>`;
}

function badgeFuncion(estado) {
  const clase = estado === 'Programada' ? 'success' : estado === 'Cancelada' ? 'danger' : 'neutral';
  return `<span class="badge ${clase}">${esc(estado)}</span>`;
}

function estadoCatalogoBadge(estado) {
  return `<span class="badge ${estado === 'Activo' ? 'success' : 'neutral'}">${esc(estado)}</span>`;
}

function badgeEntrada(estado) {
  const clase = estado === 'Emitida' ? 'success' : estado === 'Usada' ? 'outline' : 'danger';
  return `<span class="badge ${clase}">${esc(estado)}</span>`;
}

function badgeReserva(estado) {
  return `<span class="badge ${estado === 'Confirmada' ? 'success' : 'danger'}">${esc(estado)}</span>`;
}

function tipoTexto(item) {
  if (item.tipo === '2x1') return 'Dos entradas al precio de una';
  if (item.tipo === 'Descuento %') return `${item.valor}% de descuento`;
  return 'Función benéfica';
}

function resumenReserva(reserva) {
  return `${reserva.asientos.length} asiento(s) · ${esc(reserva.funcionTitulo || '')}`;
}

function esAdmin() {
  return state.sesion?.rol === 'Administrador';
}

function iniciales(nombre) {
  return String(nombre || '')
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map(parte => parte[0].toUpperCase())
    .join('') || '--';
}

function mostrarLogin(mensaje) {
  $('#login-screen').hidden = false;
  $('#app').hidden = true;
  $('#readonly-banner').hidden = true;
  document.body.classList.remove('role-usuario');
  if (mensaje) {
    $('#login-error').textContent = mensaje;
    $('#login-error').hidden = false;
  }
}

function limpiarSesion(mensaje) {
  state.token = null;
  state.sesion = null;
  mostrarLogin(mensaje);
}

async function request(url, options = {}, auth = true) {
  const headers = new Headers(options.headers || {});
  if (auth && state.token) {
    headers.set('Authorization', `Bearer ${state.token}`);
  }
  const response = await fetch(url, { ...options, headers });
  if (response.status === 204) {
    return null;
  }
  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    if (response.status === 401 && auth) {
      limpiarSesion('Tu sesión no es válida. Inicia sesión nuevamente.');
    }
    throw new Error(body.error || 'No se pudo completar la operación.');
  }
  return body;
}

async function loadState() {
  const [peliculas, tipos, salas, asientos, tarifas, funciones, productos, promociones, clientes, mediosPago] = await Promise.all([
    request('/api/peliculas'),
    request('/api/tipos-sala'),
    request('/api/salas'),
    request('/api/asientos'),
    request('/api/tarifas'),
    request('/api/funciones'),
    request('/api/productos'),
    request('/api/promociones'),
    request('/api/clientes'),
    request('/api/medios-pago')
  ]);
  state.peliculas = peliculas;
  state.tipos = tipos;
  state.salas = salas;
  state.asientos = asientos;
  state.tarifas = tarifas;
  state.funciones = funciones;
  state.productos = productos;
  state.promociones = promociones;
  state.clientes = clientes;
  state['medios-pago'] = mediosPago;
  if (!state.salaSeleccionada || !salas.some(sala => sala.id === state.salaSeleccionada)) {
    state.salaSeleccionada = salas.length ? salas[0].id : null;
  }
}

async function cargarReservas() {
  state.reservas = await request('/api/reservas');
  renderReservas();
  updateCounts();
}

async function cargarRecaudacion() {
  state.recaudacion = await request('/api/recaudacion');
  renderRecaudacion();
}

async function cargarUsuarios() {
  state.usuarios = await request('/api/usuarios');
  renderUsuarios();
  updateCounts();
}

async function cargarAuditoria() {
  state.auditoria = await request('/api/auditoria');
  renderAuditoria();
}

async function cargarEntradas() {
  state.entradas = await request('/api/entradas');
  renderEntradas();
  updateCounts();
}

async function cargarReembolsos() {
  state.reembolsos = await request('/api/reembolsos');
  renderReembolsos();
  updateCounts();
}

async function refresh() {
  await loadState();
  renderTables();
  renderSalaSelect();
  renderAsientos();
  updateCounts();
}

function rowsFor(entity) {
  const rows = state[entity] || [];
  if (entity === 'peliculas' && state.search.trim()) {
    const term = state.search.trim().toLowerCase();
    return rows.filter(row => [row.titulo, row.genero, row.clasificacion]
      .some(value => String(value).toLowerCase().includes(term)));
  }
  return rows;
}

function renderTable(entity) {
  const config = entities[entity];
  const rows = rowsFor(entity);
  const target = document.getElementById(`table-${entity}`);
  if (!rows.length) {
    target.innerHTML = '<div class="table-empty">No hay registros para mostrar.</div>';
    return;
  }
  const admin = esAdmin();
  const head = config.columns.map(column => `<th>${column.label}</th>`).join('') + (admin ? '<th></th>' : '');
  const body = rows.map(row => {
    const cells = config.columns.map(column => {
      const content = column.render ? column.render(row) : esc(row[column.key] ?? '');
      return `<td>${content}</td>`;
    }).join('');
    const actions = admin
      ? `<td class="actions">
          <button class="icon-btn" data-action="edit" data-entity="${entity}" data-id="${row.id}" title="Editar" aria-label="Editar">
            <svg class="icon"><use href="#i-edit"/></svg>
          </button>
          <button class="icon-btn danger" data-action="delete" data-entity="${entity}" data-id="${row.id}" title="Eliminar" aria-label="Eliminar">
            <svg class="icon"><use href="#i-trash"/></svg>
          </button>
        </td>`
      : '';
    return `<tr>${cells}${actions}</tr>`;
  }).join('');
  target.innerHTML = `<table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table>`;
}

function renderTables() {
  renderTable('peliculas');
  renderTable('funciones');
  renderTable('tipos');
  renderTable('salas');
  renderTable('tarifas');
  renderTable('productos');
  renderTable('promociones');
  renderTable('clientes');
  renderTable('medios-pago');
  updateCounts();
}

function updateCounts() {
  const peliculas = rowsFor('peliculas');
  $('#count-peliculas').textContent = `${peliculas.length} de ${state.peliculas.length} películas`;
  $('#count-tipos').textContent = `${state.tipos.length} tipos registrados`;
  $('#count-salas').textContent = `${state.salas.length} salas · ${state.asientos.length} asientos`;
  $('#count-tarifas').textContent = `${state.tarifas.length} tarifas registradas`;
  $('#count-funciones').textContent = `${state.funciones.length} funciones programadas`;
  $('#count-productos').textContent = `${state.productos.length} productos · ${state.productos.filter(p => p.bajoStock).length} con bajo stock`;
  $('#count-promociones').textContent = `${state.promociones.filter(p => p.vigente).length} vigentes de ${state.promociones.length}`;
  $('#count-clientes').textContent = `${state.clientes.length} clientes registrados`;
  $('#count-medios-pago').textContent = `${state['medios-pago'].length} medios configurados`;
  if ($('#count-reservas')) {
    $('#count-reservas').textContent = `${state.reservas.length} reservas registradas`;
  }
  if ($('#count-entradas')) {
    $('#count-entradas').textContent = `${state.entradas.length} entradas emitidas`;
  }
  if ($('#count-reembolsos')) {
    $('#count-reembolsos').textContent = `${state.reembolsos.length} reembolsos registrados`;
  }
  if ($('#count-usuarios')) {
    $('#count-usuarios').textContent = `${state.usuarios.length} usuarios`;
  }
}

function renderSalaSelect() {
  const select = $('#sala-select');
  select.innerHTML = state.salas.map(sala => `<option value="${sala.id}">${esc(sala.nombre)} · ${esc(sala.tipoSalaNombre)}</option>`).join('');
  if (state.salaSeleccionada && state.salas.some(sala => sala.id === state.salaSeleccionada)) {
    select.value = String(state.salaSeleccionada);
  } else if (state.salas.length) {
    state.salaSeleccionada = state.salas[0].id;
    select.value = String(state.salaSeleccionada);
  }
}

function renderAsientos() {
  const grid = $('#seat-grid');
  const counter = $('#count-asientos');
  if (!state.salas.length) {
    grid.innerHTML = '';
    counter.textContent = 'Sin salas registradas';
    return;
  }
  const salaId = Number($('#sala-select').value) || state.salaSeleccionada;
  const seats = state.asientos.filter(seat => seat.salaId === salaId);
  if (!seats.length) {
    grid.innerHTML = '<div class="table-empty">Esta sala no tiene asientos configurados.</div>';
    counter.textContent = '0 asientos';
    return;
  }
  const admin = esAdmin();
  const byRow = new Map();
  seats.forEach(seat => {
    if (!byRow.has(seat.fila)) {
      byRow.set(seat.fila, []);
    }
    byRow.get(seat.fila).push(seat);
  });
  grid.innerHTML = Array.from(byRow.entries())
    .sort((a, b) => a[0].localeCompare(b[0]))
    .map(([fila, lista]) => `
      <div class="seat-row">
        <span class="row-label">${esc(fila)}</span>
        ${lista.sort((a, b) => a.numero - b.numero).map(seat => `
          <button class="seat ${seat.estado === 'Disponible' ? '' : 'maintenance'}"
            ${admin ? 'data-action="toggle-seat"' : 'disabled'}
            data-id="${seat.id}"
            title="${admin ? `Asiento ${esc(fila)}${seat.numero} · ${esc(seat.estado)}` : 'Solo consulta'}"
            aria-label="Asiento ${esc(fila)}${seat.numero}, ${esc(seat.estado)}">
            ${seat.numero}
          </button>
        `).join('')}
      </div>
    `).join('');
  const disponibles = seats.filter(seat => seat.estado === 'Disponible').length;
  counter.textContent = `${disponibles} disponibles de ${seats.length} asientos`;
}

function renderReservas() {
  const target = $('#table-reservas');
  if (!target) {
    return;
  }
  const rows = state.reservas;
  if (!rows.length) {
    target.innerHTML = '<div class="table-empty">Todavía no hay reservas registradas.</div>';
    return;
  }
  const admin = esAdmin();
  target.innerHTML = `<table><thead><tr>
      <th>Código</th><th>Cliente</th><th>Función</th><th>Asientos</th><th>Confitería</th><th>Total</th><th>Estado</th><th>Fecha</th>${admin ? '<th></th>' : ''}
    </tr></thead><tbody>${rows.map(row => `
      <tr>
        <td><strong>${esc(row.codigo)}</strong></td>
        <td>${esc(row.cliente)}</td>
        <td><div class="cell-title"><strong>${esc(row.peliculaTitulo || '—')}</strong><span class="sub">${esc(row.salaNombre || '')} · ${esc(row.fecha || '')} ${esc(row.hora || '')}</span></div></td>
        <td>${(row.asientosDetalle || []).map(a => esc(a.etiqueta)).join(', ')}</td>
        <td>${row.productosDetalle && row.productosDetalle.length ? row.productosDetalle.map(p => `${esc(p.nombre)} x${p.cantidad}`).join('<br>') : '<span class="sub">Sin productos</span>'}</td>
        <td><strong>${money(row.total)}</strong>${row.descuento ? `<span class="sub">Desc. ${money(row.descuento)}</span>` : ''}</td>
        <td>${badgeReserva(row.estado)}</td>
        <td>${esc(row.creadaEn || '—')}</td>
        ${admin ? `<td class="actions">${row.estado === 'Confirmada' ? `<button class="icon-btn danger" data-action="cancelar-reserva" data-id="${row.id}" title="Cancelar y reembolsar" aria-label="Cancelar reserva"><svg class="icon"><use href="#i-refund"/></svg></button>` : ''}</td>` : ''}
      </tr>`).join('')}</tbody></table>`;
}

function renderEntradas() {
  const target = $('#table-entradas');
  if (!target) {
    return;
  }
  const rows = state.entradas;
  if (!rows.length) {
    target.innerHTML = '<div class="table-empty">Aún no se han emitido entradas.</div>';
    return;
  }
  target.innerHTML = `<table><thead><tr>
      <th>Código</th><th>Película</th><th>Asiento</th><th>Reserva</th><th>Precio</th><th>Estado</th><th>Emitida</th>
    </tr></thead><tbody>${rows.map(row => `
      <tr>
        <td><strong>${esc(row.codigo)}</strong></td>
        <td><div class="cell-title"><strong>${esc(row.peliculaTitulo || '—')}</strong><span class="sub">${esc(row.salaNombre || '')} · ${esc(row.fecha || '')} ${esc(row.hora || '')}</span></div></td>
        <td>${esc(row.etiqueta)}</td>
        <td>${esc(row.reservaCodigo || '—')}</td>
        <td>${money(row.precio)}</td>
        <td>${badgeEntrada(row.estado)}</td>
        <td>${esc(row.emitidaEn || '—')}</td>
      </tr>`).join('')}</tbody></table>`;
}

function renderReembolsos() {
  const target = $('#table-reembolsos');
  if (!target) {
    return;
  }
  const rows = state.reembolsos;
  if (!rows.length) {
    target.innerHTML = '<div class="table-empty">No se han registrado cancelaciones ni reembolsos.</div>';
    return;
  }
  target.innerHTML = `<table><thead><tr>
      <th>Reserva</th><th>Cliente</th><th>Monto reembolsado</th><th>Motivo</th><th>Medio</th><th>Fecha</th>
    </tr></thead><tbody>${rows.map(row => `
      <tr>
        <td><strong>${esc(row.reservaCodigo)}</strong></td>
        <td>${esc(row.cliente)}</td>
        <td><strong>${money(row.montoReembolsado)}</strong></td>
        <td>${esc(row.motivo || '—')}</td>
        <td>${esc(row.medio || '—')}</td>
        <td>${esc(row.registradoEn || '—')}</td>
      </tr>`).join('')}</tbody></table>`;
}

function renderRecaudacion() {
  const data = state.recaudacion;
  if (!data) {
    return;
  }
  const resumen = data.resumen || {};
  $('#recaudacion-stats').innerHTML = `
    <div class="stat"><span class="stat-label">Ingresos totales</span><strong class="stat-value">${money(resumen.ingresosTotales)}</strong><span class="stat-sub">${resumen.reservas || 0} reservas confirmadas</span></div>
    <div class="stat"><span class="stat-label">Entradas</span><strong class="stat-value">${money(resumen.ingresosEntradas)}</strong><span class="stat-sub">${resumen.entradasVendidas || 0} asientos vendidos</span></div>
    <div class="stat"><span class="stat-label">Confitería</span><strong class="stat-value">${money(resumen.ingresosConfiteria)}</strong><span class="stat-sub">${resumen.funcionesProgramadas || 0} funciones programadas</span></div>
    <div class="stat"><span class="stat-label">Productos bajo stock</span><strong class="stat-value">${resumen.productosBajoStock || 0}</strong><span class="stat-sub">${resumen.funcionesBeneficas || 0} funciones benéficas</span></div>`;

  const stock = $('#recaudacion-stock');
  const bajos = data.bajoStock || [];
  stock.innerHTML = bajos.length
    ? `<ul class="stock-list">${bajos.map(p => `<li><span>${esc(p.nombre)}</span><span class="badge ${p.stock === 0 ? 'danger' : 'warning'}">${p.stock} u.</span></li>`).join('')}</ul>`
    : '<div class="table-empty">Todos los productos tienen stock suficiente.</div>';

  const recientes = data.reservas || [];
  $('#recaudacion-reservas').innerHTML = recientes.length
    ? `<table><thead><tr><th>Código</th><th>Cliente</th><th>Total</th></tr></thead><tbody>${recientes.map(r => `<tr><td><strong>${esc(r.codigo)}</strong></td><td>${esc(r.cliente)}</td><td>${money(r.total)}</td></tr>`).join('')}</tbody></table>`
    : '<div class="table-empty">Sin reservas recientes.</div>';
}

function renderUsuarios() {
  const target = $('#table-usuarios');
  if (!target) {
    return;
  }
  const rows = state.usuarios;
  if (!rows.length) {
    target.innerHTML = '<div class="table-empty">No hay usuarios registrados.</div>';
    return;
  }
  target.innerHTML = `<table><thead><tr><th>Usuario</th><th>Nombre</th><th>Rol</th></tr></thead><tbody>${rows.map(row => `
    <tr>
      <td><strong>${esc(row.usuario)}</strong></td>
      <td>${esc(row.nombre)}</td>
      <td><span class="badge ${row.rol === 'Administrador' ? 'success' : 'outline'}">${esc(row.rol)}</span></td>
    </tr>`).join('')}</tbody></table>`;
}

function renderAuditoria() {
  const target = $('#table-auditoria');
  if (!target) {
    return;
  }
  const rows = state.auditoria;
  if (!rows.length) {
    target.innerHTML = '<div class="table-empty">Todavía no hay acciones registradas.</div>';
    return;
  }
  target.innerHTML = `<table><thead><tr><th>Fecha</th><th>Usuario</th><th>Acción</th><th>Recurso</th></tr></thead><tbody>${rows.map(row => `
    <tr>
      <td>${esc(row.fecha)}</td>
      <td><strong>${esc(row.usuario)}</strong></td>
      <td><span class="badge outline">${esc(row.accion)}</span></td>
      <td>${esc(row.detalle)}</td>
    </tr>`).join('')}</tbody></table>`;
}

function showSection(section, updateHash = true) {
  if (!sectionMeta[section]) {
    section = 'peliculas';
  }
  state.section = section;
  $$('.nav-item').forEach(item => item.classList.toggle('active', item.dataset.section === section));
  $$('.view').forEach(view => view.classList.remove('active'));
  const meta = sectionMeta[section];
  document.getElementById(`view-${section}`).classList.add('active');
  $('#page-title').textContent = meta.title;
  $('#page-eyebrow').textContent = meta.eyebrow;
  if (section === 'asientos') {
    renderSalaSelect();
    renderAsientos();
  }
  cargarSeccion(section);
  if (updateHash) {
    history.replaceState(null, '', `#${section}`);
  }
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function cargarSeccion(section) {
  if (section === 'reservas') {
    cargarReservas().catch(error => toast(error.message, 'error'));
  } else if (section === 'recaudacion') {
    cargarRecaudacion().catch(error => toast(error.message, 'error'));
  } else if (section === 'usuarios') {
    cargarUsuarios().catch(error => toast(error.message, 'error'));
  } else if (section === 'auditoria') {
    cargarAuditoria().catch(error => toast(error.message, 'error'));
  } else if (section === 'entradas') {
    cargarEntradas().catch(error => toast(error.message, 'error'));
  } else if (section === 'reembolsos') {
    cargarReembolsos().catch(error => toast(error.message, 'error'));
  }
}

function renderField(field, value) {
  const id = `field-${field.name}`;
  const required = field.required ? 'required' : '';
  const full = field.full ? 'full' : '';
  let control = '';

  if (field.type === 'select') {
    const options = typeof field.options === 'function' ? field.options() : (field.options || []);
    const placeholder = field.placeholder ? `<option value="">${esc(field.placeholder)}</option>` : '';
    control = `<select id="${id}" name="${field.name}" ${required}>${placeholder}${options.map(option => {
      const optionValue = typeof option === 'object' ? option.value : option;
      const optionLabel = typeof option === 'object' ? option.label : option;
      const selected = String(value) === String(optionValue) ? 'selected' : '';
      return `<option value="${esc(optionValue)}" ${selected}>${esc(optionLabel)}</option>`;
    }).join('')}</select>`;
  } else if (field.type === 'textarea') {
    control = `<textarea id="${id}" name="${field.name}" ${required} placeholder="${esc(field.placeholder || '')}">${esc(value)}</textarea>`;
  } else {
    const min = field.min !== undefined ? `min="${field.min}"` : '';
    const max = field.max !== undefined ? `max="${field.max}"` : '';
    const step = field.step !== undefined ? `step="${field.step}"` : '';
    control = `<input id="${id}" type="${field.type || 'text'}" name="${field.name}" value="${esc(value)}" ${required} ${min} ${max} ${step} placeholder="${esc(field.placeholder || '')}">`;
  }

  return `<label class="field ${full}" for="${id}"><span>${esc(field.label)}${field.required ? ' *' : ''}</span>${control}</label>`;
}

function openModal({ title, eyebrow = 'Iteración 1', fields, values = {}, submitLabel = 'Guardar', onSubmit }) {
  modalSubmitHandler = onSubmit;
  $('#modal-title').textContent = title;
  $('#modal-eyebrow').textContent = eyebrow;
  $('#modal-submit').textContent = submitLabel;
  $('#modal-fields').innerHTML = fields.map(field => {
    const value = values[field.name] ?? field.value ?? field.default ?? '';
    return renderField(field, value);
  }).join('');
  $('#modal').hidden = false;
  document.body.style.overflow = 'hidden';
  const first = $('#modal-form').querySelector('input, select, textarea');
  if (first) {
    setTimeout(() => first.focus(), 60);
  }
}

function closeModal() {
  $('#modal').hidden = true;
  document.body.style.overflow = '';
  modalSubmitHandler = null;
  $('#modal-form').reset();
  $('#modal-fields').innerHTML = '';
}

function openEntityForm(entity, id) {
  const config = entities[entity];
  const item = id ? state[entity].find(row => row.id === id) : null;
  openModal({
    title: `${item ? 'Editar' : 'Nueva'} ${config.singular}`,
    fields: config.fields,
    values: item || {},
    submitLabel: item ? 'Guardar cambios' : 'Crear registro',
    onSubmit: async data => {
      const url = item ? `${config.endpoint}/${item.id}` : config.endpoint;
      await request(url, {
        method: item ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      await refresh();
      toast(`${config.name}: registro ${item ? 'actualizado' : 'creado'} correctamente.`);
    }
  });
}

async function deleteEntity(entity, id) {
  const config = entities[entity];
  const item = state[entity].find(row => row.id === id);
  const name = item?.nombre || item?.titulo || config.singular;
  if (!window.confirm(`¿Eliminar "${name}"? Esta acción no se puede deshacer.`)) {
    return;
  }
  try {
    await request(`${config.endpoint}/${id}`, { method: 'DELETE' });
    await refresh();
    toast(`${config.name}: registro eliminado.`);
  } catch (error) {
    toast(error.message, 'error');
  }
}

async function toggleSeat(id) {
  const seat = state.asientos.find(row => row.id === id);
  if (!seat) {
    return;
  }
  const nuevoEstado = seat.estado === 'Disponible' ? 'Mantenimiento' : 'Disponible';
  try {
    const updated = await request(`/api/asientos/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado: nuevoEstado })
    });
    const index = state.asientos.findIndex(row => row.id === id);
    if (index >= 0) {
      state.asientos[index] = updated;
    }
    renderAsientos();
    updateCounts();
    toast(`Asiento ${seat.fila}${seat.numero}: ${nuevoEstado.toLowerCase()}.`);
  } catch (error) {
    toast(error.message, 'error');
  }
}

function openRedistribuir() {
  const sala = state.salas.find(row => row.id === state.salaSeleccionada);
  if (!sala) {
    toast('Primero debes crear una sala.', 'error');
    return;
  }
  const seats = state.asientos.filter(row => row.salaId === sala.id);
  const filasActuales = seats.length ? new Set(seats.map(seat => seat.fila)).size : Math.ceil(sala.capacidad / 10);
  const porFilaActual = seats.length ? Math.max(...seats.map(seat => seat.numero)) : 10;
  openModal({
    title: `Redistribuir asientos · ${sala.nombre}`,
    eyebrow: 'HU-04 · Distribución',
    fields: [
      { name: 'filas', label: 'Filas (A-Z)', type: 'number', required: true, min: 1, max: 26, placeholder: '6' },
      { name: 'porFila', label: 'Asientos por fila', type: 'number', required: true, min: 1, max: 30, placeholder: '10' }
    ],
    values: { filas: filasActuales, porFila: porFilaActual },
    submitLabel: 'Redistribuir',
    onSubmit: async data => {
      await request(`/api/salas/${sala.id}/asientos/generar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      await refresh();
      toast('Asientos redistribuidos correctamente.');
    }
  });
}

function openCancelarReserva(id) {
  const reserva = state.reservas.find(row => row.id === id);
  if (!reserva) {
    return;
  }
  openModal({
    title: `Cancelar reserva ${reserva.codigo}`,
    eyebrow: 'HU-35 · Cancelaciones y reembolsos',
    fields: [
      { name: 'motivo', label: 'Motivo de la cancelación', type: 'select', required: true, options: ['Solicitud del cliente', 'Cambio de función', 'Error de compra', 'Función cancelada', 'Otro'] },
      { name: 'medio', label: 'Medio de reembolso', type: 'select', required: true, options: ['Efectivo', 'Tarjeta', 'QR'], default: reserva.medioPago || 'Efectivo' }
    ],
    submitLabel: 'Cancelar y reembolsar',
    onSubmit: async data => {
      await request(`/api/reservas/${id}/cancelar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      await cargarReservas();
      await loadState();
      renderTable('funciones');
      renderTable('productos');
      updateCounts();
      toast(`Reserva ${reserva.codigo} cancelada y reembolsada.`);
    }
  });
}

function openUsuarioForm() {
  openModal({
    title: 'Nuevo usuario',
    eyebrow: 'HU-30 · Seguridad',
    fields: [
      { name: 'nombre', label: 'Nombre completo', type: 'text', required: true, placeholder: 'Ej. Ana Gutiérrez' },
      { name: 'usuario', label: 'Usuario', type: 'text', required: true, placeholder: 'ana.gutierrez' },
      { name: 'contrasena', label: 'Contraseña', type: 'password', required: true, placeholder: 'Mínimo 4 caracteres' },
      { name: 'rol', label: 'Rol', type: 'select', required: true, options: ['Administrador', 'Usuario'], default: 'Usuario' }
    ],
    submitLabel: 'Crear usuario',
    onSubmit: async data => {
      await request('/api/usuarios', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      await cargarUsuarios();
      toast('Usuario creado correctamente.');
    }
  });
}

function toast(message, type = 'success') {
  const element = document.createElement('div');
  element.className = `toast ${type === 'error' ? 'error' : ''}`;
  element.textContent = message;
  $('#toasts').appendChild(element);
  setTimeout(() => {
    element.classList.add('out');
    setTimeout(() => element.remove(), 260);
  }, 3200);
}

function aplicarSesion(sesion) {
  state.sesion = sesion;
  const admin = sesion.rol === 'Administrador';
  $('#app').hidden = false;
  $('#login-screen').hidden = true;
  $('#login-error').hidden = true;
  $('#sidebar-initials').textContent = iniciales(sesion.nombre);
  $('#sidebar-user-name').textContent = sesion.nombre;
  $('#sidebar-user-role').textContent = sesion.rol;
  $('#role-pill').textContent = sesion.rol;
  $('#role-pill').classList.toggle('admin', admin);
  $('#role-pill').classList.toggle('user', !admin);
  $('#readonly-banner').hidden = admin;
  document.body.classList.toggle('role-usuario', !admin);
}

async function entrarAlSistema(section) {
  try {
    await refresh();
    showSection(sectionMeta[section] ? section : 'peliculas', false);
  } catch (error) {
    toast(`No se pudo cargar la información: ${error.message}`, 'error');
    renderTables();
    renderSalaSelect();
    renderAsientos();
  }
}

async function iniciarSesion(event) {
  event.preventDefault();
  const errorBox = $('#login-error');
  const button = $('#login-submit');
  errorBox.hidden = true;
  button.disabled = true;
  button.textContent = 'Ingresando...';
  try {
    const sesion = await request('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        usuario: $('#login-usuario').value.trim(),
        contrasena: $('#login-contrasena').value,
        rol: state.rolLogin
      })
    }, false);
    state.token = sesion.token;
    if (sesion.rol !== 'Administrador') {
      window.location.href = 'cartelera.html';
      return;
    }
    aplicarSesion(sesion);
    $('#login-form').reset();
    window.history.replaceState(null, '', window.location.pathname);
    await entrarAlSistema('peliculas');
    toast(`Bienvenido, ${sesion.nombre}.`);
  } catch (error) {
    errorBox.textContent = error.message;
    errorBox.hidden = false;
  } finally {
    button.disabled = false;
    button.textContent = 'Ingresar';
  }
}

async function cerrarSesion() {
  const token = state.token;
  limpiarSesion();
  if (token) {
    fetch('/api/logout', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` }
    }).catch(() => {});
  }
  $('#login-form').reset();
  $('#login-usuario').focus();
}

function bindAuthEvents() {
  $$('.role-card').forEach(card => {
    card.addEventListener('click', () => {
      state.rolLogin = card.dataset.role;
      $$('.role-card').forEach(other => other.classList.toggle('active', other === card));
      $('#login-error').hidden = true;
    });
  });
  $('#login-form').addEventListener('submit', iniciarSesion);
  $('#logout-btn').addEventListener('click', cerrarSesion);
  ['#login-usuario', '#login-contrasena'].forEach(selector => {
    $(selector).addEventListener('input', () => {
      $('#login-error').hidden = true;
    });
  });
}

function bindEvents() {
  $$('.nav-item').forEach(item => {
    item.addEventListener('click', () => showSection(item.dataset.section));
  });

  document.addEventListener('click', event => {
    const target = event.target.closest('[data-action]');
    if (!target) {
      return;
    }
    const action = target.dataset.action;
    if (action === 'refresh-reservas') {
      cargarReservas().then(() => toast('Reservas actualizadas.')).catch(error => toast(error.message, 'error'));
      return;
    }
    if (action === 'refresh-recaudacion') {
      cargarRecaudacion().then(() => toast('Recaudación actualizada.')).catch(error => toast(error.message, 'error'));
      return;
    }
    if (action === 'refresh-auditoria') {
      cargarAuditoria().then(() => toast('Auditoría actualizada.')).catch(error => toast(error.message, 'error'));
      return;
    }
    if (action === 'refresh-entradas') {
      cargarEntradas().then(() => toast('Entradas actualizadas.')).catch(error => toast(error.message, 'error'));
      return;
    }
    if (action === 'refresh-reembolsos') {
      cargarReembolsos().then(() => toast('Reembolsos actualizados.')).catch(error => toast(error.message, 'error'));
      return;
    }
    if (!esAdmin()) {
      toast('Tu perfil es de solo consulta.', 'error');
      return;
    }
    if (action === 'cancelar-reserva') {
      openCancelarReserva(Number(target.dataset.id));
    } else if (action === 'new-usuario') {
      openUsuarioForm();
    } else if (action === 'new') {
      openEntityForm(target.dataset.entity);
    } else if (action === 'edit') {
      openEntityForm(target.dataset.entity, Number(target.dataset.id));
    } else if (action === 'delete') {
      deleteEntity(target.dataset.entity, Number(target.dataset.id));
    } else if (action === 'toggle-seat') {
      toggleSeat(Number(target.dataset.id));
    } else if (action === 'redistribuir') {
      openRedistribuir();
    }
  });

  $('#search-peliculas').addEventListener('input', event => {
    state.search = event.target.value;
    renderTable('peliculas');
    updateCounts();
  });

  $('#sala-select').addEventListener('change', event => {
    state.salaSeleccionada = Number(event.target.value);
    renderAsientos();
  });

  $('#modal-close').addEventListener('click', closeModal);
  $('#modal-cancel').addEventListener('click', closeModal);
  $('#modal').addEventListener('mousedown', event => {
    if (event.target.id === 'modal') {
      closeModal();
    }
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape' && !$('#modal').hidden) {
      closeModal();
    }
  });

  $('#modal-form').addEventListener('submit', async event => {
    event.preventDefault();
    if (!modalSubmitHandler) {
      return;
    }
    const button = $('#modal-submit');
    const original = button.textContent;
    button.disabled = true;
    button.textContent = 'Guardando...';
    try {
      const form = event.currentTarget;
      const data = {};
      Array.from(form.elements).forEach(element => {
        if (!element.name) {
          return;
        }
        if (element.type === 'number') {
          data[element.name] = element.value === '' ? null : Number(element.value);
        } else {
          data[element.name] = element.value.trim();
        }
      });
      await modalSubmitHandler(data);
      closeModal();
    } catch (error) {
      toast(error.message, 'error');
    } finally {
      button.disabled = false;
      button.textContent = original;
    }
  });
}

async function init() {
  bindAuthEvents();
  bindEvents();
  mostrarLogin();
}

document.addEventListener('DOMContentLoaded', init);
