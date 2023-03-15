package com.example.myapplication.adapter;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.NotificatonModel;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    Context context;
    List<NotificatonModel> list;

    public NotificationAdapter(Context context, List<NotificatonModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.noti_items, parent, false);
        return new NotificationHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {

        holder.notification.setText(list.get(position).getNotification());
        holder.time.setText(calculateTime(list.get(position).getTime()));

        Glide.with(context.getApplicationContext()).load(list.get(position).getImageUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    String calculateTime(Date date){
        long millis = date.toInstant().toEpochMilli();
        return DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(), 60000, DateUtils.FORMAT_SHOW_TIME).toString();

    }

    static class NotificationHolder extends RecyclerView.ViewHolder{
        TextView notification, time;
        CircleImageView imageView;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            notification = itemView.findViewById(R.id.notificationId);
            time = itemView.findViewById(R.id.notiTimeId);
            imageView = itemView.findViewById(R.id.notiDpId);
        }
    }

}
