package nelandac.app.herramientacensador.vistas_usuario;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.*;

import java.util.Date;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Calendar;

import java.text.SimpleDateFormat;

import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;
import nelandac.app.herramientacensador.modelos.VisitasAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import nelandac.app.herramientacensador.R;

public class ListaVisitasActivity extends AppCompatActivity {

    RecyclerView recyclerVisitas;
    VisitasAdapter adapter;
    List<Visita> lista;
    Chip chipHoy, chipFecha, chipMes, chipAnio, chipTodo;
    ActivityResultLauncher<Intent> launcherEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_visitas);

        launcherEditar = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        int position = result.getData().getIntExtra("POSITION", -1);
                        int id = result.getData().getIntExtra("VISITA_ID", -1);

                        if (position != -1) {

                            VisitaDAO dao = new VisitaDAO(this);
                            Visita visitaActualizada = dao.getVisitaById(id);

                            lista.set(position, visitaActualizada);
                            adapter.notifyItemChanged(position);
                        }
                    }
                }
        );

        recyclerVisitas = findViewById(R.id.recyclerVisitas);
        chipHoy = findViewById(R.id.chipHoy);
        chipFecha = findViewById(R.id.chipFecha);
        chipMes = findViewById(R.id.chipMes);
        chipAnio = findViewById(R.id.chipAnio);
        chipTodo = findViewById(R.id.chipTodo);
        FloatingActionButton fabExportar = findViewById(R.id.fabExportarExcel);

        recyclerVisitas.setLayoutManager(new LinearLayoutManager(this));

        VisitaDAO dao = new VisitaDAO(this);

        lista = dao.obtenerVisitas();

        //Funcionalidad del botón que cuando se presiona obtiene todas las visitas de hoy
        chipHoy.setOnClickListener(v -> {

            lista.clear();
            lista.addAll(dao.obtenerHoy());

            adapter.notifyDataSetChanged();

        });

        //Funcionalidad del botón que cuando se presiona se obtiene todas las visitas generales
        chipTodo.setOnClickListener(v -> {

            lista.clear();
            lista.addAll(dao.obtenerVisitas());

            adapter.notifyDataSetChanged();
        });

        //Funcionalidad que al presionar botón nos muestra un DatePicker y luego nos filtra
        //las visitas por fecha seleccionada
        chipFecha.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {

                        String fecha = year + "-" +
                                String.format("%02d", month + 1) + "-" +
                                String.format("%02d", day);

                        lista.clear();
                        lista.addAll(dao.obtenerPorFecha(fecha));

                        adapter.notifyDataSetChanged();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        //Funcionalidad del botón que cuando se presiona se obtiene todas las visitas del mes en curso
        chipMes.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int mesNumero = calendar.get(Calendar.MONTH) + 1;

            String mes = String.format("%02d", mesNumero);

            lista.clear();
            lista.addAll(dao.obtenerPorMes(mes));
            adapter.notifyDataSetChanged();

        });

        //Funcionalidad del botón que cuando se presiona se obtiene todas las visitas del año en curso
        chipAnio.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            String anio = String.valueOf(calendar.get(Calendar.YEAR));

            lista.clear();
            lista.addAll(dao.obtenerPorAnio(anio));
            adapter.notifyDataSetChanged();

        });

        fabExportar.setOnClickListener(v -> exportarExcel());

        adapter = new VisitasAdapter(this, lista, (visita, position) -> {

            Intent intent = new Intent(this, Act_NuevaVisita.class);
            intent.putExtra("VISITA_ID", visita.getId());
            intent.putExtra("POSITION", position);

            launcherEditar.launch(intent);
        });

        recyclerVisitas.setAdapter(adapter);
    }

    private void exportarExcel() {

        try {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Visitas");

            String[] headers = {
                    "ID", "Pais", "Prospector", "Tipo Cliente", "Nombre Comercial",
                    "Nombre Cliente", "Tipo Identificación", "Número Identificación",
                    "Coordenadas", "Latitud", "Longitud", "Clasificación Negocio",
                    "Teléfono", "Link Google Maps", "Modulo",
                    "Día Visita", "Solicita Apoyo Supervisor", "Fecha Coordinada",
                    "Cliente con Venta", "Cliente Nuevo", "Cliente con Código", "Fecha Registro"
            };

            Row header = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;

            for (Visita v : lista) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getPais());
                row.createCell(2).setCellValue(v.getProspector());
                row.createCell(3).setCellValue(v.getTipoCliente());
                row.createCell(4).setCellValue(v.getNombreComercial());
                row.createCell(5).setCellValue(v.getNombreCliente());
                row.createCell(6).setCellValue(v.getTipoIdentificacion());
                row.createCell(7).setCellValue(v.getNumeroIdentificacion());
                row.createCell(8).setCellValue(v.getCoordenadas());
                row.createCell(9).setCellValue(v.getLatitud());
                row.createCell(10).setCellValue(v.getLongitud());
                row.createCell(11).setCellValue(v.getClasificacionNegocio());
                row.createCell(12).setCellValue(v.getTelefono());
                row.createCell(13).setCellValue(v.getLinkGoogleMaps());
                row.createCell(14).setCellValue(v.getModulo());
                row.createCell(15).setCellValue(v.getDiaVisita());
                row.createCell(16).setCellValue(v.getSolicitaApoyoSupervisor());
                row.createCell(17).setCellValue(v.getFechaCoordinada());
                row.createCell(18).setCellValue(v.getClienteConVenta());
                row.createCell(19).setCellValue(v.getClienteNuevo());
                row.createCell(20).setCellValue(v.getClienteTieneCodigo());
                row.createCell(21).setCellValue(v.getFechaRegistro());
            }

            // Congelar encabezado
            sheet.createFreezePane(0, 1);

            // Nombre del archivo
            String fecha = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm",
                    Locale.getDefault()
            ).format(new Date());

            String nombreArchivo = "visitas_" + fecha + ".xlsx";

            File carpeta = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS),
                    "HerramientaCensador"
            );

            if (!carpeta.exists()) carpeta.mkdirs();

            File archivo = new File(carpeta, nombreArchivo);

            FileOutputStream fos = new FileOutputStream(archivo);

            workbook.write(fos);

            fos.close();
            workbook.close();

            Toast.makeText(this,
                    "Excel guardado en Documents/HerramientaCensador",
                    Toast.LENGTH_LONG).show();

            compartirExcel(archivo);

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(this,
                    "Error exportando Excel",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void compartirExcel(File archivo) {

        Uri uri = FileProvider.getUriForFile(
                this,
                "nelandac.app.herramientacensador.provider",
                archivo
        );

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Compartir Excel"));
    }
}
