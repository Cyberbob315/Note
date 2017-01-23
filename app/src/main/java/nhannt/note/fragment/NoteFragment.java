package nhannt.note.fragment;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import nhannt.note.R;
import nhannt.note.activity.NewActivity;
import nhannt.note.adapter.ImageAdapter;
import nhannt.note.database.NoteDatabase;
import nhannt.note.model.Note;
import nhannt.note.receiver.AlarmReceiver;
import nhannt.note.utils.Common;
import nhannt.note.utils.Constant;
import nhannt.note.utils.GridSpacingItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment implements View.OnClickListener, Spinner.OnItemSelectedListener,TextWatcher {

    public static final String KEY_NOTE_DETAIL = "key_note_detail";
    public static final String KEY_LAST_NOTE_ID = "key_last_note_id";
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;
    private View mView;
    private Note mItemNote;
    private boolean isFirstDateSpSelected = true;
    private boolean isFirstTimeSpSelected = true;
    private boolean isChanged = false;
    private int selectedColor;
    private int lastNoteId;

    private NoteDatabase mDatabase = NoteDatabase.getInstance(getActivity());
    private String strDateSelected = "", strTimeSelected = "";
    private AlertDialog alertDialogPhoto, alertDialogColor;
    private AlarmManager alarmManager;

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


    public NoteFragment() {
        // Required empty public constructor
    }

    public static NoteFragment newInstance(Note mItemNote, int lastNoteId) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_NOTE_DETAIL, mItemNote);
        args.putInt(KEY_LAST_NOTE_ID, lastNoteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mItemNote = (Note) bundle.getSerializable(KEY_NOTE_DETAIL);
            lastNoteId = bundle.getInt(KEY_LAST_NOTE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note, container, false);
        initControls();
        initEvents();
        setTextViewAndDateTime();
        setupSpinnerDateNSpinnerTime();
        setUpImageList();

        return mView;
    }

    private void setTextViewAndDateTime() {
        etContent.setText(mItemNote.getContent());
        etTitle.setText(mItemNote.getTitle());
        tvCurrentTime.setText(Common.getDateStrFromMilliseconds(mItemNote.getCreatedDate(), Constant.DATE_FORMAT));
        strDateSelected = Common.getDateStrFromMilliseconds(mItemNote.getNotifyDate(), Constant.DATE_FORMAT);
        strTimeSelected = Common.getDateStrFromMilliseconds(mItemNote.getNotifyDate(), Constant.TIME_FORMAT);
        Common.writeLog("date", mItemNote.getNotifyDate() + "");
        Common.writeLog("date", strDateSelected + "/" + strTimeSelected);

        selectedColor = mItemNote.getColor();
        ivBackGround.setBackgroundColor(mItemNote.getColor());

    }

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

    private void initEvents() {
        tvAlarm.setOnClickListener(this);
        btCloseDateTime.setOnClickListener(this);
        spDate.setOnItemSelectedListener(this);
        spTime.setOnItemSelectedListener(this);
        etContent.addTextChangedListener(this);
        etTitle.addTextChangedListener(this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_note, menu);
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
                if(isChanged) {
                    saveNoteToDatabase();
                    cancelNotify();
                    updateNotify();
                }else{
                    Toast.makeText(getActivity(), getString(R.string.nothing_change), Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                break;
            case R.id.bt_new_note_menu:
                Intent intent = new Intent(getActivity(), NewActivity.class);
                intent.putExtra(Constant.KEY_LAST_NOTE_ID, lastNoteId);
                startActivity(intent);
                break;
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.bt_share_menu:
                shareNote();
                break;
            case R.id.bt_delete_menu:
                showConfirmDeleteNoteDialog(mItemNote.getId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotify() {
        alarmManager = (AlarmManager) getActivity().getSystemService(AppCompatActivity.ALARM_SERVICE);
        Intent intentToAlarmClass = new Intent(getActivity(), AlarmReceiver.class);
        Note itemNoteToNotify;
        long createdDateTime = Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long notifyDateTime = Common.parseStrDateTimeToMills(strDateSelected + " " + strTimeSelected, NoteDatabase.SQL_DATE_FORMAT);
        Common.writeLog("Notify", notifyDateTime + "");
        itemNoteToNotify = new Note(mItemNote.getId(), etTitle.getText().toString(), etContent.getText().toString(),
                selectedColor, createdDateTime, notifyDateTime);
        intentToAlarmClass.putExtra(AlarmReceiver.KEY_NOTE_TO_NOTIFY, itemNoteToNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), itemNoteToNotify.getId(),
                intentToAlarmClass, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notifyDateTime, pendingIntent);
    }

    private void cancelNotify() {
        alarmManager = (AlarmManager) getActivity().getSystemService(AppCompatActivity.ALARM_SERVICE);
        Intent intentToAlarmClass = new Intent(getActivity(), AlarmReceiver.class);
        Note itemNoteToNotify;
        long createdDateTime = Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT);
        long notifyDateTime = Common.parseStrDateTimeToMills(strDateSelected + " " + strTimeSelected, NoteDatabase.SQL_DATE_FORMAT);
        Common.writeLog("Notify", notifyDateTime + "");

        itemNoteToNotify = new Note(mItemNote.getId(), etTitle.getText().toString(), etContent.getText().toString(), selectedColor,
                createdDateTime, notifyDateTime);
        intentToAlarmClass.putExtra(AlarmReceiver.KEY_NOTE_TO_NOTIFY, itemNoteToNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), itemNoteToNotify.getId(),
                intentToAlarmClass, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
        alarmManager.cancel(pendingIntent);
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
                selectedColor = ContextCompat.getColor(getActivity(), R.color.blue);
                ivBackGround.setBackgroundColor(selectedColor);
                isChanged = true;
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_red:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.colorRed);
                ivBackGround.setBackgroundColor(selectedColor);
                isChanged = true;
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_yellow:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.yellow);
                ivBackGround.setBackgroundColor(selectedColor);
                isChanged = true;
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_green:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.green);
                ivBackGround.setBackgroundColor(selectedColor);
                isChanged = true;
                alertDialogColor.dismiss();
                break;
            case R.id.iv_color_white:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.white);
                ivBackGround.setBackgroundColor(selectedColor);
                isChanged = true;
                alertDialogColor.dismiss();
                break;
        }
    }

    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = etContent.getText().toString();
        String shareSub = etTitle.getText().toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
    }

    private void saveNoteToDatabase() {
        String dateTime = strDateSelected + " " + strTimeSelected;
        ContentValues valuesNote = new ContentValues();
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_CONTENT, etContent.getText().toString().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_TITLE, etTitle.getText().toString().trim());
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTE_COLOR, selectedColor + "");
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_NOTIFY_TIME, Common.parseStrDateTimeToMills(dateTime, NoteDatabase.SQL_DATE_FORMAT));
        valuesNote.put(NoteDatabase.TBL_NOTE_COLUMN_CREATED_TIME,
                Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT));
        Log.d("test", Common.parseStrDateTimeToMills(Common.getCurrentDateTimeInStr(NoteDatabase.SQL_DATE_FORMAT), NoteDatabase.SQL_DATE_FORMAT) + "");

        long result;
        result = mDatabase.updateRecord(NoteDatabase.TBL_NOTE, valuesNote, NoteDatabase.TBL_NOTE_COLUMN_ID,
                new String[]{mItemNote.getId() + ""});
        if (result > -1) {
            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            Intent intentRefreshHomeActivity = new Intent(Constant.ACTION_REFRESH_LIST);
            getActivity().sendBroadcast(intentRefreshHomeActivity);
        }
        mDatabase.deleteRecord(NoteDatabase.TBL_IMAGE, NoteDatabase.TBL_IMAGE_COLUMN_NOTE_ID,
                new String[]{mItemNote.getId() + ""});
        for (int i = 0; i < lstImagePath.size(); i++) {
            saveImageToDatabase(lstImagePath.get(i));
        }
        getActivity().onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        isChanged = true;
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

    private void showPhotoChooserDialog() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.photos_chooser_dialog, null);

        llTakePhotos = (LinearLayout) view.findViewById(R.id.ll_take_photos);
        llChoosePhotos = (LinearLayout) view.findViewById(R.id.ll_choose_photos);
        llTakePhotos.setOnClickListener(this);
        llChoosePhotos.setOnClickListener(this);

        alertDialogPhoto = new AlertDialog.Builder(getActivity()).create();
        alertDialogPhoto.setTitle(getString(R.string.insert_photo));
        alertDialogPhoto.setIcon(R.drawable.ic_camera);
        alertDialogPhoto.setView(view);
        alertDialogPhoto.show();
    }

    private void showColorChooserDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.color_chooser, null);
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
        alertDialogColor = new AlertDialog.Builder(getActivity()).create();
        alertDialogColor.setTitle(getString(R.string.select_color));
        alertDialogColor.setView(view);
        alertDialogColor.show();
    }

    private void saveImageToDatabase(String path) {
        ContentValues valuesImage = new ContentValues();
        valuesImage.put(NoteDatabase.TBL_IMAGE_COLUMN_NOTE_ID, mItemNote.getId());
        valuesImage.put(NoteDatabase.TBL_IMAGE_COLUMN_PATH, path);
        mDatabase.insertRecord(NoteDatabase.TBL_IMAGE, valuesImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    String path = Common.saveImage(getActivity(), photo);
                    lstImagePath.add(path);
                    mImageAdapter.notifyDataSetChanged();
                    isChanged = true;
                }
                break;
            case GALLERY_REQUEST:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String selectedImagePath = Common.getRealPathFromURI(getActivity(), selectedImage);
                    Log.d("image", selectedImagePath);
                    lstImagePath.add(selectedImagePath);
                    mImageAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void setUpImageList() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        rvImageList.setLayoutManager(layoutManager);
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(4, 40, false);
        rvImageList.addItemDecoration(itemDecoration);
        new LoadImageOfNote(getActivity()).execute();
    }

    private void setupSpinnerDateNSpinnerTime() {
        //Spinner Date
        lstDate = new ArrayList<>();
        lstDate.add(getString(R.string.today));
        lstDate.add(getString(R.string.tomorrow));
        lstDate.add(getString(R.string.next) + " " + Common.getCurrentDayOfWeek());
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
        lstTime.remove(3);
        lstTime.add(strTimeSelected);
        spTime.setSelection(3);
        lstDate.remove(3);
        lstDate.add(strDateSelected);
        spDate.setSelection(3);
        isChanged = false;
    }

    private ArrayList<String> getImageFromDatabase() {
        Cursor result = mDatabase.rawQuery(NoteDatabase.QUERY_GET_IMAGE_WITH_NOTE_ID + mItemNote.getId());
        ArrayList<String> list = new ArrayList<>();
        if (result != null && result.moveToFirst()) {
            do {
                String path = result.getString(result.getColumnIndex(NoteDatabase.TBL_IMAGE_COLUMN_PATH));
                list.add(path);
            } while (result.moveToNext());
        }
        return list;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class LoadImageOfNote extends AsyncTask<Void, Void, Void> {

        Context mContext;

        public LoadImageOfNote(Context mContext) {
            this.mContext = mContext;
        }

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
            mImageAdapter = new ImageAdapter(mContext, lstImagePath);
            rvImageList.setAdapter(mImageAdapter);
        }
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

    private void showConfirmDeleteNoteDialog(final int noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.warning));
        builder.setMessage(getString(R.string.delete_note_question));
        builder.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.deleteRecord(NoteDatabase.TBL_NOTE, NoteDatabase.TBL_NOTE_COLUMN_ID, new String[]{noteId + ""});
                Intent intent = new Intent(Constant.ACTION_REFRESH_LIST);
                getActivity().sendBroadcast(intent);
                dialog.dismiss();
                cancelNotify();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
