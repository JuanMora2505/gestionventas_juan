package co.edu.ean.poo.ventas.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Vendedor {
    private int numero;
    private String nombre;
    private String apellido;
    private LocalDate fechaIngreso;
    private List<Venta> ventas;

    public Vendedor(int numero, String nombre, String apellido, LocalDate fechaIngreso) {
        this.numero = numero;
        this.nombre = (nombre==null?"":nombre).trim();
        this.apellido = (apellido==null?"":apellido).trim();
        this.fechaIngreso = fechaIngreso;
        this.ventas = new ArrayList<>();
    }

    public int getNumero() {
        return numero;
    }

    public String getNombreCompleto() {
        return (nombre + " " + apellido).trim().replaceAll("\\s+", " ");
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    // Registra una venta sólo si fecha >= fechaIngreso y valor >= 0
    public boolean registrarVenta(Venta venta) {
        if (venta == null) return false;
        if (venta.getFecha() == null) return false;
        if (fechaIngreso == null) return false;
        if (venta.getValor() < 0) return false;
        if (venta.getFecha().isBefore(fechaIngreso)) return false;
        ventas.add(venta);
        return true;
    }
}
