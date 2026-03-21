package nelandac.app.herramientacensador.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Clase BaseDatos - Versión 4 (Saneamiento Integral)
 * <p>
 * Se ha identificado una deficiencia en las migraciones previas.
 * Esta versión fuerza la integridad referencial y la sincronización de zona horaria local.
 */
public class BaseDatos extends SQLiteOpenHelper {

    public static int dataBaseVersion = 4;
    public static String dataBaseName = "DataBaseHCensador";

    public BaseDatos(@Nullable Context context) {
        super(context, dataBaseName, null, dataBaseVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Vital para que el ON DELETE CASCADE funcione en Android
        db.setForeignKeyConstraintsEnabled(true);
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
                    "fecha_registro DATETIME DEFAULT (DATETIME('now', 'localtime'))" +
                    ")";

    public static final String CREATE_TABLE_BITACORA =
            "CREATE TABLE bitacora (" +
                    "id_seguimiento INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_visita_maestro INTEGER," +
                    "fecha_seguimiento DATETIME DEFAULT (DATETIME('now', 'localtime'))," +
                    "foto_seguimiento TEXT," +
                    "comentarios TEXT," +
                    "estado_venta TEXT," +
                    "FOREIGN KEY(id_visita_maestro) REFERENCES visitas(id) ON DELETE CASCADE" +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_VISITAS);
        sqLiteDatabase.execSQL(CREATE_TABLE_BITACORA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // En un entorno de desarrollo, si el esquema ha sido inestable, 
        // recreamos las tablas críticas para asegurar la nueva estructura de tiempo y cascada.
        if (oldVersion < 4) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS bitacora");
            sqLiteDatabase.execSQL(CREATE_TABLE_BITACORA);

            // Verificamos si la columna fecha_registro existe en visitas, si no, la añadimos
            try {
                sqLiteDatabase.execSQL("ALTER TABLE visitas ADD COLUMN fecha_registro DATETIME DEFAULT (DATETIME('now', 'localtime'))");
            } catch (Exception e) {
                // La columna ya existe
            }
        }
    }
}
