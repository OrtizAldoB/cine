package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Pelicula(
        int id,
        String titulo,
        String genero,
        String clasificacion,
        int duracion,
        String estado,
        String sinopsis) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("titulo", titulo);
        map.put("genero", genero);
        map.put("clasificacion", clasificacion);
        map.put("duracion", duracion);
        map.put("estado", estado);
        map.put("sinopsis", sinopsis);
        return map;
    }
}
