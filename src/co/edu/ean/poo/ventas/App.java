package co.edu.ean.poo.ventas;

import co.edu.ean.poo.ventas.parser.ParseadorVentasVendedores;
import co.edu.ean.poo.ventas.parser.ParseadorVentasVendedoresImpl;
import co.edu.ean.poo.ventas.modelo.Vendedor;
import co.edu.ean.poo.ventas.modelo.Venta;
import co.edu.ean.poo.ventas.modelo.CalculadoraVentas;
import co.edu.ean.poo.ventas.reporte.GeneradorReporteVentasTexto;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class App {

    private static Map<Integer, Vendedor> vendedores = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Carga automática al iniciar
        cargarDatosAutomatico();

        // Generar reporte automático para 2020-01-01 a 2020-12-31
        try {
            LocalDate desde = LocalDate.of(2020,1,1);
            LocalDate hasta = LocalDate.of(2020,12,31);
            GeneradorReporteVentasTexto gen = new GeneradorReporteVentasTexto(desde, hasta);
            String outputPath = Paths.get("output", "reporte_ventas.txt").toString();
            gen.generarReporte(vendedores.values(), outputPath);

            double totalGeneral = CalculadoraVentas.totalVentasGrupo(vendedores.values(), desde, hasta);
            long ventasCount = vendedores.values().stream().mapToLong(v -> v.getVentas().stream().filter(s -> !s.getFecha().isBefore(desde) && !s.getFecha().isAfter(hasta)).count()).sum();

            System.out.println("\n=== Reporte automático generado al iniciar ===");
            System.out.println("Período: " + desde + " a " + hasta);
            System.out.println("Vendedores válidos cargados: " + vendedores.size());
            System.out.println("Ventas válidas en período: " + ventasCount);
            System.out.println("Total en ventas: $ " + String.format("%,.0f", totalGeneral));
            System.out.println("Archivo generado en: " + outputPath);
        } catch (Exception e) {
            System.out.println("Error generando reporte automático: " + e.getMessage());
        }

        // Luego mostrar menú (igual que antes)
        boolean running = true;
        while (running) {
            mostrarMenu();
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1" -> cargarDatos();
                case "2" -> registrarVentaInteractive();
                case "3" -> mostrarTotalPorVendedor();
                case "4" -> mostrarTopNVendedores();
                case "5" -> generarReporteInteractive();
                case "6" -> { running = false; System.out.println("Saliendo..."); }
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== Sistema de Gestión de Ventas ===");
        System.out.println("1. Cargar datos de vendedores y ventas");
        System.out.println("2. Registrar una nueva venta");
        System.out.println("3. Mostrar total de ventas por vendedor");
        System.out.println("4. Mostrar top N vendedores");
        System.out.println("5. Generar reporte de ventas en texto");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void cargarDatosAutomatico() {
        String dataPath = "data";
        String vendedoresFile = Paths.get(dataPath, "vendedores.csv").toString();
        String ventasFile = Paths.get(dataPath, "ventas.csv").toString();
        ParseadorVentasVendedores parser = new ParseadorVentasVendedoresImpl();
        vendedores = parser.parsearVendedores(vendedoresFile);
        parser.parsearVentas(ventasFile, vendedores);
        System.out.println("Datos cargados automáticamente. Vendedores válidos: " + vendedores.size());
    }

    private static void cargarDatos() {
        cargarDatosAutomatico();
    }

    private static void registrarVentaInteractive() {
        try {
            System.out.print("Ingrese número de vendedor: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (!vendedores.containsKey(id)) {
                System.out.println("Vendedor no encontrado. Cargue los datos primero (opción 1).");
                return;
            }
            System.out.print("Ingrese fecha de la venta (YYYY-MM-DD): ");
            LocalDate fecha = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Ingrese valor de la venta (número): ");
            double valor = Double.parseDouble(scanner.nextLine().trim());
            Venta venta = new Venta(fecha, valor);
            boolean ok = vendedores.get(id).registrarVenta(venta);
            System.out.println(ok ? "Venta registrada correctamente." : "Venta inválida. No registrada.");
        } catch (Exception e) {
            System.out.println("Error al registrar la venta: " + e.getMessage());
        }
    }

    private static void mostrarTotalPorVendedor() {
        System.out.print("Ingrese número de vendedor: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (!vendedores.containsKey(id)) {
                System.out.println("Vendedor no encontrado.");
                return;
            }
            System.out.print("Ingrese fecha inicio (YYYY-MM-DD): ");
            LocalDate desde = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Ingrese fecha fin (YYYY-MM-DD): ");
            LocalDate hasta = LocalDate.parse(scanner.nextLine().trim());
            double total = CalculadoraVentas.totalVentasVendedor(vendedores.get(id), desde, hasta);
            System.out.println("Total ventas vendedor " + id + ": " + String.format("%,.0f", total));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void mostrarTopNVendedores() {
        try {
            System.out.print("Ingrese N: ");
            int n = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Ingrese fecha inicio (YYYY-MM-DD): ");
            LocalDate desde = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Ingrese fecha fin (YYYY-MM-DD): ");
            LocalDate hasta = LocalDate.parse(scanner.nextLine().trim());
            List<Vendedor> top = CalculadoraVentas.topNVendedores(vendedores.values(), n, desde, hasta);
            System.out.println("Top " + n + " vendedores:");
            int rank = 1;
            for (Vendedor v : top) {
                double total = CalculadoraVentas.totalVentasVendedor(v, desde, hasta);
                System.out.println(rank + ". " + v.getNombreCompleto() + " (ID " + v.getNumero() + ") - " + String.format("%,.0f", total));
                rank++;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generarReporteInteractive() {
        try {
            System.out.print("Ingrese fecha inicio del reporte (YYYY-MM-DD): ");
            LocalDate desde = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Ingrese fecha fin del reporte (YYYY-MM-DD): ");
            LocalDate hasta = LocalDate.parse(scanner.nextLine().trim());
            GeneradorReporteVentasTexto generador = new GeneradorReporteVentasTexto(desde, hasta);
            String outputPath = Paths.get("output", "reporte_ventas.txt").toString();
            generador.generarReporte(vendedores.values(), outputPath);

            double totalGeneral = CalculadoraVentas.totalVentasGrupo(vendedores.values(), desde, hasta);
            long ventasCount = vendedores.values().stream().mapToLong(v -> v.getVentas().stream().filter(s -> !s.getFecha().isBefore(desde) && !s.getFecha().isAfter(hasta)).count()).sum();

            System.out.println("\n=== Reporte generado exitosamente ===");
            System.out.println("Período: " + desde + " a " + hasta);
            System.out.println("Vendedores procesados: " + vendedores.size());
            System.out.println("Ventas válidas: " + ventasCount);
            System.out.println("Total en ventas: $ " + String.format("%,.0f", totalGeneral));
            System.out.println("Archivo generado en: " + outputPath);
        } catch (Exception e) {
            System.out.println("Error al generar reporte: " + e.getMessage());
        }
    }
}
