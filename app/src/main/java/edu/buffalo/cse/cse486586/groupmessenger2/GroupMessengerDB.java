package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

public class GroupMessengerDB extends SQLiteOpenHelper {
    private static final String TAG = GroupMessengerDB.class.getName();

    public static final String DB_NAME = "edu.buffalo.cse.cse486586.groupmessenger1";
    public static final String TABLE = "kv_store";
    public static final int DB_VERSION = 1;

    public GroupMessengerDB(Context context) {
        super(context, TABLE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS kv_store (key TEXT PRIMARY KEY, value TEXT)");
        Log.d(TAG, "Created DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS kv_store");
        onCreate(db);
    }

    public long insert(ContentValues keyValue) {
        return getWritableDatabase().insertWithOnConflict(TABLE, null, keyValue, CONFLICT_REPLACE);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(TABLE);
        return sqLiteQueryBuilder.query(getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }
}
