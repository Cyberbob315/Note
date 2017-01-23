package nhannt.note.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import nhannt.note.utils.Constant;
import nhannt.note.utils.GridSpacingItemDecoration;

public class NewActivity extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener {


    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    NoteDatabase mDatabase = NoteDatabase.getInstance(this);
    String strDateSelected = "", strTimeSelected = "";
    int lastNoteId;
    int selectedColor;
    AlertDialog alertDialogPhoto, alertDialogColor;
    AlarmManager alarmManager;
    Toolbar toolbar;

    private RecyclerView rvImageList;
    private EditText etTitle, etContent;
    private TextView tvCurrentTime, tvAlarm;
    private LinearLayout llDateTime;
    private AppCompatSpinner spDate, spTime;
    private ImageView btCloseDateTime, ivBackGround;
    private ArrayAdapter spDateAdapter, spTimeAdapter;
    private ArrayList<String> lstDate, lstTime;
    private ArrayList<String> lstImagePath = new ArrayList<>();
    private LinearLayout llTakePhotos, llChoosePhotos;
    private ImageAdapter mImageAdapter;
    private ImageView btnRed, btnBlue, btnGreen, btnYellow, btnWhite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        settingToolbar();
        initControls();
        initEvents();
        getDataFromIntentAndSetUp();
        setupSpinnerDateNSpinnerTime();
        setUpImageList();
    }

    private void setUpImageList() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(4, 40, false);
        rvImageList.addItemDecoration(itemDecoration);
        rvImageList.setLayoutManager(layoutManager);
        mImageAdapter = new ImageAdapter(NewActivity.this, lstImagePath);
        rvImageList.setAdapter(mImageAdapter);
    }


    private void setupSpinnerDateNSpinnerTime() {
        //Spinner Date
        lstDate = new ArrayList<>();
        lstDate.add(getString(R.string.today));
        lstDate.add(getString(R.string.tomorrow));
        lstDate.add(getString(R.string.next) + " " + Common.getCurrentDayOfWeek());
        lstDate.add(getString(R.string.other));
        spDateAdapter = new ArrayAdapter(NewActivity.this, android.R.layout.simple_gallery_item, lstDate);
        spDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(spDateAdapter);
        strDateSelected = getString(R.string.today);

        //Spinner Time
        lstTime = new ArrayList<>();
        lstTime.add(getString(R.string.sp_time_slot1));
        lstTime.add(getString(R.string.sp_time_slot2));
        lstTime.add(getString(R.string.sp_time_slot3));
        lstTime.add(getString(R.string.other));
        spTimeAdapter = new ArrayAdapter(NewActivity.this, android.R.layout.simple_gallery_item, lstTime);
        spTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(spTimeAdapter);
        strTimeSelected = getString(R.string.sp_time_slot1);

        tvCurrentTime.setText(Common.getCurrentDateTimeInStr(Constant.DATE_FORMAT));

    }

    private void initControls() {
        ivBackGround = (ImageView) findViewById(R.id.iv_background_detail);
        rvImageList = (RecyclerView) findViewById(R.id.rv_image_list);
        etTitle = (EditText) findViewById(R.id.et_title);
        etContent = (EditText) findViewById(R.id.et_content);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        tvAlarm = (TextView) findViewById(R.id.tv_alarm);
        llDateTime = (LinearLayout) findViewById(R.id.ll_date_time);
        spDate = (AppCompatSpinner) findViewById(R.id.sp_choose_date);
        spTime = (AppCompatSpinner) findViewById(R.id.sp_choose_time);
        btCloseDateTime = (ImageView) findViewById(R.id.bt_close_date_time);
        selectedColor = ContextCompat.getColor(NewActivity.this, R.color.white);
    }

    private void initEvents() {
        tvAlarm.setOnClickListener(this);
        btCloseDateTime.setOnClickListener(this);
        spDate.setOnItemSelectedListener(this);
        spTime.setOnItemSelectedListener(this);
    }


    private void getDataFromIntentAndSetUp() {
        Intent intent = getIntent();
        lastNoteId = intent.getExtras().getInt(Constant.KEY_LAST_NOTE_ID);
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
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToNotify() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentToAlarmClass = new Intent(NewActivity.this, AlarmReceiver.class);
        Note itemNoteToNotify;
        long createdDateTime = Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long notifyDateTime = Common.parseStrDateTimeToMills(strDateSelected + " " + strTimeSelected, NoteDatabase.SQL_DATE_FORMAT);
        Common.writeLog("Notify", notifyDateTime + "");
        itemNoteToNotify = new Note(lastNoteId + 1, etTitle.getText().toString(), etContent.getText().toString(),
                selectedColor, createdDateTime, notifyDateTime);
        intentToAlarmClass.putExtra(AlarmReceiver.KEY_NOTE_TO_NOTIFY, itemNoteToNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NewActivity.this, 0,
                intentToAlarmClass, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notifyDateTime, pendingIntent);

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.bt_new_note_menu);
        menu.removeItem(R.id.bt_share_menu);
        menu.removeItem(R.id.bt_delete_menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void showPhotoChooserDialog() {
        final View view = getLayoutInflater().inflate(R.layout.photos_chooser_dialog, null);
        llTakePhotos = (LinearLayout) view.findViewById(R.id.ll_take_photos);
        llChoosePhotos = (LinearLayout) view.findViewById(R.id.ll_choose_photos);
        llTakePhotos.setOnClickListener(this);
        llChoosePhotos.setOnClickListener(this);
        alertDialogPhoto = new AlertDialog.Builder(this).create();
        alertDialogPhoto.setTitle(getString(R.string.insert_photo));
        alertDialogPhoto.setIcon(R.drawable.ic_camera);
        alertDialogPhoto.setView(view);
        alertDialogPhoto.show();
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
        alertDialogColor.setTitle(getString(R.string.select_color));
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
                selectedColor = ContextCompat.getColor(this, R.color.blue);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_red:
                selectedColor = ContextCompat.getColor(this, R.color.colorRed);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_yellow:
                selectedColor = ContextCompat.getColor(this, R.color.yellow);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_green:
                selectedColor = ContextCompat.getColor(this, R.color.green);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_white:
                selectedColor = ContextCompat.getColor(this, R.color.white);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
        }
    }

    private void saveNoteToDatabase() {
        String dateTime = strDateSelected + " " + strTimeSelected;
        ContentValues valuesNote = new ContentValues();
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_CONTENT, etContent.getText().toString().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_TITLE, etTitle.getText().toString().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_COLOR, selectedColor + "");
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTIFY_TIME, Common.parseStrDateTimeToMills(dateTime, Constant.DATE_TIME_FORMAT));
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_CREATED_TIME,
                Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), Constant.DATE_TIME_FORMAT));
        Log.d("test", Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), Constant.DATE_TIME_FORMAT) + "");

        long result;
        result = mDatabase.insertRecord(NoteDatabase.TBL_NOTE, valuesNote);

        if (result > -1) {
            Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
            Intent intentRefreshHomeActivity = new Intent(Constant.ACTION_REFRESH_LIST);
            sendBroadcast(intentRefreshHomeActivity);
        }

        for (int i = 0; i < lstImagePath.size(); i++) {
            saveImageToDatabase(lastNoteId + 1, lstImagePath.get(i));
        }
        onBackPressed();
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
                        new DatePickerDialog(this, onDateSetListener, year, month, day).show();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.sp_choose_time:
                switch (position) {
                    case 3:
                        new TimePickerDialog(this, onTimeSetListener, hour, minutes, true).show();
                        break;
                    default:
                        strTimeSelected = spTime.getSelectedItem().toString();
                        break;
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            strDateSelected = year + "-" + (month + 1) + "-" + dayOfMonth;
            lstDate.remove(3);
            lstDate.add(strDateSelected);
            spDateAdapter.notifyDataSetChanged();
        }
    };

    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            strTimeSelected = hourOfDay + ":" + minute;
            lstTime.remove(3);
            lstTime.add(strTimeSelected);
            spTimeAdapter.notifyDataSetChanged();
        }
    };


}