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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);


        /// Solicitamos los permisos correspondientes
        //Permisos de gestión de ubicación
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        /// Activamos el ToolBar para visualización de herramientas y menú
        Toolbar toolbar = findViewById(R.id.am_toolbar);
        setSupportActionBar(toolbar);

        /// Mostramos la versión de la App al Usuario
        TextView tvVersionApp;
        tvVersionApp = findViewById(R.id.tv_versionInformacion);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName; //Aquí se obtiene la vesión de la Ap
            tvVersionApp.setText(String.format("%s%s", getString(R.string.tituloVersion), version));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }//Fin del OnCreate

    /// Métodos importantes
    //Nos traslada a la vista de nueva visita para registrarla
    void ir_nueva_visita() {
        Intent inteNuevaVisita = new Intent(this, Act_NuevaVisita.class);
        startActivity(inteNuevaVisita);
    }

    void ir_ver_visitas() {
        Intent inteVerVisita = new Intent(this, ListaVisitasActivity.class);
        startActivity(inteVerVisita);
    }

    /// Metódos para diferentes componentes ya prediseñados por AndroidStudios
    //Metodo para mostrar el menú overflow de activity main
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.am_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_nueva_visita) {
            // Acción para el ítem "item_nuevo_viaje"
            ir_nueva_visita();
            return true;
        }
        if (id == R.id.item_ver_visitas) {
            // Acción para el ítem "item_ver_visitas"
            ir_ver_visitas();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}