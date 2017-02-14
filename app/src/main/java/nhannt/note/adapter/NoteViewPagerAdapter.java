package nhannt.note.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import nhannt.note.fragment.EditNoteFragment;
import nhannt.note.model.Note;

/**
 * An adapter class for showing a detail note fragment on a view pager
 */

public class NoteViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Note> mData;

    public NoteViewPagerAdapter(FragmentManager fm, ArrayList<Note> mData) {
        super(fm);
        this.mData = mData;
    }

    @Override
    public Fragment getItem(int position) {
        return EditNoteFragment.newInstance(mData.get(position), mData.get(mData.size() - 1).getId());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position).getTitle();
    }
}
