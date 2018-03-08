package com.yg.mobile.iteratort.app.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.yg.mobile.iteratort.app.model.ImageSqlModel;
import com.yg.mobile.iteratort.app.model.TableConstants;
import com.yg.mobile.iteratort.app.utils.LogUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class DataBaseHelper<T, ID> extends OrmLiteSqliteOpenHelper
{
    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "iterator_test.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static int DATABASE_VERSION = 1;

    public static DataBaseHelper mDataBaseHelper;

    public Dao<T, ID> beanDao;

    private DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DataBaseHelper getInstance(Context context)
    {
        if(mDataBaseHelper == null)
        {
            synchronized(DataBaseHelper.class)
            {
                if(mDataBaseHelper == null){
                    mDataBaseHelper = new DataBaseHelper(context);
                }
            }
        }
        return mDataBaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource)
    {
        try
        {
            TableUtils.createTableIfNotExists(connectionSource, ImageSqlModel.class);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        try
        {
            TableUtils.dropTable(connectionSource, DataBaseHelper.class, true);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        onCreate(database, connectionSource);
    }

    public Dao<T, ID> getBeanDao(Class<T> clazz)
    {
        try
        {
            beanDao = getDao(clazz);
        }
        catch(SQLException e)
        {
            LogUtils.e(this, e);
        }
        return beanDao;
    }

    public boolean batchInsertBeans(final Class<T> clazz, final List<T> list, final int batchType)
    {
        boolean doBatch = false;
        ConnectionSource connectionSource = mDataBaseHelper.getConnectionSource();
        TransactionManager transactionManager = new TransactionManager(connectionSource);
        Callable<Boolean> callable = new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return doBatch(clazz, list, batchType);
            }
        };
        try
        {
            doBatch = transactionManager.callInTransaction(callable);
        }
        catch(SQLException e)
        {
            LogUtils.e(this, e);
        }
        return doBatch;
    }

    private boolean doBatch(Class<T> clazz, List<T> list, int batchType)
    {
        int result = 0;
        try
        {
            for(T t : list)
            {
                switch(batchType)
                {
                    case TableConstants.KEY_INSERT:
                        result += mDataBaseHelper.getBeanDao(clazz).create(t);
                        break;
                    case TableConstants.KEY_DELETE:
                        result += mDataBaseHelper.getBeanDao(clazz).delete(t);
                        break;
                    case TableConstants.KEY_UPDATE:
                        result += mDataBaseHelper.getBeanDao(clazz).update(t);
                        break;
                    default:
                        LogUtils.w("no this type.");
                        break;
                }
            }
        }
        catch(SQLException e)
        {
            LogUtils.e(this,e);
        }
        return result == list.size();
    }

    public void clearTable(Class<T> clazz)
    {
        try
        {
            TableUtils.clearTable(mDataBaseHelper.getConnectionSource(),clazz);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void close()
    {
        super.close();
        beanDao = null;
        mDataBaseHelper = null;
    }
}
