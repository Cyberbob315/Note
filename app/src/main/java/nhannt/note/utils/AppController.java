package nhannt.note.utils;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import nhannt.note.database.NoteDatabase;
import nhannt.note.model.Note;

/**
 * Created by iceman on 1/23/2017.
 */

public class AppController extends Application {

    private static AppController mInstance;
    private static Context mContext;
    private NoteDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
        mDatabase = NoteDatabase.getInstance(mContext);
    }

    private static Context getContext(){
        return mContext;
    }

    public static AppController getInstance() {
        return mInstance;
    }

    public ArrayList<String> getImageFromDatabase(int noteId) {
        Cursor result = mDatabase.rawQuery(NoteDatabase.QUERY_GET_IMAGE_WITH_NOTE_ID + noteId);
        ArrayList<String> list = new ArrayList<>();
        if (result != null && result.moveToFirst()) {
            do {
                String path = result.getString(result.getColumnIndex(NoteDatabase.TBL_IMAGE_COLUMN_PATH));
                list.add(path);
            } while (result.moveToNext());
        }
        return list;
    }

    public ArrayList<Note> getListNote() {
        Cursor result = mDatabase.rawQuery(NoteDatabase.QUERY_GET_ALL_NOTE);
        ArrayList<Note> lstNote = new ArrayList<>();
        if (result != null && result.moveToFirst()) {
            do {
                int id = result.getInt(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_ID));
                String title = result.getString(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTE_TITLE));
                String content = result.getString(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTE_CONTENT));
                int color = result.getInt(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTE_COLOR));
                long createdDateTime = result.getLong(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_CREATED_TIME));
                long notifyDateTime = result.getLong(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTIFY_TIME));
                Note note = new Note(id, title, content, color, createdDateTime, notifyDateTime);
                lstNote.add(note);
            } while (result.moveToNext());
        }
        return lstNote;
    }

}
