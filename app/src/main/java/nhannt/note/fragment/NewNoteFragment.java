package nhannt.note.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.adapter.ImageAdapter;
import nhannt.note.base.BaseFragment;
import nhannt.note.database.NoteDatabase;
import nhannt.note.model.Note;
import nhannt.note.utils.Common;
import nhannt.note.utils.Constant;

/**
 * Created by iceman on 1/23/2017.
 */

public class NewNoteFragment extends BaseFragment {

    public static final String TAG = NewNoteFragment.class.getName();


    public static NewNoteFragment newInstance(Note itemNote, int lastNoteId) {

        NewNoteFragment fragment = new NewNoteFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.KEY_NOTE_DETAIL, itemNote);
        args.putSerializable(Constant.KEY_LAST_NOTE_ID, lastNoteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_note;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.removeItem(R.id.bt_share_menu);
        menu.removeItem(R.id.bt_delete_menu);
        menu.removeItem(R.id.bt_new_note_menu);
    }

    @Override
    protected void saveNote(ContentValues valuesNote) {
        long result;
        result = mDatabase.insertRecord(NoteDatabase.TBL_NOTE, valuesNote);

        if (result > -1) {
            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            Intent intentRefreshHomeActivity = new Intent(Constant.ACTION_REFRESH_LIST);
            getActivity().sendBroadcast(intentRefreshHomeActivity);
        }

        for (int i = 0; i < lstImagePath.size(); i++) {
            saveImageToDatabase(getIdNoteToSave() + 1, lstImagePath.get(i));
        }
        getActivity().onBackPressed();
    }

    @Override
    protected void setUpTextViewAndDateTime() {
        tvCurrentTime.setText(Common.getCurrentDateTimeInStr(Constant.DATE_FORMAT));
        isFirstDateSpSelected = false;
        isFirstTimeSpSelected = false;
        selectedColor = ContextCompat.getColor(getActivity(),R.color.white);
        strDateSelected = Common.getCurrentDateTimeInStr(Constant.DATE_FORMAT);
        strTimeSelected = getString(R.string.sp_time_slot1);

    }

    @Override
    protected void setAlarm() {
        setToNotify();
    }

    @Override
    protected int getIdNoteToSave() {
        return lastNoteId + 1;
}

    @Override
    protected void showImage(ArrayList lstImage) {
        lstImage = new ArrayList();
        mImageAdapter = new ImageAdapter(getActivity(), lstImage);
        rvImageList.setAdapter(mImageAdapter)  ;
    }
}
