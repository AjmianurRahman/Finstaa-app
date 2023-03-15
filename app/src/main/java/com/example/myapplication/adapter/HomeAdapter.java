package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.FragmentReplaceActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.HomeModel;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    private Activity context;
    private List<HomeModel> list;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    OnPressed onPressed;

    public interface OnPressed {
        void onLiked(int position, String id, String uid, List<String> likeList);

        void onComment(int position, String id, String uid, String comment, LinearLayout commentLayout, EditText commentEditT);

        void setCommentCount(TextView textView);
    }

    public void OnPressed(OnPressed onPressed) {
        this.onPressed = onPressed;
    }


    public HomeAdapter(Activity context, List<HomeModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_items, parent, false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(list.get(position).getProfileImage())
                .placeholder(R.drawable.user)
                .timeout(7000)
                .into(holder.profileImage);

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Glide.with(context.getApplicationContext()).load(list.get(position).getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.postImage);

        holder.userName.setText(list.get(position).getName());
        holder.postTime.setText(String.valueOf(list.get(position).getTimeStamp()));
        holder.discription.setText(list.get(position).getDescription());

        //setting likes count
        List<String> likeLis = list.get(position).getLikes();
        int count = likeLis.size();
        if (count == 0) {
            holder.likeCount.setVisibility(View.INVISIBLE);
        } else if (count == 1) {
            holder.likeCount.setText(count + " likes");
        } else {
            holder.likeCount.setText(count + " likes");
        }

        // check if already liked
        holder.likeImage.setChecked(likeLis.contains(user.getUid()));

        // set click listener to like and comment image
        holder.clickListener(position
                , list.get(position).getId()
                , list.get(position).getName()
                , list.get(position).getUid()
                , list.get(position).getLikes()
                , list.get(position).getImageUrl()
        );



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView userName, likeCount, postTime, discription, showAllComment;
        private ImageView postImage, commentImage, shareImage;
        private CheckBox likeImage;



        public HomeHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageId);
            userName = itemView.findViewById(R.id.UserNameId);
            likeCount = itemView.findViewById(R.id.likeCountTextId);
            postTime = itemView.findViewById(R.id.PostTimeTextId);
            postImage = itemView.findViewById(R.id.feedImageId);
            likeImage = itemView.findViewById(R.id.likeButtonId);
            commentImage = itemView.findViewById(R.id.commentButtonId);
            shareImage = itemView.findViewById(R.id.shareButtonId);
            discription = itemView.findViewById(R.id.descriptionTextId);
            showAllComment = itemView.findViewById(R.id.commentShowTextId);


            onPressed.setCommentCount(showAllComment);
        }

        // adding clickListener
        public void clickListener(int position, String id, String name, String uid, List<String> likeList, String imageUrl) {

            commentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, FragmentReplaceActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("uid", uid);
                    intent.putExtra("isComment", true);

                    context.startActivity(intent);
                }
            });

            likeImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    onPressed.onLiked(position, id, uid, likeList);

                }
            });

            shareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, imageUrl);
                    intent.setType("text/*");
                    context.startActivity(Intent.createChooser(intent, "Share link using///"));
                }
            });

        }


    }

}
