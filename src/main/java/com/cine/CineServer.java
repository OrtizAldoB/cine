package com.cine;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;

public class CineServer {
    private final CineStore store;
    private final AuthService auth;
    private final Path webDir;

    public CineServer(CineStore store, AuthService auth, Path webDir) {
        this.store = store;
        this.auth = auth;
        this.webDir = webDir;
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.err.println("Puerto no valido. Se usara el puerto 8080.");
            }
        }

        Path webDir = resolveWebDir();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new CineServer(new CineStore(), new AuthService(), webDir)::handle);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("==============================================");
        System.out.println("  MULTICINE POTOSI - Sprint 1: Nucleo Base");
        System.out.println("  Servidor iniciado en http://localhost:" + port);
        System.out.println("  Presiona Ctrl+C para detener el servidor");
        System.out.println("==============================================");
    }

    private static Path resolveWebDir() {
        Path directa = Paths.get("web").toAbsolutePath().normalize();
        if (Files.isDirectory(directa)) {
            return directa;
        }
        Path alternativa = Paths.get("src", "main", "web").toAbsolutePath().normalize();
        if (Files.isDirectory(alternativa)) {
            return alternativa;
        }
        throw new IllegalStateException("No se encontro el directorio web del proyecto.");
    }

    private void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.startsWith("/api/")) {
                handleApi(exchange);
            } else {
                serveStatic(exchange, path);
            }
        } catch (ApiException ex) {
            sendJson(exchange, ex.status(), Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendJson(exchange, 500, Map.of("error", "Ocurrio un error interno en el servidor."));
        } finally {
            exchange.close();
        }
    }

    private void handleApi(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] parts = exchange.getRequestURI().getPath().split("/");
        if (parts.length < 3) {
            throw new ApiException(404, "Recurso no encontrado.");
        }

        String token = bearerToken(exchange);

        if (parts.length >= 3 && parts[2].equals("public")) {
            handlePublic(exchange, method, parts);
            return;
        }

        if (parts.length == 3 && parts[2].equals("login")) {
            requireMethod(method, "POST");
            Map<String, Object> body = readBody(exchange);
            sendJson(exchange, 200, auth.login(bodyText(body, "usuario"),
                    bodyText(body, "contrasena"), bodyText(body, "rol")));
            return;
        }

        if (parts.length == 3 && parts[2].equals("logout")) {
            requireMethod(method, "POST");
            auth.logout(token);
            sendNoContent(exchange);
            return;
        }

        if (parts.length == 3 && parts[2].equals("sesion")) {
            requireMethod(method, "GET");
            sendJson(exchange, 200, auth.sesion(token));
            return;
        }

        Usuario usuario = auth.usuarioPorToken(token);
        if (!method.equals("GET")) {
            auth.requiereAdministrador(token);
        }

        switch (parts[2]) {
            case "peliculas" -> handlePeliculas(exchange, method, parts);
            case "tipos-sala" -> handleTiposSala(exchange, method, parts);
            case "salas" -> handleSalas(exchange, method, parts);
            case "asientos" -> handleAsientos(exchange, method, parts);
            case "tarifas" -> handleTarifas(exchange, method, parts);
            case "funciones" -> handleFunciones(exchange, method, parts);
            case "productos" -> handleProductos(exchange, method, parts);
            case "promociones" -> handlePromociones(exchange, method, parts);
            case "reservas" -> handleReservasAdmin(exchange, method, parts);
            case "recaudacion" -> handleRecaudacion(exchange, method, parts);
            case "usuarios" -> handleUsuarios(exchange, method, parts);
            case "auditoria" -> handleAuditoria(exchange, method, parts);
            case "clientes" -> handleClientes(exchange, method, parts);
            case "medios-pago" -> handleMediosPago(exchange, method, parts);
            case "entradas" -> handleEntradas(exchange, method, parts);
            case "reembolsos" -> handleReembolsos(exchange, method, parts);
            default -> throw new ApiException(404, "Recurso no encontrado.");
        }

        if (!method.equals("GET")) {
            store.auditar(usuario.usuario(), method, exchange.getRequestURI().getPath());
        }
    }

    private void handlePeliculas(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listPeliculas());
                case "POST" -> sendJson(exchange, 201, store.createPelicula(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getPelicula(id));
                case "PUT" -> sendJson(exchange, 200, store.updatePelicula(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deletePelicula(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleTiposSala(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listTiposSala());
                case "POST" -> sendJson(exchange, 201, store.createTipoSala(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getTipoSala(id));
                case "PUT" -> sendJson(exchange, 200, store.updateTipoSala(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteTipoSala(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleSalas(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 6 && parts[4].equals("asientos") && parts[5].equals("generar")) {
            requireMethod(method, "POST");
            sendJson(exchange, 200, store.regenerarAsientos(parseId(parts[3]), readBody(exchange)));
            return;
        }
        if (parts.length == 5 && parts[4].equals("asientos")) {
            requireMethod(method, "GET");
            sendJson(exchange, 200, store.listAsientos(parseId(parts[3])));
            return;
        }
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listSalas());
                case "POST" -> sendJson(exchange, 201, store.createSala(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getSala(id));
                case "PUT" -> sendJson(exchange, 200, store.updateSala(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteSala(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleAsientos(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> {
                    Integer salaId = queryInt(exchange, "salaId");
                    sendJson(exchange, 200, store.listAsientos(salaId));
                }
                case "POST" -> sendJson(exchange, 201, store.createAsiento(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getAsiento(id));
                case "PUT" -> sendJson(exchange, 200, store.updateAsiento(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteAsiento(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleTarifas(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listTarifas());
                case "POST" -> sendJson(exchange, 201, store.createTarifa(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getTarifa(id));
                case "PUT" -> sendJson(exchange, 200, store.updateTarifa(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteTarifa(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handlePublic(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 4 && parts[3].equals("cartelera")) {
            requireMethod(method, "GET");
            sendJson(exchange, 200, store.carteleraPublica());
            return;
        }
        if (parts.length == 6 && parts[3].equals("funciones") && parts[5].equals("asientos")) {
            requireMethod(method, "GET");
            sendJson(exchange, 200, store.asientosFuncion(parseId(parts[4])));
            return;
        }
        if (parts.length == 4 && parts[3].equals("reservas")) {
            requireMethod(method, "POST");
            sendJson(exchange, 201, store.crearReserva(readBody(exchange), "Cliente web"));
            return;
        }
        if (parts.length == 5 && parts[3].equals("reservas")) {
            requireMethod(method, "GET");
            sendJson(exchange, 200, store.buscarReserva(parts[4]));
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleFunciones(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listFunciones());
                case "POST" -> sendJson(exchange, 201, store.createFuncion(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getFuncion(id));
                case "PUT" -> sendJson(exchange, 200, store.updateFuncion(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteFuncion(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleProductos(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listProductos());
                case "POST" -> sendJson(exchange, 201, store.createProducto(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getProducto(id));
                case "PUT" -> sendJson(exchange, 200, store.updateProducto(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteProducto(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handlePromociones(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listPromociones());
                case "POST" -> sendJson(exchange, 201, store.createPromocion(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getPromocion(id));
                case "PUT" -> sendJson(exchange, 200, store.updatePromocion(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deletePromocion(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleReservasAdmin(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            requireMethod(method, "GET");
            sendJson(exchange, 200, store.listReservas());
            return;
        }
        if (parts.length == 5 && parts[4].equals("cancelar")) {
            requireMethod(method, "POST");
            sendJson(exchange, 200, store.cancelarReserva(parseId(parts[3]), readBody(exchange), "Administrador"));
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleClientes(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listClientes());
                case "POST" -> sendJson(exchange, 201, store.createCliente(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getCliente(id));
                case "PUT" -> sendJson(exchange, 200, store.updateCliente(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteCliente(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleMediosPago(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.listMediosPago());
                case "POST" -> sendJson(exchange, 201, store.createMedioPago(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        if (parts.length == 4) {
            int id = parseId(parts[3]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, store.getMedioPago(id));
                case "PUT" -> sendJson(exchange, 200, store.updateMedioPago(id, readBody(exchange)));
                case "DELETE" -> {
                    store.deleteMedioPago(id);
                    sendNoContent(exchange);
                }
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleEntradas(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            requireMethod(method, "GET");
            sendJson(exchange, 200, store.listEntradas());
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleReembolsos(HttpExchange exchange, String method, String[] parts) throws IOException {
        requireMethod(method, "GET");
        sendJson(exchange, 200, store.listReembolsos());
    }

    private void handleRecaudacion(HttpExchange exchange, String method, String[] parts) throws IOException {
        requireMethod(method, "GET");
        sendJson(exchange, 200, store.recaudacion());
    }

    private void handleUsuarios(HttpExchange exchange, String method, String[] parts) throws IOException {
        if (parts.length == 3) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, auth.listarUsuarios());
                case "POST" -> sendJson(exchange, 201, auth.crearUsuario(readBody(exchange)));
                default -> throw methodNotAllowed();
            }
            return;
        }
        throw new ApiException(404, "Recurso no encontrado.");
    }

    private void handleAuditoria(HttpExchange exchange, String method, String[] parts) throws IOException {
        requireMethod(method, "GET");
        sendJson(exchange, 200, store.listAuditoria());
    }

    private void serveStatic(HttpExchange exchange, String path) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            throw methodNotAllowed();
        }
        String relative = path.equals("/") ? "index.html" : path.substring(1);
        Path file = webDir.resolve(relative).normalize();
        if (!file.startsWith(webDir) || !Files.isRegularFile(file)) {
            sendText(exchange, 404, "Recurso no encontrado.");
            return;
        }
        byte[] content = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType(relative));
        exchange.getResponseHeaders().set("Cache-Control", "no-store, no-cache, must-revalidate");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(content);
        }
    }

    private static String contentType(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (lower.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (lower.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }

    private static Map<String, Object> readBody(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return Json.parseObject(body);
        }
    }

    private static String bearerToken(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        String token = header.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private static String bodyText(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : value.toString().trim();
    }

    private static int parseId(String segment) {
        try {
            int id = Integer.parseInt(segment);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException ex) {
            throw new ApiException(400, "El identificador indicado no es valido.");
        }
    }

    private static Integer queryInt(HttpExchange exchange, String name) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isBlank()) {
            return null;
        }
        for (String pair : query.split("&")) {
            int separator = pair.indexOf('=');
            if (separator < 0) {
                continue;
            }
            String key = URLDecoder.decode(pair.substring(0, separator), StandardCharsets.UTF_8);
            if (key.equals(name)) {
                String value = URLDecoder.decode(pair.substring(separator + 1), StandardCharsets.UTF_8);
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    throw new ApiException(400, "El parametro " + name + " no es valido.");
                }
            }
        }
        return null;
    }

    private static void requireMethod(String method, String expected) {
        if (!method.equals(expected)) {
            throw methodNotAllowed();
        }
    }

    private static ApiException methodNotAllowed() {
        return new ApiException(405, "Metodo no permitido para este recurso.");
    }

    private static void sendJson(HttpExchange exchange, int status, Object payload) throws IOException {
        byte[] content = Json.stringify(payload).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, content.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(content);
        }
    }

    private static void sendNoContent(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(204, -1);
    }

    private static void sendText(HttpExchange exchange, int status, String message) throws IOException {
        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, content.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(content);
        }
    }
}
