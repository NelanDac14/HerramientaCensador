package nelandac.app.herramientacensador.modelos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Entidad Maestro: Visita
 * 
 * Esta clase representa la entidad principal de negocio del sistema. Almacena la información 
 * recolectada durante el censo inicial de un punto comercial o cliente. Actúa como el 
 * "Maestro de Clientes" sobre el cual se vinculan los eventos de seguimiento.
 * 
 * Atributos destacados:
 * - Identificación comercial y legal completa.
 * - Datos de geolocalización (Coordenadas, Latitud, Longitud).
 * - Metadatos de auditoría (Fecha de registro, Usuario/Prospector).
 * - Estados de control para sincronización futura.
 */
public class Visita {
    private String uuid;

    /** Identificador primario autonumérico en la base de datos local. */
    private int id;
    
    /** Contexto geográfico del registro. */
    private String pais;
    
    /** Nombre del agente o censador responsable de la captura inicial. */
    private String prospector;
    
    /** Categorización jurídica o comercial del cliente. */
    private String tipoCliente;
    
    /** Nombre de fantasía o razón social del establecimiento. */
    private String nombreComercial;
    
    /** Nombre del contacto principal o propietario legal. */
    private String nombreCliente;
    
    /** Documento de identidad legal asociado. */
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    
    /** Representación combinada de ubicación (lat,lng) para visualización rápida. */
    private String coordenadas;
    
    /** Componentes atómicos de ubicación para integración con Google Maps. */
    private double latitud;
    private double longitud;
    
    /** Segmentación del giro comercial del punto. */
    private String clasificacionNegocio;
    
    /** Canal de comunicación directo. */
    private String telefono;
    
    /** Enlace externo pre-generado para navegación GPS. */
    private String linkGoogleMaps;
    
    /** Identificador del bloque o módulo operativo asignado. */
    private String modulo;
    
    /** Ruta del recurso multimedia capturado durante el censo inicial. */
    private String fotoNegocio;
    
    /** Planificación de ruta comercial. */
    private String diaVisita;
    
    /** Indicador de requerimiento de asistencia técnica/comercial superior. */
    private String solicitaApoyoSupervisor;
    
    /** Compromiso de fecha para la próxima gestión. */
    private String fechaCoordinada;
    
    /** Variables de perfilamiento comercial inicial. */
    private String clienteConVenta;
    private String clienteNuevo;
    private String clienteTieneCodigo;
    
    /** 
     * Control de integridad: 
     * 0 = Local, 1 = Sincronizado con servidor remoto.
     */
    private int estadoSync;
    
    /** Marca de tiempo de creación del registro en el dispositivo (Hora Local). */
    private String fechaRegistro;

    /**
     * Constructor para inicialización de registros nuevos desde el formulario.
     */
    public Visita() {
        this.id = 0;
        this.pais = "";
        this.prospector = "";
        this.tipoCliente = "";
        this.nombreComercial = "";
        this.nombreCliente = "";
        this.tipoIdentificacion = "";
        this.numeroIdentificacion = "";
        this.coordenadas = "";
        this.latitud = 0.0;
        this.longitud = 0.0;
        this.clasificacionNegocio = "";
        this.telefono = "";
        this.linkGoogleMaps = "";
        this.modulo = "";
        this.fotoNegocio = "";
        this.diaVisita = "";
        this.solicitaApoyoSupervisor = "";
        this.fechaCoordinada = "";
        this.clienteConVenta = "";
        this.clienteNuevo = "";
        this.clienteTieneCodigo = "";
        this.estadoSync = 0;
        this.fechaRegistro = "";
        if ((this.uuid == null) || this.uuid.isEmpty()) {
            this.uuid = java.util.UUID.randomUUID().toString();
        }
    }

