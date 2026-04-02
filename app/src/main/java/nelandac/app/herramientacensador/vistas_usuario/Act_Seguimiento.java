package nelandac.app.herramientacensador.vistas_usuario;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.modelos.Seguimiento;
import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;

/**
 * 🔥 VERSIÓN CORREGIDA - NIVEL PRODUCCIÓN
 *
 * - Elimina dependencia de Intent para datos del cliente
 * - Usa BD como fuente de verdad (source of truth)
 * - Evita NULLs en UI
 */
public class Act_Seguimiento extends AppCompatActivity {

    private TextView txtClienteReferencia;
    private ImageView imgFotoSeguimiento;
    private Button btnTomarFoto, btnGuardar;
    private Spinner spinEstadoVenta;
    private TextInputEditText edtComentarios;

    private int idVisitaMaestro;
    private String nombreCliente;
    private String rutaFotoActual;
    private Uri photoUri;

    private ActivityResultLauncher<Uri> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_seguimiento);

        // 🔥 ID DEL CLIENTE
        idVisitaMaestro = getIntent().getIntExtra("VISITA_ID", -1);

        if (idVisitaMaestro == -1) {
            Toast.makeText(this, "Error: Cliente no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initVistas();
        initListeners();
        configurarLauncherCamara();

        // =====================================================
        // 🔥 FIX REAL: CARGAR CLIENTE DESDE BD (NO DESDE INTENT)
        // =====================================================
        cargarDatosCliente();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * 🔥 MÉTODO NUEVO - SOURCE OF TRUTH
     */
    private void cargarDatosCliente() {
        VisitaDAO dao = new VisitaDAO(this);
        Visita visita = dao.getVisitaById(idVisitaMaestro);

        if (visita != null) {
            nombreCliente = visita.getNombreComercial();
            txtClienteReferencia.setText(nombreCliente);

            Log.d("CLIENTE_DEBUG", "Cliente cargado: " + nombreCliente);
        } else {
            nombreCliente = "Cliente no encontrado";
            txtClienteReferencia.setText(nombreCliente);

            Log.e("CLIENTE_DEBUG", "No se encontró cliente con ID: " + idVisitaMaestro);
        }
    }

    private void initVistas() {
        txtClienteReferencia = findViewById(R.id.txtClienteReferencia);
        imgFotoSeguimiento = findViewById(R.id.imgFotoSeguimiento);
        btnTomarFoto = findViewById(R.id.btnTomarFotoSeguimiento);
        btnGuardar = findViewById(R.id.btnGuardarSeguimiento);
        spinEstadoVenta = findViewById(R.id.spinEstadoVenta);
        edtComentarios = findViewById(R.id.edtComentarios);
    }

    private void initListeners() {
        btnTomarFoto.setOnClickListener(v -> tomarFoto());
        btnGuardar.setOnClickListener(v -> guardarSeguimiento());
    }

    private void configurarLauncherCamara() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        imgFotoSeguimiento.setImageURI(photoUri);
                    } else {
                        Toast.makeText(this, "No se capturó la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void tomarFoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
            return;
        }

        try {
            File photoFile = crearArchivoImagen();

            // 🔥 ASEGURAR RUTA
            rutaFotoActual = photoFile.getAbsolutePath();

            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);

            Log.d("FOTO_DEBUG", "Ruta: " + rutaFotoActual);

            cameraLauncher.launch(photoUri);

        } catch (IOException e) {
            Toast.makeText(this, "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "SEGUIMIENTO_" + idVisitaMaestro + "_" + timeStamp + ".jpg";

        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "HerramientaCensador/Seguimientos"
        );

        if (!storageDir.exists()) storageDir.mkdirs();

        File image = new File(storageDir, imageFileName);
        return image;
    }

    private void guardarSeguimiento() {

        if (spinEstadoVenta.getSelectedItemPosition() <= 0) {
            Toast.makeText(this, getString(R.string.seleccionar_estado_venta), Toast.LENGTH_SHORT).show();
            return;
        }

        Seguimiento s = new Seguimiento();
        s.setIdVisitaMaestro(idVisitaMaestro);
        s.setEstadoVenta(spinEstadoVenta.getSelectedItem().toString());
        s.setComentarios(edtComentarios.getText().toString().trim());
        s.setFotoSeguimiento(rutaFotoActual != null ? rutaFotoActual : "");

        VisitaDAO dao = new VisitaDAO(this);
        long id = dao.insertSeguimiento(s);

        if (id > 0) {
            Toast.makeText(this, "Seguimiento registrado con éxito", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar seguimiento", Toast.LENGTH_SHORT).show();
        }
    }
}