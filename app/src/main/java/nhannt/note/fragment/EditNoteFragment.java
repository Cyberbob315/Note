package nhannt.note.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.adapter.ImageAdapter;
import nhannt.note.base.BaseFragment;
import nhannt.note.model.Note;
import nhannt.note.utils.Common;
import nhannt.note.utils.Constant;
import nhannt.note.utils.DateTimeUtils;

/**
 * Created by iceman on 1/24/2017.
 *
 * Fragment extended BaseFragment for edit an existing note
 * Beside BaseFragment's methods,it provides and implement methods for saving current note edited
 *
 * @author nhannt
 *
 */

public class EditNoteFragment extends BaseFragment {


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
        tvCurrentTime.setText(DateTimeUtils.getDateStrFromMilliseconds(mItemNote.getCreatedDate(), Constant.DATE_FORMAT));
        strDateSelected = DateTimeUtils.getDateStrFromMilliseconds(mItemNote.getNotifyDate(), Constant.DATE_FORMAT);
        strTimeSelected = DateTimeUtils.getDateStrFromMilliseconds(mItemNote.getNotifyDate(), Constant.TIME_FORMAT);
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
    protected void saveNote(Note itemNoteToSave) {
        boolean result = mNoteHelper.update(itemNoteToSave, getIdNoteToSave());
        if (result) {
            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            Intent intentRefreshHomeActivity = new Intent(Constant.ACTION_REFRESH_LIST);
            getActivity().sendBroadcast(intentRefreshHomeActivity);
        }
        mImageHelper.delete(getIdNoteToSave());
        for (int i = 0; i < lstImagePath.size(); i++) {
            mImageHelper.insert(lstImagePath.get(i), getIdNoteToSave());
        }
        getActivity().onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_share_menu:
                shareNote();
                break;
            case R.id.bt_delete_menu:
                showConfirmDeleteNoteDialog(getIdNoteToSave());
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = etContent.getText().toString();
        String shareSub = etTitle.getText().toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
    }

    private void showConfirmDeleteNoteDialog(final int noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.warning));
        builder.setMessage(getString(R.string.delete_note_question));
        builder.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNoteHelper.delete(noteId);
                Intent intent = new Intent(Constant.ACTION_REFRESH_LIST);
                getActivity().sendBroadcast(intent);
                dialog.dismiss();
                cancelNotify();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void setAlarm() {
        cancelNotify();
        setToNotify();
    }

    @Override
    protected int getHomeAsUpIndicator() {
        return R.drawable.ic_back;
    }

    private class LoadImageOfNote extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            lstImagePath = (ArrayList<String>) mImageHelper.getListById(getIdNoteToSave());
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
