package nelandac.app.herramientacensador.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nelandac.app.herramientacensador.basedatos.BaseDatos;

/**
 * Clase VisitaDAO (Data Access Object)
 * 
 * Esta clase proporciona la interfaz necesaria para realizar operaciones CRUD (Create, Read, Update, Delete)
 * sobre la tabla "visitas" en la base de datos local SQLite. 
 * Actúa como un puente entre la lógica de negocio de la aplicación y el motor de persistencia,
 * asegurando la correcta manipulación de los objetos de tipo Visita.
 */
public class VisitaDAO {

    // Instancia del ayudante de base de datos para gestionar la conexión
    private BaseDatos dbHelper;

    /**
     * Constructor de la clase VisitaDAO.
     * @param context Contexto de la aplicación necesario para inicializar el dbHelper.
     */
    public VisitaDAO(Context context) {
        dbHelper = new BaseDatos(context);
    }

    /**
     * Inserta un nuevo registro de visita en la base de datos.
     * @param visita Objeto Visita con los datos a persistir.
     * @return El ID del registro insertado, o -1 si ocurrió un error.
     */
    public long insertVisita(Visita visita) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Mapeo de los atributos del objeto Visita a las columnas de la tabla
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

        long resultado = db.insert("visitas", null, values);
        db.close();
        return resultado;
    }

    /**
     * Recupera todos los registros de la tabla visitas con todos sus campos.
     * @return Lista de objetos Visita encontrados.
     */
    public List<Visita> getVisitas() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas", null);

        // Iteración sobre el cursor para reconstruir los objetos Visita
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
     * Elimina un registro de la tabla visitas por su identificador.
     * @param id Identificador único del registro a eliminar.
     * @return El número de filas afectadas.
     */
    public int deleteVisita(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int resultado = db.delete("visitas", "id=?", new String[]{String.valueOf(id)});
        Log.d("SQL_DELETE", "Filas borradas: " + resultado);
        db.close();
        return resultado;
    }

    /**
     * Actualiza la información de un registro existente en la base de datos.
     * @param visita Objeto Visita con la información actualizada y su ID correspondiente.
     * @return El número de filas afectadas por la actualización.
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

        int resultado = db.update(
                "visitas",
                values,
                "id=?",
                new String[]{String.valueOf(visita.getId())}
        );
        db.close();
        return resultado;
    }

    /**
     * Recupera una versión resumida de todas las visitas para ser mostrada en listas generales.
     * @return Lista de objetos Visita poblados parcialmente.
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
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Obtiene un registro único de visita a partir de su ID.
     * @param id Identificador único del registro buscado.
     * @return Objeto Visita poblado con la información de la base de datos, o null si no se encuentra.
     */
    public Visita getVisitaById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM visitas WHERE id = ?",
                new String[]{String.valueOf(id)}
        );

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
        }
        cursor.close();
        db.close();
        return v;
    }

    /**
     * Recupera los registros creados durante el día actual.
     * @return Lista de objetos Visita creados hoy.
     */
    public List<Visita> obtenerHoy() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM visitas WHERE DATE(fecha_registro) = DATE('now','localtime')",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
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
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Filtra y recupera visitas basándose en una fecha específica.
     * @param fecha Cadena de texto con la fecha en formato compatible con SQLite.
     * @return Lista de objetos Visita que coinciden con la fecha proporcionada.
     */
    public List<Visita> obtenerPorFecha(String fecha) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM visitas WHERE date(fecha_registro) = ?",
                new String[]{fecha}
        );

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Recupera todos los registros pertenecientes a un mes específico.
     * @param mes Representación del mes (ej. "01" para enero).
     * @return Lista de visitas del mes solicitado.
     */
    public List<Visita> obtenerPorMes(String mes) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM visitas WHERE strftime('%m', fecha_registro) = ?",
                new String[]{mes}
        );

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
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
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Recupera todos los registros pertenecientes a un año específico.
     * @param anio Representación del año (ej. "2024").
     * @return Lista de visitas del año solicitado.
     */
    public List<Visita> obtenerPorAnio(String anio) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM visitas WHERE strftime('%Y', fecha_registro) = ?",
                new String[]{anio}
        );

        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita();
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
                v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                lista.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

}