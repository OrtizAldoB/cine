package com.cine;

public record Usuario(
        int id,
        String usuario,
        String nombre,
        String rol,
        String contrasenaHash) {
}
