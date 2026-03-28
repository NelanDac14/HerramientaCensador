package nelandac.app.herramientacensador.vistas_usuario;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;
import nelandac.app.herramientacensador.modelos.VisitasAdapter;

/**
 * Actividad: ListaVisitasActivity
 * 
 * Gestiona el panel principal de consulta de la cartera de clientes.
 * Implementa capacidades avanzadas de filtrado temporal (Chips) y búsqueda dinámica
 * por texto, integrando además la exportación de reportes a Excel.
 * 
 * Características Senior:
 * - Arquitectura de "Doble Lista" para optimización de búsquedas en memoria.
 * - Soporte Edge-to-Edge para adaptabilidad en dispositivos modernos.
 * - Orquestación de navegación hacia formularios de edición y seguimiento.
 */
public class ListaVisitasActivity extends AppCompatActivity {

    /** Componente principal de visualización masiva de registros. */
    private RecyclerView recyclerVisitas;
    
    /** Adaptador para la gestión de la colección en el RecyclerView. */
    private VisitasAdapter adapter;
    
    /** Fuente de datos completa proveniente de la persistencia. */
    private List<Visita> listaFull; 
    
    /** Subconjunto de datos resultante de filtros y búsquedas activas. */
    private List<Visita> listaFiltrada; 
    
    /** Controles de filtrado temporal. */
    private Chip chipHoy, chipFecha, chipMes, chipAnio, chipTodo;
    
    /** Campo de entrada para búsqueda dinámica. */
    private TextInputEditText edtBuscar;

