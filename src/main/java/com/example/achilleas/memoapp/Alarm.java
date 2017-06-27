package com.example.achilleas.memoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/*this class receives the PendingIntent from the MemoHandler activity and creates a notification with the memo the user typed,
it then calls the MemoNotification activity to produce a dialog and ringtone.
 */
public class Alarm extends BroadcastReceiver {
    private final int NOTIFICATION_ID = 1234;

    public void onReceive(Context context, Intent intent){
        //gets the memo text passed from the MemoHandler
        String notificationText = intent.getStringExtra("notificationMemo");
        //creates a notification with the above memo text
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context).setContentTitle(context.getString(R.string.urgent_reminder_title))
                .setSmallIcon(R.drawable.mode_icon).setContentText(notificationText);
        NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        //creates intent to cal the MemoNotification class
        Intent notifyIntent = new Intent(context, MemoNotification.class);
        notifyIntent.putExtra("notificationText", notificationText);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notifyIntent);

        manager.notify(NOTIFICATION_ID, notification.build());  //displays the notification created above
    }

}
