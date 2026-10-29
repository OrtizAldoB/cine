package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Promocion(
        int id,
        String nombre,
        String tipo,
        double valor,
        String beneficiario,
        String fechaInicio,
        String fechaFin,
        String estado) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("tipo", tipo);
        map.put("valor", valor);
        map.put("beneficiario", beneficiario);
        map.put("fechaInicio", fechaInicio);
        map.put("fechaFin", fechaFin);
        map.put("estado", estado);
        return map;
    }
}
