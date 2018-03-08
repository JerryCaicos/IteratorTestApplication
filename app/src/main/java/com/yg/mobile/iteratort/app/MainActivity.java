package com.yg.mobile.iteratort.app;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yg.mobile.iteratort.app.model.ImageSqlModel;
import com.yg.mobile.iteratort.app.model.TableConstants;
import com.yg.mobile.iteratort.app.service.DBHelper;
import com.yg.mobile.iteratort.app.utils.LogUtils;

import org.w3c.dom.Node;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MainActivity extends AppCompatActivity
{
    public static final int MSG_SHOW_IMG = 1001;

    private TextView listAllImg;

    private RecyclerView recyclerView;

    private List<ImageSqlModel> imgPaths = new ArrayList<ImageSqlModel>();

    private DBHelper dbHelper;

    private List<ImageSqlModel> sqlList;

    private Handler handler = new MyHandler(this)
    {
        @Override
        public void handlemsg(Message msg)
        {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbHelper = DBHelper.getInstance(getApplicationContext());
        setContentView(R.layout.activity_main);

        listAllImg = (TextView) findViewById(R.id.list_all_img);
        recyclerView = (RecyclerView) findViewById(R.id.img_recycler_view);

        listAllImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getAllImgFromSD();
            }
        });
        queryImgInSql();
    }

    private void queryImgInSql()
    {
        sqlList = dbHelper.queryBeans(ImageSqlModel.class);
        if(sqlList != null && sqlList.size() > 0)
        {
            LogUtils.d("sql list size : " + sqlList.size());

        }
    }

    private void getAllImgFromSD()
    {
        File cacheDir;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
        {
            cacheDir = Environment.getExternalStorageDirectory();
            LogUtils.d("sdcard path : " + cacheDir.getAbsolutePath());
            getAllImg(cacheDir.getAbsolutePath());
        }
        else
        {
            Toast.makeText(this, "sdcard不可用", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllImg(final String path)
    {
        imgPaths.clear();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                runThread(path);
            }
        });
        thread.start();
    }

    private void runThread(String path)
    {
        long start = System.currentTimeMillis();
        File file = new File(path);
        if(file.exists())
        {
            LinkedList<File> fileList = new LinkedList<>();
            File[] files = file.listFiles();
            for(File file1 : files)
            {
                if(file1.isDirectory())
                {
                    fileList.add(file1);
                }
                else
                {
                    addImgFile(file1);
                }
            }

            File tempFile;
            while(!fileList.isEmpty())
            {
                tempFile = fileList.removeFirst();
                files = tempFile.listFiles();
                for(File file1 : files)
                {
                    if(file1.isDirectory())
                    {
                        fileList.add(file1);
                    }
                    else
                    {
                        addImgFile(file1);
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        LogUtils.d("end - start : " + (end - start));
        LogUtils.d("imgPaths size : " + imgPaths.size());
        dbHelper.clearTable(ImageSqlModel.class);
        boolean result = dbHelper.batchInsertBean(ImageSqlModel.class,imgPaths, TableConstants.KEY_INSERT);
        if(result)
        {
            List<ImageSqlModel> list = dbHelper.queryBeans(ImageSqlModel.class);
            if(list != null)
            {
                LogUtils.d("sql list size : " + list.size());
            }
        }
        handler.sendEmptyMessage(MSG_SHOW_IMG);
    }

    private void addImgFile(File file)
    {
        String fileName = file.getName().toLowerCase();
        ImageSqlModel model;
        if(fileName.endsWith(".jpg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".bmp"))
        {
            LogUtils.d("file name : " + fileName);
            LogUtils.d("file size : " + file.length());
            model = new ImageSqlModel();
            model.path = file.getAbsolutePath();
            model.lastTime = file.lastModified();
            imgPaths.add(model);
            model = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ((MainApplication)getApplication()).exit();
    }

    public abstract class MyHandler extends Handler
    {
        private WeakReference<Activity> weakReference;

        public MyHandler(Activity activity)
        {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(weakReference != null && weakReference.get() != null)
            {
                handlemsg(msg);
            }
        }

        public abstract void handlemsg(Message msg);
    }

}
