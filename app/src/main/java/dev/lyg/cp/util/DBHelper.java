package dev.lyg.cp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    // 数据库名称
    private static final String DATABASE_NAME = "TicketManagement.db";
    private static final int DATABASE_VERSION = 1;

    // 创建车票表
    private static final String TABLE_CREATE =
            "CREATE TABLE tickets (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "train_number TEXT, " +
                    "departure_date TEXT, " +  // 修正字段名
                    "departure_time TEXT, " +
                    "arrival_time TEXT, " +
                    "departure_station TEXT, " +
                    "arrival_station TEXT, " +
                    "check_in_gate TEXT, " +
                    "seat_number TEXT, " +
                    "remark1 TEXT, " +
                    "remark2 TEXT, " +
                    "remark3 TEXT, " +
                    "remark4 TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tickets");
        onCreate(db);
    }

    public long insertTicket(String trainNumber, String departureDate, String departureTime,
                             String arrivalTime, String departureStation, String arrivalStation,
                             String checkInGate, String seatNumber, String remark1, String remark2,
                             String remark3, String remark4) {
        SQLiteDatabase db = null;
        long result = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("train_number", trainNumber);
            values.put("departure_date", departureDate);
            values.put("departure_time", departureTime);
            values.put("arrival_time", arrivalTime);
            values.put("departure_station", departureStation);
            values.put("arrival_station", arrivalStation);
            values.put("check_in_gate", checkInGate);
            values.put("seat_number", seatNumber);
            values.put("remark1", remark1);
            values.put("remark2", remark2);
            values.put("remark3", remark3);
            values.put("remark4", remark4);

            result = db.insert("tickets", null, values);
        } catch (Exception e) {
            Log.e("DBHelper", "Error inserting ticket", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return result;
    }

    public Cursor showData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tickets", null);
        if (cursor != null) {
            Log.d("DBHelper", "获取的行数: " + cursor.getCount());
        }

        return db.rawQuery("SELECT * FROM tickets", null);
    }
}
