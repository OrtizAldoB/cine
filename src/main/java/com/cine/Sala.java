package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Sala(
        int id,
        String nombre,
        int tipoSalaId,
        int capacidad,
        String caracteristicas,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("tipoSalaId", tipoSalaId);
        map.put("capacidad", capacidad);
        map.put("caracteristicas", caracteristicas);
        map.put("estado", estado);
        return map;
    }
}
