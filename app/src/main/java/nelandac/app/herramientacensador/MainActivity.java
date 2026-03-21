package nelandac.app.herramientacensador;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import nelandac.app.herramientacensador.vistas_usuario.Act_NuevaVisita;
import nelandac.app.herramientacensador.vistas_usuario.ListaVisitasActivity;

/**
 * Clase MainActivity
 *
 * Actúa como el punto de entrada principal y panel de control de la aplicación.
 * Sus responsabilidades incluyen la gestión inicial de permisos críticos (ubicación),
 * la visualización de metadatos de la aplicación (versión) y el enrutamiento hacia
 * los diferentes módulos funcionales mediante un menú de opciones.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Ciclo de vida: onCreate
     * Configura el entorno visual inicial, solicita privilegios de sistema y
     * recupera información del paquete para la interfaz de usuario.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitación de renderizado Edge-to-Edge para modernización de la UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        /**
         * Solicitud de permisos en tiempo de ejecución.
         * Se requiere el permiso ACCESS_FINE_LOCATION para las funcionalidades de geolocalización
         * presentes en los módulos de registro y visualización de mapas.
         */
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Configuración del Toolbar principal como ActionBar del sistema
        Toolbar toolbar = findViewById(R.id.am_toolbar);
        setSupportActionBar(toolbar);

        /**
         * Extracción de metadatos del paquete.
         * Recupera la versión actual de la aplicación definida en el build.gradle para mostrarla al usuario.
         */
        TextView tvVersionApp = findViewById(R.id.tv_versionInformacion);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersionApp.setText(String.format("%s%s", getString(R.string.tituloVersion), version));
        } catch (PackageManager.NameNotFoundException e) {
            // Manejo de excepción en caso de que el paquete no sea localizable por el sistema
            throw new RuntimeException(e);
        }

        // Ajuste dinámico de los paddings de la vista raíz para respetar las Insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Lógica de navegación: ir_nueva_visita
     * Realiza la transición hacia la pantalla de registro de nuevos formularios.
     */
    void ir_nueva_visita() {
        Intent inteNuevaVisita = new Intent(this, Act_NuevaVisita.class);
        startActivity(inteNuevaVisita);
    }

    /**
     * Lógica de navegación: ir_ver_visitas
     * Realiza la transición hacia la pantalla de listado histórico y gestión de registros.
     */
    void ir_ver_visitas() {
        Intent inteVerVisita = new Intent(this, ListaVisitasActivity.class);
        startActivity(inteVerVisita);
    }

    /**
     * Inicialización del menú de opciones (Overflow Menu).
     * Infla el recurso XML de menú para añadir acciones a la Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.am_menu, menu);
        return true;
    }

    /**
     * Controlador de eventos de selección del menú.
     * Despacha la ejecución según el identificador de la acción seleccionada.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_nueva_visita) {
            // Navegación dirigida al registro de censo
            ir_nueva_visita();
            return true;
        }
        if (id == R.id.item_ver_visitas) {
            // Navegación dirigida a la consulta histórica
            ir_ver_visitas();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
