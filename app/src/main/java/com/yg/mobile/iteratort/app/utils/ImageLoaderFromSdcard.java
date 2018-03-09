package com.yg.mobile.iteratort.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chenaxing on 2018/3/9.
 */

public class ImageLoaderFromSdcard
{
    private static final String TAG = "ImageLoader";
    private static int DEFAULT_LOAD_IMAGE_ID = -1;
    private BaseLruCache mMemoryCache;
    private static DiskLruCache mDiskLruCache;
    private ExecutorService executorService;
    private LoadHandler mHandler;
    private int mLoadingImageId;
    private CacheType mCacheType;

    public ImageLoaderFromSdcard(Context context)
    {
        this(context, DEFAULT_LOAD_IMAGE_ID);
    }

    public ImageLoaderFromSdcard(Context context, int loadingImageId)
    {
        this.mCacheType = CacheType.ONLY_MEMORY;
        this.mLoadingImageId = loadingImageId;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 16;
        this.mMemoryCache = new BaseLruCache(cacheSize);
        if(mDiskLruCache == null)
        {
            File cacheDir = this.getDiskCacheDir(context, "iterator_test");
            if(!cacheDir.exists())
            {
                cacheDir.mkdirs();
            }

            try
            {
                mDiskLruCache = DiskLruCache.open(cacheDir, this.getAppVersion(context), 1, 10485760L);
            }
            catch(IOException var7)
            {
                LogUtils.e("ImageLoader", var7);
            }
        }

        this.executorService = Executors.newFixedThreadPool(5);
        this.mHandler = new LoadHandler(this);
    }

    private File getDiskCacheDir(Context context, String cacheDirName)
    {
        String cachePath;
        if(!"mounted".equals(Environment.getExternalStorageState()) && Environment.isExternalStorageRemovable())
        {
            cachePath = context.getCacheDir().getPath();
        }
        else
        {
            cachePath = context.getExternalCacheDir().getPath();
        }

        return new File(cachePath + File.separator + cacheDirName);
    }

    private int getAppVersion(Context context)
    {
        try
        {
            PackageInfo e = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return e.versionCode;
        }
        catch(PackageManager.NameNotFoundException var3)
        {
            LogUtils.e("ImageLoader", var3);
            return 1;
        }
    }

    public void loadImageFromSD(final boolean shouldCompress, final String imageUrl, final ImageView imageView, final int loadingImageId, final int reqwidth, final int reqHeight, final OnLoadCompleteListener loadListener)
    {
        if(imageView != null && !TextUtils.isEmpty(imageUrl))
        {
            imageView.setTag(this.mLoadingImageId, imageUrl);
            Bitmap bitmap = this.getBitmapFromMemoryCache(imageUrl);
            if(bitmap != null && !bitmap.isRecycled())
            {
                if(loadListener != null)
                {
                    loadListener.onLoadComplete(bitmap, imageView, imageUrl, new ImageLoadedParams(0L));
                }
                else
                {
                    imageView.setImageBitmap(bitmap);
                }

            }
            else
            {
                if(loadingImageId > 0)
                {
                    imageView.setImageResource(loadingImageId);
                }

                if(!this.executorService.isShutdown())
                {
                    this.executorService.execute(new Runnable()
                    {
                        public void run()
                        {
                            LoadResult result = null;
                            String key = ImageLoaderFromSdcard.getImageName(imageUrl);
                            Bitmap bitmap = null;
                            if(shouldCompress)
                            {
                                bitmap = ImageLoaderFromSdcard.this.getBitmapFromSd(imageUrl, reqwidth, reqHeight);
                            }
                            else
                            {
                                bitmap = ImageLoaderFromSdcard.this.getBitmapFromSd(imageUrl, reqwidth, reqHeight);
                            }

                            result = new LoadResult(bitmap, new ImageLoadedParams(-1L));
                            if(result.bitmap != null)
                            {
                                ImageLoaderFromSdcard.this.addBitmapToMemoryCache(imageUrl, bitmap);
                            }

                            Object currentUrl = imageView.getTag(loadingImageId);
                            if(currentUrl == null || currentUrl.equals(imageUrl))
                            {
                                ImageLoaderFromSdcard.this.mHandler.postLoaderResult(imageUrl, imageView, loadListener, result);
                            }
                        }
                    });
                }
            }
        }
    }


