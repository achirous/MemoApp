package com.example.achilleas.memoapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


//This is the activity where the user inputs the memo info
public class MemoHandler extends AppCompatActivity {
    ImageButton dateButton, timeButton;
    int year, month, day, position;
    int hour, minute;
    String memo, date, time, mode;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    public EditText memoText;
    public EditText dateText;
    public EditText timeText;
    Spinner spinner;
    ArrayAdapter<CharSequence> spinner_adapter;
    public final int ALARM_REQUEST_CODE = 123;

    //date picker dialog to let the user select a date from a calendar
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int this_year, int this_month, int this_day){
            year = this_year;   //gets year selected
            month = this_month + 1; //gets month selected
            day = this_day;     //gets day selected

            //sets the ediText to date selected
            EditText editDateText = (EditText) findViewById(R.id.Date);
            editDateText.setText(String.format("%02d/%02d/%04d", day, month, year));
        }
    };
    //time picker dialog to let the user select a time
    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int this_hour, int this_minute) {
            hour = this_hour;   //gets hour selected
            minute = this_minute;   //gets minute selected

            //sets the editText to the time selected
            EditText editTimeText = (EditText) findViewById(R.id.Time);
            editTimeText.setText(String.format("%02d:%02d", hour, minute));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();   //get Instance of Calendar class
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        //gets the year month and day values from the calendar
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDialogOnClick();
        //create a spinner
        spinner = (Spinner)findViewById(R.id.spinner);
        //create a spinner adapter
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.modes, android.R.layout.simple_spinner_item);
        //set drop down view for the spinner
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //attach adapter to the spinner
        spinner.setAdapter(spinner_adapter);
        //set a click listener for the items in the drop down view
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //get the String value of the item selected
                mode = spinner.getSelectedItem().toString();
                //checks if the item selected is of value Urgent
                if(mode.equals(spinner_adapter.getItem(2).toString())){
                    //creates dialog to ask if user wants to set an alarm
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MemoHandler.this, R.style.MyDialogTheme);
                    dialog.setTitle(R.string.alarm_setter_title);
                    dialog.setMessage(R.string.alram_setter_message);
                    //if yes button is clicked then it sets an alarm
                    dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent intent = new Intent(MemoHandler.this, Alarm.class);
                            intent.putExtra("notificationMemo", memo);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MemoHandler.this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT| Intent.FILL_IN_DATA);
                            alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                            Toast.makeText(MemoHandler.this, R.string.toast_alarm_set_message, Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){

                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mode = spinner.getSelectedItem().toString();
            }
        });

        //get the editText views for memo date and time
        memoText = (EditText)findViewById(R.id.MemoText);
        dateText = (EditText)findViewById(R.id.Date);
        timeText = (EditText)findViewById(R.id.Time);
        spinner = (Spinner)findViewById(R.id.spinner);

        //set a click listener for dateText and timeText
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        //set home button as back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent!=null){
            //gets the extras from when the Edit button is clicked
            memo = intent.getStringExtra("extractMemo");
            date = intent.getStringExtra("extractDate");
            time = intent.getStringExtra("extractTime");
            position = intent.getIntExtra("position", -1);
            int requestCode = intent.getIntExtra("requestCode", -2);
            //checks if edit button was clicked
            if(requestCode == 2){
                //sets the values passed by the MemoAdapter to the ediText views
                memoText.setText(memo);
                dateText.setText(date);
                timeText.setText(time);
            }
        }


    }

    public boolean onOptionsItemSelected(MenuItem item){
        //get the id of the item that was clicked
        int id = item.getItemId();
        //get memo that was typed in the memoText
        memo = memoText.getText().toString();
        //gets the date and time selected from the Date and Time Picker respectively
        date = String.format("%02d/%02d/%04d", day, month, year);
        time = String.format("%02d:%02d", hour, minute);
        if(id==R.id.Done){
            //if done is clicked then start a new intent to the MainActivity
            Intent doneIntent = new Intent(getApplicationContext(), MainActivity.class);
            if(position!=-1){
                doneIntent.putExtra("position", position);
            }
            //pass the memo, date, time and mode values to the MainActivity to populate the ListView
            doneIntent.putExtra("memo", memo);
            doneIntent.putExtra("date", date);
            doneIntent.putExtra("time", time);
            doneIntent.putExtra("mode", mode);
            setResult(RESULT_OK, doneIntent);
            finish();
            Toast.makeText(this, R.string.toast_add_memo, Toast.LENGTH_LONG).show();
            return true;
        }else if(id==R.id.Cancel){
            //if cancel button is clicked just return to MainActivity without sending any values
            Intent cancelIntent = new Intent (getApplicationContext(), MainActivity.class);
            setResult(RESULT_CANCELED, cancelIntent);
            finish();
            return true;
        }else{
            //if something else is clicked go back to MainActivity similarly to cancel button
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

    }
    //create buttons on the action bar
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    //set click listeners for date and time buttons to spawn their respective dialogs
    public void showDialogOnClick(){
        dateButton = (ImageButton) findViewById(R.id.calendar_button);
        timeButton = (ImageButton) findViewById(R.id.time_button);
        View.OnClickListener dateDialog = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showDialog(DATE_DIALOG_ID);
            }

        };
        View.OnClickListener timeDialog = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showDialog(TIME_DIALOG_ID);
            }
        };

        timeButton.setOnClickListener(timeDialog);
        dateButton.setOnClickListener(dateDialog);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DATE_DIALOG_ID){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateListener, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);   //make previous dates un-selectable on calendar
            return datePickerDialog;
        }
        if(id == TIME_DIALOG_ID){
            return new TimePickerDialog(this, timeListener, hour, minute, false);
        }
        return null;
    }
}
