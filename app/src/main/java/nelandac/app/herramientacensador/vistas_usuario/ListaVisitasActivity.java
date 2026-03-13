package nelandac.app.herramientacensador.vistas_usuario;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;
import nelandac.app.herramientacensador.modelos.VisitasAdapter;

import nelandac.app.herramientacensador.R;

public class ListaVisitasActivity extends AppCompatActivity {

    RecyclerView recyclerVisitas;
    VisitasAdapter adapter;
    List<Visita> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_visitas);

        recyclerVisitas = findViewById(R.id.recyclerVisitas);

        recyclerVisitas.setLayoutManager(new LinearLayoutManager(this));

        VisitaDAO dao = new VisitaDAO(this);

        lista = dao.obtenerVisitas();

        adapter = new VisitasAdapter(this, lista);

        recyclerVisitas.setAdapter(adapter);
    }
}