    public Bitmap getBitmapFromSd(String imgFilePath, int width, int height)
    {
        try
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgFilePath, opts);
            computeSampleSize(opts, width, height);
            final Bitmap bmp = BitmapFactory.decodeFile(imgFilePath, opts);
            return bmp;
        }
        catch(Exception e)
        {
        }
        return null;
    }

    /**
     * @param opts
     * @param reqWidth
     * @param reqHeight
     */
    private void computeSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight)
    {
        int sampleSize = 1;
        int height = opts.outHeight;
        int width = opts.outWidth;
        if(height > reqHeight || width > reqWidth)
        {
            final int heightRatio;
            final int widthRatio;
            if(reqHeight == 0)
            {
                sampleSize = (int) Math.floor((float) width / (float) reqWidth);
            }
            else if(reqWidth == 0)
            {
                sampleSize = (int) Math.floor((float) height / (float) reqHeight);
            }
            else
            {
                heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                sampleSize = Math.min(heightRatio, widthRatio);
            }
        }
        opts.inSampleSize = sampleSize;
        opts.inJustDecodeBounds = false;
    }

    public static String getImageName(String url)
    {
        String[] array = url.trim().split("/");
        int len = array.length;
        String name;
        if(len > 4)
        {
            name = array[len - 3] + "_" + array[len - 2] + "_" + array[len - 1];
        }
        else if(len > 3)
        {
            name = array[len - 2] + "_" + array[len - 1];
        }
        else
        {
            name = url;
        }

        name = name.toLowerCase(Locale.ENGLISH);
        int suffixPos = name.indexOf("?");
        if(suffixPos > 0)
        {
            String pos = name.substring(suffixPos + 1);
            name = pos + "_" + name.substring(0, suffixPos);
        }

        int pos1 = -1;
        if(name.contains(".jpg"))
        {
            pos1 = name.indexOf(".jpg");
        }
        else if(name.contains(".png"))
        {
            pos1 = name.indexOf(".png");
        }
        else
        {
        }

        if(pos1 > 0)
        {
            name = name.substring(0, pos1);
        }

        return name;
    }


    public Bitmap getBitmapFromMemoryCache(String key)
    {
        return this.mCacheType != CacheType.ONLY_SDCARD && this.mMemoryCache != null && key != null ? (Bitmap) this.mMemoryCache.get(key) : null;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if(this.mCacheType != CacheType.ONLY_SDCARD && this.mMemoryCache != null && bitmap != null && key != null)
        {
            this.mMemoryCache.put(key, bitmap);
        }

    }

    public void destory()
    {
        this.executorService.shutdown();
        this.mMemoryCache.destory();
    }

    public static enum CacheType
    {
        ONLY_MEMORY,
        ONLY_SDCARD,
        MEMORY_SDCARD;

        private CacheType()
        {
        }
    }

    private static class LoadHandler extends Handler
    {
        WeakReference<ImageLoaderFromSdcard> mmLoaderReference;

        public LoadHandler(ImageLoaderFromSdcard loader)
        {
            this.mmLoaderReference = new WeakReference(loader);
        }

        public void postLoaderResult(final String imageUrl, final ImageView imageView, final OnLoadCompleteListener loadListener, final LoadResult loadResult)
        {
            ImageLoaderFromSdcard loader = (ImageLoaderFromSdcard) this.mmLoaderReference.get();
            if(loader != null)
            {
                this.post(new Runnable()
                {
                    public void run()
                    {
                        if(loadListener != null)
                        {
                            loadListener.onLoadComplete(loadResult.bitmap, imageView, imageUrl, loadResult.loadedParams);
                        }
                        else if(imageView != null && loadResult.bitmap != null)
                        {
                            imageView.setImageBitmap(loadResult.bitmap);
                        }

                    }
                });
            }
        }
    }

    private static class LoadResult
    {
        Bitmap bitmap;
        ImageLoadedParams loadedParams;

        LoadResult(Bitmap bitmap, ImageLoadedParams loadedParams)
        {
            this.bitmap = bitmap;
            this.loadedParams = loadedParams;
        }
    }

    public interface OnLoadCompleteListener
    {
        void onLoadComplete(Bitmap var1, View var2, String var3, ImageLoadedParams var4);
    }

    static final class BaseLruCache extends LruCache<String, Bitmap>
    {
        private boolean isDestory;

        public BaseLruCache(int maxSize)
        {
            super(maxSize);
        }

        protected void entryRemoved(boolean evicted, String key, Bitmap oldBitmap, Bitmap newBitmap)
        {
            if(this.isDestory && oldBitmap != null)
            {
                oldBitmap.recycle();
            }

        }

        protected int sizeOf(String key, Bitmap bitmap)
        {
            return bitmap != null ? bitmap.getByteCount() : 0;
        }

        protected void destory()
        {
            this.evictAll();
        }
    }
}
