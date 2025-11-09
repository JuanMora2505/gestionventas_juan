package co.edu.ean.poo.ventas.modelo;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CalculadoraVentas {


    // Total de ventas de un vendedor en un rango de fechas (inclusive)
    public static double totalVentasVendedor(Vendedor v, LocalDate desde, LocalDate hasta) {
        if (v == null || desde == null || hasta == null) return 0.0;
        return v.getVentas().stream()
                .filter(s -> !s.getFecha().isBefore(desde) && !s.getFecha().isAfter(hasta))
                .mapToDouble(Venta::getValor)
                .sum();
    }

    // Total de ventas de un grupo de vendedores en un rango de fechas
    public static double totalVentasGrupo(Collection<Vendedor> vendedores, LocalDate desde, LocalDate hasta) {
        if (vendedores == null) return 0.0;
        return vendedores.stream()
                .mapToDouble(v -> totalVentasVendedor(v, desde, hasta))
                .sum();
    }

    // Top N vendedores por valor de ventas en un rango de fechas
    public static List<Vendedor> topNVendedores(Collection<Vendedor> vendedores, int n, LocalDate desde, LocalDate hasta) {
        if (vendedores == null) return Collections.emptyList();
        return sellersSortedBySales(vendedores, desde, hasta).stream().limit(n).collect(Collectors.toList());
    }

    private static List<Vendedor> sellersSortedBySales(Collection<Vendedor> vendedores, LocalDate desde, LocalDate hasta) {
        List<Vendedor> list = new ArrayList<>(vendedores);
        list.sort(Comparator.comparingDouble((Vendedor v) -> totalVentasVendedor(v, desde, hasta)).reversed()
                .thenComparingInt(Vendedor::getNumero));
        return list;
    }
}
