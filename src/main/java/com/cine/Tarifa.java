package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Tarifa(
        int id,
        String nombre,
        int tipoSalaId,
        String condicion,
        double precio,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("tipoSalaId", tipoSalaId);
        map.put("condicion", condicion);
        map.put("precio", precio);
        map.put("estado", estado);
        return map;
    }
}
