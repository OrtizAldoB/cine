package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record MedioPago(
        int id,
        String nombre,
        String descripcion,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("descripcion", descripcion);
        map.put("estado", estado);
        return map;
    }
}
