package com.example.achilleas.memoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//The custom adapter class that handles the ListView items
public class MemoAdapter extends ArrayAdapter<Memo> {
    private ArrayList<Memo> memo;
    Memo newMemo;
    Context context;
    DatabaseHandler database;
    public int edit_requestCode = 2;

    //MemoAdapter constructor
    public MemoAdapter(Context context,int resourceId, ArrayList<Memo> memo){
        super(context,resourceId,memo);
        this.memo = memo;
        this.context = context;

    }

    //get how many Memo objects are in the ArrayList
    public int getCount(){
        return memo.size();
    }

    //get Memo object at specific position of the arraylist
    public Memo getItem(int position){
        return memo.get(position);
    }

    //caches view references
    public static class ViewHolder{
        TextView memoView;  //TextView that displays the memo text
        TextView dateView;  //TextView that displays the date
        TextView timeView;  //TextView that displays the time
        TextView modeView;  //TextView that displays the mode
        ImageButton editButton; //Edit ImageButton
        ImageButton deleteButton;   //Delete ImageButton
        ImageView mode_icon;    //ImageView that displays the mode icon that changes colors depending on the mode
    }

    public View getView(int position, View convertView, ViewGroup parent){
        newMemo = memo.get(position);
        database = new DatabaseHandler(context);
        ViewHolder viewHolder;
        //checks if the view is empty
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false); //inflates the view if it is empty
            convertView.setClickable(true);
            convertView.setFocusable(true);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //gets the TextViews and the Buttons from the list_item.xml
        if(newMemo!=null){
            viewHolder.memoView = (TextView)convertView.findViewById(R.id.item_memo_text);
            viewHolder.dateView = (TextView)convertView.findViewById(R.id.item_date_text);
            viewHolder.timeView = (TextView)convertView.findViewById(R.id.item_time_text);
            viewHolder.modeView = (TextView)convertView.findViewById(R.id.mode_text);
            viewHolder.editButton = (ImageButton)convertView.findViewById(R.id.edit_button);
            viewHolder.deleteButton = (ImageButton)convertView.findViewById(R.id.delete_button);
            viewHolder.mode_icon = (ImageView)convertView.findViewById(R.id.mode_icon);
            convertView.setTag(viewHolder);
            //sets the data from the Memo object to the TextViews collected above
            if(viewHolder.memoView!=null){
                viewHolder.memoView.setText(newMemo.getMemo());
            }
            if(viewHolder.dateView!=null){
                viewHolder.dateView.setText(newMemo.getDate());
            }
            if(viewHolder.timeView!=null){
                viewHolder.timeView.setText(newMemo.getTime());
            }
            if(viewHolder.modeView!=null){
                viewHolder.modeView.setText(newMemo.getMode());
                String mode = viewHolder.modeView.getText().toString();
                //sets the color of the ImageView depending on what mode the user has selected
                if(mode.equals("Urgent")){
                    int color = context.getResources().getColor(R.color.urgent_mode_color);
                    viewHolder.mode_icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }else if(mode.equals("Important")){
                    int color = context.getResources().getColor(R.color.important_mode_color);
                    viewHolder.mode_icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }else{
                    int color = context.getResources().getColor(R.color.normal_mode_color);
                    viewHolder.mode_icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
            }
        }

        //sets a click listener for the edit button
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView listView = (ListView)view.getParent().getParent();
                final int position = listView.getPositionForView((View)view.getParent());
                //sets an intent to the MemoHandler class
                Intent intent = new Intent(context, MemoHandler.class);
                //gets the Memo object data
                String extractMemo = newMemo.getMemo();
                String extractDate = newMemo.getDate();
                String extractTime = newMemo.getTime();
                //passes Memo object data to MemoHandler as extras
                intent.putExtra("extractMemo", extractMemo);
                intent.putExtra("extractDate", extractDate);
                intent.putExtra("extractTime", extractTime);
                intent.putExtra("position", position);
                intent.putExtra("requestCode", edit_requestCode);   //passes the request code so that the MemoHandler can tell if edit was selected
                ((Activity)context).startActivityForResult(intent,edit_requestCode);
            }
        });
        //sets a click listener for the delete button
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView listView = (ListView)view.getParent().getParent();
                //get the position of the listview item whose delete button was clicked
                final int position = listView.getPositionForView((View)view.getParent());
                //delete the Memo object at that position from the database
                database.deleteMemo(getItem(position));
                //remove that Memo object from ArrayList
                memo.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

}
