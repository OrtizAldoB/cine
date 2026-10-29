package com.cine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    private final Map<String, Usuario> sesiones = new LinkedHashMap<>();

    public AuthService() {
        registrar(new Usuario(1, "admin", "Administrador General", "Administrador", hash("admin123")));
        registrar(new Usuario(2, "usuario", "Usuario de Taquilla", "Usuario", hash("usuario123")));
    }

    private void registrar(Usuario usuario) {
        usuarios.put(usuario.usuario().toLowerCase(), usuario);
    }

    public synchronized Map<String, Object> login(String usuario, String contrasena, String rol) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new ApiException(400, "Ingresa tu usuario y contraseña.");
        }
        Usuario encontrado = usuarios.get(usuario.trim().toLowerCase());
        if (encontrado == null || !encontrado.contrasenaHash().equals(hash(contrasena))) {
            throw new ApiException(401, "Usuario o contraseña incorrectos.");
        }
        if (rol != null && !rol.isBlank() && !encontrado.rol().equalsIgnoreCase(rol.trim())) {
            throw new ApiException(403, "La cuenta no corresponde al perfil seleccionado.");
        }

        String token = UUID.randomUUID().toString();
        sesiones.put(token, encontrado);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("token", token);
        respuesta.put("usuario", encontrado.usuario());
        respuesta.put("nombre", encontrado.nombre());
        respuesta.put("rol", encontrado.rol());
        return respuesta;
    }

    public synchronized Map<String, Object> sesion(String token) {
        Usuario usuario = usuarioPorToken(token);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("usuario", usuario.usuario());
        respuesta.put("nombre", usuario.nombre());
        respuesta.put("rol", usuario.rol());
        return respuesta;
    }

    public synchronized void logout(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    public synchronized Usuario usuarioPorToken(String token) {
        Usuario usuario = token == null ? null : sesiones.get(token);
        if (usuario == null) {
            throw new ApiException(401, "Tu sesión no es válida. Inicia sesión nuevamente.");
        }
        return usuario;
    }

    public synchronized void requiereAdministrador(String token) {
        Usuario usuario = usuarioPorToken(token);
        if (!usuario.rol().equals("Administrador")) {
            throw new ApiException(403, "Solo un administrador puede realizar esta acción.");
        }
    }

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    private final Map<String, Usuario> sesiones = new LinkedHashMap<>();
    private int usuarioSeq;

    public AuthService() {
        registrar(new Usuario(1, "admin", "Administrador General", "Administrador", hash("admin123")));
        registrar(new Usuario(2, "usuario", "Usuario de Taquilla", "Usuario", hash("usuario123")));
        usuarioSeq = 3;
    }

    private void registrar(Usuario usuario) {
        usuarios.put(usuario.usuario().toLowerCase(), usuario);
    }

    public synchronized Map<String, Object> login(String usuario, String contrasena, String rol) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new ApiException(400, "Ingresa tu usuario y contraseña.");
        }
        Usuario encontrado = usuarios.get(usuario.trim().toLowerCase());
        if (encontrado == null || !encontrado.contrasenaHash().equals(hash(contrasena))) {
            throw new ApiException(401, "Usuario o contraseña incorrectos.");
        }
        if (rol != null && !rol.isBlank() && !encontrado.rol().equalsIgnoreCase(rol.trim())) {
            throw new ApiException(403, "La cuenta no corresponde al perfil seleccionado.");
        }

        String token = UUID.randomUUID().toString();
        sesiones.put(token, encontrado);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("token", token);
        respuesta.put("usuario", encontrado.usuario());
        respuesta.put("nombre", encontrado.nombre());
        respuesta.put("rol", encontrado.rol());
        return respuesta;
    }

    public synchronized Map<String, Object> sesion(String token) {
        Usuario usuario = usuarioPorToken(token);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("usuario", usuario.usuario());
        respuesta.put("nombre", usuario.nombre());
        respuesta.put("rol", usuario.rol());
        return respuesta;
    }

    public synchronized void logout(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    public synchronized Usuario usuarioPorToken(String token) {
        Usuario usuario = token == null ? null : sesiones.get(token);
        if (usuario == null) {
            throw new ApiException(401, "Tu sesión no es válida. Inicia sesión nuevamente.");
        }
        return usuario;
    }

    public synchronized void requiereAdministrador(String token) {
        Usuario usuario = usuarioPorToken(token);
        if (!usuario.rol().equals("Administrador")) {
            throw new ApiException(403, "Solo un administrador puede realizar esta acción.");
        }
    }

    public synchronized List<Map<String, Object>> listarUsuarios() {
        return usuarios.values().stream()
                .sorted(Comparator.comparingInt(Usuario::id))
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.id());
                    m.put("usuario", u.usuario());
                    m.put("nombre", u.nombre());
                    m.put("rol", u.rol());
                    m.put("contrasenaHash", u.contrasenaHash());
                    return m;
                })
                .toList();
    }

    public synchronized Map<String, Object> crearUsuario(Map<String, Object> body) {
        String usuario = CineStore.requerido(body, "usuario", "usuario");
        if (usuarios.containsKey(usuario.trim().toLowerCase())) {
            throw new ApiException(409, "Ya existe un usuario con ese nombre de acceso.");
        }
        String nombre = CineStore.requerido(body, "nombre", "nombre completo");
        String rol = CineStore.requerido(body, "rol", "rol");
        if (!rol.equals("Administrador") && !rol.equals("Usuario")) {
            throw new ApiException(400, "El rol debe ser Administrador o Usuario.");
        }
        String contrasena = CineStore.requerido(body, "contrasena", "contraseña");
        if (contrasena.length() < 4) {
            throw new ApiException(400, "La contraseña debe tener al menos 4 caracteres.");
        }

        Usuario nuevo = new Usuario(usuarioSeq++, usuario.trim(), nombre.trim(), rol, hash(contrasena));
        registrar(nuevo);
        return Map.of(
                "id", nuevo.id(),
                "usuario", nuevo.usuario(),
                "nombre", nuevo.nombre(),
                "rol", nuevo.rol()
        );
    }

    public synchronized Usuario usuarioActivoPorToken(String token) {
        try {
            return usuarioPorToken(token);
        } catch (ApiException ex) {
            return null;
        }
    }

    private static String hash(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder();
            for (byte value : bytes) {
                resultado.append(String.format("%02x", value));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No se pudo inicializar el algoritmo de seguridad.", exception);
        }
    }
}
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder();
            for (byte value : bytes) {
                resultado.append(String.format("%02x", value));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No se pudo inicializar el algoritmo de seguridad.", exception);
        }
    }
}
