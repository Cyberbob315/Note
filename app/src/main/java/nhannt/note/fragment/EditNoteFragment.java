package nhannt.note.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.adapter.ImageAdapter;
import nhannt.note.base.BaseFragment;
import nhannt.note.database.NoteDatabase;
import nhannt.note.model.Note;
import nhannt.note.utils.AppController;
import nhannt.note.utils.Common;
import nhannt.note.utils.Constant;

/**
 * Created by iceman on 1/24/2017.
 */

public class EditNoteFragment extends BaseFragment {

    public static final String TAG = EditNoteFragment.class.getName();

    public static EditNoteFragment newInstance(Note itemNote, int lastNoteId) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.KEY_NOTE_DETAIL, itemNote);
        args.putInt(Constant.KEY_LAST_NOTE_ID, lastNoteId);
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.note_option_content;
    }

    @Override
    protected void showImage(ArrayList lstImage) {
        new LoadImageOfNote().execute();
    }

    @Override
    protected void setUpTextViewAndDateTime() {
        etContent.setText(mItemNote.getContent());
        etTitle.setText(mItemNote.getTitle());
        tvCurrentTime.setText(Common.getDateStrFromMilliseconds(mItemNote.getCreatedDate(), Constant.DATE_FORMAT));
        strDateSelected = Common.getDateStrFromMilliseconds(mItemNote.getNotifyDate(), Constant.DATE_FORMAT);
        strTimeSelected = Common.getDateStrFromMilliseconds(mItemNote.getNotifyDate(), Constant.TIME_FORMAT);
        Common.writeLog("date", mItemNote.getNotifyDate() + "");
        Common.writeLog("date", strDateSelected + "/" + strTimeSelected);
        isFirstDateSpSelected = true;
        isFirstTimeSpSelected = true;
        selectedColor = mItemNote.getColor();
        ivBackGround.setBackgroundColor(mItemNote.getColor());
        lstTime.remove(3);
        lstTime.add(strTimeSelected);
        spTime.setSelection(3);
        lstDate.remove(3);
        lstDate.add(strDateSelected);
        spDate.setSelection(3);

    }

    @Override
    protected int getIdNoteToSave() {
        return mItemNote.getId();
    }

    @Override
    protected void saveNote(ContentValues valuesNote) {
        long result;
        result = mDatabase.updateRecord(NoteDatabase.TBL_NOTE, valuesNote, NoteDatabase.TBL_NOTE_COLUMN_ID,
                new String[]{getIdNoteToSave() + ""});
        if (result > -1) {
            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            Intent intentRefreshHomeActivity = new Intent(Constant.ACTION_REFRESH_LIST);
            getActivity().sendBroadcast(intentRefreshHomeActivity);
        }
        mDatabase.deleteRecord(NoteDatabase.TBL_IMAGE, NoteDatabase.TBL_IMAGE_COLUMN_NOTE_ID,
                new String[]{getIdNoteToSave() + ""});
        for (int i = 0; i < lstImagePath.size(); i++) {
            saveImageToDatabase(getIdNoteToSave(), lstImagePath.get(i));
        }
        getActivity().onBackPressed();
    }

    @Override
    protected void setAlarm() {
        cancelNotify();
        setToNotify();
    }

    public class LoadImageOfNote extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            lstImagePath = AppController.getInstance().getImageFromDatabase(getIdNoteToSave());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mImageAdapter = new ImageAdapter(getActivity(), lstImagePath);
            rvImageList.setAdapter(mImageAdapter);
            super.onPostExecute(aVoid);
        }


    }
}
