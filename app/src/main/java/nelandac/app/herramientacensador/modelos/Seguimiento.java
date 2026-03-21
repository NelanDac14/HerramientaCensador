package nelandac.app.herramientacensador.modelos;

/**
 * Entidad de Negocio: Seguimiento (Bitácora)
 * 
 * Esta clase representa un registro individual en la bitácora de seguimiento de visitas.
 * Permite documentar la evolución de la relación comercial con un cliente (Visita maestra)
 * a través del tiempo, almacenando estados de venta, evidencias fotográficas y observaciones.
 * 
 * Se ha diseñado bajo el patrón POJO para garantizar la integridad en la capa de persistencia.
 */
public class Seguimiento {

    /** Identificador único autonumérico en la tabla bitacora. */
    private int idSeguimiento;
    
    /** Clave foránea que vincula este seguimiento con un registro maestro en la tabla visitas. */
    private int idVisitaMaestro;
    
    /** Marca de tiempo de la realización del seguimiento (Fecha y Hora local del dispositivo). */
    private String fechaSeguimiento;
    
    /** Ruta absoluta del archivo de imagen que sirve como evidencia de la visita actual. */
    private String fotoSeguimiento;
    
    /** Glosa técnica o comercial capturada por el censador durante la visita. */
    private String comentarios;
    
    /** Representación del resultado de la gestión comercial (ej. "Sí", "No"). */
    private String estadoVenta;

    /**
     * Constructor predeterminado requerido para la instanciación dinámica por el motor de base de datos.
     */
    public Seguimiento() {
    }

    /**
     * Constructor integral para la reconstrucción de objetos desde la capa de persistencia.
     * 
     * @param idSeguimiento ID de la base de datos.
     * @param idVisitaMaestro ID del cliente maestro.
     * @param fechaSeguimiento Fecha local del registro.
     * @param fotoSeguimiento Ruta de la imagen de evidencia.
     * @param comentarios Observaciones de campo.
     * @param estadoVenta Estado de la venta.
     */
    public Seguimiento(int idSeguimiento, int idVisitaMaestro, String fechaSeguimiento, 
                       String fotoSeguimiento, String comentarios, String estadoVenta) {
        this.idSeguimiento = idSeguimiento;
        this.idVisitaMaestro = idVisitaMaestro;
        this.fechaSeguimiento = fechaSeguimiento;
        this.fotoSeguimiento = fotoSeguimiento;
        this.comentarios = comentarios;
        this.estadoVenta = estadoVenta;
    }

    // Métodos de acceso encapsulados

    public int getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(int idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public int getIdVisitaMaestro() {
        return idVisitaMaestro;
    }

    public void setIdVisitaMaestro(int idVisitaMaestro) {
        this.idVisitaMaestro = idVisitaMaestro;
    }

    public String getFechaSeguimiento() {
        return fechaSeguimiento;
    }

    public void setFechaSeguimiento(String fechaSeguimiento) {
        this.fechaSeguimiento = fechaSeguimiento;
    }

    public String getFotoSeguimiento() {
        return fotoSeguimiento;
    }

    public void setFotoSeguimiento(String fotoSeguimiento) {
        this.fotoSeguimiento = fotoSeguimiento;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getEstadoVenta() {
        return estadoVenta;
    }

    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }
}
