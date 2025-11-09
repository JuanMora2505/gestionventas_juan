package co.edu.ean.poo.ventas.reporte;

import co.edu.ean.poo.ventas.modelo.Vendedor;
import co.edu.ean.poo.ventas.modelo.CalculadoraVentas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.text.DecimalFormat;

public class GeneradorReporteVentasTexto extends GeneradorReporteEnTexto {

    private LocalDate desde;
    private LocalDate hasta;

    public GeneradorReporteVentasTexto(LocalDate desde, LocalDate hasta) {
        this.desde = desde;
        this.hasta = hasta;
    }

    @Override
    public String generarReporteEnTexto(Collection<Vendedor> vendedores) {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE DE VENTAS\n");
        sb.append("==================\n\n");
        sb.append(String.format("%s al %s\n\n", desde.toString(), hasta.toString()));
        sb.append(String.format("%-5s %-30s %15s\n", "ID", "VENDEDOR", "TOTAL VENTAS"));
        sb.append("-------------------------------------------------------\n");
        List<Vendedor> list = new ArrayList<>(vendedores);
        list.sort(Comparator.comparingInt(Vendedor::getNumero));
        double totalGeneral = 0.0;
        for (Vendedor v : list) {
            double total = CalculadoraVentas.totalVentasVendedor(v, desde, hasta);
            if (total <= 0) continue;
            sb.append(String.format("%-5d %-30s %15s\n", v.getNumero(), v.getNombreCompleto(), formatNumber(total)));
            totalGeneral += total;
        }
        sb.append("-------------------------------------------------------\n\n");
        sb.append(String.format("%37s %15s\n", "TOTAL GENERAL:", formatNumber(totalGeneral)));
        return sb.toString();
    }

    private String formatNumber(double val) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(Math.round(val));
    }

    public void generarReporte(Collection<Vendedor> vendedores, String pathOutput) {
        String contenido = generarReporteEnTexto(vendedores);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathOutput))) {
            bw.write(contenido);
        } catch (Exception e) {
            System.err.println("Error escribiendo reporte: " + e.getMessage());
        }
    }
}