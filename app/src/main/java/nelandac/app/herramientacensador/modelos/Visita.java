package nelandac.app.herramientacensador.modelos;

public class Visita {


    private int id;
    private String pais;
    private String prospector;
    private String tipoCliente;
    private String nombreComercial;
    private String nombreCliente;
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String coordenadas;
    private double latitud;
    private double longitud;
    private String clasificacionNegocio;
    private String telefono;
    private String linkGoogleMaps;
    private String modulo;
    private String fotoNegocio;
    private String diaVisita;
    private String solicitaApoyoSupervisor;
    private String fechaCoordinada;
    private String clienteConVenta;
    private String clienteNuevo;
    private String clienteTieneCodigo;
    private int estadoSync;
    private String fechaRegistro;

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
