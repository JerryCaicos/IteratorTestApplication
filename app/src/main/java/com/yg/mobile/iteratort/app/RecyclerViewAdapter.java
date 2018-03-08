package com.yg.mobile.iteratort.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by chenaxing on 2018/3/8.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>
{
    private Context mContext;

    public RecyclerViewAdapter(Context context)
    {
        mContext = context;
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

    }

    @Override
    public int getItemCount()
    {
        return 0;
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
