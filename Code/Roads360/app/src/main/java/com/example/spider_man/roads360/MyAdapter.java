package com.example.spider_man.roads360;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Spider-Man on 12/14/2018.
 */

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private Context context;
    private List<Upload> uploads;
    private OnItemClickListener mListener;

    public MyAdapter(Context context, List<Upload> uploads)
    {
        this.uploads = uploads;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_images,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Upload upload = uploads.get(position);
        holder.showLocation.setText(upload.getLocation());
        Glide.with(context).load(upload.getUrl()).fitCenter().centerCrop().into(holder.imageView);
        holder.showTime.setText(upload.getCurrentTime());
        holder.showDate.setText(upload.getCurrentDate());
        holder.showAvgTraficSituation.setText(upload.getAvgTrafficSituation());
        holder.showTrafficHour.setText(upload.getTrafficHour());
        holder.showTrafficDay.setText(upload.getTrafficDay());
        holder.showDetailsLocation.setText(upload.getDetailsLocation());

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener {

        public TextView showLocation, showTime, showDate, showTrafficHour, showTrafficDay,showAvgTraficSituation,showDetailsLocation;
        public ImageView imageView;


        @Override
        public void onClick(View view) {

            if(mListener != null)
            {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                   mListener.onItemClick(position);
                }
            }
        }
        public ViewHolder(View itemView) {
            super(itemView);
            showLocation = (TextView)itemView.findViewById(R.id.showLocation);
            imageView = (ImageView)itemView.findViewById(R.id.showImage);
            showTime = (TextView)itemView.findViewById(R.id.showTime);
            showDate =(TextView)itemView.findViewById(R.id.showDate);
            showTrafficHour =(TextView)itemView.findViewById(R.id.showTraficHour);
            showTrafficDay=(TextView)itemView.findViewById(R.id.showTraficDay);
            showAvgTraficSituation = (TextView)itemView.findViewById(R.id.showavgtrafficSituation);
            showDetailsLocation = (TextView)itemView.findViewById(R.id.showDetailsLocation);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null)
            {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    switch(menuItem.getItemId()){
                        case 1:
                            mListener.onWhateverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;


                    }
                }
            }

            return false;
        }



        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.setHeaderTitle("Select Action");
            MenuItem doWhatever = contextMenu.add(Menu.NONE,1,1,"Do Whatever");
            MenuItem delete = contextMenu.add(Menu.NONE,2,2,"Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onWhateverClick(int position);
        void onDeleteClick(int postion);
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;

    }
}
