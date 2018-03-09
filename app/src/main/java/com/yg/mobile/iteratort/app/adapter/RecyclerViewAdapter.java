package com.yg.mobile.iteratort.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yg.mobile.iteratort.app.R;
import com.yg.mobile.iteratort.app.model.ImageSqlModel;
import com.yg.mobile.iteratort.app.utils.BitmapUtils;
import com.yg.mobile.iteratort.app.utils.ImageLoaderFromSdcard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chenaxing on 2018/3/8.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>
{
    private Context mContext;

    private ImageLoaderFromSdcard imageLoader;

    private List<ImageSqlModel> imageSqlModelList;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    public RecyclerViewAdapter(Context context,List<ImageSqlModel> list,ImageLoaderFromSdcard loader)
    {
        mContext = context;
        imageLoader = loader;
        imageSqlModelList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ImageSqlModel model = imageSqlModelList.get(position);
        if(imageLoader != null)
        {
            BitmapUtils.loadImageWithImageLoaderFromSD(imageLoader,mContext,holder.imageView,model.path,-1,100,100,null);
        }

        holder.imageTime.setText(format.format(new Date(model.lastTime)));
    }

    @Override
    public int getItemCount()
    {
        return imageSqlModelList.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder
{

    ImageView imageView;
    TextView imageTime;

    public ViewHolder(View itemView)
    {
        super(itemView);
        imageTime = (TextView) itemView.findViewById(R.id.view_img_time);
        imageView = (ImageView) itemView.findViewById(R.id.view_img);
    }

}
