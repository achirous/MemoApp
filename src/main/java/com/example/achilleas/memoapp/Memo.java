package com.example.achilleas.memoapp;

import android.os.Parcel;
import android.os.Parcelable;


//the Memo object class, represents every entry in the ListView
public class Memo implements Parcelable{
    public String memo;
    public String date;
    public String time;
    public String mode;

    //make this object parcelable in order to be able to save and restore instances
    public static final Parcelable.Creator<Memo> CREATOR = new Parcelable.Creator<Memo>(){
        public Memo createFromParcel(Parcel in){
            return new Memo(in);
        }
        public Memo[] newArray(int size){
            return new Memo[size];
        }
    };

    public int describeContents(){
        return 0;
    }

    //writes Memo data to parcel
    public void writeToParcel(Parcel out, int flags){
        out.writeString(memo);
        out.writeString(date);
        out.writeString(time);
        out.writeString(mode);
    }
    //empty constructor
    public Memo(){

    }
    //Memo object constructor
    public Memo(String memo, String date, String time, String mode){
        this.memo = memo;
        this.date = date;
        this.time = time;
        this.mode = mode;
    }
    //gets Memo data from parcel
    private Memo(Parcel in){
        memo = in.readString();
        date = in.readString();
        time = in.readString();
        mode = in.readString();
    }
    //get the memo text the user typed
    public String getMemo(){
        return memo;
    }
    //sets the memo
    public void setMemo(String memo){
        this.memo = memo;
    }
    //gets the date
    public String getDate(){
        return date;
    }
    //sets the date
    public void setDate(String date){
        this.date = date;
    }
    //gets the time
    public String getTime(){
        return time;
    }
    //sets the time
    public void setTime(String time){
        this.time = time;
    }
    //gets the mode
    public String getMode(){
        return mode;
    }
    //sets the mode
    public void setMode(String mode){
        this.mode = mode;
    }
}
