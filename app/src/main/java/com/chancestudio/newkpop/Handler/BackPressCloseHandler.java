package com.chancestudio.newkpop.Handler;

import android.app.Activity;
import android.widget.Toast;

import com.chancestudio.newkpop.R;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();

        } else if(System.currentTimeMillis() <= backKeyPressedTime + 2000){

            activity.moveTaskToBack(true);
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());

            toast.cancel();
        }
    }
    public void showGuide() {
        toast = Toast.makeText(activity,
                R.string.back_msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
