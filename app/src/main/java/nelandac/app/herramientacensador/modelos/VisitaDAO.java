package nelandac.app.herramientacensador.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nelandac.app.herramientacensador.basedatos.BaseDatos;

/**
 * Persistencia: VisitaDAO (Data Access Object)
 * <p>
 * Clase responsable de la orquestación de operaciones CRUD y consultas especializadas sobre
 * el motor de base de datos local SQLite. Implementa la lógica de negocio para la gestión
 * de las entidades Maestro (Visita) y Detalle (Seguimiento).
 * <p>
 * Funcionalidades Senior:
 * - Inyección manual de marcas de tiempo regionales para evitar desfases UTC.
 * - Consultas optimizadas con recuperación integral de metadatos (coordenadas, fechas).
 * - Saneamiento de recursos mediante el cierre controlado de conexiones.
 */
public class VisitaDAO {

    /**
     * Ayudante para la gestión del ciclo de vida de la base de datos.
     */
    private BaseDatos dbHelper;

    /**
     * Inicializa la capa de acceso a datos.
     *
     * @param context Contexto requerido por el SQLiteOpenHelper.
     */
    public VisitaDAO(Context context) {
        dbHelper = new BaseDatos(context);
    }

    /**
     * Captura el momento exacto del registro según la configuración del dispositivo.
     *
     * @return Cadena formateada compatible con el motor SQL.
     */
    private String getLocalTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Registra un nuevo punto comercial (Censo inicial).
     *
     * @param visita Objeto poblado desde la UI.
     * @return ID único del registro insertado.
     */
    public long insertVisita(Visita visita) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("pais", visita.getPais());
        values.put("prospector", visita.getProspector());
        values.put("tipo_cliente", visita.getTipoCliente());
        values.put("nombre_comercial", visita.getNombreComercial());
        values.put("nombre_cliente", visita.getNombreCliente());
        values.put("tipo_identificacion", visita.getTipoIdentificacion());
        values.put("numero_identificacion", visita.getNumeroIdentificacion());
        values.put("coordenadas", visita.getCoordenadas());
        values.put("latitud", visita.getLatitud());
        values.put("longitud", visita.getLongitud());
        values.put("clasificacion_negocio", visita.getClasificacionNegocio());
        values.put("telefono", visita.getTelefono());
        values.put("link_google_maps", visita.getLinkGoogleMaps());
        values.put("modulo", visita.getModulo());
        values.put("foto_negocio", visita.getFotoNegocio());
        values.put("dia_visita", visita.getDiaVisita());
        values.put("solicita_apoyo_supervisor", visita.getSolicitaApoyoSupervisor());
        values.put("fecha_coordinada", visita.getFechaCoordinada());
        values.put("cliente_con_venta", visita.getClienteConVenta());
        values.put("cliente_nuevo", visita.getClienteNuevo());
        values.put("cliente_tiene_codigo", visita.getClienteTieneCodigo());
        values.put("estado_sync", visita.getEstadoSync());

        // Persistencia de hora local explícita
        values.put("fecha_registro", getLocalTimestamp());

