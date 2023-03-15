package com.example.myapplication.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private final List<Users> list;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    OnUserClicked onUserClicked;


    public UserAdapter(List<Users> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_items, parent, false);

        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, @SuppressLint("RecyclerView") int position) {
        if (list.get(position).getUid().equals(user.getUid())) {
            holder.layout.setVisibility(View.GONE);
            holder.imageView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.layout.setVisibility(View.VISIBLE);
        }

        holder.textView.setText(list.get(position).getName());

        Glide.with(holder.imageView.getContext().getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.user)
                .timeout(6500)
                .into(holder.imageView);


        /* holder.itemView.setOnClickListener(view ->
                onUserClicked.onClicked(list.get(position).getUid())
        ); */

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getUid() != null){
                    onUserClicked.onClicked(list.get(position).getUid());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class UserHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textView;

        RelativeLayout layout;

        public UserHolder(@NonNull View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.searchItemProfileImageId);
            textView = itemView.findViewById(R.id.searchItemTextId);
            layout = itemView.findViewById(R.id.searchLayoutId);
        }


    }

    public void OnUserClicked(OnUserClicked onUserClicked) {
        this.onUserClicked = onUserClicked;
    }

    public interface OnUserClicked {
        void onClicked(String uid);
    }

}
