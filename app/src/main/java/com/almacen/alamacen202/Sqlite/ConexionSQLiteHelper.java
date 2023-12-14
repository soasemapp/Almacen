package com.almacen.alamacen202.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    final String CREAR_TABLA_INVENTARIOALM = "CREATE TABLE INVENTARIOALM (" +
            "PRODUCTO VARCHAR (45)," +
            "CANTIDAD INTEGER (11)," +
            "ESCAN INTEGER (11)," +//lo que se esta escaneando
            "PRIMARY KEY(PRODUCTO))";

    final String CREAR_TABLA_INVENTARIO = "CREATE TABLE INVENTARIO (" +
            "EMPRESA VARCHAR (15)," +
            "PRODUCTO VARCHAR (45)," +
            "CANTIDAD INTEGER (11)," +
            "SURTIDO INTEGER (11),"+
            "UBICACION VARCHAR(45),PRIMARY KEY(EMPRESA,PRODUCTO))";

    final String CREAR_TABLA_DIFUBIEXIST = "CREATE TABLE DIFUBIEXIST (" +
            "EMPRESA VARCHAR (15)," +
            "PRODUCTO VARCHAR (45)," +
            "CANTIDAD INTEGER (11)," +
            "EXISTENCIA INTEGER (11),"+
            "DIFERENCIA INTEGER (11),"+
            "UBICACION VARCHAR(45),"+
            "CONTEO INTEGER (11),"+
            "ESTATUS INTEGER,"+//CONTADOS 1 NO CONTADOS 0
            "PRIMARY KEY(EMPRESA,PRODUCTO))";

    final String CREAR_TABLA_RECEPCONT = "CREATE TABLE RECEPCONT(" +
            "FOLIO VARCHAR (15)," +
            "PRODUCTO VARCHAR (15)," +
            "NAMEPALET VARCHAR(50)," +
            "CANTIDAD INTEGER (11)," +
            "PRIORIDAD VARCHAR (2),"+
            "PRIMARY KEY(FOLIO,PRODUCTO,NAMEPALET))";



    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }//constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_INVENTARIOALM);
        db.execSQL(CREAR_TABLA_INVENTARIO);
        db.execSQL(CREAR_TABLA_DIFUBIEXIST);
        db.execSQL(CREAR_TABLA_RECEPCONT);

    }//onCreate

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS CREAR_TABLA_INVENTARIOALM");
        db.execSQL("DROP TABLE IF EXISTS CREAR_TABLA_INVENTARIO");
        db.execSQL("DROP TABLE IF EXISTS CREAR_TABLA_DIFUBIEXIST");
        db.execSQL("DROP TABLE IF EXISTS CREAR_TABLA_RECEPCONT");
        onCreate(db);
    }//onUpgrade
}
