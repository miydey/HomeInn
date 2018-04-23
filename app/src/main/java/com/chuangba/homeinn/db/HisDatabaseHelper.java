package com.chuangba.homeinn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jinyh on 2017/4/20.
 */

public class HisDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = HisDatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static HisDatabaseHelper instance;
    private static final String DATABASE_NAME= "history.db";
    private static final String TABLE_NAME = "face";
    private Context mContext;
    private static final String CREATE_TABLE = "create table face ( id INTEGER primary key autoincrement," +
            "name TEXT," +
            "date INTEGER," +
            "sex TEXT,"+
            "cardNumber TEXT,"+
            "type TEXT," +
            "picCamera BLOB," +
            "picID BLOB," +
            "sim FLOAT)";

    public HisDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public synchronized static HisDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HisDatabaseHelper(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.e(TAG, "onCreate: success" );
        final int FIRST_DATABASE_VERSION = 1;
        onUpgrade(db, FIRST_DATABASE_VERSION, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) { // 跨版本升级
            switch (i) {
                case 1:
                    upgradeToVersion2(db);
                    break;
                default:
                    break;
            }
        }
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        String addResult = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN result TEXT";// 添加比对结果
        db.execSQL(addResult);
        Log.e(TAG, "upgradeToVersion2 " );
    }
}
