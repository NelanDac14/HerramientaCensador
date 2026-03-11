package nelandac.app.herramientacensador.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseDatos extends SQLiteOpenHelper {

    ///Atributos de la Base de Datos
    public static int dataBaseVersion = 1;
    public static String dataBaseName = "DataBaseHCensador";
    public BaseDatos(@Nullable Context context) {
        super(context, dataBaseName, null, dataBaseVersion);
    }

    public static final String CREATE_TABLE_VISITAS =
            "CREATE TABLE visitas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pais TEXT," +
                    "prospector TEXT," +
                    "tipo_cliente TEXT," +
                    "nombre_comercial TEXT," +
                    "nombre_cliente TEXT," +
                    "tipo_identificacion TEXT," +
                    "numero_identificacion TEXT," +
                    "coordenadas TEXT," +
                    "latitud REAL," +
                    "longitud REAL," +
                    "clasificacion_negocio TEXT," +
                    "telefono TEXT," +
                    "link_google_maps TEXT," +
                    "modulo TEXT," +
                    "foto_negocio TEXT," +
                    "dia_visita TEXT," +
                    "solicita_apoyo_supervisor TEXT," +
                    "fecha_coordinada TEXT," +
                    "cliente_con_venta TEXT," +
                    "cliente_nuevo TEXT," +
                    "cliente_tiene_codigo TEXT," +
                    "estado_sync INTEGER DEFAULT 0," +
                    "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_VISITAS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
