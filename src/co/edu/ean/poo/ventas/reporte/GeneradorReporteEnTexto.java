package co.edu.ean.poo.ventas.reporte;

import co.edu.ean.poo.ventas.modelo.Vendedor;
import java.util.Collection;

public abstract class GeneradorReporteEnTexto {
    public abstract String generarReporteEnTexto(Collection<Vendedor> vendedores);
}