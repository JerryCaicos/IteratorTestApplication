package com.yg.mobile.iteratort.app.service;

import android.content.Context;

/**
 * Created by chenaxing on 2018/3/7.
 */

public class DataBaseService implements BasicService
{
    private DBHelper mDbHelper;

    @Override
    public void onApplicationCreated(Context context)
    {
        mDbHelper = DBHelper.getInstance(context);
    }

    @Override
    public void onApplicationDestory(Context context)
    {
        mDbHelper.close();
    }

    private void initData()
    {

    }
}
