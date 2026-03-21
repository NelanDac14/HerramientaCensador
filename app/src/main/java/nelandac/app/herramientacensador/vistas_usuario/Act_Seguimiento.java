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
import nelandac.app.herramientacensador.modelos.VisitaDAO;

/**
 * Actividad: Act_Seguimiento
 *
 * Esta clase es la responsable de gestionar la bitácora de visitas recurrentes para clientes ya censados.
 * Permite capturar información dinámica sobre el estado comercial, observaciones de campo y evidencia
 * multimedia, manteniendo la relación de integridad con el registro maestro de la visita.
 *
 * Como Senior Developer, esta actividad implementa:
 * - Persistencia relacional mediante SQLite.
 * - Integración segura con la cámara del sistema vía FileProvider.
 * - Soporte para interfaces Edge-to-Edge y manejo de Insets.
 * - Registro de marcas de tiempo en formato local sincronizado.
 */
public class Act_Seguimiento extends AppCompatActivity {

    // Componentes de interfaz de usuario
    private TextView txtClienteReferencia;
    private ImageView imgFotoSeguimiento;
    private Button btnTomarFoto, btnGuardar;
    private Spinner spinEstadoVenta;
    private TextInputEditText edtComentarios;

    // Atributos de estado y control de datos
    private int idVisitaMaestro;
    private String nombreCliente;
    private String rutaFotoActual;
    private Uri photoUri;
    
    /** Lanzador de resultados para la captura de fotografía desde la cámara. */
    private ActivityResultLauncher<Uri> cameraLauncher;

    /**
     * Ciclo de vida inicial de la actividad.
     * Configura el entorno visual, recupera parámetros del cliente y registra los callbacks de sistema.
     *
     * @param savedInstanceState Estado persistido de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Habilitación de renderizado Edge-to-Edge para modernización de la UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_seguimiento);

        // Recuperación de la identidad del cliente maestro desde el Intent
        idVisitaMaestro = getIntent().getIntExtra("VISITA_ID", -1);
        nombreCliente = getIntent().getStringExtra("NOMBRE_CLIENTE");

        // Validación de integridad de navegación
        if (idVisitaMaestro == -1) {
            Toast.makeText(this, "Error: Cliente no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicialización de lógica y componentes
        initVistas();
        initListeners();
        configurarLauncherCamara();

        // Visualización del contexto informativo del cliente
        txtClienteReferencia.setText(String.format("%s: %s", getString(R.string.datos_cliente), nombreCliente));

        // Ajuste dinámico de los márgenes para respetar las barras de sistema (Status y Navigation Bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Vincula las variables locales con los widgets definidos en el archivo de diseño XML.
     */
    private void initVistas() {
        txtClienteReferencia = findViewById(R.id.txtClienteReferencia);
        imgFotoSeguimiento = findViewById(R.id.imgFotoSeguimiento);
        btnTomarFoto = findViewById(R.id.btnTomarFotoSeguimiento);
        btnGuardar = findViewById(R.id.btnGuardarSeguimiento);
        spinEstadoVenta = findViewById(R.id.spinEstadoVenta);
        edtComentarios = findViewById(R.id.edtComentariosSeguimiento);
    }

    /**
     * Define y asigna los disparadores de eventos para la interacción del usuario.
     */
    private void initListeners() {
        btnTomarFoto.setOnClickListener(v -> tomarFoto());
        btnGuardar.setOnClickListener(v -> guardarSeguimiento());
    }

    /**
     * Registra el contrato para obtener resultados de la aplicación de cámara.
     * En caso de éxito, actualiza la previsualización de la evidencia multimedia.
     */
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

    /**
     * Inicia el flujo de trabajo para la captura de evidencia fotográfica.
     * Realiza la validación de privilegios de hardware y genera URIs seguras vía FileProvider.
     */
    private void tomarFoto() {
        // Validación de permisos de cámara en tiempo de ejecución
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
            return;
        }

        try {
            File photoFile = crearArchivoImagen();
            // Generación de URI segura para comunicación entre procesos (Inter-process communication)
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            cameraLauncher.launch(photoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Crea un recurso físico en el almacenamiento público para la nueva captura.
     * Emplea un esquema de nomenclatura cronológico (YYYYMMDD_HHMMSS) para evitar colisiones.
     *
     * @return Referencia al archivo físico creado.
     * @throws IOException Ante fallos en el sistema de ficheros.
     */
    private File crearArchivoImagen() throws IOException {
        // Marca de tiempo en formato local explícito para trazabilidad
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "SEGUIMIENTO_" + idVisitaMaestro + "_" + timeStamp + ".jpg";
        
        // Organización jerárquica en subdirectorios de seguimientos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "HerramientaCensador/Seguimientos");
        
        if (!storageDir.exists()) storageDir.mkdirs();
        
        File image = new File(storageDir, imageFileName);
        rutaFotoActual = image.getAbsolutePath();
        return image;
    }

    /**
     * Orquesta la persistencia del evento de bitácora en la base de datos local.
     * Realiza validaciones de reglas de negocio antes de ejecutar la inserción SQL.
     */
    private void guardarSeguimiento() {
        // Validación de selección obligatoria en el selector de estado
        if (spinEstadoVenta.getSelectedItemPosition() <= 0) {
            Toast.makeText(this, getString(R.string.seleccionar_estado_venta), Toast.LENGTH_SHORT).show();
            return;
        }

        // Construcción del objeto de dominio con los datos recolectados
        Seguimiento s = new Seguimiento();
        s.setIdVisitaMaestro(idVisitaMaestro);
        s.setEstadoVenta(spinEstadoVenta.getSelectedItem().toString());
        s.setComentarios(edtComentarios.getText().toString().trim());
        s.setFotoSeguimiento(rutaFotoActual != null ? rutaFotoActual : "");

        // Delegación de la persistencia a la capa DAO
        VisitaDAO dao = new VisitaDAO(this);
        long id = dao.insertSeguimiento(s);

        if (id > 0) {
            Toast.makeText(this, "Seguimiento registrado con éxito", Toast.LENGTH_SHORT).show();
            finish(); // Cierre de pantalla y retorno al listado tras éxito
        } else {
            Toast.makeText(this, "Error al registrar seguimiento", Toast.LENGTH_SHORT).show();
        }
    }
}
