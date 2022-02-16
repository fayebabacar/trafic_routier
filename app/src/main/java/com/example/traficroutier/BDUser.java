package com.example.traficroutier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BDUser extends SQLiteOpenHelper {
    public static final String Bd_name = "maBD.db";
    public static final String table = "usager";
    public static final String column1="user";

    public String request = "create table usager (imei TEXT);";


    public BDUser(Context context) {
        super(context,Bd_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(request);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+table);
        onCreate(db);

    }

    //allow to insert data inside the database
    public void insertData(String imei){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nom",imei);

        db.insert(table, null, contentValues);
    }

    //allow to read data from Database

    public Cursor afficher(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM   "+table,null);
        return res;
    }
}
