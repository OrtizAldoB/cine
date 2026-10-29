package com.cine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CineStore {
    private static final Set<String> CLASIFICACIONES = Set.of("TP", "+7", "+13", "+16", "+18");
    private static final Set<String> CONDICIONES = Set.of("General", "Estudiante", "Niño", "Adulto Mayor", "Socio");
    private static final Set<String> ESTADOS_FUNCION = Set.of("Programada", "Cancelada");
    private static final Set<String> CATEGORIAS_CONFITERIA = Set.of("Combos", "Palomitas", "Bebidas", "Snacks", "Dulces");
    private static final Set<String> TIPOS_PROMOCION = Set.of("2x1", "Descuento %", "Función benéfica");
    private static final Set<String> MEDIOS_PAGO = Set.of("QR", "Tarjeta", "Efectivo");
    private static final Set<String> ESTADOS_ENTRADA = Set.of("Emitida", "Usada", "Anulada");
    private static final Set<String> ESTADOS_RESERVA = Set.of("Confirmada", "Cancelada");
    private static final int ASIENTOS_POR_FILA = 10;
    private static final int BUFFER_ENTRE_FUNCIONES = 15;
    private static final int HORAS_LIMITE_REEMBOLSO = 2;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Map<Integer, Pelicula> peliculas = new LinkedHashMap<>();
    private final Map<Integer, TipoSala> tiposSala = new LinkedHashMap<>();
    private final Map<Integer, Sala> salas = new LinkedHashMap<>();
    private final Map<Integer, Asiento> asientos = new LinkedHashMap<>();
    private final Map<Integer, Tarifa> tarifas = new LinkedHashMap<>();
    private final Map<Integer, Funcion> funciones = new LinkedHashMap<>();
    private final Map<Integer, Producto> productos = new LinkedHashMap<>();
    private final Map<Integer, Promocion> promociones = new LinkedHashMap<>();
    private final Map<Integer, Reserva> reservas = new LinkedHashMap<>();
    private final Map<Integer, Cliente> clientes = new LinkedHashMap<>();
    private final Map<Integer, MedioPago> mediosPago = new LinkedHashMap<>();
    private final Map<Integer, Entrada> entradas = new LinkedHashMap<>();
    private final Map<Integer, Reembolso> reembolsos = new LinkedHashMap<>();
    private final List<Map<String, Object>> auditoria = new ArrayList<>();

    private int peliculaSeq = 1;
    private int tipoSalaSeq = 1;
    private int salaSeq = 1;
    private int asientoSeq = 1;
    private int tarifaSeq = 1;
    private int funcionSeq = 1;
    private int productoSeq = 1;
    private int promocionSeq = 1;
    private int reservaSeq = 1;
    private int clienteSeq = 1;
    private int medioPagoSeq = 1;
    private int entradaSeq = 1;
    private int reembolsoSeq = 1;

    public CineStore() {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        tiposSala.put(tipoSalaSeq, new TipoSala(tipoSalaSeq++, "2D",
                "Proyección digital estándar en dos dimensiones.", "Activa"));
        tiposSala.put(tipoSalaSeq, new TipoSala(tipoSalaSeq++, "3D",
                "Proyección con profundidad y lentes estereoscópicos.", "Activa"));

        salas.put(salaSeq, new Sala(salaSeq++, "Sala 1", 1, 60,
                "Planta baja · Pantalla de 12 m · Sonido Dolby", "Activa"));
        salas.put(salaSeq, new Sala(salaSeq++, "Sala 2", 2, 50,
                "Planta alta · Pantalla de 10 m · Proyección 3D", "Activa"));
        salas.put(salaSeq, new Sala(salaSeq++, "Sala 3", 1, 80,
                "Planta baja · Pantalla de 15 m · Sonido envolvente", "Activa"));

        for (Sala sala : salas.values()) {
            ajustarAsientos(sala.id(), sala.capacidad());
        }

        agregarPelicula("El Último Horizonte", "Ciencia ficción", "+13", 128,
                "Una tripulación busca un nuevo hogar para la humanidad.",
                "Activa");
        agregarPelicula("Risas en Cadena", "Comedia", "TP", 95,
                "Una familia descubre que la risa se contagia de verdad.",
                "Activa");
        agregarPelicula("Noche de Sombras", "Terror", "+16", 110,
                "Un pueblo queda atrapado bajo una oscuridad que no cesa.",
                "Activa");
        agregarPelicula("Corazón de Acero", "Acción", "+13", 135,
                "Un exsoldado debe proteger la ciudad que lo vio caer.",
                "Activa");
        agregarPelicula("Viaje a la Luna", "Aventura", "TP", 88,
                "Dos hermanos construyen un cohete en el patio de su casa.",
                "Inactiva");

        agregarTarifa("General 2D", 1, "General", 25.00, "Activa");
        agregarTarifa("General 3D", 2, "General", 35.00, "Activa");
        agregarTarifa("Estudiante 2D", 1, "Estudiante", 18.00, "Activa");
        agregarTarifa("Estudiante 3D", 2, "Estudiante", 25.00, "Activa");
        agregarTarifa("Niño 2D", 1, "Niño", 15.00, "Activa");
        agregarTarifa("Adulto Mayor 2D", 1, "Adulto Mayor", 15.00, "Activa");
        agregarTarifa("Socio 3D", 2, "Socio", 28.00, "Inactiva");

        LocalDate hoy = LocalDate.now();
        agregarFuncion(1, 1, hoy.toString(), "14:30", "Programada");
        agregarFuncion(2, 2, hoy.toString(), "16:00", "Programada");
        agregarFuncion(3, 1, hoy.toString(), "18:45", "Programada");
        agregarFuncion(1, 2, hoy.plusDays(1).toString(), "15:00", "Programada");
        agregarFuncion(4, 3, hoy.plusDays(1).toString(), "19:30", "Programada");
        agregarFuncion(2, 3, hoy.plusDays(2).toString(), "20:00", "Programada");

        agregarProducto("Combo Clásico", "Combos", 45.00, 40, 10, "Activa");
        agregarProducto("Combo Dúo", "Combos", 65.00, 25, 8, "Activa");
        agregarProducto("Palomitas Grandes", "Palomitas", 28.00, 30, 10, "Activa");
        agregarProducto("Refresco 600 ml", "Bebidas", 18.00, 50, 15, "Activa");
        agregarProducto("Nachos con Queso", "Snacks", 22.00, 8, 10, "Activa");
        agregarProducto("Chocolate Artesanal", "Dulces", 12.00, 3, 6, "Activa");
        agregarProducto("Combo Familiar", "Combos", 95.00, 0, 5, "Inactiva");

        agregarPromocion("2x1 Martes de Cine", "2x1", 0,
                "", hoy.minusDays(30).toString(), hoy.plusDays(60).toString(), "Activa");
        agregarPromocion("Descuento Estudiante 20%", "Descuento %", 20,
                "", hoy.minusDays(10).toString(), hoy.plusDays(90).toString(), "Activa");
        agregarPromocion("Función Benéfica Hogar San José", "Función benéfica", 0,
                "Hogar San José", hoy.toString(), hoy.plusDays(30).toString(), "Activa");
        agregarPromocion("Miércoles de Palomitas", "2x1", 0,
                "", hoy.minusDays(60).toString(), hoy.minusDays(5).toString(), "Inactiva");

        agregarCliente("María Fernández", "7712345", "maria@ejemplo.com", "71234567", "Activa");
        agregarCliente("Javier Quispe", "8891234", "javier@ejemplo.com", "69876543", "Activa");
        agregarCliente("Lucía Rojas", "6654321", "lucia@ejemplo.com", "72345678", "Inactiva");

        agregarMedioPago("Efectivo", "Pago en ventanilla de boletería.", "Activo");
        agregarMedioPago("Tarjeta", "Débito o crédito con POS.", "Activo");
        agregarMedioPago("QR", "Pago mediante código QR bancario.", "Activo");

        Map<String, Object> reservaInicial = new LinkedHashMap<>();
        reservaInicial.put("funcionId", 2);
        reservaInicial.put("asientos", List.of(61, 62));
        reservaInicial.put("productos", Map.of("1", 1));
        reservaInicial.put("cliente", "María Fernández");
        reservaInicial.put("email", "maria@ejemplo.com");
        reservaInicial.put("medioPago", "QR");
        crearReserva(reservaInicial, "Sistema");
    }

    private void agregarCliente(String nombre, String documento, String email, String telefono, String estado) {
        clientes.put(clienteSeq, new Cliente(clienteSeq++, nombre, documento, email, telefono, estado));
    }

    private void agregarMedioPago(String nombre, String descripcion, String estado) {
        mediosPago.put(medioPagoSeq, new MedioPago(medioPagoSeq++, nombre, descripcion, estado));
    }

    private void agregarPelicula(String titulo, String genero, String clasificacion, int duracion,
            String sinopsis, String estado) {
        peliculas.put(peliculaSeq, new Pelicula(peliculaSeq++, titulo, genero, clasificacion,
                duracion, estado, sinopsis));
    }

    private void agregarTarifa(String nombre, int tipoSalaId, String condicion, double precio, String estado) {
        tarifas.put(tarifaSeq, new Tarifa(tarifaSeq++, nombre, tipoSalaId, condicion, precio, estado));
    }

    private void agregarFuncion(int peliculaId, int salaId, String fecha, String hora, String estado) {
        funciones.put(funcionSeq, new Funcion(funcionSeq++, peliculaId, salaId, fecha, hora, estado));
    }

    private void agregarProducto(String nombre, String categoria, double precio, int stock, int stockMinimo,
            String estado) {
        productos.put(productoSeq, new Producto(productoSeq++, nombre, categoria, precio, stock, stockMinimo,
                estado));
    }

    private void agregarPromocion(String nombre, String tipo, double valor, String beneficiario, String fechaInicio,
            String fechaFin, String estado) {
        promociones.put(promocionSeq, new Promocion(promocionSeq++, nombre, tipo, valor, beneficiario,
                fechaInicio, fechaFin, estado));
    }

    public synchronized List<Map<String, Object>> listPeliculas() {
        return peliculas.values().stream()
                .sorted(Comparator.comparingInt(Pelicula::id))
                .map(Pelicula::toMap)
                .toList();
    }

    public synchronized Map<String, Object> getPelicula(int id) {
        return pelicula(id).toMap();
    }

    public synchronized Map<String, Object> createPelicula(Map<String, Object> body) {
        String titulo = requerido(body, "titulo", "título");
        String genero = requerido(body, "genero", "género");
        String clasificacion = requerido(body, "clasificacion", "clasificación");
        validarClasificacion(clasificacion);
        int duracion = enteroRequerido(body, "duracion", "duración", 1, 600);
        String estado = estadoActivo(body, "Activa");
        String sinopsis = opcional(body, "sinopsis");
        validarTituloUnico(titulo, 0);

        Pelicula pelicula = new Pelicula(peliculaSeq++, titulo, genero, clasificacion,
                duracion, estado, sinopsis);
        peliculas.put(pelicula.id(), pelicula);
        return pelicula.toMap();
    }

    public synchronized Map<String, Object> updatePelicula(int id, Map<String, Object> body) {
        Pelicula actual = pelicula(id);
        String titulo = requerido(body, "titulo", "título");
        String genero = requerido(body, "genero", "género");
        String clasificacion = requerido(body, "clasificacion", "clasificación");
        validarClasificacion(clasificacion);
        int duracion = enteroRequerido(body, "duracion", "duración", 1, 600);
        String estado = estadoActivo(body, actual.estado());
        String sinopsis = opcional(body, "sinopsis");
        validarTituloUnico(titulo, id);

        Pelicula actualizada = new Pelicula(id, titulo, genero, clasificacion, duracion,
                estado, sinopsis);
        peliculas.put(id, actualizada);
        return actualizada.toMap();
    }

    public synchronized void deletePelicula(int id) {
        pelicula(id);
        peliculas.remove(id);
    }

    public synchronized List<Map<String, Object>> listTiposSala() {
        return tiposSala.values().stream()
                .sorted(Comparator.comparingInt(TipoSala::id))
                .map(TipoSala::toMap)
                .toList();
    }

    public synchronized Map<String, Object> getTipoSala(int id) {
        return tipoSala(id).toMap();
    }

    public synchronized Map<String, Object> createTipoSala(Map<String, Object> body) {
        String nombre = requerido(body, "nombre", "nombre");
        String descripcion = opcional(body, "descripcion");
        String estado = estadoActivo(body, "Activa");
        validarTipoSalaUnico(nombre, 0);

        TipoSala tipo = new TipoSala(tipoSalaSeq++, nombre, descripcion, estado);
        tiposSala.put(tipo.id(), tipo);
        return tipo.toMap();
    }

    public synchronized Map<String, Object> updateTipoSala(int id, Map<String, Object> body) {
        TipoSala actual = tipoSala(id);
        String nombre = requerido(body, "nombre", "nombre");
        String descripcion = opcional(body, "descripcion");
        String estado = estadoActivo(body, actual.estado());
        validarTipoSalaUnico(nombre, id);

        TipoSala actualizado = new TipoSala(id, nombre, descripcion, estado);
        tiposSala.put(id, actualizado);
        return actualizado.toMap();
    }

    public synchronized void deleteTipoSala(int id) {
        tipoSala(id);
        boolean enUsoSala = salas.values().stream().anyMatch(s -> s.tipoSalaId() == id);
        boolean enUsoTarifa = tarifas.values().stream().anyMatch(t -> t.tipoSalaId() == id);
        if (enUsoSala || enUsoTarifa) {
            throw new ApiException(409,
                    "No se puede eliminar el tipo de sala porque está asignado a salas o tarifas.");
        }
        tiposSala.remove(id);
    }

    public synchronized List<Map<String, Object>> listSalas() {
        return salas.values().stream()
                .sorted(Comparator.comparingInt(Sala::id))
                .map(this::salaMap)
                .toList();
    }

    public synchronized Map<String, Object> getSala(int id) {
        return salaMap(sala(id));
    }

    public synchronized Map<String, Object> createSala(Map<String, Object> body) {
        String nombre = requerido(body, "nombre", "nombre");
        int tipoSalaId = enteroRequerido(body, "tipoSalaId", "tipo de sala", 1, Integer.MAX_VALUE);
        tipoSala(tipoSalaId);
        int capacidad = enteroRequerido(body, "capacidad", "capacidad", 1, 500);
        String caracteristicas = opcional(body, "caracteristicas");
        String estado = estadoActivo(body, "Activa");
        validarSalaUnica(nombre, 0);

        Sala sala = new Sala(salaSeq++, nombre, tipoSalaId, capacidad, caracteristicas, estado);
        salas.put(sala.id(), sala);
        ajustarAsientos(sala.id(), capacidad);
        return salaMap(sala);
    }

    public synchronized Map<String, Object> updateSala(int id, Map<String, Object> body) {
        Sala actual = sala(id);
        String nombre = requerido(body, "nombre", "nombre");
        int tipoSalaId = enteroRequerido(body, "tipoSalaId", "tipo de sala", 1, Integer.MAX_VALUE);
        tipoSala(tipoSalaId);
        int capacidad = enteroRequerido(body, "capacidad", "capacidad", 1, 500);
        String caracteristicas = opcional(body, "caracteristicas");
        String estado = estadoActivo(body, actual.estado());
        validarSalaUnica(nombre, id);

        Sala actualizada = new Sala(id, nombre, tipoSalaId, capacidad, caracteristicas, estado);
        salas.put(id, actualizada);
        if (capacidad != actual.capacidad()) {
            ajustarAsientos(id, capacidad);
        }
        return salaMap(actualizada);
    }

    public synchronized void deleteSala(int id) {
        sala(id);
        asientos.values().removeIf(a -> a.salaId() == id);
        salas.remove(id);
    }

    public synchronized List<Map<String, Object>> listAsientos(Integer salaId) {
        return asientos.values().stream()
                .filter(a -> salaId == null || a.salaId() == salaId)
                .sorted(Comparator.comparingInt(Asiento::salaId)
                        .thenComparing(Asiento::fila)
                        .thenComparingInt(Asiento::numero))
                .map(this::asientoMap)
                .toList();
    }

    public synchronized Map<String, Object> getAsiento(int id) {
        return asientoMap(asiento(id));
    }

    public synchronized Map<String, Object> createAsiento(Map<String, Object> body) {
        int salaId = enteroRequerido(body, "salaId", "sala", 1, Integer.MAX_VALUE);
        sala(salaId);
        String fila = requerido(body, "fila", "fila").toUpperCase();
        int numero = enteroRequerido(body, "numero", "número", 1, 999);
        String estado = estadoAsiento(body, "Disponible");
        validarAsientoUnico(salaId, fila, numero, 0);

        Asiento asiento = new Asiento(asientoSeq++, salaId, fila, numero, estado);
        asientos.put(asiento.id(), asiento);
        return asientoMap(asiento);
    }

    public synchronized Map<String, Object> updateAsiento(int id, Map<String, Object> body) {
        Asiento actual = asiento(id);
        int salaId = actual.salaId();
        if (body.get("salaId") != null && !body.get("salaId").toString().isBlank()) {
            salaId = enteroRequerido(body, "salaId", "sala", 1, Integer.MAX_VALUE);
            sala(salaId);
        }
        String fila = body.get("fila") == null || body.get("fila").toString().isBlank()
                ? actual.fila()
                : requerido(body, "fila", "fila").toUpperCase();
        int numero = body.get("numero") == null || body.get("numero").toString().isBlank()
                ? actual.numero()
                : enteroRequerido(body, "numero", "número", 1, 999);
        String estado = estadoAsiento(body, actual.estado());
        validarAsientoUnico(salaId, fila, numero, id);

        Asiento actualizado = new Asiento(id, salaId, fila, numero, estado);
        asientos.put(id, actualizado);
        return asientoMap(actualizado);
    }

    public synchronized void deleteAsiento(int id) {
        asiento(id);
        asientos.remove(id);
    }

    public synchronized Map<String, Object> regenerarAsientos(int salaId, Map<String, Object> body) {
        Sala actual = sala(salaId);
        int filas = enteroRequerido(body, "filas", "filas", 1, 26);
        int porFila = enteroRequerido(body, "porFila", "asientos por fila", 1, 30);
        int capacidad = filas * porFila;
        if (capacidad > 500) {
            throw new ApiException(400, "La sala no puede superar los 500 asientos.");
        }

        asientos.values().removeIf(a -> a.salaId() == salaId);
        for (int fila = 0; fila < filas; fila++) {
            String letra = String.valueOf((char) ('A' + fila));
            for (int numero = 1; numero <= porFila; numero++) {
                Asiento asiento = new Asiento(asientoSeq++, salaId, letra, numero, "Disponible");
                asientos.put(asiento.id(), asiento);
            }
        }

        Sala actualizada = new Sala(actual.id(), actual.nombre(), actual.tipoSalaId(), capacidad,
                actual.caracteristicas(), actual.estado());
        salas.put(salaId, actualizada);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("mensaje", "Asientos redistribuidos correctamente.");
        map.put("capacidad", capacidad);
        map.put("filas", filas);
        map.put("porFila", porFila);
        return map;
    }

    public synchronized List<Map<String, Object>> listTarifas() {
        return tarifas.values().stream()
                .sorted(Comparator.comparingInt(Tarifa::id))
                .map(this::tarifaMap)
                .toList();
    }

    public synchronized Map<String, Object> getTarifa(int id) {
        return tarifaMap(tarifa(id));
    }

    public synchronized Map<String, Object> createTarifa(Map<String, Object> body) {
        String nombre = requerido(body, "nombre", "nombre");
        int tipoSalaId = enteroRequerido(body, "tipoSalaId", "tipo de sala", 0, Integer.MAX_VALUE);
        if (tipoSalaId > 0) {
            tipoSala(tipoSalaId);
        }
        String condicion = requerido(body, "condicion", "condición");
        if (!CONDICIONES.contains(condicion)) {
            throw new ApiException(400, "La condición indicada no es válida.");
        }
        double precio = decimalRequerido(body, "precio", "precio", 0, 10000);
        String estado = estadoActivo(body, "Activa");
        validarTarifaUnica(nombre, 0);

        Tarifa tarifa = new Tarifa(tarifaSeq++, nombre, tipoSalaId, condicion, precio, estado);
        tarifas.put(tarifa.id(), tarifa);
        return tarifaMap(tarifa);
    }

    public synchronized Map<String, Object> updateTarifa(int id, Map<String, Object> body) {
        Tarifa actual = tarifa(id);
        String nombre = requerido(body, "nombre", "nombre");
        int tipoSalaId = enteroRequerido(body, "tipoSalaId", "tipo de sala", 0, Integer.MAX_VALUE);
        if (tipoSalaId > 0) {
            tipoSala(tipoSalaId);
        }
        String condicion = requerido(body, "condicion", "condición");
        if (!CONDICIONES.contains(condicion)) {
            throw new ApiException(400, "La condición indicada no es válida.");
        }
        double precio = decimalRequerido(body, "precio", "precio", 0, 10000);
        String estado = estadoActivo(body, actual.estado());
        validarTarifaUnica(nombre, id);

        Tarifa actualizada = new Tarifa(id, nombre, tipoSalaId, condicion, precio, estado);
        tarifas.put(id, actualizada);
        return tarifaMap(actualizada);
    }

    public synchronized void deleteTarifa(int id) {
        tarifa(id);
        tarifas.remove(id);
    }

    public synchronized List<Map<String, Object>> listFunciones() {
        return funciones.values().stream()
                .sorted(Comparator.comparing(Funcion::fecha).thenComparing(Funcion::hora).thenComparingInt(Funcion::id))
                .map(this::funcionMap)
                .toList();
    }

    public synchronized Map<String, Object> getFuncion(int id) {
        return funcionMap(funcion(id));
    }

    public synchronized Map<String, Object> createFuncion(Map<String, Object> body) {
        int peliculaId = enteroRequerido(body, "peliculaId", "película", 1, Integer.MAX_VALUE);
        Pelicula pelicula = pelicula(peliculaId);
        if (!pelicula.estado().equals("Activa")) {
            throw new ApiException(409, "La película debe estar activa para programar funciones.");
        }
        int salaId = enteroRequerido(body, "salaId", "sala", 1, Integer.MAX_VALUE);
        Sala sala = sala(salaId);
        if (!sala.estado().equals("Activa")) {
            throw new ApiException(409, "La sala debe estar activa para programar funciones.");
        }
        String fecha = fechaRequerida(body);
        String hora = horaRequerida(body);
        String estado = estadoFuncion(body, "Programada");
        validarConflicto(0, peliculaId, salaId, fecha, hora, estado);

        Funcion funcion = new Funcion(funcionSeq++, peliculaId, salaId, fecha, hora, estado);
        funciones.put(funcion.id(), funcion);
        return funcionMap(funcion);
    }

    public synchronized Map<String, Object> updateFuncion(int id, Map<String, Object> body) {
        funcion(id);
        int peliculaId = enteroRequerido(body, "peliculaId", "película", 1, Integer.MAX_VALUE);
        Pelicula pelicula = pelicula(peliculaId);
        if (!pelicula.estado().equals("Activa")) {
            throw new ApiException(409, "La película debe estar activa para programar funciones.");
        }
        int salaId = enteroRequerido(body, "salaId", "sala", 1, Integer.MAX_VALUE);
        Sala sala = sala(salaId);
        if (!sala.estado().equals("Activa")) {
            throw new ApiException(409, "La sala debe estar activa para programar funciones.");
        }
        String fecha = fechaRequerida(body);
        String hora = horaRequerida(body);
        String estado = estadoFuncion(body, "Programada");
        validarConflicto(id, peliculaId, salaId, fecha, hora, estado);

        Funcion actualizada = new Funcion(id, peliculaId, salaId, fecha, hora, estado);
        funciones.put(id, actualizada);
        return funcionMap(actualizada);
    }

    public synchronized void deleteFuncion(int id) {
        funcion(id);
        boolean conReservas = reservas.values().stream()
                .anyMatch(r -> r.funcionId() == id && r.estado().equals("Confirmada"));
        if (conReservas) {
            throw new ApiException(409, "No se puede eliminar la función porque tiene reservas confirmadas.");
        }
        funciones.remove(id);
    }

    public synchronized List<Map<String, Object>> listProductos() {
        return productos.values().stream()
                .sorted(Comparator.comparingInt(Producto::id))
                .map(this::productoMap)
                .toList();
    }

    public synchronized Map<String, Object> getProducto(int id) {
        return productoMap(producto(id));
    }

    public synchronized Map<String, Object> createProducto(Map<String, Object> body) {
        String nombre = requerido(body, "nombre", "nombre");
        String categoria = requerido(body, "categoria", "categoría");
        if (!CATEGORIAS_CONFITERIA.contains(categoria)) {
            throw new ApiException(400, "La categoría debe ser Combos, Palomitas, Bebidas, Snacks o Dulces.");
        }
        double precio = decimalRequerido(body, "precio", "precio", 0, 10000);
        int stock = enteroRequerido(body, "stock", "stock", 0, 100000);
        int stockMinimo = enteroRequerido(body, "stockMinimo", "stock mínimo", 0, 100000);
        String estado = estadoActivo(body, "Activa");
        validarProductoUnico(nombre, 0);

        Producto producto = new Producto(productoSeq++, nombre, categoria, precio, stock, stockMinimo, estado);
        productos.put(producto.id(), producto);
        return productoMap(producto);
    }

    public synchronized Map<String, Object> updateProducto(int id, Map<String, Object> body) {
        Producto actual = producto(id);
        String nombre = requerido(body, "nombre", "nombre");
        String categoria = requerido(body, "categoria", "categoría");
        if (!CATEGORIAS_CONFITERIA.contains(categoria)) {
            throw new ApiException(400, "La categoría debe ser Combos, Palomitas, Bebidas, Snacks o Dulces.");
        }
        double precio = decimalRequerido(body, "precio", "precio", 0, 10000);
        int stock = enteroRequerido(body, "stock", "stock", 0, 100000);
        int stockMinimo = enteroRequerido(body, "stockMinimo", "stock mínimo", 0, 100000);
        String estado = estadoActivo(body, actual.estado());
        validarProductoUnico(nombre, id);

        Producto actualizado = new Producto(id, nombre, categoria, precio, stock, stockMinimo, estado);
        productos.put(id, actualizado);
        return productoMap(actualizado);
    }

    public synchronized void deleteProducto(int id) {
        producto(id);
        productos.remove(id);
    }

    public synchronized List<Map<String, Object>> listPromociones() {
        return promociones.values().stream()
                .sorted(Comparator.comparingInt(Promocion::id))
                .map(this::promocionMap)
                .toList();
    }

    public synchronized Map<String, Object> getPromocion(int id) {
        return promocionMap(promocion(id));
    }

    public synchronized Map<String, Object> createPromocion(Map<String, Object> body) {
        String nombre = requerido(body, "nombre", "nombre");
        String tipo = requerido(body, "tipo", "tipo de promoción");
        validarTipoPromocion(tipo);
        double valor = decimalRequerido(body, "valor", "valor", 0, 100);
        if (tipo.equals("Descuento %") && valor <= 0) {
            throw new ApiException(400, "El descuento debe ser mayor a 0%.");
        }
        String beneficiario = opcional(body, "beneficiario");
        if (tipo.equals("Función benéfica") && beneficiario.isEmpty()) {
            throw new ApiException(400, "Indica el beneficiario de la función benéfica.");
        }
        String fechaInicio = fechaOpcional(body, "fechaInicio", "fecha de inicio");
        String fechaFin = fechaOpcional(body, "fechaFin", "fecha de fin");
        validarRangoFechas(fechaInicio, fechaFin);
        String estado = estadoActivo(body, "Activa");
        validarPromocionUnica(nombre, 0);

        Promocion promocion = new Promocion(promocionSeq++, nombre, tipo, valor, beneficiario,
                fechaInicio, fechaFin, estado);
        promociones.put(promocion.id(), promocion);
        return promocionMap(promocion);
    }

    public synchronized Map<String, Object> updatePromocion(int id, Map<String, Object> body) {
        Promocion actual = promocion(id);
        String nombre = requerido(body, "nombre", "nombre");
        String tipo = requerido(body, "tipo", "tipo de promoción");
        validarTipoPromocion(tipo);
        double valor = decimalRequerido(body, "valor", "valor", 0, 100);
        if (tipo.equals("Descuento %") && valor <= 0) {
            throw new ApiException(400, "El descuento debe ser mayor a 0%.");
        }
        String beneficiario = opcional(body, "beneficiario");
        if (tipo.equals("Función benéfica") && beneficiario.isEmpty()) {
            throw new ApiException(400, "Indica el beneficiario de la función benéfica.");
        }
        String fechaInicio = fechaOpcional(body, "fechaInicio", "fecha de inicio");
        String fechaFin = fechaOpcional(body, "fechaFin", "fecha de fin");
        validarRangoFechas(fechaInicio, fechaFin);
        String estado = estadoActivo(body, actual.estado());
        validarPromocionUnica(nombre, id);

        Promocion actualizada = new Promocion(id, nombre, tipo, valor, beneficiario,
                fechaInicio, fechaFin, estado);
        promociones.put(id, actualizada);
        return promocionMap(actualizada);
    }

    public synchronized void deletePromocion(int id) {
        promocion(id);
        promociones.remove(id);
    }

    public synchronized List<Map<String, Object>> listClientes() {
        return clientes.values().stream()
                .sorted(Comparator.comparingInt(Cliente::id))
                .map(Cliente::toMap)
                .toList();
    }

    public synchronized Map<String, Object> getCliente(int id) {
        return cliente(id).toMap();
    }

    public synchronized Map<String, Object> createCliente(Map<String, Object> body) {
        String nombre = requerido(body, "nombre", "nombre");
        String documento = requerido(body, "documento", "documento");
        String email = opcional(body, "email");
        validarEmail(email);
        String telefono = opcional(body, "telefono");
        String estado = estadoActivo(body, "Activa");
        validarClienteUnico(documento, 0);

        Cliente cliente = new Cliente(clienteSeq++, nombre, documento, email, telefono, estado);
        clientes.put(cliente.id(), cliente);
        return cliente.toMap();
    }

    public synchronized Map<String, Object> updateCliente(int id, Map<String, Object> body) {
        Cliente actual = cliente(id);
        String nombre = requerido(body, "nombre", "nombre");
        String documento = requerido(body, "documento", "documento");
        String email = opcional(body, "email");
        validarEmail(email);
        String telefono = opcional(body, "telefono");
        String estado = estadoActivo(body, actual.estado());
        validarClienteUnico(documento, id);

        Cliente actualizado = new Cliente(id, nombre, documento, email, telefono, estado);
        clientes.put(id, actualizado);
        return actualizado.toMap();
    }

    public synchronized void deleteCliente(int id) {
        cliente(id);
        clientes.remove(id);
    }

    public synchronized List<Map<String, Object>> listMediosPago() {
        return mediosPago.values().stream()
                .sorted(Comparator.comparingInt(MedioPago::id))
                .map(MedioPago::toMap)
                .toList();
    }

    public synchronized Map<String, Object> getMedioPago(int id) {
        return medioPago(id).toMap();
    }

    public synchronized Map<String, Object> createMedioPago(Map<String, Object> body) {
        String nombre = requerido(body, "nombre", "nombre");
        String descripcion = opcional(body, "descripcion");
        String estado = estadoCatalogo(body, "Activo");
        validarMedioPagoUnico(nombre, 0);

        MedioPago medio = new MedioPago(medioPagoSeq++, nombre, descripcion, estado);
        mediosPago.put(medio.id(), medio);
        return medio.toMap();
    }

    public synchronized Map<String, Object> updateMedioPago(int id, Map<String, Object> body) {
        MedioPago actual = medioPago(id);
        String nombre = requerido(body, "nombre", "nombre");
        String descripcion = opcional(body, "descripcion");
        String estado = estadoCatalogo(body, actual.estado());
        validarMedioPagoUnico(nombre, id);

        MedioPago actualizado = new MedioPago(id, nombre, descripcion, estado);
        mediosPago.put(id, actualizado);
        return actualizado.toMap();
    }

    public synchronized void deleteMedioPago(int id) {
        medioPago(id);
        mediosPago.remove(id);
    }

    public synchronized List<Map<String, Object>> listEntradas() {
        return entradas.values().stream()
                .sorted(Comparator.comparingInt(Entrada::id).reversed())
                .map(this::entradaMap)
                .toList();
    }

    public synchronized List<Map<String, Object>> listReservas() {
        return reservas.values().stream()
                .sorted(Comparator.comparingInt(Reserva::id).reversed())
                .map(this::reservaMap)
                .toList();
    }

    public synchronized Map<String, Object> buscarReserva(String codigo) {
        String buscado = codigo == null ? "" : codigo.trim();
        return reservas.values().stream()
                .filter(r -> r.codigo().equalsIgnoreCase(buscado))
                .findFirst()
                .map(this::reservaMap)
                .orElseThrow(() -> new ApiException(404, "No se encontró una reserva con ese código."));
    }

    public synchronized Map<String, Object> crearReserva(Map<String, Object> body, String usuario) {
        int funcionId = enteroRequerido(body, "funcionId", "función", 1, Integer.MAX_VALUE);
        Funcion funcion = funcion(funcionId);
        if (!funcion.estado().equals("Programada")) {
            throw new ApiException(409, "La función seleccionada no está disponible.");
        }

        Object rawAsientos = body.get("asientos");
        if (!(rawAsientos instanceof List<?> listaAsientos) || listaAsientos.isEmpty()) {
            throw new ApiException(400, "Selecciona al menos un asiento.");
        }
        if (listaAsientos.size() > 6) {
            throw new ApiException(400, "Solo puedes reservar hasta 6 asientos por compra.");
        }

        List<Integer> asientoIds = new ArrayList<>();
        Set<String> posiciones = new HashSet<>();
        Set<Integer> yaReservados = asientosReservados(funcionId);
        for (Object raw : listaAsientos) {
            int asientoId = numeroEntero(raw, "asiento");
            if (asientoIds.contains(asientoId)) {
                throw new ApiException(400, "No puedes repetir el mismo asiento.");
            }
            Asiento asiento = asiento(asientoId);
            if (asiento.salaId() != funcion.salaId()) {
                throw new ApiException(400, "El asiento " + asiento.fila() + asiento.numero()
                        + " no pertenece a la sala de la función.");
            }
            if (!asiento.estado().equals("Disponible")) {
                throw new ApiException(409, "El asiento " + asiento.fila() + asiento.numero()
                        + " está en mantenimiento.");
            }
            if (yaReservados.contains(asientoId)) {
                throw new ApiException(409, "El asiento " + asiento.fila() + asiento.numero()
                        + " ya fue reservado para esta función.");
            }
            if (!posiciones.add(asiento.fila() + "-" + asiento.numero())) {
                throw new ApiException(400, "No puedes repetir el mismo asiento.");
            }
            asientoIds.add(asientoId);
        }

        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        Object rawProductos = body.get("productos");
        if (rawProductos instanceof Map<?, ?> mapaProductos) {
            for (Map.Entry<?, ?> entry : mapaProductos.entrySet()) {
                int productoId = numeroEntero(entry.getKey(), "producto");
                int cantidad = numeroEntero(entry.getValue(), "cantidad");
                if (cantidad <= 0) {
                    continue;
                }
                if (cantidad > 50) {
                    throw new ApiException(400, "La cantidad máxima por producto es 50.");
                }
                Producto producto = producto(productoId);
                if (!producto.estado().equals("Activa")) {
                    throw new ApiException(409, "El producto " + producto.nombre() + " no está disponible.");
                }
                if (producto.stock() < cantidad) {
                    throw new ApiException(409, "Stock insuficiente de " + producto.nombre()
                            + ". Disponible: " + producto.stock() + ".");
                }
                cantidades.put(productoId, cantidad);
            }
        }

        String cliente = requerido(body, "cliente", "nombre del cliente");
        String email = opcional(body, "email");
        if (!email.isEmpty() && (!email.contains("@") || email.length() < 5)) {
            throw new ApiException(400, "El correo electrónico no es válido.");
        }
        String medioPago = requerido(body, "medioPago", "medio de pago");
        if (!MEDIOS_PAGO.contains(medioPago)) {
            throw new ApiException(400, "El medio de pago debe ser QR, Tarjeta o Efectivo.");
        }

        double precioEntrada = precioEntrada(funcion);
        double subtotalEntradas = redondear(precioEntrada * asientoIds.size());
        double descuento = descuentoEntradas(subtotalEntradas, asientoIds.size());
        double subtotalConfiteria = 0;
        for (Map.Entry<Integer, Integer> entry : cantidades.entrySet()) {
            Producto producto = producto(entry.getKey());
            subtotalConfiteria += producto.precio() * entry.getValue();
        }
        subtotalConfiteria = redondear(subtotalConfiteria);
        double total = redondear(Math.max(0, subtotalEntradas - descuento) + subtotalConfiteria);

        for (Map.Entry<Integer, Integer> entry : cantidades.entrySet()) {
            Producto producto = producto(entry.getKey());
            productos.put(producto.id(), new Producto(producto.id(), producto.nombre(), producto.categoria(),
                    producto.precio(), producto.stock() - entry.getValue(), producto.stockMinimo(),
                    producto.estado()));
        }

        Reserva reserva = new Reserva(reservaSeq++, codigoReserva(), funcionId, List.copyOf(asientoIds),
                Map.copyOf(cantidades), cliente, email, medioPago, subtotalEntradas, descuento,
                subtotalConfiteria, total, "Confirmada",
                LocalDateTime.now().format(FORMATO_FECHA_HORA));
        reservas.put(reserva.id(), reserva);

        emitirEntradas(reserva);

        sincronizarCliente(cliente, email);

        auditar(usuario, "Reserva confirmada",
                reserva.codigo() + " · " + asientoIds.size() + " entrada(s) · Bs " + String.format(Locale.US, "%.2f", total));
        return reservaMap(reserva);
    }

    private void emitirEntradas(Reserva reserva) {
        int cantidad = reserva.asientoIds().size();
        if (cantidad == 0) {
            return;
        }
        double neto = reserva.subtotalEntradas() - reserva.descuento();
        double precioBase = redondear(neto / cantidad);
        double acumulado = 0;
        String ahora = LocalDateTime.now().format(FORMATO_FECHA_HORA);
        List<Integer> ids = reserva.asientoIds();
        for (int i = 0; i < cantidad; i++) {
            Asiento asiento = asientos.get(ids.get(i));
            if (asiento == null) {
                continue;
            }
            double precio = i == cantidad - 1 ? redondear(neto - acumulado) : precioBase;
            acumulado = redondear(acumulado + precio);
            Entrada entrada = new Entrada(entradaSeq++, codigoEntrada(), reserva.id(), reserva.funcionId(),
                    asiento.id(), asiento.fila(), asiento.numero(), precio, "Emitida", ahora);
            entradas.put(entrada.id(), entrada);
        }
    }

    private void sincronizarCliente(String nombre, String email) {
        boolean existe = clientes.values().stream()
                .anyMatch(c -> c.nombre().equalsIgnoreCase(nombre)
                        && (email.isEmpty() || c.email().equalsIgnoreCase(email)));
        if (!existe) {
            agregarCliente(nombre, "WEB-" + System.nanoTime() % 1000000, email, "", "Activa");
        }
    }

    public synchronized Map<String, Object> cancelarReserva(int id, Map<String, Object> body, String usuario) {
        Reserva reserva = reserva(id);
        if (reserva.estado().equals("Cancelada")) {
            throw new ApiException(409, "La reserva ya fue cancelada.");
        }
        String motivo = opcional(body, "motivo");
        if (motivo.isEmpty()) {
            motivo = "Cancelación sin motivo especificado";
        }

        double porcentaje = calcularPorcentajeReembolso(reserva);
        double montoReembolsado = redondear(reserva.total() * porcentaje / 100.0);
        String medio = opcional(body, "medio");
        if (medio.isEmpty()) {
            medio = reserva.medioPago();
        }
        String ahora = LocalDateTime.now().format(FORMATO_FECHA_HORA);

        Reserva cancelada = new Reserva(reserva.id(), reserva.codigo(), reserva.funcionId(),
                reserva.asientoIds(), reserva.productos(), reserva.cliente(), reserva.email(),
                reserva.medioPago(), reserva.subtotalEntradas(), reserva.descuento(),
                reserva.subtotalConfiteria(), reserva.total(), "Cancelada", reserva.creadaEn());
        reservas.put(id, cancelada);

        restaurarStock(reserva);

        for (Entrada entrada : entradas.values()) {
            if (entrada.reservaId() == id && entrada.estado().equals("Emitida")) {
                Entrada anulada = new Entrada(entrada.id(), entrada.codigo(), entrada.reservaId(),
                        entrada.funcionId(), entrada.asientoId(), entrada.fila(), entrada.numero(),
                        entrada.precio(), "Anulada", entrada.emitidaEn());
                entradas.put(entrada.id(), anulada);
            }
        }

        Reembolso reembolso = new Reembolso(reembolsoSeq++, reserva.id(), reserva.codigo(),
                reserva.cliente(), montoReembolsado, motivo, medio, ahora);
        reembolsos.put(reembolso.id(), reembolso);

        auditar(usuario, "Reserva cancelada", reserva.codigo() + " · reembolso Bs "
                + String.format(Locale.US, "%.2f", montoReembolsado) + " (" + porcentaje + "%)");
        return reservaMap(cancelada);
    }

    private double calcularPorcentajeReembolso(Reserva reserva) {
        Funcion funcion = funciones.get(reserva.funcionId());
        if (funcion == null) {
            return 100;
        }
        LocalDateTime inicioFuncion = LocalDateTime.of(LocalDate.parse(funcion.fecha()),
                LocalTime.parse(funcion.hora()));
        long horas = java.time.Duration.between(LocalDateTime.now(), inicioFuncion).toHours();
        if (horas >= HORAS_LIMITE_REEMBOLSO) {
            return 100;
        }
        return 50;
    }

    private void restaurarStock(Reserva reserva) {
        for (Map.Entry<Integer, Integer> entry : reserva.productos().entrySet()) {
            Producto producto = productos.get(entry.getKey());
            if (producto == null) {
                continue;
            }
            productos.put(producto.id(), new Producto(producto.id(), producto.nombre(), producto.categoria(),
                    producto.precio(), producto.stock() + entry.getValue(), producto.stockMinimo(),
                    producto.estado()));
        }
    }

    public synchronized List<Map<String, Object>> listReembolsos() {
        return reembolsos.values().stream()
                .sorted(Comparator.comparingInt(Reembolso::id).reversed())
                .map(Reembolso::toMap)
                .toList();
    }

    public synchronized Map<String, Object> recaudacion() {
        List<Reserva> confirmadas = reservas.values().stream()
                .filter(r -> r.estado().equals("Confirmada"))
                .toList();
        int entradasVendidas = confirmadas.stream().mapToInt(r -> r.asientoIds().size()).sum();
        double ingresosEntradas = confirmadas.stream()
                .mapToDouble(r -> r.subtotalEntradas() - r.descuento()).sum();
        double ingresosConfiteria = confirmadas.stream().mapToDouble(Reserva::subtotalConfiteria).sum();
        double ingresosTotales = confirmadas.stream().mapToDouble(Reserva::total).sum();

        List<Map<String, Object>> bajoStock = productos.values().stream()
                .filter(p -> p.estado().equals("Activa") && p.stock() <= p.stockMinimo())
                .sorted(Comparator.comparingInt(Producto::stock))
                .map(this::productoMap)
                .toList();

        LocalDate hoy = LocalDate.now();
        long funcionesProgramadas = funciones.values().stream()
                .filter(f -> f.estado().equals("Programada") && !LocalDate.parse(f.fecha()).isBefore(hoy))
                .count();
        long funcionesBeneficas = promociones.values().stream()
                .filter(p -> p.tipo().equals("Función benéfica") && p.estado().equals("Activa") && vigente(p))
                .count();

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("reservas", confirmadas.size());
        resumen.put("entradasVendidas", entradasVendidas);
        resumen.put("ingresosEntradas", redondear(ingresosEntradas));
        resumen.put("ingresosConfiteria", redondear(ingresosConfiteria));
        resumen.put("ingresosTotales", redondear(ingresosTotales));
        resumen.put("funcionesProgramadas", funcionesProgramadas);
        resumen.put("funcionesBeneficas", funcionesBeneficas);
        resumen.put("productosBajoStock", bajoStock.size());

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("resumen", resumen);
        respuesta.put("bajoStock", bajoStock);
        respuesta.put("reservas", confirmadas.stream()
                .sorted(Comparator.comparingInt(Reserva::id).reversed())
                .limit(30)
                .map(this::reservaMap)
                .toList());
        return respuesta;
    }

    public synchronized Map<String, Object> carteleraPublica() {
        LocalDate hoy = LocalDate.now();
        List<Map<String, Object>> activas = new ArrayList<>();
        for (Pelicula pelicula : peliculas.values().stream()
                .filter(p -> p.estado().equals("Activa"))
                .sorted(Comparator.comparingInt(Pelicula::id))
                .toList()) {
            List<Map<String, Object>> funcionesPelicula = funciones.values().stream()
                    .filter(f -> f.peliculaId() == pelicula.id())
                    .filter(f -> f.estado().equals("Programada"))
                    .filter(f -> !LocalDate.parse(f.fecha()).isBefore(hoy))
                    .sorted(Comparator.comparing(Funcion::fecha).thenComparing(Funcion::hora))
                    .map(this::funcionMap)
                    .toList();
            if (!funcionesPelicula.isEmpty()) {
                Map<String, Object> map = pelicula.toMap();
                map.put("funciones", funcionesPelicula);
                activas.add(map);
            }
        }

        List<Map<String, Object>> proximos = peliculas.values().stream()
                .filter(p -> p.estado().equals("Inactiva"))
                .sorted(Comparator.comparingInt(Pelicula::id))
                .map(Pelicula::toMap)
                .toList();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("peliculas", activas);
        respuesta.put("proximos", proximos);
        respuesta.put("promociones", promocionesActivas());
        respuesta.put("confiteria", productosActivos());
        respuesta.put("tiposSala", listTiposSala());
        return respuesta;
    }

    public synchronized Map<String, Object> asientosFuncion(int funcionId) {
        Funcion funcion = funcion(funcionId);
        Set<Integer> ocupados = asientosReservados(funcionId);
        List<Map<String, Object>> mapa = asientos.values().stream()
                .filter(a -> a.salaId() == funcion.salaId())
                .sorted(Comparator.comparing(Asiento::fila).thenComparingInt(Asiento::numero))
                .map(asiento -> {
                    Map<String, Object> map = asiento.toMap();
                    if (ocupados.contains(asiento.id())) {
                        map.put("estado", "Ocupado");
                    }
                    map.put("etiqueta", asiento.fila() + asiento.numero());
                    return map;
                })
                .toList();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("funcion", funcionMap(funcion));
        respuesta.put("asientos", mapa);
        return respuesta;
    }

    public synchronized List<Map<String, Object>> promocionesActivas() {
        return promociones.values().stream()
                .filter(p -> p.estado().equals("Activa") && vigente(p))
                .sorted(Comparator.comparingInt(Promocion::id))
                .map(this::promocionMap)
                .toList();
    }

    public synchronized List<Map<String, Object>> productosActivos() {
        return productos.values().stream()
                .filter(p -> p.estado().equals("Activa"))
                .sorted(Comparator.comparingInt(Producto::id))
                .map(this::productoMap)
                .toList();
    }

    public synchronized void auditar(String usuario, String accion, String detalle) {
        Map<String, Object> registro = new LinkedHashMap<>();
        registro.put("fecha", LocalDateTime.now().format(FORMATO_FECHA_HORA));
        registro.put("usuario", usuario == null || usuario.isBlank() ? "Cliente web" : usuario);
        registro.put("accion", accion);
        registro.put("detalle", detalle);
        auditoria.add(0, registro);
        while (auditoria.size() > 200) {
            auditoria.remove(auditoria.size() - 1);
        }
    }

    public synchronized List<Map<String, Object>> listAuditoria() {
        return new ArrayList<>(auditoria);
    }

    private Funcion funcion(int id) {
        Funcion funcion = funciones.get(id);
        if (funcion == null) {
            throw new ApiException(404, "La función solicitada no existe.");
        }
        return funcion;
    }

    private Producto producto(int id) {
        Producto producto = productos.get(id);
        if (producto == null) {
            throw new ApiException(404, "El producto solicitado no existe.");
        }
        return producto;
    }

    private Promocion promocion(int id) {
        Promocion promocion = promociones.get(id);
        if (promocion == null) {
            throw new ApiException(404, "La promoción solicitada no existe.");
        }
        return promocion;
    }

    private Cliente cliente(int id) {
        Cliente cliente = clientes.get(id);
        if (cliente == null) {
            throw new ApiException(404, "El cliente solicitado no existe.");
        }
        return cliente;
    }

    private MedioPago medioPago(int id) {
        MedioPago medio = mediosPago.get(id);
        if (medio == null) {
            throw new ApiException(404, "El medio de pago solicitado no existe.");
        }
        return medio;
    }

    private Reserva reserva(int id) {
        Reserva reserva = reservas.get(id);
        if (reserva == null) {
            throw new ApiException(404, "La reserva solicitada no existe.");
        }
        return reserva;
    }

    private Map<String, Object> entradaMap(Entrada entrada) {
        Map<String, Object> map = entrada.toMap();
        Funcion funcion = funciones.get(entrada.funcionId());
        if (funcion != null) {
            Pelicula pelicula = peliculas.get(funcion.peliculaId());
            Sala sala = salas.get(funcion.salaId());
            map.put("peliculaTitulo", pelicula == null ? "" : pelicula.titulo());
            map.put("salaNombre", sala == null ? "" : sala.nombre());
            map.put("fecha", funcion.fecha());
            map.put("hora", funcion.hora());
        } else {
            map.put("peliculaTitulo", "");
            map.put("salaNombre", "");
            map.put("fecha", "");
            map.put("hora", "");
        }
        Reserva reserva = reservas.get(entrada.reservaId());
        map.put("reservaCodigo", reserva == null ? "" : reserva.codigo());
        map.put("cliente", reserva == null ? "" : reserva.cliente());
        return map;
    }

    private String codigoEntrada() {
        String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder("EN-");
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    private Map<String, Object> funcionMap(Funcion funcion) {
        Map<String, Object> map = funcion.toMap();
        Pelicula pelicula = peliculas.get(funcion.peliculaId());
        Sala sala = salas.get(funcion.salaId());
        TipoSala tipo = sala == null ? null : tiposSala.get(sala.tipoSalaId());
        map.put("peliculaTitulo", pelicula == null ? "Película eliminada" : pelicula.titulo());
        map.put("genero", pelicula == null ? "" : pelicula.genero());
        map.put("clasificacion", pelicula == null ? "TP" : pelicula.clasificacion());
        map.put("duracion", pelicula == null ? 0 : pelicula.duracion());
        map.put("salaNombre", sala == null ? "Sala eliminada" : sala.nombre());
        map.put("tipoSalaId", sala == null ? 0 : sala.tipoSalaId());
        map.put("tipoSalaNombre", tipo == null ? "Sin tipo" : tipo.nombre());
        int capacidad = sala == null ? 0 : sala.capacidad();
        int ocupados = asientosReservados(funcion.id()).size();
        map.put("capacidad", capacidad);
        map.put("ocupados", ocupados);
        map.put("disponibles", Math.max(0, capacidad - ocupados));
        map.put("precio", precioEntrada(funcion));
        map.put("fin", horaFin(funcion));
        return map;
    }

    private Map<String, Object> productoMap(Producto producto) {
        Map<String, Object> map = producto.toMap();
        map.put("bajoStock", producto.estado().equals("Activa") && producto.stock() <= producto.stockMinimo());
        return map;
    }

    private Map<String, Object> promocionMap(Promocion promocion) {
        Map<String, Object> map = promocion.toMap();
        map.put("vigente", vigente(promocion));
        return map;
    }

    private Map<String, Object> reservaMap(Reserva reserva) {
        Map<String, Object> map = reserva.toMap();
        Funcion funcion = funciones.get(reserva.funcionId());
        if (funcion != null) {
            Pelicula pelicula = peliculas.get(funcion.peliculaId());
            Sala sala = salas.get(funcion.salaId());
            map.put("peliculaTitulo", pelicula == null ? "" : pelicula.titulo());
            map.put("salaNombre", sala == null ? "" : sala.nombre());
            map.put("fecha", funcion.fecha());
            map.put("hora", funcion.hora());
        } else {
            map.put("peliculaTitulo", "");
            map.put("salaNombre", "");
            map.put("fecha", "");
            map.put("hora", "");
        }

        List<Map<String, Object>> asientosDetalle = new ArrayList<>();
        for (Integer asientoId : reserva.asientoIds()) {
            Asiento asiento = asientos.get(asientoId);
            if (asiento == null) {
                continue;
            }
            Map<String, Object> detalle = new LinkedHashMap<>();
            detalle.put("id", asiento.id());
            detalle.put("fila", asiento.fila());
            detalle.put("numero", asiento.numero());
            detalle.put("etiqueta", asiento.fila() + asiento.numero());
            asientosDetalle.add(detalle);
        }
        asientosDetalle.sort(Comparator
                .comparing((Map<String, Object> a) -> String.valueOf(a.get("fila")))
                .thenComparingInt(a -> (Integer) a.get("numero")));
        map.put("asientosDetalle", asientosDetalle);
        map.put("cantidadEntradas", reserva.asientoIds().size());

        List<Map<String, Object>> productosDetalle = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : reserva.productos().entrySet()) {
            Producto producto = productos.get(entry.getKey());
            if (producto == null) {
                continue;
            }
            Map<String, Object> detalle = new LinkedHashMap<>();
            detalle.put("productoId", producto.id());
            detalle.put("nombre", producto.nombre());
            detalle.put("cantidad", entry.getValue());
            detalle.put("precio", producto.precio());
            detalle.put("subtotal", redondear(producto.precio() * entry.getValue()));
            productosDetalle.add(detalle);
        }
        map.put("productosDetalle", productosDetalle);
        return map;
    }

    private double precioEntrada(Funcion funcion) {
        Sala sala = salas.get(funcion.salaId());
        if (sala == null) {
            return 0;
        }
        return tarifas.values().stream()
                .filter(t -> t.estado().equals("Activa") && t.condicion().equals("General"))
                .filter(t -> t.tipoSalaId() == sala.tipoSalaId() || t.tipoSalaId() == 0)
                .sorted(Comparator.comparingInt((Tarifa t) -> t.tipoSalaId() == sala.tipoSalaId() ? 0 : 1))
                .map(Tarifa::precio)
                .findFirst()
                .orElse(0.0);
    }

    private Set<Integer> asientosReservados(int funcionId) {
        Set<Integer> ids = new LinkedHashSet<>();
        for (Reserva reserva : reservas.values()) {
            if (reserva.funcionId() == funcionId && reserva.estado().equals("Confirmada")) {
                ids.addAll(reserva.asientoIds());
            }
        }
        return ids;
    }

    private double descuentoEntradas(double subtotal, int cantidad) {
        if (cantidad <= 0) {
            return 0;
        }
        double mejor = 0;
        for (Promocion promocion : promociones.values()) {
            if (!promocion.estado().equals("Activa") || !vigente(promocion)) {
                continue;
            }
            if (promocion.tipo().equals("2x1")) {
                mejor = Math.max(mejor, (cantidad / 2) * (subtotal / cantidad));
            } else if (promocion.tipo().equals("Descuento %")) {
                mejor = Math.max(mejor, subtotal * promocion.valor() / 100.0);
            }
        }
        return redondear(mejor);
    }

    private boolean vigente(Promocion promocion) {
        LocalDate hoy = LocalDate.now();
        if (!promocion.fechaInicio().isEmpty() && hoy.isBefore(LocalDate.parse(promocion.fechaInicio()))) {
            return false;
        }
        return promocion.fechaFin().isEmpty() || !hoy.isAfter(LocalDate.parse(promocion.fechaFin()));
    }

    private void validarConflicto(int idExcluido, int peliculaId, int salaId, String fecha, String hora,
            String estado) {
        if (!estado.equals("Programada")) {
            return;
        }
        Pelicula nueva = pelicula(peliculaId);
        int inicio = minutos(hora);
        for (Funcion otra : funciones.values()) {
            if (otra.id() == idExcluido || !otra.estado().equals("Programada")) {
                continue;
            }
            if (!otra.fecha().equals(fecha) || otra.salaId() != salaId) {
                continue;
            }
            Pelicula existente = peliculas.get(otra.peliculaId());
            int duracionExistente = existente == null ? 0 : existente.duracion();
            int margen = Math.max(nueva.duracion(), duracionExistente) + BUFFER_ENTRE_FUNCIONES;
            if (Math.abs(inicio - minutos(otra.hora())) < margen) {
                throw new ApiException(409, "Cruce de horarios con \"" + (existente == null ? "otra función" : existente.titulo())
                        + "\" a las " + otra.hora() + " en " + sala(salaId).nombre()
                        + ". Deja al menos " + BUFFER_ENTRE_FUNCIONES + " minutos entre funciones.");
            }
        }
    }

    private String horaFin(Funcion funcion) {
        Pelicula pelicula = peliculas.get(funcion.peliculaId());
        if (pelicula == null) {
            return funcion.hora();
        }
        LocalTime inicio = LocalTime.parse(funcion.hora(), FORMATO_HORA);
        return inicio.plusMinutes(pelicula.duracion()).format(FORMATO_HORA);
    }

    private static int minutos(String hora) {
        LocalTime tiempo = LocalTime.parse(hora, FORMATO_HORA);
        return tiempo.getHour() * 60 + tiempo.getMinute();
    }

    private static String fechaRequerida(Map<String, Object> body) {
        String fecha = requerido(body, "fecha", "fecha");
        try {
            LocalDate.parse(fecha);
        } catch (RuntimeException ex) {
            throw new ApiException(400, "La fecha debe tener el formato AAAA-MM-DD.");
        }
        return fecha;
    }

    private static String horaRequerida(Map<String, Object> body) {
        String hora = requerido(body, "hora", "hora");
        try {
            LocalTime.parse(hora, FORMATO_HORA);
        } catch (RuntimeException ex) {
            throw new ApiException(400, "La hora debe tener el formato HH:mm.");
        }
        return hora;
    }

    private static String fechaOpcional(Map<String, Object> body, String key, String label) {
        String fecha = opcional(body, key);
        if (fecha.isEmpty()) {
            return "";
        }
        try {
            LocalDate.parse(fecha);
        } catch (RuntimeException ex) {
            throw new ApiException(400, "La " + label + " debe tener el formato AAAA-MM-DD.");
        }
        return fecha;
    }

    private static void validarRangoFechas(String inicio, String fin) {
        if (!inicio.isEmpty() && !fin.isEmpty() && LocalDate.parse(inicio).isAfter(LocalDate.parse(fin))) {
            throw new ApiException(400, "La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
    }

    private static String estadoFuncion(Map<String, Object> body, String porDefecto) {
        String estado = opcional(body, "estado");
        if (estado.isEmpty()) {
            return porDefecto;
        }
        if (!ESTADOS_FUNCION.contains(estado)) {
            throw new ApiException(400, "El estado de la función debe ser Programada o Cancelada.");
        }
        return estado;
    }

    private static void validarTipoPromocion(String tipo) {
        if (!TIPOS_PROMOCION.contains(tipo)) {
            throw new ApiException(400, "El tipo debe ser 2x1, Descuento % o Función benéfica.");
        }
    }

    private void validarProductoUnico(String nombre, int idExcluido) {
        boolean duplicado = productos.values().stream()
                .anyMatch(p -> p.id() != idExcluido && p.nombre().equalsIgnoreCase(nombre));
        if (duplicado) {
            throw new ApiException(409, "Ya existe un producto de confitería con ese nombre.");
        }
    }

    private void validarPromocionUnica(String nombre, int idExcluido) {
        boolean duplicado = promociones.values().stream()
                .anyMatch(p -> p.id() != idExcluido && p.nombre().equalsIgnoreCase(nombre));
        if (duplicado) {
            throw new ApiException(409, "Ya existe una promoción con ese nombre.");
        }
    }

    private void validarClienteUnico(String documento, int idExcluido) {
        boolean duplicado = clientes.values().stream()
                .anyMatch(c -> c.id() != idExcluido && c.documento().equalsIgnoreCase(documento));
        if (duplicado) {
            throw new ApiException(409, "Ya existe un cliente con ese documento.");
        }
    }

    private void validarMedioPagoUnico(String nombre, int idExcluido) {
        boolean duplicado = mediosPago.values().stream()
                .anyMatch(m -> m.id() != idExcluido && m.nombre().equalsIgnoreCase(nombre));
        if (duplicado) {
            throw new ApiException(409, "Ya existe un medio de pago con ese nombre.");
        }
    }

    private static void validarEmail(String email) {
        if (!email.isEmpty() && (!email.contains("@") || email.length() < 5)) {
            throw new ApiException(400, "El correo electrónico no es válido.");
        }
    }

    private static String estadoCatalogo(Map<String, Object> body, String porDefecto) {
        String estado = opcional(body, "estado");
        if (estado.isEmpty()) {
            return porDefecto;
        }
        if (!estado.equals("Activo") && !estado.equals("Inactivo")) {
            throw new ApiException(400, "El estado debe ser Activo o Inactivo.");
        }
        return estado;
    }

    private static int numeroEntero(Object value, String label) {
        if (value == null || value.toString().isBlank()) {
            throw new ApiException(400, "El campo " + label + " es obligatorio.");
        }
        try {
            double numero = Double.parseDouble(value.toString());
            int entero = (int) numero;
            if (entero != numero) {
                throw new NumberFormatException();
            }
            return entero;
        } catch (NumberFormatException ex) {
            throw new ApiException(400, "El campo " + label + " debe ser un número entero.");
        }
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private static String codigoReserva() {
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
        return "MP-" + uuid.substring(0, 6);
    }

    private void ajustarAsientos(int salaId, int capacidad) {
        List<Asiento> actuales = asientos.values().stream()
                .filter(a -> a.salaId() == salaId)
                .sorted(Comparator.comparing(Asiento::fila).thenComparingInt(Asiento::numero))
                .toList();
        Map<String, Asiento> existentes = new LinkedHashMap<>();
        for (Asiento asiento : actuales) {
            existentes.put(claveAsiento(asiento.fila(), asiento.numero()), asiento);
        }

        List<String> deseados = new ArrayList<>();
        List<Asiento> nuevos = new ArrayList<>();
        for (int i = 0; i < capacidad; i++) {
            String fila = String.valueOf((char) ('A' + (i / ASIENTOS_POR_FILA)));
            int numero = (i % ASIENTOS_POR_FILA) + 1;
            String clave = claveAsiento(fila, numero);
            deseados.add(clave);
            if (!existentes.containsKey(clave)) {
                nuevos.add(new Asiento(asientoSeq++, salaId, fila, numero, "Disponible"));
            }
        }

        for (Asiento asiento : actuales) {
            if (!deseados.contains(claveAsiento(asiento.fila(), asiento.numero()))) {
                asientos.remove(asiento.id());
            }
        }
        for (Asiento asiento : nuevos) {
            asientos.put(asiento.id(), asiento);
        }
    }

    private String claveAsiento(String fila, int numero) {
        return fila + "-" + numero;
    }

    private void validarClasificacion(String clasificacion) {
        if (!CLASIFICACIONES.contains(clasificacion)) {
            throw new ApiException(400, "La clasificación debe ser TP, +7, +13, +16 o +18.");
        }
    }

    private void validarTituloUnico(String titulo, int idExcluido) {
        boolean duplicado = peliculas.values().stream()
                .anyMatch(p -> p.id() != idExcluido && p.titulo().equalsIgnoreCase(titulo));
        if (duplicado) {
            throw new ApiException(409, "Ya existe una película con ese título.");
        }
    }

    private void validarTipoSalaUnico(String nombre, int idExcluido) {
        boolean duplicado = tiposSala.values().stream()
                .anyMatch(t -> t.id() != idExcluido && t.nombre().equalsIgnoreCase(nombre));
        if (duplicado) {
            throw new ApiException(409, "Ya existe un tipo de sala con ese nombre.");
        }
    }

    private void validarSalaUnica(String nombre, int idExcluido) {
        boolean duplicado = salas.values().stream()
                .anyMatch(s -> s.id() != idExcluido && s.nombre().equalsIgnoreCase(nombre));
        if (duplicado) {
            throw new ApiException(409, "Ya existe una sala con ese nombre.");
        }
    }

    private void validarTarifaUnica(String nombre, int idExcluido) {
        boolean duplicado = tarifas.values().stream()
                .anyMatch(t -> t.id() != idExcluido && t.nombre().equalsIgnoreCase(nombre));
        if (duplicado) {
            throw new ApiException(409, "Ya existe una tarifa con ese nombre.");
        }
    }

    private void validarAsientoUnico(int salaId, String fila, int numero, int idExcluido) {
        boolean duplicado = asientos.values().stream()
                .anyMatch(a -> a.id() != idExcluido && a.salaId() == salaId
                        && a.fila().equalsIgnoreCase(fila) && a.numero() == numero);
        if (duplicado) {
            throw new ApiException(409, "Ya existe un asiento con esa fila y número en la sala.");
        }
    }

    private Map<String, Object> salaMap(Sala sala) {
        Map<String, Object> map = sala.toMap();
        TipoSala tipo = tiposSala.get(sala.tipoSalaId());
        map.put("tipoSalaNombre", tipo == null ? "Sin tipo" : tipo.nombre());
        return map;
    }

    private Map<String, Object> asientoMap(Asiento asiento) {
        Map<String, Object> map = asiento.toMap();
        Sala sala = salas.get(asiento.salaId());
        map.put("salaNombre", sala == null ? "" : sala.nombre());
        return map;
    }

    private Map<String, Object> tarifaMap(Tarifa tarifa) {
        Map<String, Object> map = tarifa.toMap();
        TipoSala tipo = tiposSala.get(tarifa.tipoSalaId());
        map.put("tipoSalaNombre", tipo == null ? "Todas las salas" : tipo.nombre());
        return map;
    }

    private Pelicula pelicula(int id) {
        Pelicula pelicula = peliculas.get(id);
        if (pelicula == null) {
            throw new ApiException(404, "La película solicitada no existe.");
        }
        return pelicula;
    }

    private TipoSala tipoSala(int id) {
        TipoSala tipo = tiposSala.get(id);
        if (tipo == null) {
            throw new ApiException(404, "El tipo de sala solicitado no existe.");
        }
        return tipo;
    }

    private Sala sala(int id) {
        Sala sala = salas.get(id);
        if (sala == null) {
            throw new ApiException(404, "La sala solicitada no existe.");
        }
        return sala;
    }

    private Asiento asiento(int id) {
        Asiento asiento = asientos.get(id);
        if (asiento == null) {
            throw new ApiException(404, "El asiento solicitado no existe.");
        }
        return asiento;
    }

    private Tarifa tarifa(int id) {
        Tarifa tarifa = tarifas.get(id);
        if (tarifa == null) {
            throw new ApiException(404, "La tarifa solicitada no existe.");
        }
        return tarifa;
    }

    private static String opcional(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : value.toString().trim();
    }

    static String requerido(Map<String, Object> body, String key, String label) {
        String value = opcional(body, key);
        if (value.isEmpty()) {
            throw new ApiException(400, "El campo " + label + " es obligatorio.");
        }
        return value;
    }

    static int enteroRequerido(Map<String, Object> body, String key, String label, int min, int max) {
        Object value = body.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new ApiException(400, "El campo " + label + " es obligatorio.");
        }
        try {
            double numero = Double.parseDouble(value.toString());
            int entero = (int) numero;
            if (entero != numero) {
                throw new NumberFormatException();
            }
            if (entero < min || entero > max) {
                throw new ApiException(400, "El campo " + label + " está fuera del rango permitido.");
            }
            return entero;
        } catch (NumberFormatException ex) {
            throw new ApiException(400, "El campo " + label + " debe ser un número entero.");
        }
    }

    private static double decimalRequerido(Map<String, Object> body, String key, String label, double min, double max) {
        Object value = body.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new ApiException(400, "El campo " + label + " es obligatorio.");
        }
        try {
            double numero = Double.parseDouble(value.toString());
            if (numero < min || numero > max) {
                throw new ApiException(400, "El campo " + label + " está fuera del rango permitido.");
            }
            return Math.round(numero * 100.0) / 100.0;
        } catch (NumberFormatException ex) {
            throw new ApiException(400, "El campo " + label + " debe ser un número válido.");
        }
    }

    private static String estadoActivo(Map<String, Object> body, String porDefecto) {
        String estado = opcional(body, "estado");
        if (estado.isEmpty()) {
            return porDefecto;
        }
        if (!estado.equals("Activa") && !estado.equals("Inactiva")) {
            throw new ApiException(400, "El estado debe ser Activa o Inactiva.");
        }
        return estado;
    }

    private static String estadoAsiento(Map<String, Object> body, String porDefecto) {
        String estado = opcional(body, "estado");
        if (estado.isEmpty()) {
            return porDefecto;
        }
        if (!estado.equals("Disponible") && !estado.equals("Mantenimiento")) {
            throw new ApiException(400, "El estado del asiento debe ser Disponible o Mantenimiento.");
        }
        return estado;
    }
}