    /** Lanzadores de resultados para sincronización de cambios desde otras pantallas. */
    private ActivityResultLauncher<Intent> launcherEditar;
    private ActivityResultLauncher<Intent> launcherSeguimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Habilitación de renderizado Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_visitas);

        // Inicialización de componentes y lógica de negocio
        initLaunchers();
        initVistas();
        setupInsets();
        cargarDatosIniciales();
        setupBusqueda();
    }

    /**
     * Registra los callbacks para procesar el retorno desde pantallas de edición o bitácora.
     */
    private void initLaunchers() {
        launcherEditar = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        cargarDatosIniciales(); 
                    }
                }
        );

        launcherSeguimiento = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> cargarDatosIniciales()
        );
    }

    /**
     * Vincula los widgets del layout XML y configura los disparadores de filtrado.
     */
    private void initVistas() {
        recyclerVisitas = findViewById(R.id.recyclerVisitas);
        chipHoy = findViewById(R.id.chipHoy);
        chipFecha = findViewById(R.id.chipFecha);
        chipMes = findViewById(R.id.chipMes);
        chipAnio = findViewById(R.id.chipAnio);
        chipTodo = findViewById(R.id.chipTodo);
        edtBuscar = findViewById(R.id.edtBuscar);
        
        FloatingActionButton fabExportar = findViewById(R.id.fabExportarExcel);
        FloatingActionButton fabMapa = findViewById(R.id.fabMapa);

        recyclerVisitas.setLayoutManager(new LinearLayoutManager(this));

        VisitaDAO dao = new VisitaDAO(this);

        // Lógica de filtrado con persistencia de búsqueda actual
        chipHoy.setOnClickListener(v -> {
            listaFull = dao.obtenerHoy();
            filtrarPorNombre(edtBuscar.getText().toString());
        });

        chipTodo.setOnClickListener(v -> {
            listaFull = dao.obtenerVisitas();
            filtrarPorNombre(edtBuscar.getText().toString());
        });

        chipFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String fecha = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
                listaFull = dao.obtenerPorFecha(fecha);
                filtrarPorNombre(edtBuscar.getText().toString());
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        chipMes.setOnClickListener(v -> {
            String mes = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
            listaFull = dao.obtenerPorMes(mes);
            filtrarPorNombre(edtBuscar.getText().toString());
        });

        chipAnio.setOnClickListener(v -> {
            String anio = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            listaFull = dao.obtenerPorAnio(anio);
            filtrarPorNombre(edtBuscar.getText().toString());
        });

        fabExportar.setOnClickListener(v -> exportarExcel());
        fabMapa.setOnClickListener(v -> startActivity(new Intent(this, MapaVisitasActivity.class)));
    }

    /**
     * Implementa la lógica de búsqueda dinámica (Incremental Search).
     * Reacciona a cada evento de teclado para actualizar la lista en tiempo real.
     */
    private void setupBusqueda() {
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPorNombre(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Procesa la lista maestra para generar el subconjunto filtrado por coincidencia textual.
     * @param texto Cadena de búsqueda.
     */
    private void filtrarPorNombre(String texto) {
        listaFiltrada.clear();
        String query = texto.toLowerCase().trim();

        if (query.isEmpty()) {
            listaFiltrada.addAll(listaFull);
        } else {
            for (Visita v : listaFull) {
                // Búsqueda en múltiples campos comerciales
                if (v.getNombreComercial().toLowerCase().contains(query) || 
                    v.getNombreCliente().toLowerCase().contains(query)) {
                    listaFiltrada.add(v);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /** Ajusta los paddings de la actividad para respetar las barras de navegación y estado. */
    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** Carga inicial del histórico completo y configuración del adaptador. */
    private void cargarDatosIniciales() {
        VisitaDAO dao = new VisitaDAO(this);
        listaFull = dao.obtenerHoy();
        listaFiltrada = new ArrayList<>(listaFull);
        
        adapter = new VisitasAdapter(this, listaFiltrada, new VisitasAdapter.OnItemActionListener() {
            @Override
            public void onEditar(Visita visita, int position) {
                Intent intent = new Intent(ListaVisitasActivity.this, Act_NuevaVisita.class);
                intent.putExtra("VISITA_ID", visita.getId());
                intent.putExtra("POSITION", position);
                launcherEditar.launch(intent);
            }

            @Override
            public void onSeguimiento(Visita visita) {
                Intent intent = new Intent(ListaVisitasActivity.this, Act_Seguimiento.class);
                intent.putExtra("VISITA_ID", visita.getId());
                intent.putExtra("NOMBRE_CLIENTE", visita.getNombreComercial());
                launcherSeguimiento.launch(intent);
            }
        });
        recyclerVisitas.setAdapter(adapter);
    }

    /** Genera y exporta el reporte Excel respetando los filtros de visualización activos. */
    private void exportarExcel() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Visitas");
            //String[] headers = {"ID", "Pais", "Prospector", "Tipo Cliente", "Nombre Comercial", "Nombre Cliente", "Teléfono", "Día Visita", "Fecha Registro"};
            String[] headers = {"ID", "Nombre Comercial", "Nombre Cliente", "Tipo Cliente", "Identificación", "Dirección Coordenadas", "Clas. Negocio",
                    "Teléfono", "Link Google Maps", "Día Visita", "¿Venta?", "¿Alta?", "¿Código?", "Fecha Registro"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) headerRow.createCell(i).setCellValue(headers[i]);

            int rowNum = 1;
            for (Visita v : listaFiltrada) { 
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getNombreComercial());
                row.createCell(2).setCellValue(v.getNombreCliente());
                row.createCell(3).setCellValue(v.getTipoCliente());
                row.createCell(4).setCellValue(v.getNumeroIdentificacion());
                row.createCell(5).setCellValue(v.getCoordenadas());
                row.createCell(6).setCellValue(v.getClasificacionNegocio());
                row.createCell(7).setCellValue(v.getTelefono());
                row.createCell(8).setCellValue(v.getLinkGoogleMaps());
                row.createCell(9).setCellValue(v.getDiaVisita());
                row.createCell(10).setCellValue(v.getClienteConVenta());
                row.createCell(11).setCellValue(v.getClienteNuevo());
                row.createCell(12).setCellValue(v.getClienteTieneCodigo());
                row.createCell(13).setCellValue(v.getFechaRegistroSoloFecha());
            }

            File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "HerramientaCensador");
            if (!carpeta.exists()) carpeta.mkdirs();
            File archivo = new File(carpeta, "visitas_reporte.xlsx");
            FileOutputStream fos = new FileOutputStream(archivo);
            workbook.write(fos);
            fos.close();
            workbook.close();
            compartirExcel(archivo);
        } catch (Exception e) {
            Toast.makeText(this, "Error al exportar", Toast.LENGTH_SHORT).show();
        }
    }

    /** Lanza el selector global para compartir el reporte generado. */
    private void compartirExcel(File archivo) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", archivo);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir Reporte"));
    }
}
