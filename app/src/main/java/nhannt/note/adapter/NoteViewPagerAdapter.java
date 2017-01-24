package nhannt.note.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import nhannt.note.fragment.EditNoteFragment;
import nhannt.note.model.Note;

/**
 * Created by iceman on 1/22/2017.
 */

public class NoteViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ArrayList<Note> mData;

    public NoteViewPagerAdapter(FragmentManager fm, Context mContext, ArrayList<Note> mData) {
        super(fm);
        this.mContext = mContext;
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
