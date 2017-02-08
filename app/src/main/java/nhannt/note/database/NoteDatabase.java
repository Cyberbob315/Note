package nhannt.note.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IceMan on 12/30/2016.
 */

public class NoteDatabase extends SQLiteOpenHelper {

    public static NoteDatabase mInstance;


    public static NoteDatabase getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new NoteDatabase(mContext);
        }
        return mInstance;
    }

    public static final String DB_NAME = "NoteManagement.db";
    public static final int DB_VERSION = 1;

    public static final String TBL_NOTE = "NoteTable";
    public static final String TBL_NOTE_COLUMN_ID = "NoteId";
    public static final String TBL_NOTE_COLUMN_NOTE_TITLE = "NoteTitle";
    public static final String TBL_NOTE_COLUMN_NOTE_CONTENT = "NoteContent";
    public static final String TBL_NOTE_COLUMN_NOTE_COLOR = "NoteColor";
    public static final String TBL_NOTE_COLUMN_CREATED_TIME = "CreatedTime";
    public static final String TBL_NOTE_COLUMN_NOTIFY_TIME = "NotifyTime";

    public static final String TBL_IMAGE = "ImageTable";
    public static final String TBL_IMAGE_COLUMN_ID = "ImageId";
    public static final String TBL_IMAGE_COLUMN_NOTE_ID = "NoteId";
    public static final String TBL_IMAGE_COLUMN_PATH = "ImagePath";

    public static final String SQL_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private static final String CREATE_TABLE_NOTE = "create table " + TBL_NOTE + "("
            + TBL_NOTE_COLUMN_ID + " integer primary key autoincrement,"
            + TBL_NOTE_COLUMN_NOTE_TITLE + " text,"
            + TBL_NOTE_COLUMN_NOTE_CONTENT + " text,"
            + TBL_NOTE_COLUMN_NOTE_COLOR + " integer,"
            + TBL_NOTE_COLUMN_CREATED_TIME + " integer ,"
            + TBL_NOTE_COLUMN_NOTIFY_TIME + " integer);";

    private static final String CREATE_TABLE_IMAGE = "create table " + TBL_IMAGE + "("
            + TBL_IMAGE_COLUMN_ID + " integer primary key autoincrement,"
            + TBL_IMAGE_COLUMN_NOTE_ID + " int,"
            + TBL_IMAGE_COLUMN_PATH + " text,"
            + "foreign key (" + TBL_IMAGE_COLUMN_NOTE_ID + ") references " + TBL_NOTE + "(" + TBL_NOTE_COLUMN_ID + ") "
            + "on update cascade on delete cascade);";

    public static final String QUERY_GET_ALL_NOTE = "select * from " + TBL_NOTE + " order by " + TBL_NOTE_COLUMN_ID + " asc;";
    public static final String QUERY_GET_IMAGE_WITH_NOTE_ID = "select * from " + TBL_IMAGE
            + " where " + TBL_IMAGE_COLUMN_NOTE_ID + "=";

    public NoteDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onOpen(android.database.sqlite.SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor rawQuery(String sqlQuery) {
        return getReadableDatabase().rawQuery(sqlQuery, null);
    }

    public long insertRecord(String tableName, ContentValues values) {
        return getWritableDatabase().insert(tableName, null, values);
    }

    public long updateRecord(String tableName, ContentValues values, String columnID, String[] id) {
        return getWritableDatabase().update(tableName, values, columnID + " =? ", id);
    }

    public long deleteRecord(String tableName, String columnID, String[] id) {
        return getWritableDatabase().delete(tableName, columnID + " =? ", id);
    }
}