    /**
     * Constructor integral para la reconstrucción del modelo desde la persistencia SQLite.
     */
    public Visita(int id, String pais, String prospector, String tipoCliente,
                  String nombreComercial, String nombreCliente, String tipoIdentificacion,
                  String numeroIdentificacion, String coordenadas, double latitud,
                  double longitud, String clasificacionNegocio, String telefono,
                  String linkGoogleMaps, String modulo, String fotoNegocio,
                  String diaVisita, String solicitaApoyoSupervisor, String fechaCoordinada,
                  String clienteConVenta, String clienteNuevo, String clienteTieneCodigo,
                  int estadoSync, String fechaRegistro) {
        this.id = id;
        this.pais = pais;
        this.prospector = prospector;
        this.tipoCliente = tipoCliente;
        this.nombreComercial = nombreComercial;
        this.nombreCliente = nombreCliente;
        this.tipoIdentificacion = tipoIdentificacion;
        this.numeroIdentificacion = numeroIdentificacion;
        this.coordenadas = coordenadas;
        this.latitud = latitud;
        this.longitud = longitud;
        this.clasificacionNegocio = clasificacionNegocio;
        this.telefono = telefono;
        this.linkGoogleMaps = linkGoogleMaps;
        this.modulo = modulo;
        this.fotoNegocio = fotoNegocio;
        this.diaVisita = diaVisita;
        this.solicitaApoyoSupervisor = solicitaApoyoSupervisor;
        this.fechaCoordinada = fechaCoordinada;
        this.clienteConVenta = clienteConVenta;
        this.clienteNuevo = clienteNuevo;
        this.clienteTieneCodigo = clienteTieneCodigo;
        this.estadoSync = estadoSync;
        this.fechaRegistro = fechaRegistro;
        if ((this.uuid == null) || this.uuid.isEmpty()) {
            this.uuid = java.util.UUID.randomUUID().toString();
        }
    }

    // Métodos de acceso (Getters y Setters) encapsulados

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProspector() {
        return prospector;
    }

    public void setProspector(String prospector) {
        this.prospector = prospector;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getClasificacionNegocio() {
        return clasificacionNegocio;
    }

    public void setClasificacionNegocio(String clasificacionNegocio) {
        this.clasificacionNegocio = clasificacionNegocio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLinkGoogleMaps() {
        return linkGoogleMaps;
    }

    public void setLinkGoogleMaps(String linkGoogleMaps) {
        this.linkGoogleMaps = linkGoogleMaps;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getFotoNegocio() {
        return fotoNegocio;
    }

    public void setFotoNegocio(String fotoNegocio) {
        this.fotoNegocio = fotoNegocio;
    }

    public String getDiaVisita() {
        return diaVisita;
    }

    public void setDiaVisita(String diaVisita) {
        this.diaVisita = diaVisita;
    }

    public String getSolicitaApoyoSupervisor() {
        return solicitaApoyoSupervisor;
    }

    public void setSolicitaApoyoSupervisor(String solicitaApoyoSupervisor) {
        this.solicitaApoyoSupervisor = solicitaApoyoSupervisor;
    }

    public String getFechaCoordinada() {
        return fechaCoordinada;
    }

    public void setFechaCoordinada(String fechaCoordinada) {
        this.fechaCoordinada = fechaCoordinada;
    }

    public String getClienteConVenta() {
        return clienteConVenta;
    }

    public void setClienteConVenta(String clienteConVenta) {
        this.clienteConVenta = clienteConVenta;
    }

    public String getClienteNuevo() {
        return clienteNuevo;
    }

    public void setClienteNuevo(String clienteNuevo) {
        this.clienteNuevo = clienteNuevo;
    }

    public String getClienteTieneCodigo() {
        return clienteTieneCodigo;
    }

    public void setClienteTieneCodigo(String clienteTieneCodigo) {
        this.clienteTieneCodigo = clienteTieneCodigo;
    }

    public int getEstadoSync() {
        return estadoSync;
    }

    public void setEstadoSync(int estadoSync) {
        this.estadoSync = estadoSync;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFechaRegistroSoloFecha() {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoSalida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date fecha = formatoEntrada.parse(this.fechaRegistro);
            return formatoSalida.format(fecha);

        } catch (Exception e) {
            e.printStackTrace();
            return this.fechaRegistro; // fallback
        }
    }
}
