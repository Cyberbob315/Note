package nhannt.note.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import nhannt.note.base.IDAOHandle;
import nhannt.note.database.NoteDatabase;
import nhannt.note.model.Note;

/**
 * A class implements IDAOHandle for accessing and saving Note from database
 */

public class NoteHelper implements IDAOHandle<Note, Integer> {

    private static NoteHelper mInstance;
    private final NoteDatabase mDatabase;

    public static NoteHelper getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new NoteHelper(mContext);
        }
        return mInstance;
    }

    private NoteHelper(Context mContext) {
        mDatabase = NoteDatabase.getInstance(mContext);
    }


    @Override
    public List<Note> getAllElement() {
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

    @Override
    public List<Note> getListById(Integer noteId) {
        return null;
    }

    @Override
    public boolean insert(Note obj, Integer id) {
        ContentValues valuesNote = new ContentValues();
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_CONTENT, obj.getContent().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_TITLE, obj.getTitle().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_COLOR, obj.getColor());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTIFY_TIME, obj.getNotifyDate());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_CREATED_TIME, obj.getCreatedDate());
        long result = mDatabase.insertRecord(NoteDatabase.TBL_NOTE, valuesNote);
        boolean isSuccess;
        isSuccess = (result > -1);
        return isSuccess;
    }

    @Override
    public boolean update(Note obj, Integer id) {
        ContentValues valuesNote = new ContentValues();
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_CONTENT, obj.getContent().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_TITLE, obj.getTitle().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_COLOR, obj.getColor());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTIFY_TIME, obj.getNotifyDate());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_CREATED_TIME, obj.getCreatedDate());
        long result = mDatabase.updateRecord(NoteDatabase.TBL_NOTE, valuesNote, NoteDatabase.TBL_NOTE_COLUMN_ID,
                new String[]{obj.getId() + ""});
        boolean isSuccess;
        isSuccess = result > -1;
        return isSuccess;
    }

    @Override
    public boolean delete(Integer noteId) {
        long result = mDatabase.deleteRecord(NoteDatabase.TBL_NOTE, NoteDatabase.TBL_NOTE_COLUMN_ID, new String[]{noteId + ""});
        boolean isSuccess;
        isSuccess = result > 0;
        return isSuccess;
    }
}
