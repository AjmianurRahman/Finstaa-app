package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.HomeAdapter;
import com.example.myapplication.model.HomeModel;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView, storyRecycler;
    private Toolbar toolbar;

    HomeAdapter adapter;

    private final MutableLiveData<Integer> commentCount = new MutableLiveData<Integer>();
    private List<HomeModel> list;



    FirebaseUser user;
    DocumentReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbarId);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        initialize(view);
        //showing news feed posts
        reference = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        loadDataFromFirebase();

        // ading listener to like option
        adapter.OnPressed(new HomeAdapter.OnPressed() {

            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList) {
                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection("Users").document(uid)
                        .collection("Post Images").document(id);

                if (likeList.contains(user.getUid())) {
                    likeList.remove(user.getUid());
                } else {
                    likeList.add(user.getUid());
                }

                Map<String, Object> map = new HashMap<>();
                map.put("likes", map);
                documentReference.update(map);

            }

            @Override
            public void onComment(int position, String id, String uid, String comment, LinearLayout commentLayout, EditText commentEditT) {

            }


            @Override
            public void setCommentCount(TextView textView) {
                Activity activity = getActivity();

                commentCount.observe((LifecycleOwner) activity, new Observer<Integer>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(Integer integer) {
                        if ( commentCount.getValue() == 0){
                            textView.setVisibility(View.GONE);
                        }else {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("See all "+commentCount.getValue()+" comments");

                        }
                    }
                });


            }
        });
        // adding listener ends here
    }
    private void initialize(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new HomeAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

             user = FirebaseAuth.getInstance().getCurrentUser();
    }


    private void loadDataFromFirebase() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());
        // .collection("Post Images");
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) return;
                if (value == null) return;

                List<String> uidList = (List<String>) value.get("following");
                if (uidList == null || uidList.isEmpty()) return;
                // loading only the images of the people i follow

                collectionReference.whereIn("uid", uidList)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                for (QueryDocumentSnapshot snapshot : value) {
                                    snapshot.getReference().collection("Post Images")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @SuppressLint("NotifyDataSetChanged")
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                    if (error != null) return;
                                                    if (value == null) return;
                                                    list.clear();
                                                    for (final QueryDocumentSnapshot snapshot : value) {
                                                        if (!snapshot.exists()) {
                                                            return;
                                                        }

                                                        HomeModel model = snapshot.toObject(HomeModel.class);
                                                        list.add(new HomeModel(
                                                                model.getName(),
                                                                model.getId(),
                                                                model.getProfileImage(),
                                                                model.getImageUrl(),
                                                                model.getUid(),
                                                                model.getTimeStamp(),
                                                                model.getDescription(),
                                                                model.getLikes()
                                                        ));
                                                        // for the comments of this post
                                                        snapshot.getReference().collection("Comments")
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                         int count = 0;
                                                                        if (task.isSuccessful()){
                                                                            for (QueryDocumentSnapshot snapshot: task.getResult()){
                                                                              count++;
                                                                            }
                                                                        }
                                                                        commentCount.setValue(count);
                                                                    }
                                                                });

                                                    }
                                                    adapter.notifyDataSetChanged();

                                                }
                                            });

                                }

                            }
                        });
                // todo: fatch stories


            }
        });


    }




}