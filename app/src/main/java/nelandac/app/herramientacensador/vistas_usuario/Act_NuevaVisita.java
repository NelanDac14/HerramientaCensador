package nelandac.app.herramientacensador.vistas_usuario;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import android.net.Uri;
import android.os.Environment;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.textfield.TextInputEditText;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;

/**
 * Clase Act_NuevaVisita
 * <p>
 * Esta actividad gestiona el ciclo de vida de la creación y edición de registros de visitas.
 * Implementa funcionalidades críticas como la obtención de geolocalización mediante Google Play Services,
 * captura de imágenes mediante la cámara del sistema e integración con la galería del dispositivo.
 * <p>
 * La clase sigue un patrón de diseño imperativo para la gestión de la interfaz de usuario (UI)
 * y utiliza un objeto de acceso a datos (DAO) para la persistencia en una base de datos local SQLite.
 */
public class Act_NuevaVisita extends AppCompatActivity {

    // Declaración de componentes de la interfaz de usuario
    private Toolbar toolbar;

    // Selectores para datos categóricos
    private Spinner spinPais, spinProspector, spinTipoCliente,
            spinTipIdentificacion, spinClasComercio, spinDiaVisita,
            spinApoySupervisor, spinVenta, spinClieNuevo, spinCodigo;

    // Campos de entrada de texto utilizando componentes de Material Design
    private TextInputEditText txvNombComercial, txvNombCliente, txvNumIdentificacion,
            txvCoordenadas, txvNumTelefono, txvLinkGoogle, txvModulo,
            txvFotoComercio, txvFechaSupervisor;

    // Visualización de la imagen capturada o seleccionada
    private ImageView imgFotoComercio;

    // Botones de acción principal
    private Button btnObtener, btnGuardar, btnFotoComercio, btnAgregarFoto;

    // Cliente para servicios de ubicación de Google Play
    private FusedLocationProviderClient fusedLocationClient;

    // Variables para la gestión de archivos y rutas de imágenes
    private Uri photoUri;
    private String rutaFotoActual;

    // Lanzadores para resultados de actividades externas (Cámara y Galería)
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> galeriaLauncher;

    // Variables de control de estado para el modo edición
    private int visitaId = -1;
    private int position = -1;

    /**
     * Punto de entrada de la actividad.
     * Configura el entorno visual, inicializa servicios y registra los callbacks de los ActivityResultLaunchers.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitación de renderizado de borde a borde para una experiencia visual inmersiva
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_nueva_visita);

        // Inicialización del cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicialización de componentes y eventos
        iniVistas();
        initListeners();

        // Recuperación de parámetros pasados por el Intent para determinar si es creación o actualización
        visitaId = getIntent().getIntExtra("VISITA_ID", -1);
        position = getIntent().getIntExtra("POSITION", -1);

        // Si existe un ID válido, se procede a cargar los datos del registro para su edición
        if (visitaId != -1) {
            cargarDatos(visitaId);
            btnGuardar.setText(R.string.actualizar);
        }

        /**
         * Registro del contrato para la captura de fotografía desde la cámara.
         * En caso de éxito, actualiza la UI con la ruta del archivo y previsualiza la imagen.
         */
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        txvFotoComercio.setText(rutaFotoActual);
                        imgFotoComercio.setImageURI(photoUri);

                        // Notifica al sistema de medios para que la imagen sea visible en la galería
                        MediaScannerConnection.scanFile(
                                this,
                                new String[]{rutaFotoActual},
                                null,
                                null
                        );

