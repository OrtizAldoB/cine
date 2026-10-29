package com.cine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Reserva(
        int id,
        String codigo,
        int funcionId,
        List<Integer> asientoIds,
        Map<Integer, Integer> productos,
        String cliente,
        String email,
        String medioPago,
        double subtotalEntradas,
        double descuento,
        double subtotalConfiteria,
        double total,
        String estado,
        String creadaEn) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("codigo", codigo);
        map.put("funcionId", funcionId);
        map.put("asientoIds", asientoIds);
        map.put("productos", productos);
        map.put("cliente", cliente);
        map.put("email", email);
        map.put("medioPago", medioPago);
        map.put("subtotalEntradas", subtotalEntradas);
        map.put("descuento", descuento);
        map.put("subtotalConfiteria", subtotalConfiteria);
        map.put("total", total);
        map.put("estado", estado);
        map.put("creadaEn", creadaEn);
        return map;
    }
}
