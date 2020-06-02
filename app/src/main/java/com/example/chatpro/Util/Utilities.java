package com.example.chatpro.Util;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class Utilities {

    private static int seconds,minutes,hrs,days;
    private static long time;

    public static String getTime(long args) {
        Date date = new Date();
        time = date.getTime()-args;
        seconds = (int) (time/1000);
        minutes = seconds/60;
        hrs=minutes/60;
        days=hrs/24;
        if(minutes ==0 || minutes == 1){
            return "Just Now";
        }
        else if(minutes >=2 && minutes <= 59)
            return minutes+" min ago";
        else if(hrs >=1 && hrs <= 23)
            return hrs+" hrs ago";
        else
            return days+" days ago";
    }

    static long getTimeNow(){
        return (new Date()).getTime();
    }
}
