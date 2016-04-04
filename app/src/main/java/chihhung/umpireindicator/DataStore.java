package chihhung.umpireindicator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DataStore {
    public SQLiteDatabase db = null;
    private final static String DATABASE_NAME = "data.db";
    private final static String TABLE_NAME = "umpireindicator";
    private final static String _ID = "_id";
    private final static String VIBRATE = "vibrate";
    private final static String SOUND = "sound";
    private final static String RATE = "rate";
    private final static String USEDAYS = "usedays";
    private final static String DOUBLECHECK = "doublecheck";
    /* 建立資料表的欄位 */
    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + _ID + " INTEGER PRIMARY KEY,"
            + VIBRATE + " TEXT,"
            + SOUND + " TEXT,"
            + RATE + " TEXT,"
            + USEDAYS + " TEXT,"
            + DOUBLECHECK + " TEXT)";

    private Context mCtx = null;

    public DataStore(Context ctx) { // 建構式
        this.mCtx = ctx;
    }

    public void open() throws SQLException {
        db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        try {
            db.execSQL(CREATE_TABLE);
        } catch (Exception e) {
        }
    }

    public void close() {
        db.close();
    }

    public String getData(String item) {
        int pos=0;
        switch (item) {
            case "vibrate" :
                pos = 1;
                break;
            case "sound" :
                pos = 2;
                break;
            case "rate" :
                pos = 3;
                break;
            case "usedays":
                pos = 4;
                break;
            case "doublecheck":
                pos = 5;
                break;
        }
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();
        return c.getString(pos);
    }

    public void addData() { // 新增資料
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ContentValues args = new ContentValues();
        c.moveToFirst();
        if(c.getCount()<=0) {
            args.put(VIBRATE, "true");
            args.put(SOUND, "true");
            args.put(RATE, "no");
            args.put(USEDAYS, "0");
            args.put(DOUBLECHECK, "true");
            db.insert(TABLE_NAME, null, args);
        } else {
            if(c.isNull(1)) {
                args.put(VIBRATE, "true");
                db.update(TABLE_NAME, args, _ID + "=" + 1, null);
                //Log.d("data", "VibisNull");
            }
            if(c.isNull(2)) {
                args.put(SOUND, "true");
                db.update(TABLE_NAME, args, _ID + "=" + 1, null);
                //Log.d("data", "SouisNull");
            }
            if(c.isNull(3)) {
                args.put(RATE, "no");
                db.update(TABLE_NAME, args, _ID + "=" + 1, null);
                //Log.d("data", "RatisNull");
            }
            if(c.isNull(4)) {
                args.put(USEDAYS, "0");
                db.update(TABLE_NAME, args, _ID + "=" + 1, null);
                //Log.d("data", "UseisNull");
            }
            if(c.isNull(5)) {
                args.put(DOUBLECHECK, "true");
                db.update(TABLE_NAME, args, _ID + "=" + 1, null);
                //Log.d("data", "DouisNull");
            }
        }
    }

    public Cursor get(long rowId) throws SQLiteException {
        Cursor mCursor = db.query(TABLE_NAME,new String[] { _ID, VIBRATE}, _ID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateOption(long rowId, String vibrate, String doublecheck) {
        ContentValues args = new ContentValues();
        args.put(VIBRATE, vibrate);
        args.put(DOUBLECHECK, doublecheck);
        return db.update(TABLE_NAME, args, _ID + "=" + rowId, null) > 0;
    }

    public boolean update(long rowId, String rate) {
        ContentValues args = new ContentValues();
        args.put(RATE, rate);
        return db.update(TABLE_NAME, args, _ID + "=" + rowId, null) > 0;
    }

    public boolean updateUseDays(long rowId, String usedays) {
        ContentValues args = new ContentValues();
        args.put(USEDAYS, usedays);
        return db.update(TABLE_NAME, args, _ID + "=" + rowId, null) > 0;
    }
}
