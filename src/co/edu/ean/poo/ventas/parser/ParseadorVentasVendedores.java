package co.edu.ean.poo.ventas.parser;

import co.edu.ean.poo.ventas.modelo.Vendedor;

import java.util.Map;

/**
 * Interfaz para parsear vendedores y ventas desde archivos CSV.
 */
public interface ParseadorVentasVendedores {
    Map<Integer, Vendedor> parsearVendedores(String pathVendedores);
    void parsearVentas(String pathVentas, Map<Integer, Vendedor> vendedores);
}
