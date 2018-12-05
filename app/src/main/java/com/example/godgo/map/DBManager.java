package com.example.godgo.map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DBManager {


    // DB관련 상수 선언
    private final String dbName;
    private final String tableName = "MissionInfo";
    public static final int dbVersion = 1;

    // DB관련 객체 선언
    private OpenHelper opener; // DB opener
    private SQLiteDatabase db; // DB controller

    // 부가적인 객체들
    private Context context;

    // 생성자
    public DBManager(Context context,String dbNameByContractAddr) {
        dbName = dbNameByContractAddr.substring(2,6) + ".db";
        this.context = context;
        this.opener = new OpenHelper(context, dbName, null, dbVersion);
        db = opener.getWritableDatabase();
    }

    // Opener of DB and Table
    private class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
            super(context, name, null, version);
            // TODO Auto-generated constructor stub
        }

        // 생성된 DB가 없을 경우에 한번만 호출됨
        @Override
        public void onCreate(SQLiteDatabase arg0) {
            // String dropSql = "drop table if exists " + tableName;
            // db.execSQL(dropSql);

            String createSql = "create table " + tableName + " ("
                    + "id integer primary key autoincrement, " + "drone text, "
                    + "missionindex integer)";
            arg0.execSQL(createSql);
            Toast.makeText(context, "DB is opened", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        }
    }

    // 데이터 추가
    public void insertData(MissinInfo info) {
        String sql = "insert into " + tableName + " values(NULL, '"
                + info.getDroneAddr() + "', " + info.getMissionIndex() + ");";
        db.execSQL(sql);
    }
    /*
    // 데이터 갱신
    public void updateData(APinfo info, int index) {
        String sql = "update " + tableName + " set SSID = '" + info.getSSID()
                + "', capabilities = " + info.getCapabilities()
                + ", passwd = '" + info.getPasswd() + "' where id = " + index
                + ";";
        db.execSQL(sql);
    }

    // 데이터 삭제
    public void removeData(int index) {
        String sql = "delete from " + tableName + " where id = " + index + ";";
        db.execSQL(sql);
    }

    // 데이터 검색
    public APinfo selectData(int index) {
        String sql = "select * from " + tableName + " where id = " + index
                + ";";
        Cursor result = db.rawQuery(sql, null);

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result.moveToFirst()) {
            APinfo info = new APinfo(result.getInt(0), result.getString(1),
                    result.getInt(2), result.getString(3));
            result.close();
            return info;
        }
        result.close();
        return null;
    }
*/

    // 데이터 전체 검색
    public ArrayList<MissinInfo> selectAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();
        ArrayList<MissinInfo> infos = new ArrayList<MissinInfo>();

        while (!results.isAfterLast()) {
            MissinInfo info = new MissinInfo(results.getString(1), results.getInt(2));
            infos.add(info);
            results.moveToNext();
        }
        results.close();
        return infos;
    }
    public ArrayList<MissinInfo> selectSpecifyAddr() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();
        ArrayList<MissinInfo> infos = new ArrayList<MissinInfo>();

        while (!results.isAfterLast()) {
            MissinInfo info = new MissinInfo(results.getString(1), results.getInt(2));
            infos.add(info);
            results.moveToNext();
        }
        results.close();
        return infos;
    }
}
