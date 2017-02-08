package nhannt.note.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.activity.DetailActivity;
import nhannt.note.model.Note;
import nhannt.note.utils.Common;
import nhannt.note.utils.Constant;

/**
 * Created by IceMan on 1/20/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static final String KEY_NOTE_TO_NOTIFY = "key_note_notify";

    @Override
    public void onReceive(Context context, Intent intent) {
        Note itemNoteReceived;
        if (intent != null) {
            itemNoteReceived = (Note) intent.getExtras().getSerializable(KEY_NOTE_TO_NOTIFY);
            //Show notification
            NotificationCompat.Builder mBuilder
                    = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(itemNoteReceived.getTitle())
                    .setAutoCancel(true);
            Intent intentToDetailActivity = new Intent(context, DetailActivity.class);
            ArrayList<Note> lstNote = new ArrayList<>();
            lstNote.add(itemNoteReceived);
            intentToDetailActivity.putExtra(Constant.KEY_LIST_NOTE, lstNote);
            intentToDetailActivity.putExtra(Constant.KEY_NOTE_POSITION, 0);
            intentToDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Common.writeLog("color", itemNoteReceived.getColor() + "");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, itemNoteReceived.getId(), intentToDetailActivity, 0);
            mBuilder.setContentIntent(pendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(itemNoteReceived.getId(), mBuilder.build());
        }
    }
}
