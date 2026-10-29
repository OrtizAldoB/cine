package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Entrada(
        int id,
        String codigo,
        int reservaId,
        int funcionId,
        int asientoId,
        String fila,
        int numero,
        double precio,
        String estado,
        String emitidaEn) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("codigo", codigo);
        map.put("reservaId", reservaId);
        map.put("funcionId", funcionId);
        map.put("asientoId", asientoId);
        map.put("fila", fila);
        map.put("numero", numero);
        map.put("etiqueta", fila + numero);
        map.put("precio", precio);
        map.put("estado", estado);
        map.put("emitidaEn", emitidaEn);
        return map;
    }
}
