package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.ChatUserModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserHolder> {

    Context context;
    List<ChatUserModel> list;

    public OnstartChat startChat;

    public ChatUserAdapter(Context context, List<ChatUserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_users_item, parent, false);
        return new ChatUserHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatUserHolder holder, @SuppressLint("RecyclerView") int position) {
        fetchImageUrl(list.get(position).getUid(), (ChatUserHolder) holder);
        // setting time of message


        holder.time.setText(calculateTime(list.get(position).getTime()));

        holder.unseenMessage.setText(list.get(position).getLastMessage());

        // add listener
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> uids = list.get(position).getUid();
                startChat.clicked(position, uids, list.get(position).getId());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
     String calculateTime(Date date){
         long millis = date.toInstant().toEpochMilli();
         return DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(), 60000, DateUtils.FORMAT_SHOW_TIME).toString();

     }


    void fetchImageUrl(List<String> uids, ChatUserHolder holder) {
        String oppositeUid;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (uids.get(0).equalsIgnoreCase(user.getUid())) {
            oppositeUid = uids.get(0);
        } else {
            oppositeUid = uids.get(1);
        }
        FirebaseFirestore.getInstance().collection("Users").document(oppositeUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            Glide.with(context.getApplicationContext()).load(snapshot.getString("profileImage"))
                                    .into(holder.imageView);
                            holder.name.setText(snapshot.getString("name"));
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ChatUserHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
         TextView name, unseenMessage, unseenCount, time;

        public ChatUserHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.chatDpId);
            name = itemView.findViewById(R.id.chatNameId);
            unseenMessage = itemView.findViewById(R.id.chatmessageId);
            unseenCount = itemView.findViewById(R.id.newMassageCountId);
            time = itemView.findViewById(R.id.chatTimeId);

            unseenCount.setVisibility(View.GONE);
        }
    }

    public interface OnstartChat {
        void clicked(int position, List<String> uids, String chatId);
    }

    public void OnStartChat(OnstartChat startChat) {
        this.startChat = startChat;
    }

}
