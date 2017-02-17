package nhannt.note.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.adapter.ImageAdapter;
import nhannt.note.base.BaseFragment;
import nhannt.note.model.Note;
import nhannt.note.utils.Constant;
import nhannt.note.utils.DateTimeUtils;

/**
 * A fragment extends base fragment and contains some other method for creating new note
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
    protected void saveNote(Note itemNoteToSave) {
        boolean result = mNoteHelper.insert(itemNoteToSave, getIdNoteToSave());

        if (result) {
            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            Intent intentRefreshHomeActivity = new Intent(Constant.ACTION_REFRESH_LIST);
            getActivity().sendBroadcast(intentRefreshHomeActivity);
        }

        for (int i = 0; i < lstImagePath.size(); i++) {
            mImageHelper.insert(lstImagePath.get(i), getIdNoteToSave());
        }
        getActivity().onBackPressed();
    }

    @Override
    protected void setUpTextViewAndDateTime() {
        tvCurrentTime.setText(DateTimeUtils.getCurrentDateTimeInStr(Constant.DATE_FORMAT));
        isFirstDateSpSelected = false;
        isFirstTimeSpSelected = false;
        selectedColor = ContextCompat.getColor(getActivity(), R.color.white);
        strDateSelected = DateTimeUtils.getCurrentDateTimeInStr(Constant.DATE_FORMAT);
        strTimeSelected = getString(R.string.sp_time_slot1);

    }

    @Override
    protected int getHomeAsUpIndicator() {
        return R.drawable.ic_back;
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
    @SuppressWarnings("unchecked")
    protected void showImage(ArrayList lstImage) {
        lstImage = new ArrayList();
        mImageAdapter = new ImageAdapter(getActivity(), lstImage);
        rvImageList.setAdapter(mImageAdapter);
    }
}
