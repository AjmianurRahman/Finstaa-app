package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.CommentModel;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    Context context;
    List<CommentModel> list;

    public CommentAdapter(Context context, List<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_items, parent, false);

        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        Glide.with(context).load(list.get(position).getProfileImageUrl())
                .into(holder.commentDp);
        holder.name.setText(list.get(position).getName());
        holder.comment.setText(list.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CommentHolder extends RecyclerView.ViewHolder{
        CircleImageView commentDp;
        TextView name, comment;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            commentDp = itemView.findViewById(R.id.commentItemDpId);
            name = itemView.findViewById(R.id.commentItemNameId);
            comment = itemView.findViewById(R.id.commentItemCommentId);



        }
    }
}
