package nhannt.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import nhannt.note.R;
import nhannt.note.fragment.NewNoteFragment;
import nhannt.note.model.Note;
import nhannt.note.utils.Constant;

public class HostActivity extends AppCompatActivity {

    private Note itemNote;
    private int lastNoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        getDataFromIntent();
        NewNoteFragment newNoteFragment = NewNoteFragment.newInstance(itemNote, lastNoteId);
        showFragment(newNoteFragment, NewNoteFragment.TAG);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        itemNote = (Note) intent.getExtras().getSerializable(Constant.KEY_NOTE_DETAIL);
        lastNoteId = intent.getExtras().getInt(Constant.KEY_LAST_NOTE_ID);
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.addToBackStack("");
        fragmentTransaction.add(R.id.activity_host, fragment, tag);
        fragmentTransaction.commit();
    }
}
