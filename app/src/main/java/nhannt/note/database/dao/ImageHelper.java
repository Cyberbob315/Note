package nhannt.note.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import nhannt.note.interfaces.IDAOHandle;
import nhannt.note.database.NoteDatabase;

/**
 * A class implements IDAOHandle for accessing and saving Images of a note from database
 */

public class ImageHelper implements IDAOHandle<String, Integer> {

    private static ImageHelper mInstance;
    private final NoteDatabase mDatabase;

    public static ImageHelper getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new ImageHelper(mContext);
        }
        return mInstance;
    }

    private ImageHelper(Context mContext) {
        mDatabase = NoteDatabase.getInstance(mContext);
    }

    @Override
    public List<String> getAllElement() {
        return null;
    }

    @Override
    public List<String> getListById(Integer noteId) {
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

    @Override
    public boolean insert(String path, Integer noteId) {
        ContentValues valuesImage = new ContentValues();
        boolean isSuccess;
        valuesImage.put(NoteDatabase.TBL_IMAGE_COLUMN_NOTE_ID, noteId);
        valuesImage.put(NoteDatabase.TBL_IMAGE_COLUMN_PATH, path);
        long result = mDatabase.insertRecord(NoteDatabase.TBL_IMAGE, valuesImage);
        isSuccess = result > -1;
        return isSuccess;
    }

    @Override
    public boolean update(String obj, Integer id) {
        return false;
    }

    @Override
    public boolean delete(Integer noteId) {
        boolean isSuccess;
        long result = mDatabase.deleteRecord(NoteDatabase.TBL_IMAGE, NoteDatabase.TBL_IMAGE_COLUMN_NOTE_ID,
                new String[]{noteId + ""});
        isSuccess = result > 0;
        return isSuccess;
    }
}
