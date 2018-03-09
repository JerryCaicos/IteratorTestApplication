package com.yg.mobile.iteratort.app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * Created by chenaxing on 2018/3/9.
 */

public class BitmapUtils
{
    public static void loadImageWithImageLoaderFromSD(ImageLoaderFromSdcard mImageLoader, Context context,
            ImageView target, String imageUrl, int loadingDr, int wid,
            int heig, ImageLoaderFromSdcard.OnLoadCompleteListener loadListener)
    {
        if (target == null)
        {
            return;
        }
        if (!TextUtils.isEmpty(imageUrl))
        {
            mImageLoader.loadImageFromSD(false, imageUrl, target, loadingDr,
                    wid, heig, loadListener);
        }
        else
        {
            target.setImageResource(loadingDr);
        }
    }
}