        long resultado = db.insert("visitas", null, values);
        db.close();
        return resultado;
    }

    /**
     * Recupera el histórico total de clientes con todos sus atributos maestros.
     */
    public List<Visita> getVisitas() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas", null);

        if (cursor.moveToFirst()) {
            do {
                Visita visita = new Visita();
                visita.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                visita.setPais(cursor.getString(cursor.getColumnIndexOrThrow("pais")));
                visita.setProspector(cursor.getString(cursor.getColumnIndexOrThrow("prospector")));
                visita.setTipoCliente(cursor.getString(cursor.getColumnIndexOrThrow("tipo_cliente")));
                visita.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                visita.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                visita.setTipoIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("tipo_identificacion")));
                visita.setNumeroIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("numero_identificacion")));
                visita.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
                visita.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                visita.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                visita.setClasificacionNegocio(cursor.getString(cursor.getColumnIndexOrThrow("clasificacion_negocio")));
                visita.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                visita.setLinkGoogleMaps(cursor.getString(cursor.getColumnIndexOrThrow("link_google_maps")));
                visita.setModulo(cursor.getString(cursor.getColumnIndexOrThrow("modulo")));
                visita.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                visita.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                visita.setSolicitaApoyoSupervisor(cursor.getString(cursor.getColumnIndexOrThrow("solicita_apoyo_supervisor")));
                visita.setFechaCoordinada(cursor.getString(cursor.getColumnIndexOrThrow("fecha_coordinada")));
                visita.setClienteConVenta(cursor.getString(cursor.getColumnIndexOrThrow("cliente_con_venta")));
                visita.setClienteNuevo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_nuevo")));
                visita.setClienteTieneCodigo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_tiene_codigo")));
                visita.setEstadoSync(cursor.getInt(cursor.getColumnIndexOrThrow("estado_sync")));
                visita.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(visita);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Baja física de un registro maestro.
     */
    public int deleteVisita(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int resultado = db.delete("visitas", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado;
    }

    /**
     * Actualización atómica de los campos de un cliente existente.
     */
    public int updateVisita(Visita visita) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("pais", visita.getPais());
        values.put("prospector", visita.getProspector());
        values.put("tipo_cliente", visita.getTipoCliente());
        values.put("nombre_comercial", visita.getNombreComercial());
        values.put("nombre_cliente", visita.getNombreCliente());
        values.put("tipo_identificacion", visita.getTipoIdentificacion());
        values.put("numero_identificacion", visita.getNumeroIdentificacion());
        values.put("coordenadas", visita.getCoordenadas());
        values.put("latitud", visita.getLatitud());
        values.put("longitud", visita.getLongitud());
        values.put("clasificacion_negocio", visita.getClasificacionNegocio());
        values.put("telefono", visita.getTelefono());
        values.put("link_google_maps", visita.getLinkGoogleMaps());
        values.put("modulo", visita.getModulo());
        values.put("foto_negocio", visita.getFotoNegocio());
        values.put("dia_visita", visita.getDiaVisita());
        values.put("solicita_apoyo_supervisor", visita.getSolicitaApoyoSupervisor());
        values.put("fecha_coordinada", visita.getFechaCoordinada());
        values.put("cliente_con_venta", visita.getClienteConVenta());
        values.put("cliente_nuevo", visita.getClienteNuevo());
        values.put("cliente_tiene_codigo", visita.getClienteTieneCodigo());
        values.put("estado_sync", visita.getEstadoSync());

        int resultado = db.update("visitas", values, "id=?", new String[]{String.valueOf(visita.getId())});
        db.close();
        return resultado;
    }

    /**
     * Consulta optimizada para el listado principal.
     * Recupera el subconjunto de datos necesario para la visualización del item.
     */
    public List<Visita> obtenerVisitas() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas", null);

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                v.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Recuperación de un expediente individual por ID único.
     */
    public Visita getVisitaById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE id = ?", new String[]{String.valueOf(id)});

        Visita v = null;
        if (cursor.moveToFirst()) {
            v = new Visita();
            v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            v.setPais(cursor.getString(cursor.getColumnIndexOrThrow("pais")));
            v.setProspector(cursor.getString(cursor.getColumnIndexOrThrow("prospector")));
            v.setTipoCliente(cursor.getString(cursor.getColumnIndexOrThrow("tipo_cliente")));
            v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
            v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
            v.setTipoIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("tipo_identificacion")));
            v.setNumeroIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("numero_identificacion")));
            v.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
            v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
            v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
            v.setClasificacionNegocio(cursor.getString(cursor.getColumnIndexOrThrow("clasificacion_negocio")));
            v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
            v.setLinkGoogleMaps(cursor.getString(cursor.getColumnIndexOrThrow("link_google_maps")));
            v.setModulo(cursor.getString(cursor.getColumnIndexOrThrow("modulo")));
            v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
            v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
            v.setSolicitaApoyoSupervisor(cursor.getString(cursor.getColumnIndexOrThrow("solicita_apoyo_supervisor")));
            v.setFechaCoordinada(cursor.getString(cursor.getColumnIndexOrThrow("fecha_coordinada")));
            v.setClienteConVenta(cursor.getString(cursor.getColumnIndexOrThrow("cliente_con_venta")));
            v.setClienteNuevo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_nuevo")));
            v.setClienteTieneCodigo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_tiene_codigo")));
            v.setEstadoSync(cursor.getInt(cursor.getColumnIndexOrThrow("estado_sync")));
            v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
        }
        cursor.close();
        db.close();
        return v;
    }

    /**
     * Consulta filtrada por el día actual del calendario.
     */
    public List<Visita> obtenerHoy() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE DATE(fecha_registro) = DATE('now','localtime')", null);

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                v.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Consulta filtrada por marca de tiempo específica.
     */

    public List<Visita> obtenerPorFecha(String fecha) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE date(fecha_registro) = ?", new String[]{fecha});

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                v.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Consulta filtrada por agrupación mensual.
     */

    public List<Visita> obtenerPorMes(String mes) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE strftime('%m', fecha_registro) = ?", new String[]{mes});

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                v.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Consulta filtrada por agrupación anual.
     */

    public List<Visita> obtenerPorAnio(String anio) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE strftime('%Y', fecha_registro) = ?", new String[]{anio});

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                v.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Persiste un evento de seguimiento vinculado a un cliente.
     */
    public long insertSeguimiento(Seguimiento s) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_visita_maestro", s.getIdVisitaMaestro());
        values.put("foto_seguimiento", s.getFotoSeguimiento());
        values.put("comentarios", s.getComentarios());
        values.put("estado_venta", s.getEstadoVenta());

        // Grabación obligatoria de tiempo local
        values.put("fecha_seguimiento", getLocalTimestamp());

        long id = db.insert("bitacora", null, values);
        db.close();
        return id;
    }

    /**
     * Obtiene la bitácora cronológica de un cliente.
     *
     * @param idCliente Identificador del cliente maestro.
     */
    public List<Seguimiento> getSeguimientosPorCliente(int idCliente) {
        List<Seguimiento> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bitacora WHERE id_visita_maestro = ? ORDER BY fecha_seguimiento DESC",
                new String[]{String.valueOf(idCliente)});

        if (cursor.moveToFirst()) {
            do {
                Seguimiento s = new Seguimiento();
                s.setIdSeguimiento(cursor.getInt(cursor.getColumnIndexOrThrow("id_seguimiento")));
                s.setIdVisitaMaestro(cursor.getInt(cursor.getColumnIndexOrThrow("id_visita_maestro")));
                s.setFechaSeguimiento(cursor.getString(cursor.getColumnIndexOrThrow("fecha_seguimiento")));
                s.setFotoSeguimiento(cursor.getString(cursor.getColumnIndexOrThrow("foto_seguimiento")));
                s.setComentarios(cursor.getString(cursor.getColumnIndexOrThrow("comentarios")));
                s.setEstadoVenta(cursor.getString(cursor.getColumnIndexOrThrow("estado_venta")));
                lista.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
}
