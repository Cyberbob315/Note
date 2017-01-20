package nhannt.note.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import nhannt.note.R;
import nhannt.note.adapter.ImageAdapter;
import nhannt.note.database.NoteDatabase;
import nhannt.note.model.Note;
import nhannt.note.receiver.AlarmReceiver;
import nhannt.note.utils.Common;
import nhannt.note.utils.GridSpacingItemDecoration;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener {

    public static final String ACTION_REFRESH_IMAGE_LIST = "nhannt.note.ACTION_REFRESH_IMAGE_LIST";
    public static final String KEY_IS_CREATE_NEW = "key_is_create_new";
    public static final String KEY_NOTE = "key_note";
    public static final String KEY_LAST_NOTE_ID = "key_last_note";
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;


    boolean isCreateNew, isFirstDateSpSelected, isFirstTimeSpSelected;
    Note itemNote;
    NoteDatabase mDatabase = NoteDatabase.getInstance(this);
    String strDateSelected = "", strTimeSelected = "";
    int lastNoteId;
    int selectedColor = R.color.white;
    AlertDialog alertDialogPhoto, alertDialogColor;
    AlarmManager alarmManager;
    Toolbar toolbar;

    RecyclerView rvImageList;
    EditText etTitle, etContent;
    TextView tvCurrentTime, tvAlarm;
    LinearLayout llDateTime, llOptions;
    AppCompatSpinner spDate, spTime;
    ImageView btBack, btNext, btShare, btDelete, btCloseDateTime, ivBackGround;
    ArrayAdapter spDateAdapter, spTimeAdapter;
    ArrayList<String> lstDate, lstTime;
    ArrayList<String> lstImagePath = new ArrayList<>();
    LinearLayout llTakePhotos, llChoosePhotos;
    ImageAdapter mImageAdapter;
    ImageView btnRed, btnBlue, btnGreen, btnYellow, btnWhite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        settingToolbar();
        initControls();
        initEvents();
        getDataFromIntentAndSetUp();
        setupSpinnerDateNSpinnerTime();
        setUpImageList();
    }

    private void setUpImageList() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        rvImageList.setLayoutManager(layoutManager);
        if (!isCreateNew) {
            new LoadImageOfNote().execute();
        } else {
            mImageAdapter = new ImageAdapter(DetailActivity.this, lstImagePath);
            rvImageList.setAdapter(mImageAdapter);
        }
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(4, 40, false);
        rvImageList.addItemDecoration(itemDecoration);

    }

    private ArrayList<String> getImageFromDatabase() {
        Cursor result = mDatabase.rawQuery(NoteDatabase.QUERY_GET_IMAGE_WITH_NOTE_ID + itemNote.getId());
        ArrayList<String> list = new ArrayList<>();
        if (result != null && result.moveToFirst()) {
            do {
                String path = result.getString(result.getColumnIndex(NoteDatabase.TBL_IMAGE_COLUMN_PATH));
                Common.writeLog(path);
                list.add(path);
            } while (result.moveToNext());
        }
        Common.writeLog(list.size() + "");
        return list;
    }

    private void setupSpinnerDateNSpinnerTime() {
        //Spinner Date
        lstDate = new ArrayList<>();
        lstDate.add(getString(R.string.today));
        lstDate.add(getString(R.string.tomorrow));
        lstDate.add(getString(R.string.next) + " " + Common.getCurrentDayOfWeek());
        lstDate.add(getString(R.string.other));
        spDateAdapter = new ArrayAdapter(DetailActivity.this, android.R.layout.simple_gallery_item, lstDate);
        spDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(spDateAdapter);

        //Spinner Time
        lstTime = new ArrayList<>();
        lstTime.add("9:00");
        lstTime.add("13:00");
        lstTime.add("17:00");
        lstTime.add(getString(R.string.other));
        spTimeAdapter = new ArrayAdapter(DetailActivity.this, android.R.layout.simple_gallery_item, lstTime);
        spTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(spTimeAdapter);

        if (!isCreateNew) {
            lstTime.remove(3);
            lstTime.add(strTimeSelected);
            spTime.setSelection(3);
            lstDate.remove(3);
            lstDate.add(strDateSelected);
            spDate.setSelection(3);
        }
    }

    private void initControls() {
        ivBackGround = (ImageView) findViewById(R.id.iv_background_detail);
        rvImageList = (RecyclerView) findViewById(R.id.rv_image_list);
        etTitle = (EditText) findViewById(R.id.et_title);
        etContent = (EditText) findViewById(R.id.et_content);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        tvAlarm = (TextView) findViewById(R.id.tv_alarm);
        llDateTime = (LinearLayout) findViewById(R.id.ll_date_time);
        llOptions = (LinearLayout) findViewById(R.id.ll_option);
        spDate = (AppCompatSpinner) findViewById(R.id.sp_choose_date);
        spTime = (AppCompatSpinner) findViewById(R.id.sp_choose_time);
        btBack = (ImageView) findViewById(R.id.bt_back_option);
        btNext = (ImageView) findViewById(R.id.bt_next_option);
        btShare = (ImageView) findViewById(R.id.bt_share_option);
        btDelete = (ImageView) findViewById(R.id.bt_delete_option);
        btCloseDateTime = (ImageView) findViewById(R.id.bt_close_date_time);
    }

    private void initEvents() {
        tvAlarm.setOnClickListener(this);
        btCloseDateTime.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btShare.setOnClickListener(this);
        btDelete.setOnClickListener(this);
        spDate.setOnItemSelectedListener(this);
        spTime.setOnItemSelectedListener(this);
    }


    private void getDataFromIntentAndSetUp() {
        Intent intent = getIntent();
        isCreateNew = intent.getExtras().getBoolean(KEY_IS_CREATE_NEW);
        if (!isCreateNew) {
            itemNote = (Note) intent.getExtras().getSerializable(KEY_NOTE);
            llOptions.setVisibility(View.VISIBLE);
            etContent.setText(itemNote.getContent());
            etTitle.setText(itemNote.getTitle());
            tvCurrentTime.setText(Common.getDateStrFromMilliseconds(itemNote.getCreatedDate(), "yyyy-MM-dd"));
            strDateSelected = Common.getDateStrFromMilliseconds(itemNote.getNotifyDate(), "yyyy-MM-dd");
            strTimeSelected = Common.getDateStrFromMilliseconds(itemNote.getNotifyDate(), "HH:mm");
            isFirstDateSpSelected = true;
            isFirstTimeSpSelected = true;
            ivBackGround.setBackgroundColor(itemNote.getColor());

        } else {
            llOptions.setVisibility(View.GONE);
            lastNoteId = intent.getExtras().getInt(KEY_LAST_NOTE_ID);
            isFirstDateSpSelected = false;
            isFirstTimeSpSelected = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_choose_picture_menu:
                showPhotoChooserDialog();
                break;
            case R.id.bt_choose_color_menu:
                showColorChooserDialog();
                break;
            case R.id.bt_done_menu:
                saveNoteToDatabase();
                setToNotify();
                break;
            case R.id.bt_new_note_menu:
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToNotify() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentToAlarmClass = new Intent(DetailActivity.this, AlarmReceiver.class);
        Note itemNoteToNotify;
        long createdDateTime = Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long notifyDateTime = Common.parseStrDateTimeToMills(strDateSelected + " " + strTimeSelected, NoteDatabase.SQL_DATE_FORMAT);
        Common.writeLog("Notify:" + notifyDateTime);
        if (isCreateNew) {
            itemNoteToNotify = new Note(lastNoteId + 1, etTitle.getText().toString(), etContent.getText().toString(),
                    ContextCompat.getColor(DetailActivity.this,selectedColor),
                    createdDateTime, notifyDateTime);
        } else {
            itemNoteToNotify = new Note(itemNote.getId(), etTitle.getText().toString(), etContent.getText().toString(),
                    ContextCompat.getColor(DetailActivity.this,selectedColor),
                    createdDateTime, notifyDateTime);
        }
        intentToAlarmClass.putExtra(AlarmReceiver.KEY_NOTE_TO_NOTIFY, itemNoteToNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailActivity.this, 0,
                intentToAlarmClass, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notifyDateTime, pendingIntent);

    }

    private void cancelNotify() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentToAlarmClass = new Intent(DetailActivity.this, AlarmReceiver.class);
        Note itemNoteToNotify;
        long createdDateTime = Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long notifyDateTime = Common.parseStrDateTimeToMills(strDateSelected + " " + strTimeSelected, NoteDatabase.SQL_DATE_FORMAT);
        Common.writeLog("Notify:" + notifyDateTime);
        if (isCreateNew) {
            itemNoteToNotify = new Note(lastNoteId + 1, etTitle.getText().toString(), etContent.getText().toString(), selectedColor,
                    createdDateTime, notifyDateTime);
        } else {
            itemNoteToNotify = new Note(itemNote.getId(), etTitle.getText().toString(), etContent.getText().toString(), selectedColor,
                    createdDateTime, notifyDateTime);
        }
        intentToAlarmClass.putExtra(AlarmReceiver.KEY_NOTE_TO_NOTIFY, itemNoteToNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailActivity.this, 0,
                intentToAlarmClass, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isCreateNew = getIntent().getExtras().getBoolean(KEY_IS_CREATE_NEW);
        if (isCreateNew) {
            menu.removeItem(R.id.bt_new_note_menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showPhotoChooserDialog() {
        final View view = getLayoutInflater().inflate(R.layout.photos_chooser_dialog, null);

        llTakePhotos = (LinearLayout) view.findViewById(R.id.ll_take_photos);
        llChoosePhotos = (LinearLayout) view.findViewById(R.id.ll_choose_photos);
        llTakePhotos.setOnClickListener(this);
        llChoosePhotos.setOnClickListener(this);

        alertDialogPhoto = new AlertDialog.Builder(this).create();
        alertDialogPhoto.setTitle("Insert Photos");
        alertDialogPhoto.setIcon(R.drawable.ic_camera);
        alertDialogPhoto.setView(view);
        alertDialogPhoto.show();
    }

    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = etContent.getText().toString();
        String shareSub = etTitle.getText().toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    private void showColorChooserDialog() {
        View view = getLayoutInflater().inflate(R.layout.color_chooser, null);
        btnRed = (ImageView) view.findViewById(R.id.iv_color_red);
        btnBlue = (ImageView) view.findViewById(R.id.iv_color_blue);
        btnYellow = (ImageView) view.findViewById(R.id.iv_color_yellow);
        btnGreen = (ImageView) view.findViewById(R.id.iv_color_green);
        btnWhite = (ImageView) view.findViewById(R.id.iv_color_white);

        btnWhite.setOnClickListener(this);
        btnRed.setOnClickListener(this);
        btnBlue.setOnClickListener(this);
        btnYellow.setOnClickListener(this);
        btnGreen.setOnClickListener(this);
        alertDialogColor = new AlertDialog.Builder(this).create();
        alertDialogColor.setTitle("Select Color");
        alertDialogColor.setView(view);
        alertDialogColor.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_alarm:
                llDateTime.setVisibility(View.VISIBLE);
                tvAlarm.setVisibility(View.GONE);
                break;
            case R.id.bt_back_option:
                break;
            case R.id.bt_next_option:
                break;
            case R.id.bt_share_option:
                shareNote();
                break;
            case R.id.bt_delete_option:
                if (itemNote != null) {
                    showConfirmDeleteNoteDialog(itemNote.getId());
                }
                break;
            case R.id.bt_close_date_time:
                llDateTime.setVisibility(View.GONE);
                tvAlarm.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_take_photos:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                alertDialogPhoto.dismiss();
                break;
            case R.id.ll_choose_photos:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                alertDialogPhoto.dismiss();
                break;
            case R.id.iv_color_blue:
                selectedColor = R.color.blue;
                ivBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_red:
                selectedColor = R.color.colorRed;
                ivBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed));
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_yellow:
                selectedColor = R.color.yellow;
                ivBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_green:
                selectedColor = R.color.green;
                ivBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_white:
                selectedColor = R.color.white;
                ivBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                alertDialogColor.dismiss();
                break;
        }
    }

    private void saveNoteToDatabase() {
        String dateTime = strDateSelected + " " + strTimeSelected;
        ContentValues valuesNote = new ContentValues();
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_CONTENT, etContent.getText().toString().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_TITLE, etTitle.getText().toString().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_COLOR, ContextCompat.getColor(DetailActivity.this, selectedColor) + "");
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTIFY_TIME, Common.parseStrDateTimeToMills(dateTime, NoteDatabase.SQL_DATE_FORMAT));
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_CREATED_TIME,
                Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT));
        Log.d("test", Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT) + "");

        long result;
        if (isCreateNew) {
            result = mDatabase.insertRecord(NoteDatabase.TBL_NOTE, valuesNote);
        } else {
            result = mDatabase.updateRecord(NoteDatabase.TBL_NOTE, valuesNote, NoteDatabase.TBL_NOTE_COLUMN_ID,
                    new String[]{itemNote.getId() + ""});
        }
        if (result > -1) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            Intent intentRefreshHomeActivity = new Intent(HomeActivity.ACTION_REFRESH_LIST);
            sendBroadcast(intentRefreshHomeActivity);
        }
        if (isCreateNew) {
            for (int i = 0; i < lstImagePath.size(); i++) {
                saveImageToDatabase(lastNoteId + 1, lstImagePath.get(i));
            }
        } else {
            mDatabase.deleteRecord(NoteDatabase.TBL_IMAGE, NoteDatabase.TBL_IMAGE_COLUMN_NOTE_ID,
                    new String[]{itemNote.getId() + ""});
            for (int i = 0; i < lstImagePath.size(); i++) {
                saveImageToDatabase(itemNote.getId(), lstImagePath.get(i));
            }
        }
    }

    private void saveImageToDatabase(int noteId, String path) {
        ContentValues valuesImage = new ContentValues();
        valuesImage.put(NoteDatabase.TBL_IMAGE_COLUMN_NOTE_ID, noteId);
        valuesImage.put(NoteDatabase.TBL_IMAGE_COLUMN_PATH, path);
        mDatabase.insertRecord(NoteDatabase.TBL_IMAGE, valuesImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    String path = Common.saveImage(this, photo);
                    lstImagePath.add(path);
                    mImageAdapter.notifyDataSetChanged();
                }
                break;
            case GALLERY_REQUEST:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String selectedImagePath = Common.getRealPathFromURI(this, selectedImage);
                    Log.d("image", selectedImagePath);
                    lstImagePath.add(selectedImagePath);
                    mImageAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        switch (parent.getId()) {
            case R.id.sp_choose_date:
                switch (position) {
                    case 0:
                        strDateSelected = year + "-" + month + "-" + day;
                        break;
                    case 1:
                        strDateSelected = year + "-" + month + "-" + (day + 1);
                        break;
                    case 2:
                        strDateSelected = year + "-" + month + "-" + (day + 7);
                        break;
                    case 3:
                        if (!isFirstDateSpSelected) {
                            new DatePickerDialog(this, onDateSetListener, year, month, day).show();
                        }
                        isFirstDateSpSelected = false;
                        break;
                    default:
                        break;
                }
                break;
            case R.id.sp_choose_time:
                switch (position) {
                    case 3:
                        if (!isFirstTimeSpSelected) {
                            new TimePickerDialog(this, onTimeSetListener, hour, minutes, true).show();
                        }
                        isFirstTimeSpSelected = false;
                        break;
                    default:
                        strTimeSelected = spTime.getSelectedItem().toString();
                        break;
                }
                break;
        }
    }

    private class LoadImageOfNote extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            lstImagePath = getImageFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mImageAdapter = new ImageAdapter(DetailActivity.this, lstImagePath);
            rvImageList.setAdapter(mImageAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            strDateSelected = year + "-" + (month + 1) + "-" + dayOfMonth;
            lstDate.remove(3);
            lstDate.add(strDateSelected);
            spDateAdapter.notifyDataSetChanged();
        }
    };

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            strTimeSelected = hourOfDay + ":" + minute;
            lstTime.remove(3);
            lstTime.add(strTimeSelected);
            spTimeAdapter.notifyDataSetChanged();
        }
    };


    private void showConfirmDeleteNoteDialog(final int noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        builder.setMessage("Are you sure to delete this ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.deleteRecord(NoteDatabase.TBL_NOTE, NoteDatabase.TBL_NOTE_COLUMN_ID, new String[]{noteId + ""});
                Intent intent = new Intent(HomeActivity.ACTION_REFRESH_LIST);
                sendBroadcast(intent);
                dialog.dismiss();
                cancelNotify();
                onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}