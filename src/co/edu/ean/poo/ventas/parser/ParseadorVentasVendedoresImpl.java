package co.edu.ean.poo.ventas.parser;

import co.edu.ean.poo.ventas.modelo.Vendedor;
import co.edu.ean.poo.ventas.modelo.Venta;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


public class ParseadorVentasVendedoresImpl implements ParseadorVentasVendedores {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Map<Integer, Vendedor> parsearVendedores(String pathVendedores) {
        Map<Integer, Vendedor> map = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(pathVendedores))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (first && line.toLowerCase().startsWith("numero")) { first = false; continue; }
                first = false;
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;
                String idS = parts[0].trim();
                String nombre = parts[1].trim();
                String apellido = parts[2].trim();
                String fechaS = parts[3].trim();
                int id;
                try {
                    id = Integer.parseInt(idS);
                } catch (Exception e) {
                    continue;
                }
                LocalDate fecha;
                try {
                    fecha = LocalDate.parse(fechaS, ISO);
                } catch (DateTimeParseException e) {
                    continue;
                }
                if (map.containsKey(id)) continue;
                Vendedor v = new Vendedor(id, nombre, apellido, fecha);
                map.put(id, v);
            }
        } catch (Exception ex) {
            System.err.println("Error leyendo vendedores: " + ex.getMessage());
        }
        return map;
    }

    @Override
    public void parsearVentas(String pathVentas, Map<Integer, Vendedor> vendedores) {
        if (vendedores == null) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(pathVentas))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (first && line.toLowerCase().startsWith("numero")) { first = false; continue; }
                first = false;
                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;
                String idS = parts[0].trim();
                String fechaS = parts[1].trim();
                String valorS = parts[2].trim();
                int id;
                try {
                    id = Integer.parseInt(idS);
                } catch (Exception e) {
                    continue;
                }
                if (!vendedores.containsKey(id)) continue;
                Vendedor v = vendedores.get(id);
                LocalDate fecha;
                try {
                    fecha = LocalDate.parse(fechaS, ISO);
                } catch (DateTimeParseException e) {
                    continue;
                }
                double valor;
                try {
                    String clean = valorS.replaceAll("[^0-9\\.\\-]", "");
                    if (clean.isEmpty()) continue;
                    valor = Double.parseDouble(clean);
                } catch (Exception e) {
                    continue;
                }
                Venta venta = new Venta(fecha, valor);
                v.registrarVenta(venta);
            }
        } catch (Exception ex) {
            System.err.println("Error leyendo ventas: " + ex.getMessage());
        }
    }
}
