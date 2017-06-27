package com.example.achilleas.memoapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

//this is the activity called by the Alarm class that creates a ringtone and a dialog that gives a reminder to the user
public class MemoNotification extends AppCompatActivity {
    public String memoToDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_notification);
        Intent notifyIntent = getIntent();
        memoToDisplay = notifyIntent.getStringExtra("notificationText");    //gets the memo to be displayed from the alarm class

        //creates and plays a ringtone
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();

        //creates and displays a dialog with the memo obtained above
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MemoNotification.this, R.style.MyDialogTheme);
        dialogBuilder.setTitle(R.string.urgent_reminder_title);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage(getString(R.string.urgent_reminder_message) + memoToDisplay);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ringtone.stop();
                finish();
            }
        });
        dialogBuilder.create().show();
    }
}
