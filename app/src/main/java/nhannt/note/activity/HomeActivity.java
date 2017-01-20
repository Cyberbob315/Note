package nhannt.note.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.adapter.NoteAdapter;
import nhannt.note.database.NoteDatabase;
import nhannt.note.model.Note;
import nhannt.note.utils.Common;
import nhannt.note.utils.GridSpacingItemDecoration;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    public static final String ACTION_REFRESH_LIST = "nhannt.note.ACTION_REFRESH_LIST";

    private Toolbar toolbar;
    RecyclerView rvListNote;
    FloatingActionButton fabAddNewNote;
    ProgressBar pbLoadListNote;
    ArrayList<Note> mListNote;
    NoteAdapter mNoteAdapter;
    NoteDatabase mNoteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingToolbar();
        initControls();
        initEvents();
        registerBroadcastRefresh();
        mNoteDatabase = NoteDatabase.getInstance(this);
        if (Common.isMarshMallow()) {
            if (!checkPermission()) {
                requestPermission();
            }else{
                doMainWork();
            }
        } else {
            doMainWork();
        }
    }

    private void initEvents() {
        fabAddNewNote.setOnClickListener(this);
    }

    private void doMainWork() {
        new LoadNotes().execute();
    }

    private void initControls() {
        rvListNote = (RecyclerView) findViewById(R.id.rv_list_note);
        pbLoadListNote = (ProgressBar) findViewById(R.id.pb_load_note);
        fabAddNewNote = (FloatingActionButton) findViewById(R.id.fab_add_new_note);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, 2);
        rvListNote.setLayoutManager(gridLayoutManager);
        rvListNote.addItemDecoration(new GridSpacingItemDecoration(2, 40, true));
    }

    private void settingToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(getString(R.string.app_name));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_new_note:
                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.KEY_IS_CREATE_NEW, true);
                if (mListNote != null && mListNote.size() > 0) {
                    intent.putExtra(DetailActivity.KEY_LAST_NOTE_ID, mListNote.get(mListNote.size() - 1).getId());
                } else {
                    intent.putExtra(DetailActivity.KEY_LAST_NOTE_ID, 0);
                }
                startActivity(intent);
                break;
        }
    }


    class LoadNotes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadListNote.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mListNote = getListNote();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mNoteAdapter = new NoteAdapter(HomeActivity.this, mListNote);
            rvListNote.setAdapter(mNoteAdapter);
            pbLoadListNote.setVisibility(View.GONE);
        }
    }

    private ArrayList<Note> getListNote() {
        Cursor result = mNoteDatabase.rawQuery(NoteDatabase.QUERY_GET_ALL_NOTE);
        ArrayList<Note> lstNote = new ArrayList<>();
        if (result != null && result.moveToFirst()) {
            do {
                int id = result.getInt(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_ID));
                String title = result.getString(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTE_TITLE));
                String content = result.getString(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTE_CONTENT));
                int color = result.getInt(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTE_COLOR));
                long createdDateTime = result.getLong(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_CREATED_TIME));
                long notifyDateTime = result.getLong(result.getColumnIndex(NoteDatabase.TBL_NOTE_COLUMN_NOTIFY_TIME));
                Note note = new Note(id, title, content, color,createdDateTime, notifyDateTime);
                lstNote.add(note);
            } while (result.moveToNext());
        }
        return lstNote;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeAccepted && readAccepted) {
                        doMainWork();
                    } else {
                        Toast.makeText(this, "Permission Denied, You cannot access database", Toast.LENGTH_SHORT).show();
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                            showMessageOKCancel("You need to allow access to the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermission();
                                        }
                                    });
                            return;
                        }
                    }
                }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    BroadcastReceiver broadcastReceiverRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadNotes().execute();
        }
    };

    private void registerBroadcastRefresh(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_LIST);
        registerReceiver(broadcastReceiverRefresh,filter);
    }

    private void unregisterBroadcastRefresh(){
        unregisterReceiver(broadcastReceiverRefresh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastRefresh();
    }
}
