package com.example.sunny.up_education_board;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Sunny on 4/19/2017.
 */

public class Restart extends Application
{
    @Override
    public void onCreate() {

        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException (Thread thread, Throwable e) {
                        handleUncaughtException (thread, e);
                    }
                });
    }

    private void handleUncaughtException (Thread thread, Throwable e) {

        // The following shows what I'd like, though it won't work like this.
        Intent intent = new Intent(getApplicationContext(),Manage.class);
        startActivity(intent);

        // Add some code logic if needed based on your requirement
    }
}