                        Toast.makeText(this, "Foto tomada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No se tomó la foto", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        /**
         * Registro del contrato para la selección de contenido desde la galería.
         * En caso de éxito, realiza una copia local del archivo para asegurar su persistencia en el scope de la app.
         */
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            String ruta = copiarImagenAGaleriaLocal(uri);

                            // 🔥 ESTA LÍNEA ES LA CLAVE
                            rutaFotoActual = ruta;

                            txvFotoComercio.setText(ruta);
                            imgFotoComercio.setImageURI(Uri.fromFile(new File(ruta)));

                            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error al copiar imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Ajuste dinámico de paddings para respetar las barras del sistema (Status y Navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Vincula las variables locales con los elementos definidos en el archivo de diseño XML.
     */
    private void iniVistas() {
        // Configuración del ToolBar como ActionBar de la actividad
        toolbar = findViewById(R.id.nueVisita_toolbar);
        setSupportActionBar(toolbar);

        // Mapeo de selectores (Spinners)
        spinPais = findViewById(R.id.spinPaises);
        spinProspector = findViewById(R.id.spinProspector);
        spinTipoCliente = findViewById(R.id.spinTipCliente);
        spinTipIdentificacion = findViewById(R.id.spinTipoId);
        spinClasComercio = findViewById(R.id.spinClasNegocio);
        spinDiaVisita = findViewById(R.id.spinDiaVisita);
        spinApoySupervisor = findViewById(R.id.spinSolicApoyo);
        spinVenta = findViewById(R.id.spinClieVenta);
        spinClieNuevo = findViewById(R.id.spinClieNuevo);
        spinCodigo = findViewById(R.id.spinClieCodigo);

        // Mapeo de campos de texto (EditText)
        txvNombComercial = findViewById(R.id.edtNombComercial);
        txvNombCliente = findViewById(R.id.edtNombCliente);
        txvNumIdentificacion = findViewById(R.id.edtIdCliente);
        txvCoordenadas = findViewById(R.id.edtCoordenadas);
        txvNumTelefono = findViewById(R.id.edtTelefono);
        txvLinkGoogle = findViewById(R.id.edtLinkGoogle);
        txvModulo = findViewById(R.id.edtModuloVisita);
        txvFotoComercio = findViewById(R.id.edtFotoComercio);
        txvFechaSupervisor = findViewById(R.id.edtFechaApoyo);

        // Mapeo de vistas de imagen y botones
        imgFotoComercio = findViewById(R.id.imgFotoComercio);
        btnObtener = findViewById(R.id.btnNVObtCoordenadas);
        btnGuardar = findViewById(R.id.btnNVGuardar);
        btnFotoComercio = findViewById(R.id.btnTomarFoto);
        btnAgregarFoto = findViewById(R.id.btnAgregarFoto);
    }

    /**
     * Ejecuta la lógica de validación de negocio sobre los campos obligatorios del formulario.
     *
     * @return true si todos los campos cumplen con los requisitos mínimos; false en caso contrario.
     */
    private boolean validarCamposVisita() {
        // Validación masiva de campos de texto
        List<EditText> editTexts = Arrays.asList(
                txvNombComercial, txvNombCliente, txvNumIdentificacion,
                txvCoordenadas, txvNumTelefono, txvLinkGoogle,
                txvModulo, txvFotoComercio, txvFechaSupervisor);

        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError(getString(R.string.camposObligatorios));
                editText.requestFocus();
                return false;
            }
        }

        // Validación masiva de Spinners para asegurar que se ha seleccionado una opción distinta a la por defecto
        List<Spinner> spinners = Arrays.asList(spinPais, spinProspector,
                spinTipoCliente, spinTipIdentificacion, spinClasComercio,
                spinDiaVisita, spinApoySupervisor, spinVenta, spinClieNuevo,
                spinCodigo);

        for (Spinner sp : spinners) {
            if (sp.getSelectedItemPosition() <= 0) {
                View selectedView = sp.getSelectedView();
                if (selectedView instanceof TextView) {
                    TextView tv = (TextView) selectedView;
                    tv.setError(getString(R.string.camposObligatorios));
                }
                sp.requestFocus();
                sp.post(sp::performClick);
                return false;
            }
        }
        return true;
    }

