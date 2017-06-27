package com.example.achilleas.memoapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Memo newMemo;                                        //Memo object
    public String memo, date, time, mode;                       //memo text, date, time, mode entries
    private ArrayList<Memo> arrayList = new ArrayList<Memo>();  //ArrayList of Memo objects
    private MemoAdapter adapter;                                //The adapter for the ArrayList
    DatabaseHandler database;                               //DataBaseHandler object to save and restore data
    public int add_requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new DatabaseHandler(this);   //initialize DatabaseHandler

        //checks if database is empty
        if(database.getMemosCount()!=0){
            arrayList = database.getAllMemos(); //if not empty get Memos from database
        }else{
            arrayList = new ArrayList<Memo>();  //if empty create new ArrayList
        }

        ListView listView = (ListView)findViewById(R.id.MemoList);  //create listView

        //check if an instance has been saved(when phone changes orientation)
        if(savedInstanceState!=null){
            arrayList = savedInstanceState.getParcelableArrayList("key");   //if orientation has been changed then recover data
        }
        adapter = new MemoAdapter(this,R.layout.list_item, arrayList);  //create adapter
        listView.setAdapter(adapter);                                   //attach adapter to listView
    }

    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }
    //create add button on actionBar
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //checks if add button is selected
        if(id == R.id.Add){
            Intent intent = new Intent(this, MemoHandler.class); //go to MemoHandler class
            startActivityForResult(intent,add_requestCode);
            return true;
        }else if(id == R.id.Clear){
            //if clear button is selected create dialog to ask user whether he/she wants to delete all memos
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            dialogBuilder.setTitle(R.string.clear_dialog_title);
            dialogBuilder.setMessage(R.string.clear_dialog_message);
            dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    database.deleteAllMemos();  //delete all memos from database
                    adapter.clear();            //delete all memos from adapter
                    adapter.notifyDataSetChanged(); //notify adapter that changes have been made
                }
            });
            dialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){

                }
            });
            dialogBuilder.create().show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("key", arrayList);    //save instance state for when orientation changes
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        arrayList = savedInstanceState.getParcelableArrayList("key");   //restores instance state when orientation changes
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        //checks if Done is selected in MemoHandler
        if(resultCode==Activity.RESULT_OK){
            //checks if Add is selected
            if(requestCode==add_requestCode){
                if(intent!=null){
                    //get data from MemoHandler class
                    memo = intent.getStringExtra("memo");
                    date = intent.getStringExtra("date");
                    time = intent.getStringExtra("time");
                    mode = intent.getStringExtra("mode");
                    //create new Memo object with the data collected above
                    newMemo = new Memo(memo, date, time, mode);
                    //add Memo object to database
                    database.addMemo(newMemo);
                    //add Memo object to adapter
                    adapter.add(newMemo);
                    adapter.notifyDataSetChanged();
                }
            //if Edit is selected
            }else{
                //get data from MemoHandler when edit is selected
                memo = intent.getStringExtra("memo");
                date = intent.getStringExtra("date");
                time = intent.getStringExtra("time");
                mode = intent.getStringExtra("mode");
                //get the position of the item we have edited
                int editPosition = intent.getIntExtra("position", -1);
                newMemo = new Memo(memo, date, time, mode);

                if(editPosition!=-1){
                    //delete item at that position from database
                    database.deleteMemo(adapter.getItem(editPosition));
                    //add the new Memo object to the database
                    database.addMemo(newMemo);
                    //remove item at that position from adapter
                    adapter.remove(adapter.getItem(editPosition));
                    //add item to the adapter at that same position
                    adapter.insert(newMemo, editPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }


}
