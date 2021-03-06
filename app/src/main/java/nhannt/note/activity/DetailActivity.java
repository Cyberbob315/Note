package nhannt.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.adapter.NoteViewPagerAdapter;
import nhannt.note.base.BaseActivity;
import nhannt.note.model.Note;
import nhannt.note.utils.Constant;

public class DetailActivity extends BaseActivity {


    private ViewPager mNoteViewPager;
    private ArrayList<Note> mListNote;
    private NoteViewPagerAdapter mVPAdapter;
    private int notePos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControls();
        setupToolbar();
        getDataFromIntent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mListNote.get(notePos).getTitle());
        }

        setUpViewPager();

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_detail;
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    private void setUpViewPager() {
        mVPAdapter = new NoteViewPagerAdapter(getSupportFragmentManager(), mListNote);
        mNoteViewPager.setOffscreenPageLimit(3);
        mNoteViewPager.setAdapter(mVPAdapter);
        mNoteViewPager.setCurrentItem(notePos);
        mNoteViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mVPAdapter.getPageTitle(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @SuppressWarnings("unchecked")
    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mListNote = (ArrayList<Note>) intent.getExtras().getSerializable(Constant.KEY_LIST_NOTE);
            notePos = intent.getExtras().getInt(Constant.KEY_NOTE_POSITION);
        }
    }

    private void initControls() {
        mNoteViewPager = (ViewPager) findViewById(R.id.view_pager_note);
    }
}
