package com.masudinn.crudsqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


public class SqliteHelper extends SQLiteOpenHelper {

    //constructor
    SqliteHelper(Context context,
                 String name,
                 SQLiteDatabase.CursorFactory factory,
                 int version){
        super(context,name,factory,version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insert data
    public void insertData(String nama,String nim,String alamat,byte[] images){
        SQLiteDatabase database = getWritableDatabase();
        //query insert data
        //RECORD is table
        String sql = "INSERT INTO RECORD VALUES(NULL,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1,nama);
        statement.bindString(2,nim);
        statement.bindString(3,alamat);
        statement.bindBlob(4,images);
        statement.executeInsert();
    }
        //update data
    public void updateData(String nama ,String nim ,String alamat,byte[] images,int id){
        SQLiteDatabase database = getWritableDatabase();
        //query update data
        String query = "UPDATE RECORD SET nama=? , nim=? , kelas=? , image=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.bindString(1,nama);
        statement.bindString(2,nim);
        statement.bindString(3,alamat);
        statement.bindBlob(4,images);
        statement.bindDouble(5,(double)id);
        statement.executeUpdateDelete();
        database.close();
    }

        //delete data
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        //query delete data
        String query = "DELETE FROM RECORD WHERE id=?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindDouble(1,(double)id);
        statement.execute();
        statement.close();
    }
    public Cursor getData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
