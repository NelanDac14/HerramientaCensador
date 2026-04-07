package nelandac.app.herramientacensador.modelos;

public class ReporteItem {

    public static final String TIPO_VISITA = "VISITA";
    public static final String TIPO_SEGUIMIENTO = "SEGUIMIENTO";

    private String tipo;
    private Visita visita;
    private Seguimiento seguimiento;

    public ReporteItem(String tipo, Visita visita, Seguimiento seguimiento) {
        this.tipo = tipo;
        this.visita = visita;
        this.seguimiento = seguimiento;
    }

    public String getTipo() { return tipo; }
    public Visita getVisita() { return visita; }
    public Seguimiento getSeguimiento() { return seguimiento; }
}
