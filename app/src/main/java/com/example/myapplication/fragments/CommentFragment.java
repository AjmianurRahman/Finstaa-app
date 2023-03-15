package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CommentAdapter;
import com.example.myapplication.model.CommentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentFragment extends Fragment implements View.OnClickListener {

    private EditText editTextComment;
    private ImageView sendIcon;
    private RecyclerView recyclerView;

    CommentAdapter adapter;

    FirebaseUser user;
    CollectionReference collectionReference;

    List<CommentModel> list;
    String id, uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextComment = view.findViewById(R.id.commentFragEditId);
        sendIcon = view.findViewById(R.id.commentFragSendButtonId);

        recyclerView = view.findViewById(R.id.commentRecyclerId);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new CommentAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        sendIcon.setOnClickListener(this);

        if (getArguments() == null) {
            return;
        }
        id = getArguments().getString("id");
        uid = getArguments().getString("uid");

        collectionReference = FirebaseFirestore.getInstance()
                .collection("Users").document(uid)
                .collection("Post Images").document(id)
                .collection("Comments");

        loadCommentData();


    }

    private void loadCommentData() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) return;
                if (value == null) return;

                for (QueryDocumentSnapshot snapshot: value){

                    CommentModel model = snapshot.toObject(CommentModel.class);
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
       /* String image = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fmedia.istockphoto.com%2Fid%2F1300845620%2Fvector%2Fuser-icon-flat-isolated-on-white-background-user-symbol-vector-illustration.jpg%3Fs%3D612x612%26w%3D0%26k%3D20%26c%3DyBeyba0hUkh14_jgv1OKqIH0CCSWU_4ckRkAoy2p73o%3D&imgrefurl=https%3A%2F%2Fwww.istockphoto.com%2Fillustrations%2Fperson-icon&tbnid=3ieVDLEJVcWu5M&vet=12ahUKEwjqjvTV1pr9AhXLmdgFHZJXAXsQMygAegUIARDoAQ..i&docid=nMa4EhgTujOH9M&w=612&h=612&q=person%20image%20icon&ved=2ahUKEwjqjvTV1pr9AhXLmdgFHZJXAXsQMygAegUIARDoAQ";
        UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
        request.setPhotoUri(Uri.parse(image));
        user.updateProfile(request.build());*/


        String comment = editTextComment.getText().toString();
        if (view.getId() == R.id.commentFragSendButtonId) {


            if (comment.isEmpty()) {
                Toast.makeText(getContext(), "Enter a Comment", Toast.LENGTH_SHORT).show();
                return;
            }


            String commentId = collectionReference.document().getId();
            Map<String, Object> map = new HashMap<>();
            map.put("uid", user.getUid());
            map.put("comment", comment);
            map.put("commentId", commentId);
            map.put("postId", id);
            map.put("name", user.getDisplayName());
            map.put("profileImageUrl", user.getPhotoUrl().toString());

            collectionReference.document(commentId).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                editTextComment.setText("");

                            } else {
                                Toast.makeText(getContext(), "Failed to comment, try again", Toast.LENGTH_SHORT).show();
                                editTextComment.setText("");

                            }
                        }
                    });

        }

    }
}