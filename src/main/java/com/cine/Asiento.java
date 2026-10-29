package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Asiento(
        int id,
        int salaId,
        String fila,
        int numero,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("salaId", salaId);
        map.put("fila", fila);
        map.put("numero", numero);
        map.put("estado", estado);
        return map;
    }
}
