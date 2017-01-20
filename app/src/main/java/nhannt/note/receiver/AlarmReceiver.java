package nhannt.note.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import nhannt.note.R;
import nhannt.note.activity.DetailActivity;
import nhannt.note.model.Note;
import nhannt.note.utils.Common;

/**
 * Created by IceMan on 1/20/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static final String KEY_NOTE_TO_NOTIFY = "key_note_notify";

    @Override
    public void onReceive(Context context, Intent intent) {
        Note itemNoteReceived;
        Common.writeLog("NOTIFY");
        if (intent != null) {
            itemNoteReceived = (Note) intent.getExtras().getSerializable(KEY_NOTE_TO_NOTIFY);
            //Show notification
            NotificationCompat.Builder mBuilder
                    = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(itemNoteReceived.getTitle())
                    .setAutoCancel(true);
            Intent intentToDetailActivity = new Intent(context, DetailActivity.class);
            intentToDetailActivity.putExtra(DetailActivity.KEY_IS_CREATE_NEW, false);
            intentToDetailActivity.putExtra(DetailActivity.KEY_NOTE, itemNoteReceived);
            intentToDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToDetailActivity, 0);
            mBuilder.setContentIntent(pendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
