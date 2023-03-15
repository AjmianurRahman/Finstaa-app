package com.example.myapplication.fragments;

import static android.app.Activity.RESULT_OK;

import static com.example.myapplication.MainActivity.IS_SEARCHED_USER;
import static com.example.myapplication.MainActivity.USER_UID;
import static com.example.myapplication.utils.Constents.PREF_DIRECTORY;
import static com.example.myapplication.utils.Constents.PREF_NAME;
import static com.example.myapplication.utils.Constents.PREF_STORED;
import static com.example.myapplication.utils.Constents.PREF_URL;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.myapplication.Chat.ChatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.PostImageModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView pUserName, toolUserName, bio, postNumber, followerN, followingN;
    private CircleImageView profileImage;
    private Button follow, sendMessage;
    private ImageView editProfile;
    private RecyclerView recyclerView;
    private LinearLayout showPff;

    String profileUrl;
   String uri;

    FirebaseUser user;
    DocumentReference documentReference, myRef;
    DocumentReference reference;
    FirestoreRecyclerAdapter<PostImageModel, PostImageHolder> adapter;

    List<Object> followersList, followingList, followingList_2;
    boolean isMyProfile = true;
    boolean is_followed;
    String userUid;
    int count;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbarId2);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        initialize(view);

        documentReference = FirebaseFirestore.getInstance()
                .collection("Users").document(userUid);

        myRef = FirebaseFirestore.getInstance()
                .collection("Users").document(user.getUid());

        if (IS_SEARCHED_USER) {
            userUid = USER_UID;
            isMyProfile = false;

            loadFollowerFollowingData();

        } else {
            userUid = user.getUid();
            isMyProfile = true;
        }


        if (isMyProfile) {
            editProfile.setVisibility(View.VISIBLE);
            follow.setVisibility(View.GONE);
            // message button invisible if my account
            sendMessage.setVisibility(View.GONE);


        } else {
            editProfile.setVisibility(View.GONE);
            follow.setVisibility(View.VISIBLE);
            //  showPff.setVisibility(View.GONE);
        }

        loadData();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        loadPostImages();
        recyclerView.setAdapter(adapter);

    }

    private void loadFollowerFollowingData() {
        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) return;

                if (!value.exists()) {
                    return;
                }

                followingList_2 = (List<Object>) value.get("following");

            }
        });
    }

    private void loadData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseFirestore.getInstance().collection("Users")
                .document(userUid);
        reference.addSnapshotListener((value, error) -> {

            assert value != null;
            if (value.exists()) {

                followersList = (List<Object>) value.get("followers");
                followingList = (List<Object>) value.get("following");

                pUserName.setText(value.getString("name"));

                toolUserName.setText(value.getString("name"));
                bio.setText(value.getString("bio"));
                followerN.setText("" + followersList.size());
                followingN.setText("" + followingList.size());
                profileUrl = value.getString("profileImage");

                Glide.with(getContext().getApplicationContext())
                        .load(profileUrl)
                        .placeholder(R.drawable.user)
                        .circleCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                storeProfileImage(bitmap, profileUrl);
                                return false;
                            }
                        })
                        .timeout(6500)
                        .into(profileImage);

                if (followersList.contains(user.getUid())) {
                    follow.setText("Unfollow");
                    is_followed = true;
                    sendMessage.setVisibility(View.VISIBLE);
                } else {
                    follow.setText("Follow");
                    is_followed = false;
                    sendMessage.setVisibility(View.GONE);
                }

            }
        });

    }

    private void storeProfileImage(Bitmap bitmap, String url) {

        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isStored = preferences.getBoolean(PREF_STORED, false);
        String urlString = preferences.getString(PREF_URL, "");

        SharedPreferences.Editor editor = preferences.edit();

        if (isStored && urlString.equals(url)) {
            return;
        }

        if (IS_SEARCHED_USER) return;

        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());

        File directory = contextWrapper.getDir("image_data", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File path = new File(directory, "profile.png");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        editor.putBoolean(PREF_STORED, true);
        editor.putString(PREF_URL, url);
        editor.putString(PREF_DIRECTORY, directory.getAbsolutePath());
        editor.apply();

    }

    private void initialize(View view) {

        pUserName = view.findViewById(R.id.PUserNameId);
        toolUserName = view.findViewById(R.id.toolBarUserNameId);
        bio = view.findViewById(R.id.BioId);
        postNumber = view.findViewById(R.id.postCountId);
        followerN = view.findViewById(R.id.followerCountId);
        followingN = view.findViewById(R.id.followingCountId);
        profileImage = view.findViewById(R.id.profilePictureId);
        follow = view.findViewById(R.id.followButtonId);
        recyclerView = view.findViewById(R.id.profileRecyclerId);
        showPff = view.findViewById(R.id.linearLayout2Id);
        editProfile = view.findViewById(R.id.editProfileImageId);
        sendMessage = view.findViewById(R.id.sendMessageId);

        editProfile.setOnClickListener(this);
        follow.setOnClickListener(this);
        sendMessage.setOnClickListener(this);


    }


    private void loadPostImages() {


        Query query = documentReference.collection("Post Images");

        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class).build();

        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_image_items, parent, false);
                return new PostImageHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {

                Glide.with(holder.imageView.getContext().getApplicationContext())
                        .load(model.getImageUrl())
                        .timeout(6500)
                        .into(holder.imageView);

                count = getItemCount();
                postNumber.setText("" + count);

            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    class PostImageHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pImageid);
        }
    }

    @Override
    public void onClick(View view) {
        // edit profile picture
        if (view.getId() == R.id.editProfileImageId) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), ProfileFragment.this);
        }

        // follow and unfollow button
        if (view.getId() == R.id.followButtonId) {
            if (is_followed) {
                followersList.remove(user.getUid()); // opposite user
                followingList_2.remove(userUid);

                Map<String, Object> map = new HashMap<>();
                map.put("following", followingList);

                final Map<String, Object> map2 = new HashMap<>();
                map.put("followers", followingList_2);

                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            follow.setText("Follow");
                            myRef.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        } else {
                            Log.e("tag", task.getException().getMessage());
                        }
                    }
                });
            } else {
                generateNotification();
                followersList.add(user.getUid()); // opposite user
                followingList_2.add(userUid);

                Map<String, Object> map = new HashMap<>();
                map.put("following", followingList_2);

                final Map<String, Object> map2 = new HashMap<>();
                map.put("followers", followingList);
                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            follow.setText("Unfollow");
                            myRef.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        } else {
                            Log.e("tag", task.getException().getMessage());
                        }
                    }
                });

            }
        }

        if (view.getId() == R.id.sendMessageId) {
            queryChat();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();

                uploadImageToStorage(uri);
            }
        }
    }

    void queryChat() {

        StylishAlertDialog alertDialog = new StylishAlertDialog(getContext(), StylishAlertDialog.PROGRESS);
        alertDialog.setTitleText("Message being send...");
        alertDialog.setCancelable(false);
        alertDialog.show();

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Messages");
        collectionReference.whereArrayContains("uid", userUid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot.isEmpty()) {
                                startChat(alertDialog);
                            } else {
                                alertDialog.dismissWithAnimation();
                                for (DocumentSnapshot documentSnapshot : snapshot) {
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("oppositeUid", userUid);
                                    intent.putExtra("id", documentSnapshot.getId());
                                    startActivity(intent);
                                }

                            }
                        } else {
                            alertDialog.dismissWithAnimation();
                        }
                    }
                });
        ;


    }

    void startChat(StylishAlertDialog alertDialog) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");

        List<String> list = new ArrayList<>();
        list.add(0, user.getUid());
        list.add(1, userUid);

        String pushId = reference.document().getId();

        Map<String, Object> map = new HashMap<>();
        map.put("id", pushId);
        map.put("lastMessage", "Hi");
        map.put("time", FieldValue.serverTimestamp());
        map.put("uid", list);
        reference.document(pushId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {
                    reference.document(pushId).set(map);
                }
            }

            ;
        });
        //todo ---- ----------- ------
        //Messages

        CollectionReference mReference = FirebaseFirestore.getInstance().collection("Messages")
                .document(pushId).collection("messages");

        String mPushId = mReference.document().getId();

        Map<String, Object> mMap = new HashMap<>();
        map.put("id", mPushId);
        map.put("time", FieldValue.serverTimestamp());
        map.put("senderId", user.getUid());
        map.put("message", "Hi");
        mReference.document(mPushId).update(mMap);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismissWithAnimation();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("oppositeUid", userUid);
                intent.putExtra("id", pushId);
                startActivity(intent);
            }
        }, 2000);
    }

    void uploadImageToStorage(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference("Post Images/")
                .child(user.getUid()).child("profile Image");
        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                            request.setPhotoUri(uri);
                            user.updateProfile(request.build());

                            Map<String, Object> map = new HashMap<>();
                            map.put("profileImage", uri.toString());


                            FirebaseFirestore.getInstance().collection("Users")
                                    .document(user.getUid())
                                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Uploaded sucessfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                    });
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    void generateNotification() {
       
       DocumentReference documentReference= FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());
       documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               DocumentSnapshot snapshot = task.getResult();
               uri = snapshot.getString("profileImage");
           }
       });

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Notification");
        String id = collectionReference.document().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("imageUrl", uri );
        map.put("id", id);
        map.put("uid", userUid);
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", user.getDisplayName() + " started following you");

        collectionReference.document(id).set(map);
    }
}

