package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    Context context;
    List<ChatModel> list;
    FirebaseUser user;

    public ChatAdapter(Context context, List<ChatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (list.get(position).getSenderId().equalsIgnoreCase(user.getUid())){
            holder.leftChat.setVisibility(View.GONE);
            holder.rightChat.setVisibility(View.VISIBLE);
            holder.rightChat.setText(list.get(position).getMessage());
        }else {
            holder.leftChat.setVisibility(View.VISIBLE);
            holder.rightChat.setVisibility(View.GONE);
            holder.leftChat.setText(list.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ChatHolder extends RecyclerView.ViewHolder{

        TextView leftChat, rightChat;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            leftChat = itemView.findViewById(R.id.leftChatId);
            rightChat = itemView.findViewById(R.id.rightChatId);

        }
    }
}
