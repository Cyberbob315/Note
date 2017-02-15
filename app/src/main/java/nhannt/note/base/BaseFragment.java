package nhannt.note.base;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import nhannt.note.R;
import nhannt.note.activity.HostActivity;
import nhannt.note.adapter.ImageAdapter;
import nhannt.note.database.NoteDatabase;
import nhannt.note.database.dao.ImageHelper;
import nhannt.note.database.dao.NoteHelper;
import nhannt.note.model.Note;
import nhannt.note.receiver.AlarmReceiver;
import nhannt.note.utils.Common;
import nhannt.note.utils.Constant;
import nhannt.note.utils.DateTimeUtils;
import nhannt.note.utils.GridSpacingItemDecoration;


/**
 * An abstract fragment which contains all methods use to display,save note for EditNoteFragment and NewNoteFragment
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener, Spinner.OnItemSelectedListener {

    private Context mContext;
    protected final NoteHelper mNoteHelper = NoteHelper.getInstance(getActivity());
    protected final ImageHelper mImageHelper = ImageHelper.getInstance(getActivity());
    private View mView;
    protected Note mItemNote;
    protected int lastNoteId, selectedColor;
    protected String strDateSelected = "", strTimeSelected = "";
    private AlertDialog alertDialogPhoto, alertDialogColor;
    private AlarmManager alarmManager;
    protected RecyclerView rvImageList;
    protected EditText etTitle, etContent;
    protected TextView tvCurrentTime;
    private TextView tvAlarm;
    private LinearLayout llDateTime;
    protected AppCompatSpinner spDate, spTime;
    private ImageView btCloseDateTime;
    protected ImageView ivBackGround;
    private ArrayAdapter spDateAdapter, spTimeAdapter;
    protected ArrayList<String> lstDate, lstTime;
    protected ArrayList<String> lstImagePath = new ArrayList<>();
    protected ImageAdapter mImageAdapter;
    protected boolean isFirstTimeSpSelected, isFirstDateSpSelected;
    private ViewGroup root;


    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {

            mItemNote = (Note) bundle.getSerializable(Constant.KEY_NOTE_DETAIL); //Get selected note from home list
            lastNoteId = bundle.getInt(Constant.KEY_LAST_NOTE_ID); // Get last note id in database to determine next note id to save
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        root = container;
        return inflater.inflate(getLayout(), container, false);
    }

    protected abstract int getLayout();


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_note, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingToolbar(view);
        mView = view;
        initControls();
        initEvents();
        setUpImageList();
        setupSpinnerDateNSpinnerTime();
        setUpTextViewAndDateTime();

    }


    protected abstract void setUpTextViewAndDateTime();

    private void initEvents() {
        tvAlarm.setOnClickListener(this);
        btCloseDateTime.setOnClickListener(this);
        spDate.setOnItemSelectedListener(this);
        spTime.setOnItemSelectedListener(this);
    }

    @SuppressWarnings("unchecked")
    private void setupSpinnerDateNSpinnerTime() {
        //Spinner Date
        lstDate = new ArrayList<>();
        lstDate.add(getString(R.string.today));
        lstDate.add(getString(R.string.tomorrow));
        lstDate.add(getString(R.string.next) + " " + DateTimeUtils.getCurrentDayOfWeek());
        lstDate.add(getString(R.string.other));
        spDateAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_gallery_item, lstDate);
        spDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(spDateAdapter);

        //Spinner Time
        lstTime = new ArrayList<>();
        lstTime.add(getString(R.string.sp_time_slot1));
        lstTime.add(getString(R.string.sp_time_slot2));
        lstTime.add(getString(R.string.sp_time_slot3));
        lstTime.add(getString(R.string.other));
        spTimeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_gallery_item, lstTime);
        spTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(spTimeAdapter);
    }

    private void setUpImageList() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        rvImageList.setLayoutManager(layoutManager);
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(4, 30, false);
        rvImageList.addItemDecoration(itemDecoration);
        showImage(lstImagePath);
    }

    protected abstract void showImage(ArrayList lstImage);


    protected abstract int getIdNoteToSave();

    private void saveNoteToDatabase() {
        String dateTime = strDateSelected + " " + strTimeSelected;
        long dateCreated = DateTimeUtils.parseStrDateTimeToMills(DateTimeUtils.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long dateNotify = DateTimeUtils.parseStrDateTimeToMills(dateTime, NoteDatabase.SQL_DATE_FORMAT);
        Note itemNoteToSave = new Note(getIdNoteToSave(), etTitle.getText().toString(), etContent.getText().toString(),
                selectedColor, dateCreated, dateNotify);
        saveNote(itemNoteToSave);
    }

    protected abstract void saveNote(Note itemNoteToSave);


    private void initControls() {
        ivBackGround = (ImageView) mView.findViewById(R.id.iv_background_detail);
        rvImageList = (RecyclerView) mView.findViewById(R.id.rv_image_list);
        etTitle = (EditText) mView.findViewById(R.id.et_title);
        etContent = (EditText) mView.findViewById(R.id.et_content);
        tvCurrentTime = (TextView) mView.findViewById(R.id.tv_current_time);
        tvAlarm = (TextView) mView.findViewById(R.id.tv_alarm);
        llDateTime = (LinearLayout) mView.findViewById(R.id.ll_date_time);
        spDate = (AppCompatSpinner) mView.findViewById(R.id.sp_choose_date);
        spTime = (AppCompatSpinner) mView.findViewById(R.id.sp_choose_time);
        btCloseDateTime = (ImageView) mView.findViewById(R.id.bt_close_date_time);

    }

    private void settingToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                if (getHomeAsUpIndicator() != 0) {
                    actionBar.setHomeAsUpIndicator(getHomeAsUpIndicator());
                }
                actionBar.setTitle(getActionbarName());
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
                setAlarm();
                break;
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.bt_new_note_menu:
                Intent intent = new Intent(mContext, HostActivity.class);
                intent.putExtra(Constant.KEY_LAST_NOTE_ID, lastNoteId);
                mContext.startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void setAlarm();

    //Set current note to notify
    protected void setToNotify() {
        alarmManager = (AlarmManager) mContext.getSystemService(AppCompatActivity.ALARM_SERVICE);
        Intent intentToAlarmClass = new Intent(getActivity(), AlarmReceiver.class);
        Note itemNoteToNotify;
        long createdDateTime = DateTimeUtils.parseStrDateTimeToMills(DateTimeUtils.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long notifyDateTime = DateTimeUtils.parseStrDateTimeToMills(strDateSelected + " " + strTimeSelected, NoteDatabase.SQL_DATE_FORMAT);
        Common.writeLog("Notify", notifyDateTime + "");
        itemNoteToNotify = new Note(getIdNoteToSave(), etTitle.getText().toString(), etContent.getText().toString(),
                selectedColor, createdDateTime, notifyDateTime);
        intentToAlarmClass.putExtra(AlarmReceiver.KEY_NOTE_TO_NOTIFY, itemNoteToNotify); //push note item to intent

        //Create pending intent to broadcast for Alarm manager,use noteId to create an unique alarm manager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), getIdNoteToSave(),
                intentToAlarmClass, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notifyDateTime, pendingIntent);

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.CAMERA_REQUEST:
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data"); //get bitmap of photo just taken
                    String path = Common.saveImage(getActivity(), photo); //save to storage then get path
                    lstImagePath.add(path);
                    mImageAdapter.refreshList((ArrayList<String>) lstImagePath.clone());
                }
                break;
            case Constant.GALLERY_REQUEST:
                if (data != null) {
                    Uri selectedImage = data.getData(); //get uri of photo just selected
                    String selectedImagePath = Common.getRealPathFromURI(getActivity(), selectedImage); //get path from uri
                    Log.d("image", selectedImagePath);
                    lstImagePath.add(selectedImagePath);
                    mImageAdapter.refreshList((ArrayList<String>) lstImagePath.clone());
                }
                break;
        }
    }


    private String getActionbarName() {
        return getActivity().getResources().getString(R.string.app_name);
    }

    protected abstract int getHomeAsUpIndicator();

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
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
                startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST);
                alertDialogPhoto.dismiss();
                break;
            case R.id.ll_choose_photos:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, Constant.GALLERY_REQUEST);
                alertDialogPhoto.dismiss();
                break;
            case R.id.iv_color_blue:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.blue);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_red:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.colorRed);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_yellow:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.yellow);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_green:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.green);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_white:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.white);
                ivBackGround.setBackgroundColor(selectedColor);
                alertDialogColor.dismiss();
                break;
        }
    }

    private final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            strDateSelected = year + "-" + (month + 1) + "-" + dayOfMonth;
            lstDate.remove(3);
            lstDate.add(strDateSelected);
            spDateAdapter.notifyDataSetChanged();
        }
    };


    private final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            strTimeSelected = hourOfDay + ":" + minute;
            lstTime.remove(3);
            lstTime.add(strTimeSelected);
            spTimeAdapter.notifyDataSetChanged();
        }
    };

    private void showPhotoChooserDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.photos_chooser_dialog, root, false);  //inflate layout for dialog

        //find view from layout inflated to handle click event
        LinearLayout llTakePhotos = (LinearLayout) view.findViewById(R.id.ll_take_photos);
        LinearLayout llChoosePhotos = (LinearLayout) view.findViewById(R.id.ll_choose_photos);
        llTakePhotos.setOnClickListener(this);
        llChoosePhotos.setOnClickListener(this);

        alertDialogPhoto = new AlertDialog.Builder(getActivity()).create();   //build dialog
        alertDialogPhoto.setTitle(getString(R.string.insert_photo));
        alertDialogPhoto.setIcon(R.drawable.ic_camera);
        alertDialogPhoto.setView(view);
        alertDialogPhoto.show(); //show dialog
    }

    private void showColorChooserDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.color_chooser, root, false);
        ImageView btnRed = (ImageView) view.findViewById(R.id.iv_color_red);
        ImageView btnBlue = (ImageView) view.findViewById(R.id.iv_color_blue);
        ImageView btnYellow = (ImageView) view.findViewById(R.id.iv_color_yellow);
        ImageView btnGreen = (ImageView) view.findViewById(R.id.iv_color_green);
        ImageView btnWhite = (ImageView) view.findViewById(R.id.iv_color_white);

        btnWhite.setOnClickListener(this);
        btnRed.setOnClickListener(this);
        btnBlue.setOnClickListener(this);
        btnYellow.setOnClickListener(this);
        btnGreen.setOnClickListener(this);
        alertDialogColor = new AlertDialog.Builder(getActivity()).create();
        alertDialogColor.setTitle(getString(R.string.select_color));
        alertDialogColor.setView(view);
        alertDialogColor.show();
    }


    protected void cancelNotify() {
        alarmManager = (AlarmManager) getActivity().getSystemService(AppCompatActivity.ALARM_SERVICE);
        Intent intentToAlarmClass = new Intent(getActivity(), AlarmReceiver.class);
        Note itemNoteToNotify;
        long createdDateTime = DateTimeUtils.parseStrDateTimeToMills(DateTimeUtils.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long notifyDateTime = DateTimeUtils.parseStrDateTimeToMills(strDateSelected + " " + strTimeSelected, NoteDatabase.SQL_DATE_FORMAT);
        Common.writeLog("Notify", notifyDateTime + "");
        itemNoteToNotify = new Note(getIdNoteToSave(), etTitle.getText().toString(), etContent.getText().toString(), selectedColor,
                createdDateTime, notifyDateTime);
        intentToAlarmClass.putExtra(AlarmReceiver.KEY_NOTE_TO_NOTIFY, itemNoteToNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), getIdNoteToSave(),
                intentToAlarmClass, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
        alarmManager.cancel(pendingIntent);
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
                            new DatePickerDialog(getActivity(), onDateSetListener, year, month, day).show();
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
                            new TimePickerDialog(getActivity(), onTimeSetListener, hour, minutes, true).show();
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
