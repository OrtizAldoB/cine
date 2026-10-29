package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Cliente(
        int id,
        String nombre,
        String documento,
        String email,
        String telefono,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("documento", documento);
        map.put("email", email);
        map.put("telefono", telefono);
        map.put("estado", estado);
        return map;
    }
}
