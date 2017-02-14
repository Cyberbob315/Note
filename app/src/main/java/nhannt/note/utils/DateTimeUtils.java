package nhannt.note.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A class with methods to handle with date and time like convert date from string format
 * to date format,parse date format to millis,..
 */

public class DateTimeUtils {

    /**
     * @return current day of week in string
     */
    public static String getCurrentDayOfWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        return sdf.format(d);
    }

    /**
     * Parse date in mills to string format
     * @param dateInMillis date time in mills format
     * @param format Format of date string
     * @return Date formatted in string
     */
    public static String getDateStrFromMilliseconds(long dateInMillis, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(new Date(dateInMillis));
    }

    /**
     * Parse date in string format to millisecond
     * @param strDateTime Date in string
     * @param dateTimeFormat Format of date in string
     * @return Date in millisecond
     */
    public static long parseStrDateTimeToMills(String strDateTime, String dateTimeFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat, Locale.getDefault());
        long mills = 0;
        try {
            Date d = sdf.parse(strDateTime);
            mills = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mills;
    }

    /**
     * Get current date time in string
     * @param strFormat Format of date time
     * @return Current date time in string
     */
    public static String getCurrentDateTimeInStr(String strFormat) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.getDefault());
        return sdf.format(c.getTime());
    }
}
