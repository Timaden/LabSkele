package com.example.labskeletest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "machine_info";
    private DBAccess dba = new DBAccess();
    private DBConfiguration dbc = new DBConfiguration();
    private Connection conn;
    SQLiteDatabase db;

    public databaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
        db = getWritableDatabase();
        this.dropData();
        onCreate(db);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE machine_info (ip TEXT, host TEXT, occupied TEXT )";
        db.execSQL(createTable);
        createTable = "CREATE TABLE available_software (Name TEXT, LabID TEXT)";
        db.execSQL(createTable);

        createTable = "CREATE TABLE classes (start_time TEXT, end_time TEXT, location TEXT, days TEXT )";
        db.execSQL(createTable);

        createTable = "CREATE TABLE labs (LabID TEXT, Total TEXT )";
        db.execSQL(createTable);

        try {
            setDB(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public Cursor getData(String table){
        String query = "SELECT * FROM  " + table;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
    public void dropData(){
        db.execSQL("DROP TABLE IF EXISTS machine_info");
        db.execSQL("DROP TABLE IF EXISTS available_software");
        db.execSQL("DROP TABLE IF EXISTS classes");
        db.execSQL("DROP TABLE IF EXISTS labs");

    }
    public void setDB (SQLiteDatabase db) throws SQLException{
        ResultSet rs = dba.getDBMachine_Info();
        while(rs.next()){
            ContentValues contentValues = new ContentValues();
            contentValues.put("ip",rs.getString("ip"));
            contentValues.put("host",rs.getString("host"));
            contentValues.put("occupied",rs.getString("occupied"));
            db.insert("machine_info", null, contentValues);
        }

        rs = dba.getDBAvailable_software();
        while(rs.next()){
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name",rs.getString("Name"));
            contentValues.put("LabID",rs.getString("LabID"));
            db.insert("available_software", null, contentValues);
        }

        rs = dba.getDBclasses();
        while(rs.next()){
            ContentValues contentValues = new ContentValues();
            contentValues.put("start_time",rs.getString("start_time"));
            contentValues.put("end_time",rs.getString("end_time"));
            contentValues.put("location",rs.getString("location"));
            contentValues.put("days",rs.getString("days"));
            db.insert("classes", null, contentValues);
        }

        rs = dba.getDBlabs();
        while(rs.next()){
            ContentValues contentValues = new ContentValues();
            contentValues.put("LabID",rs.getString("LabID"));
            contentValues.put("Total",rs.getString("Total"));
            db.insert("labs", null, contentValues);
        }
    }
    public Cursor getLabStatus(String room){
        String query =
                "Select \n" +
                        "(Select SUM (CASE WHEN occupied = 1 THEN 1 ELSE 0 END) as InUse FROM machine_info where host like '%" + room + "X0%') as InUse, \n" +
                        "(select Total from labs where LabID LIKE '%" + room + "%') as PresetTotal, \n" +
                        "(select COUNT(*) AS Total from machine_info where host LIKE '%" + room + "X0%') as Total"


                ;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
    public Cursor getSoftware(String room){
        String query = "Select * from available_software where LabID = '" + room + "'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }
    public Cursor getClassTimes(String room){
        String query = "select * from classes where location like '%" + room + "%' order by location ASC;";
        Cursor data = db.rawQuery(query,null);
        return data;
    }
    public Cursor getLabs(String building){
        String query = "select LabID from labs where LabID like '%" + building + "%';";
        Cursor data = db.rawQuery(query,null);
        return data;
    }
}
