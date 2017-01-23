package nhannt.note.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by IceMan on 12/30/2016.
 */

public class Common {
    private static final String TAG = "NhanNT";

    public static void writeLog(String tag, String content) {
        Log.d(TAG + ": " + tag, content);
    }


    public static String getCurrentDateTimeInStr(String strFormat) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static Date convertStrToDate(String dateTimeInStr, String dateTmeFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateTmeFormat);
        Date date = null;
        try {
            date = df.parse(dateTimeInStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @return true when the caller API version is at least lollipop 21
     */
    public static boolean isMarshMallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static String getCurrentDayOfWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }

    public static String getDateStrFromMilliseconds(long dateInMillis, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(new Date(dateInMillis));
        return dateString;
    }

    public static long parseStrDateTimeToMills(String strDateTime, String dateTimeFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        long mills = 0;
        try {
            Date d = sdf.parse(strDateTime);
            mills = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mills;
    }

    public static String saveImage(Context mContext, Bitmap bitmap) {
        ContextWrapper wrapper = new ContextWrapper(mContext);
        File file = wrapper.getDir("Images", MODE_PRIVATE);
        file = new File(file, "Image-" + getCurrentDateTimeInStr("yyyy-MM-dd$HH:mm:ss") + ".jpg");
        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static String getRealPathFromURI(Context mContext, Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

}
