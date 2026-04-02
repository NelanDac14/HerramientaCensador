package nelandac.app.herramientacensador.vistas_usuario;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.material.textfield.TextInputEditText;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;
import nelandac.app.herramientacensador.modelos.VisitasAdapter;
import nelandac.app.herramientacensador.sync.SyncManager;

public class ListaVisitasActivity extends AppCompatActivity {

    private RecyclerView recyclerVisitas;
    private VisitasAdapter adapter;
    private List<Visita> listaFull;
    private List<Visita> listaFiltrada;

    private Chip chipHoy, chipFecha, chipMes, chipAnio, chipTodo, chipAltas;
    private TextInputEditText edtBuscar;
    Toolbar toolbar;

    private ActivityResultLauncher<Intent> launcherEditar;
    private ActivityResultLauncher<Intent> launcherSeguimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_visitas);

        initLaunchers();
        initVistas();
        setupInsets();
        cargarDatosIniciales();
        setupBusqueda();
    }

    private void initLaunchers() {
        launcherEditar = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        cargarDatosIniciales();
                    }
                }
        );

        launcherSeguimiento = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> cargarDatosIniciales()
        );
    }

    private void initVistas() {
        recyclerVisitas = findViewById(R.id.recyclerVisitas);
        chipHoy = findViewById(R.id.chipHoy);
        chipFecha = findViewById(R.id.chipFecha);
        chipMes = findViewById(R.id.chipMes);
        chipAnio = findViewById(R.id.chipAnio);
        chipTodo = findViewById(R.id.chipTodo);
        chipAltas = findViewById(R.id.chipAltas);
        edtBuscar = findViewById(R.id.edtBuscar);
        toolbar = findViewById(R.id.listaVisitas_toolbar);
        setSupportActionBar(toolbar);

        recyclerVisitas.setLayoutManager(new LinearLayoutManager(this));

        VisitaDAO dao = new VisitaDAO(this);

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
                String fecha = year + "-" +
                        String.format("%02d", month + 1) + "-" +
                        String.format("%02d", day);

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

        chipAltas.setOnClickListener(v -> {
            listaFull = dao.obtenerAltas();
            filtrarPorNombre(edtBuscar.getText().toString());
        });
    }

    // 🔥 MENÚ
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_visitas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_sync_subir) {
            new SyncManager(this).sincronizar();
            Toast.makeText(this, "Subiendo datos...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.menu_sync_descargar) {
            new SyncManager(this).descargarDesdeFirebase();
            Toast.makeText(this, "Descargando datos...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.menu_mapa) {
            startActivity(new Intent(this, MapaVisitasActivity.class));
            return true;
        }

        if (id == R.id.menu_exportar) {
            exportarExcel();
            return true;
        }

        if (id == R.id.menu_reset_sync) {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Sincronización completa")
                    .setMessage("Esto volverá a subir TODOS los datos a la nube. ¿Deseas continuar?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        VisitaDAO dao = new VisitaDAO(this);
                        dao.resetearSync();

                        new SyncManager(this).sincronizar();

                        Toast.makeText(this, "Sincronización completa iniciada", Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupBusqueda() {
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPorNombre(s.toString());
            }
        });
    }

    private void filtrarPorNombre(String texto) {
        listaFiltrada.clear();

        String query = texto.toLowerCase().trim();

        if (query.isEmpty()) {
            listaFiltrada.addAll(listaFull);
        } else {
            for (Visita v : listaFull) {
                if (v.getNombreComercial().toLowerCase().contains(query) ||
                        v.getNombreCliente().toLowerCase().contains(query)) {
                    listaFiltrada.add(v);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void cargarDatosIniciales() {
        VisitaDAO dao = new VisitaDAO(this);

        listaFull = dao.obtenerHoy();
        listaFiltrada = new ArrayList<>(listaFull);

        adapter = new VisitasAdapter(this, listaFiltrada, new VisitasAdapter.OnItemActionListener() {
            @Override
            public void onEditar(Visita visita, int position) {
                Intent intent = new Intent(ListaVisitasActivity.this, Act_NuevaVisita.class);
                intent.putExtra("VISITA_ID", visita.getId());
                launcherEditar.launch(intent);
            }

            @Override
            public void onSeguimiento(Visita visita) {
                Intent intent = new Intent(ListaVisitasActivity.this, Act_Seguimiento.class);
                intent.putExtra("VISITA_ID", visita.getId());
                launcherSeguimiento.launch(intent);
            }
        });

        recyclerVisitas.setAdapter(adapter);
    }

    private void exportarExcel() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Visitas");

            String[] headers = {"ID", "Nombre Comercial", "Nombre Cliente", "Tipo Cliente",
                    "Identificación", "Dirección", "Clasificación",
                    "Teléfono", "Maps", "Día", "Venta", "Nuevo", "Código", "Fecha"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

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

            File archivo = new File(getExternalFilesDir(null), "visitas.xlsx");
            FileOutputStream fos = new FileOutputStream(archivo);
            workbook.write(fos);
            fos.close();
            workbook.close();

            compartirExcel(archivo);

        } catch (Exception e) {
            Toast.makeText(this, "Error al exportar", Toast.LENGTH_SHORT).show();
        }
    }

    private void compartirExcel(File archivo) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", archivo);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir"));
    }
}