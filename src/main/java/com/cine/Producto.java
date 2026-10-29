package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Producto(
        int id,
        String nombre,
        String categoria,
        double precio,
        int stock,
        int stockMinimo,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("categoria", categoria);
        map.put("precio", precio);
        map.put("stock", stock);
        map.put("stockMinimo", stockMinimo);
        map.put("estado", estado);
        return map;
    }
}
