package co.edu.ean.poo.ventas.modelo;

import java.time.LocalDate;

public class Venta {
    private LocalDate fecha;
    private double valor;

    public Venta(LocalDate fecha, double valor) {
        this.fecha = fecha;
        this.valor = valor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getValor() {
        return valor;
    }
}
