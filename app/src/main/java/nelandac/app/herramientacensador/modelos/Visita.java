package nelandac.app.herramientacensador.modelos;

/**
 * Clase de modelo Visita.
 * 
 * Representa la entidad de negocio para un registro de censo o visita comercial.
 * Esta clase actúa como un Data Transfer Object (DTO) para el transporte de información
 * entre la interfaz de usuario, la lógica de negocio y la capa de persistencia (SQLite).
 * Contiene atributos detallados del comercio, ubicación geográfica y estados de control.
 */
public class Visita {

    // Identificador único del registro en la base de datos
    private int id;
    
    // Información contextual de la ubicación y el responsable
    private String pais;
    private String prospector;
    private String tipoCliente;
    
    // Datos de identificación del establecimiento y el propietario
    private String nombreComercial;
    private String nombreCliente;
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    
    // Datos de geolocalización
    private String coordenadas;
    private double latitud;
    private double longitud;
    
    // Información de contacto y clasificación comercial
    private String clasificacionNegocio;
    private String telefono;
    private String linkGoogleMaps;
    
    // Información técnica y evidencias
    private String modulo;
    private String fotoNegocio;
    
    // Planificación y estados de seguimiento
    private String diaVisita;
    private String solicitaApoyoSupervisor;
    private String fechaCoordinada;
    
    // Indicadores de perfilamiento de cliente
    private String clienteConVenta;
    private String clienteNuevo;
    private String clienteTieneCodigo;
    
    // Atributos de control de sistema
    private int estadoSync;
    private String fechaRegistro;

    /**
     * Constructor parametrizado para la instanciación completa de un objeto Visita.
     * Utilizado principalmente al recuperar registros existentes desde la base de datos.
     * 
     * @param id Identificador único
     * @param pais País de origen
     * @param prospector Nombre del censador
     * @param tipoCliente Clasificación del cliente
     * @param nombreComercial Nombre del establecimiento
     * @param nombreCliente Nombre del titular
     * @param tipoIdentificacion Tipo de documento legal
     * @param numeroIdentificacion Número de documento legal
     * @param coordenadas Representación String de ubicación (lat,lng)
     * @param latitud Valor numérico de latitud
     * @param longitud Valor numérico de longitud
     * @param clasificacionNegocio Giro del negocio
     * @param telefono Número de contacto
     * @param linkGoogleMaps Enlace externo de ubicación
     * @param modulo Módulo asignado
     * @param fotoNegocio Ruta local de la evidencia fotográfica
     * @param diaVisita Día programado
     * @param solicitaApoyoSupervisor Indicador de requerimiento de supervisión
     * @param fechaCoordinada Fecha pactada para la visita
     * @param clienteConVenta Estado de venta actual
     * @param clienteNuevo Indicador de nuevo registro
     * @param clienteTieneCodigo Estado de código interno
     * @param estadoSync Estado de sincronización con el servidor remoto
     * @param fechaRegistro Marca de tiempo de creación del registro
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
    }

    /**
     * Constructor predeterminado.
     * Inicializa los atributos con valores por defecto para evitar punteros nulos durante
     * el proceso de carga de formularios nuevos.
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
    }

    // Métodos de acceso (Getters y Setters)
    // Implementan la encapsulación de los datos de la clase.

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
}
