package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.GalleryImages;

import java.util.List;



public class GalleryAdapter extends RecyclerView.Adapter <GalleryAdapter.GalleryHolder>{

    private List<GalleryImages> list;
    SendImage onSendImage;

    public GalleryAdapter(List<GalleryImages> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_image_item,parent,false);

        return new GalleryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(list.get(position).getImageUri())
                                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(list.get(position).getImageUri());
            }
        });

    }

    private void chooseImage(Uri imageUri) {
        onSendImage.onSend(imageUri);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GalleryHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.addItemImageViewId);
        }
    }

    public interface SendImage{
        void onSend(Uri uri);
    }
    public void SendImage(SendImage sendImage){
        this.onSendImage = sendImage;
    }


}
