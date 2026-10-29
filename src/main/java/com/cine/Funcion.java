package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Funcion(
        int id,
        int peliculaId,
        int salaId,
        String fecha,
        String hora,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("peliculaId", peliculaId);
        map.put("salaId", salaId);
        map.put("fecha", fecha);
        map.put("hora", hora);
        map.put("estado", estado);
        return map;
    }
}
