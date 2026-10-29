package com.cine;

import java.util.LinkedHashMap;
import java.util.Map;

public record Reembolso(
        int id,
        int reservaId,
        String reservaCodigo,
        String cliente,
        double montoReembolsado,
        String motivo,
        String medio,
        String registradoEn) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("reservaId", reservaId);
        map.put("reservaCodigo", reservaCodigo);
        map.put("cliente", cliente);
        map.put("montoReembolsado", montoReembolsado);
        map.put("motivo", motivo);
        map.put("medio", medio);
        map.put("registradoEn", registradoEn);
        return map;
    }
}