    /**
     * Define y asigna los escuchadores de eventos para los componentes interactivos.
     */
    private void initListeners() {
        btnGuardar.setOnClickListener(v -> guardarVisita());
        btnObtener.setOnClickListener(v -> obtenerCoordenadas());
        btnFotoComercio.setOnClickListener(v -> tomarFotoComercio());

        // Acción de pulsación larga para abrir la galería
        btnAgregarFoto.setOnLongClickListener(v -> {
            galeriaLauncher.launch("image/*");
            return true;
        });

        // Despliegue del selector de fecha al interactuar con el campo correspondiente
        txvFechaSupervisor.setOnClickListener(v -> mostrarDatePicker());

        spinApoySupervisor.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String valor = parent.getItemAtPosition(position).toString();

                if (valor.equalsIgnoreCase("No")) {
                    txvFechaSupervisor.setText(R.string.fecha_no_requerida);
                    txvFechaSupervisor.setEnabled(false);
                    txvFechaSupervisor.setFocusable(false);
                    txvFechaSupervisor.setClickable(false);
                } else {
                    txvFechaSupervisor.setText("");
                    txvFechaSupervisor.setEnabled(true);
                    txvFechaSupervisor.setFocusable(true);
                    txvFechaSupervisor.setClickable(true);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // No aplica
            }
        });
    }

    /**
     * Gestiona la lógica para establecer la ubicación geográfica.
     * Prioriza la generación de un enlace de Google Maps a partir de coordenadas existentes,
     * o solicita la ubicación actual del dispositivo si el campo está vacío.
     * Orquestador inteligente de obtención de coordenadas.
     * <p>
     * Estrategia:
     * 1. Intentar obtener coordenadas desde metadatos EXIF de la imagen.
     * 2. Si falla, usar coordenadas ya ingresadas manualmente.
     * 3. Como último recurso, obtener ubicación GPS del dispositivo.
     * <p>
     * Este enfoque garantiza resiliencia ante diferentes fuentes de datos.
     */
    private void obtenerCoordenadas() {

        Log.d("COORD_FLOW", "Iniciando obtención de coordenadas...");
        Log.d("COORD_FLOW", "Ruta actual imagen: " + rutaFotoActual);

        // =====================================================
        // 🔥 1. INTENTO DESDE IMAGEN (EXIF)
        // =====================================================
        if (rutaFotoActual != null && !rutaFotoActual.isEmpty()) {

            File file = new File(rutaFotoActual);

            if (file.exists()) {

                try {
                    ExifInterface exif = new ExifInterface(rutaFotoActual);

                    // 🔍 DEBUG PROFUNDO EXIF
                    String latRaw = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    String lngRaw = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

                    Log.d("EXIF_DEBUG", "LAT RAW: " + latRaw);
                    Log.d("EXIF_DEBUG", "LNG RAW: " + lngRaw);

                    float[] latLong = new float[2];

                    if (exif.getLatLong(latLong)) {

                        double lat = latLong[0];
                        double lng = latLong[1];

                        String coordenadas = lat + "," + lng;

                        txvCoordenadas.setText(coordenadas);
                        txvLinkGoogle.setText("https://maps.google.com/?q=" + coordenadas);

                        Toast.makeText(this, "📸 Coordenadas obtenidas desde la imagen", Toast.LENGTH_LONG).show();

                        Log.d("COORD_FLOW", "EXIF OK → " + coordenadas);

                        return;
                    } else {
                        Log.w("COORD_FLOW", "Imagen sin datos GPS en EXIF");
                        Toast.makeText(this, "⚠️ Imagen sin coordenadas GPS", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("COORD_FLOW", "Error leyendo EXIF", e);
                    Toast.makeText(this, "Error leyendo metadatos de imagen", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.e("COORD_FLOW", "Archivo NO existe: " + rutaFotoActual);
            }
        }

        // =====================================================
        // 🔥 2. COORDENADAS EXISTENTES EN UI
        // =====================================================
        String coordenadasActuales = txvCoordenadas.getText().toString().trim();

        if (!coordenadasActuales.isEmpty() && coordenadasActuales.contains(",")) {

            try {
                String[] latLng = coordenadasActuales.split(",");

                String lat = latLng[0].trim();
                String lng = latLng[1].trim();

                txvLinkGoogle.setText("https://maps.google.com/?q=" + lat + "," + lng);

                Toast.makeText(this, "🔗 Link generado desde coordenadas existentes", Toast.LENGTH_SHORT).show();

                Log.d("COORD_FLOW", "USANDO COORDENADAS MANUALES → " + lat + "," + lng);

                return;

            } catch (Exception e) {
                Log.e("COORD_FLOW", "Error parseando coordenadas manuales", e);
            }
        }

        // =====================================================
        // 🔥 3. FALLBACK GPS
        // =====================================================
        Log.d("COORD_FLOW", "Usando GPS como fallback...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            Toast.makeText(this, "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {

                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        String coordenadas = lat + "," + lng;

                        txvCoordenadas.setText(coordenadas);
                        txvLinkGoogle.setText("https://maps.google.com/?q=" + coordenadas);

                        Toast.makeText(this, "📍 Coordenadas obtenidas por GPS", Toast.LENGTH_SHORT).show();

                        Log.d("COORD_FLOW", "GPS OK → " + coordenadas);

                    } else {
                        Log.e("COORD_FLOW", "GPS devolvió null");
                        Toast.makeText(this, "❌ No se pudo obtener ubicación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Orquestador para el guardado de datos.
     * Determina si debe realizar una inserción o una actualización basándose en el estado de la actividad.
     */
    private void guardarVisita() {
        if (!validarCamposVisita()) {
            return;
        }

        Visita visita = obtenerDatosFormulario();
        VisitaDAO visitaDAO = new VisitaDAO(this);

        if (visitaId != -1) {
            // Operación de Actualización (Update)
            visita.setId(visitaId);
            int filas = visitaDAO.updateVisita(visita);

            if (filas > 0) {
                Toast.makeText(this, "Visita actualizada", Toast.LENGTH_LONG).show();
                Intent data = new Intent();
                data.putExtra("VISITA_ID", visitaId);
                data.putExtra("POSITION", position);
                setResult(RESULT_OK, data);
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_LONG).show();
            }
        } else {
            // Operación de Inserción (Insert)
            long resultado = visitaDAO.insertVisita(visita);
            if (resultado > 0) {
                Toast.makeText(this, "Visita registrada correctamente", Toast.LENGTH_LONG).show();
                limpiarFormulario();
            } else {
                Toast.makeText(this, "Error al registrar la visita", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Mapea los valores de la interfaz de usuario a una instancia del modelo de datos Visita.
     *
     * @return Objeto Visita con la información recolectada del formulario.
     */
    private Visita obtenerDatosFormulario() {
        Visita visita = new Visita();

        visita.setPais(spinPais.getSelectedItem().toString());
        visita.setProspector(spinProspector.getSelectedItem().toString());
        visita.setTipoCliente(spinTipoCliente.getSelectedItem().toString());
        visita.setNombreComercial(txvNombComercial.getText().toString().trim());
        visita.setNombreCliente(txvNombCliente.getText().toString().trim());
        visita.setTipoIdentificacion(spinTipIdentificacion.getSelectedItem().toString());
        visita.setNumeroIdentificacion(txvNumIdentificacion.getText().toString().trim());
        visita.setCoordenadas(txvCoordenadas.getText().toString().trim());

        // Procesamiento de coordenadas para almacenamiento atómico en latitud/longitud
        String[] latLng = txvCoordenadas.getText().toString().split(",");
        if (latLng.length == 2) {
            visita.setLatitud(Double.parseDouble(latLng[0]));
            visita.setLongitud(Double.parseDouble(latLng[1]));
        }

        visita.setClasificacionNegocio(spinClasComercio.getSelectedItem().toString());
        visita.setTelefono(txvNumTelefono.getText().toString().trim());
        visita.setLinkGoogleMaps(txvLinkGoogle.getText().toString().trim());
        visita.setModulo(txvModulo.getText().toString().trim());
        visita.setFotoNegocio(txvFotoComercio.getText().toString().trim());
        visita.setDiaVisita(spinDiaVisita.getSelectedItem().toString());
        visita.setSolicitaApoyoSupervisor(spinApoySupervisor.getSelectedItem().toString());
        visita.setFechaCoordinada(txvFechaSupervisor.getText().toString().trim());
        visita.setClienteConVenta(spinVenta.getSelectedItem().toString());
        visita.setClienteNuevo(spinClieNuevo.getSelectedItem().toString());
        visita.setClienteTieneCodigo(spinCodigo.getSelectedItem().toString());

        // Estado por defecto para sincronización posterior
        visita.setEstadoSync(0);

        return visita;
    }

    /**
     * Inicia el flujo de captura de imagen.
     * Verifica permisos de cámara y genera un URI seguro mediante FileProvider para la comunicación con la app de cámara.
     */
    /**
     * Inicia captura de imagen asegurando persistencia de ruta absoluta.
     * Este método garantiza que rutaFotoActual siempre esté disponible
     * para procesos posteriores como EXIF o subida a servidor.
     */
    private void tomarFotoComercio() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
            return;
        }

        try {
            File photoFile = crearArchivoImagen();

            // 🔥 ASEGURAR RUTA SIEMPRE
            rutaFotoActual = photoFile.getAbsolutePath();

            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile
            );

            Log.d("FOTO_DEBUG", "Ruta: " + rutaFotoActual);
            Log.d("FOTO_DEBUG", "URI: " + photoUri);

            cameraLauncher.launch(photoUri);

        } catch (IOException e) {
            Log.e("FOTO_DEBUG", "Error creando archivo", e);
            Toast.makeText(this, "Error creando archivo de imagen", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Crea un archivo temporal en el almacenamiento público de imágenes para guardar la captura de la cámara.
     *
     * @return Objeto File apuntando a la nueva imagen.
     * @throws IOException Sí ocurre un error durante la creación del archivo.
     */
    private File crearArchivoImagen() throws IOException {
        // Marca de tiempo en formato local explícito
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "FOTO_COMERCIO_" + timeStamp + ".jpg";

        // Carpeta persistente bajo el directorio de imágenes del sistema
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "HerramientaCensador"
        );

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName);
        rutaFotoActual = image.getAbsolutePath();

        return image;
    }

    /**
     * Restablece todos los campos del formulario a su estado inicial.
     */
    private void limpiarFormulario() {
        txvNombComercial.setText("");
        txvNombCliente.setText("");
        txvNumIdentificacion.setText("");
        txvCoordenadas.setText("");
        txvNumTelefono.setText("");
        txvLinkGoogle.setText("");
        txvModulo.setText("");
        txvFotoComercio.setText("");
        txvFechaSupervisor.setText("");

        imgFotoComercio.setImageDrawable(null);

        spinPais.setSelection(0);
        spinProspector.setSelection(0);
        spinTipoCliente.setSelection(0);
        spinTipIdentificacion.setSelection(0);
        spinClasComercio.setSelection(0);
        spinDiaVisita.setSelection(0);
        spinApoySupervisor.setSelection(0);
        spinVenta.setSelection(0);
        spinClieNuevo.setSelection(0);
        spinCodigo.setSelection(0);

        rutaFotoActual = null;
    }

    /**
     * Carga la información de una visita desde la base de datos y actualiza la UI para modo edición.
     *
     * @param id Identificador único del registro en la base de datos.
     */
    private void cargarDatos(int id) {
        VisitaDAO dao = new VisitaDAO(this);
        Visita v = dao.getVisitaById(id);

        if (v != null) {
            spinPais.setSelection(getIndex(spinPais, v.getPais()));
            spinProspector.setSelection(getIndex(spinProspector, v.getProspector()));
            spinTipoCliente.setSelection(getIndex(spinTipoCliente, v.getTipoCliente()));
            txvNombComercial.setText(v.getNombreComercial());
            txvNombCliente.setText(v.getNombreCliente());
            spinTipIdentificacion.setSelection(getIndex(spinTipIdentificacion, v.getTipoIdentificacion()));
            txvNumIdentificacion.setText(v.getNumeroIdentificacion());
            txvCoordenadas.setText(v.getCoordenadas());
            txvNumTelefono.setText(v.getTelefono());
            txvLinkGoogle.setText(v.getLinkGoogleMaps());
            txvModulo.setText(v.getModulo());
            txvFotoComercio.setText(v.getFotoNegocio());
            txvFechaSupervisor.setText(v.getFechaCoordinada());
            spinClasComercio.setSelection(getIndex(spinClasComercio, v.getClasificacionNegocio()));
            spinDiaVisita.setSelection(getIndex(spinDiaVisita, v.getDiaVisita()));
            spinApoySupervisor.setSelection(getIndex(spinApoySupervisor, v.getSolicitaApoyoSupervisor()));
            spinVenta.setSelection(getIndex(spinVenta, v.getClienteConVenta()));
            spinClieNuevo.setSelection(getIndex(spinClieNuevo, v.getClienteNuevo()));
            spinCodigo.setSelection(getIndex(spinCodigo, v.getClienteTieneCodigo()));

            if (v.getFotoNegocio() != null && !v.getFotoNegocio().isEmpty()) {
                Uri uri = Uri.fromFile(new File(v.getFotoNegocio()));
                imgFotoComercio.setImageURI(uri);
            }
        }
    }

    /**
     * Utilidad para encontrar la posición de un valor específico dentro del adaptador de un Spinner.
     *
     * @param spinner El componente Spinner a evaluar.
     * @param value   El valor en formato String a localizar.
     * @return El índice del valor encontrado o 0 por defecto.
     */
    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Realiza una copia física de una imagen seleccionada desde una URI externa a una ubicación local gestionada por la app.
     *
     * @param uri URI de origen de la imagen.
     * @return Ruta absoluta del archivo copiado en el almacenamiento local.
     * @throws IOException Si falla el flujo de lectura o escritura.
     */
    private String copiarImagenAGaleriaLocal(Uri uri) throws IOException {
        File carpeta = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "HerramientaCensador"
        );

        if (!carpeta.exists()) carpeta.mkdirs();

        // Nombre de archivo con marca de tiempo local
        String nombreArchivo = "FOTO_COMERCIO_" + System.currentTimeMillis() + ".jpg";
        File archivoDestino = new File(carpeta, nombreArchivo);

        InputStream inputStream = getContentResolver().openInputStream(uri);
        OutputStream outputStream = new FileOutputStream(archivoDestino);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();

        return archivoDestino.getAbsolutePath();
    }

    /**
     * Despliega un DatePickerDialog para la selección de fechas y formatea la salida para el usuario.
     */
    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String fecha = String.format("%02d", day) + "-" +
                            String.format("%02d", month + 1) + "-" +
                            year;
                    txvFechaSupervisor.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }
}