package com.yg.mobile.iteratort.app;

import android.app.Application;

import com.yg.mobile.iteratort.app.service.DataBaseService;

/**
 * Created by chenaxing on 2018/3/8.
 */

public class MainApplication extends Application
{
    DataBaseService dataBaseService;
    @Override
    public void onCreate()
    {
        super.onCreate();
        dataBaseService = new DataBaseService();
        dataBaseService.onApplicationCreated(getApplicationContext());
    }

    public void exit()
    {
        if(dataBaseService != null)
        {
            dataBaseService.onApplicationDestory(getApplicationContext());
        }
        System.exit(0);
    }
}
