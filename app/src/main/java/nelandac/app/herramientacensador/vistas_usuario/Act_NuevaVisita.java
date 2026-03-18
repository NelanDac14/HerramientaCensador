package nelandac.app.herramientacensador.vistas_usuario;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
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

public class Act_NuevaVisita extends AppCompatActivity {
    /// Componentes interactivos del Activity
    private Toolbar toolbar;
    private Spinner spinPais, spinProspector, spinTipoCliente,
            spinTipIdentificacion, spinClasComercio, spinDiaVisita,
            spinApoySupervisor, spinVenta, spinClieNuevo, spinCodigo;

    private TextInputEditText txvNombComercial, txvNombCliente, txvNumIdentificacion,
            txvCoordenadas, txvNumTelefono, txvLinkGoogle, txvModulo,
            txvFotoComercio, txvFechaSupervisor;

    private ImageView imgFotoComercio;
    private Button btnObtener, btnGuardar, btnFotoComercio;

    private FusedLocationProviderClient fusedLocationClient;
    private Uri photoUri;
    private String rutaFotoActual;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private int visitaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_nueva_visita);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        iniVistas();
        initListeners();
        visitaId = getIntent().getIntExtra("VISITA_ID", -1);

        if (visitaId != -1) {
            cargarDatos(visitaId);
            btnGuardar.setText("Actualizar"); // UX PRO 🔥
        }
        //Iniciamos el launcher de la cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {

                    if (result) {

                        txvFotoComercio.setText(rutaFotoActual);

                        imgFotoComercio.setImageURI(photoUri);

                        MediaScannerConnection.scanFile(
                                this,
                                new String[]{rutaFotoActual},
                                null,
                                null
                        );

                        Toast.makeText(this,
                                "Foto tomada correctamente",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(this,
                                "No se tomó la foto",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /// Método que nos inicializa todas las de la actividad actual
    private void iniVistas() {
        /// Activamos el ToolBar para visualización de herramientas y menú
        //Toolbar
        toolbar = findViewById(R.id.nueVisita_toolbar);
        setSupportActionBar(toolbar);
        //Spinner
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
        //EditText
        txvNombComercial = findViewById(R.id.edtNombComercial);
        txvNombCliente = findViewById(R.id.edtNombCliente);
        txvNumIdentificacion = findViewById(R.id.edtIdCliente);
        txvCoordenadas = findViewById(R.id.edtCoordenadas);
        txvNumTelefono = findViewById(R.id.edtTelefono);
        txvLinkGoogle = findViewById(R.id.edtLinkGoogle);
        txvModulo = findViewById(R.id.edtModuloVisita);
        txvFotoComercio = findViewById(R.id.edtFotoComercio);
        txvFechaSupervisor = findViewById(R.id.edtFechaApoyo);
        //ImageView
        imgFotoComercio = findViewById(R.id.imgFotoComercio);
        //Button
        btnObtener = findViewById(R.id.btnNVObtCoordenadas);
        btnGuardar = findViewById(R.id.btnNVGuardar);
        btnFotoComercio = findViewById(R.id.btnTomarFoto);


    }

    /// Método que nos ayuda a validar que todos los campos estén debidamente llenos o seleccionados
    private boolean validarCamposVisita() {

        // Lista de EditText obligatorios
        List<EditText> editTexts = Arrays.asList(
                txvNombComercial, txvNombCliente, txvNumIdentificacion,
                txvCoordenadas, txvNumTelefono, txvLinkGoogle,
                txvModulo, txvFotoComercio, txvFechaSupervisor);

        // Lista de Spinners obligatorios
        List<Spinner> spinners = Arrays.asList(spinPais, spinProspector,
                spinTipoCliente, spinTipIdentificacion, spinClasComercio,
                spinDiaVisita, spinApoySupervisor, spinVenta, spinClieNuevo,
                spinCodigo);

        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError(getString(R.string.camposObligatorios));
                editText.requestFocus();
                return false;
            }
        }

        for (Spinner sp : spinners) {
            if (sp.getSelectedItemPosition() <= 0) {
                // Resaltar el Spinner de forma visual
                View selectedView = sp.getSelectedView();
                if (selectedView instanceof TextView) {
                    TextView tv = (TextView) selectedView;
                    tv.setError(getString(R.string.camposObligatorios)); // Muestra el icono de error
                }
                sp.requestFocus(); // Coloca el foco en el Spinner
                sp.post(sp::performClick); // Simula un clic en el Spinner
                return false;
            }
        }

        // Si todo pasó la validación retorna true
        return true;
    }

    private void initListeners() {
        btnGuardar.setOnClickListener(v -> guardarVisita());
        btnObtener.setOnClickListener(v -> obtenerCoordenadas());
        btnFotoComercio.setOnClickListener(v -> tomarFotoComercio());
    }

    /// Método que nos permite obtener las coordenadas de la ubicación en donde nos encontramos

    private void obtenerCoordenadas() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100
            );

            Toast.makeText(this,
                    "Se necesita permiso de ubicación",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                double latitud = location.getLatitude();
                double longitud = location.getLongitude();

                String coordenadas = latitud + "," + longitud;

                txvCoordenadas.setText(coordenadas);

                // Generar link Google Maps automáticamente
                String linkMaps = "https://maps.google.com/?q=" + latitud + "," + longitud;
                txvLinkGoogle.setText(linkMaps);

                Toast.makeText(this,
                        "Ubicación obtenida correctamente",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this,
                        "No se pudo obtener la ubicación. Active el GPS.",
                        Toast.LENGTH_LONG).show();
            }

        }).addOnFailureListener(e -> {

            Toast.makeText(this,
                    "Error obteniendo ubicación: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        });
    }

    private void guardarVisita() {

        if (!validarCamposVisita()) {
            return;
        }

        Visita visita = obtenerDatosFormulario();
        VisitaDAO visitaDAO = new VisitaDAO(this);

        if (visitaId != -1) {

            // MODO EDICIÓN
            visita.setId(visitaId);

            int filas = visitaDAO.updateVisita(visita);

            if (filas > 0) {
                Toast.makeText(this, "Visita actualizada", Toast.LENGTH_LONG).show();
                finish(); // regresar
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_LONG).show();
            }

        } else {

            // 🆕 MODO NUEVO
            long resultado = visitaDAO.insertVisita(visita);

            if (resultado > 0) {
                Toast.makeText(this, "Visita registrada correctamente", Toast.LENGTH_LONG).show();
                limpiarFormulario();
            } else {
                Toast.makeText(this, "Error al registrar la visita", Toast.LENGTH_LONG).show();
            }
        }
    }

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

        // Separar latitud y longitud
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

        visita.setEstadoSync(0);

        return visita;
    }

    private void tomarFotoComercio() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    101);

            return;
        }

        try {

            File photoFile = crearArchivoImagen();

            photoUri = FileProvider.getUriForFile(
                    this,
                    "nelandac.app.herramientacensador.provider",
                    photoFile
            );

            Log.d("FOTO_PATH", photoFile.getAbsolutePath());
            Log.d("FOTO_URI", photoUri.toString());
            //Lanzamos la cámara
            cameraLauncher.launch(photoUri);

        } catch (IOException e) {

            Toast.makeText(this,
                    "Error creando archivo de imagen",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File crearArchivoImagen() throws IOException {

        String timeStamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date());

        String imageFileName = "FOTO_COMERCIO_" + timeStamp + ".jpg";

        // Carpeta pública de imágenes
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                "HerramientaCensador"
        );

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName);

        rutaFotoActual = image.getAbsolutePath();

        return image;
    }

    private void limpiarFormulario() {

        // Limpiar EditText
        txvNombComercial.setText("");
        txvNombCliente.setText("");
        txvNumIdentificacion.setText("");
        txvCoordenadas.setText("");
        txvNumTelefono.setText("");
        txvLinkGoogle.setText("");
        txvModulo.setText("");
        txvFotoComercio.setText("");
        txvFechaSupervisor.setText("");

        // Limpiar imagen
        imgFotoComercio.setImageDrawable(null);

        // Resetear Spinners
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

        // Limpiar ruta de foto
        rutaFotoActual = null;
    }
    private void cargarDatos(int id) {

        VisitaDAO dao = new VisitaDAO(this);
        Visita v = dao.getVisitaById(id);

        if (v != null) {

            spinPais.setSelection(getIndex(spinPais, v.getPais()));
            spinProspector.setSelection(getIndex(spinProspector, v.getProspector()));
            spinTipoCliente.setSelection(getIndex(spinTipoCliente, v.getTipoCliente()));

            txvNombComercial.setText(v.getNombreComercial());
            txvNombCliente.setText(v.getNombreCliente());
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

            // Imagen
            if (v.getFotoNegocio() != null && !v.getFotoNegocio().isEmpty()) {
                Uri uri = Uri.fromFile(new File(v.getFotoNegocio()));
                imgFotoComercio.setImageURI(uri);
            }
        }
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }
}