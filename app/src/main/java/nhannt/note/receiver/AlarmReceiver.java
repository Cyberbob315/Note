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
import nhannt.note.utils.Constant;

/**
 * A BroadcastReceiver which receive data when a note come to notify time,it will push
 * a notification with a pending intent to go to detail activity
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static final String KEY_NOTE_TO_NOTIFY = "key_note_notify";

    @Override
    public void onReceive(Context context, Intent intent) {
        Note itemNoteReceived;
        if (intent != null) {
            itemNoteReceived = (Note) intent.getExtras().getSerializable(KEY_NOTE_TO_NOTIFY);
            String title = "";
            int noteId = 0;
            //Build notification
            if (itemNoteReceived != null) {
                title = itemNoteReceived.getTitle();    //get title of note to show on notification
                noteId = itemNoteReceived.getId();      //get id of note to create an unique pending intent
            }
            NotificationCompat.Builder mBuilder
                    = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setAutoCancel(true);
            //Create intent and push note data for detail activity
            Intent intentToDetailActivity = new Intent(context, DetailActivity.class);
            ArrayList<Note> lstNote = new ArrayList<>();
            lstNote.add(itemNoteReceived);
            intentToDetailActivity.putExtra(Constant.KEY_LIST_NOTE, lstNote);
            intentToDetailActivity.putExtra(Constant.KEY_NOTE_POSITION, 0);
            intentToDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, noteId, intentToDetailActivity, 0);
            mBuilder.setContentIntent(pendingIntent);

            //Show notification
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(noteId, mBuilder.build());
        }
    }
}
