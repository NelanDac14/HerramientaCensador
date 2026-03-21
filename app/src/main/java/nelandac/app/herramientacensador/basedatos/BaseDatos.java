package nelandac.app.herramientacensador.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Clase BaseDatos
 * 
 * Esta clase hereda de SQLiteOpenHelper y es la responsable de la gestión del ciclo de vida 
 * de la base de datos local de la aplicación. Se encarga de la creación inicial del esquema, 
 * la definición de las tablas y el control de versiones para futuras migraciones de datos.
 */
public class BaseDatos extends SQLiteOpenHelper {

    /**
     * Atributos de configuración de la Base de Datos.
     * dataBaseVersion: Versión actual del esquema. Debe incrementarse al realizar cambios estructurales.
     * dataBaseName: Nombre físico del archivo de base de datos en el almacenamiento privado de la app.
     */
    public static int dataBaseVersion = 1;
    public static String dataBaseName = "DataBaseHCensador";

    /**
     * Constructor de la clase.
     * @param context Contexto de la aplicación para localizar la ruta de la base de datos.
     */
    public BaseDatos(@Nullable Context context) {
        super(context, dataBaseName, null, dataBaseVersion);
    }

    /**
     * Sentencia SQL DDL (Data Definition Language) para la creación de la tabla de visitas.
     * Define la estructura de almacenamiento, tipos de datos y restricciones de integridad.
     * Incluye campos para identificación, geolocalización, metadatos comerciales y control de auditoría.
     */
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

    /**
     * Método ejecutado por el framework de Android cuando la base de datos es creada por primera vez.
     * @param sqLiteDatabase Instancia de la base de datos sobre la cual ejecutar las sentencias DDL.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_VISITAS);
    }

    /**
     * Método ejecutado cuando se detecta un incremento en la versión de la base de datos.
     * Proporciona el punto de entrada para scripts de migración, alteración de tablas 
     * o reconstrucción del esquema sin pérdida de integridad.
     * 
     * @param sqLiteDatabase Instancia de la base de datos.
     * @param oldVersion Versión anterior del esquema.
     * @param newVersion Nueva versión del esquema.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Implementación de lógica de migración según sea requerido por el roadmap del proyecto.
    }
}
