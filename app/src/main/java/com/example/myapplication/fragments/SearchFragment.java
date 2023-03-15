package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.myapplication.R;
import com.example.myapplication.adapter.UserAdapter;
import com.example.myapplication.model.Users;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    SearchView searchView;
    RecyclerView recyclerView;

    UserAdapter adapter;
    List<Users> list = new ArrayList<>();

    CollectionReference collectionReference;

    //to pass data to mainActivity()
    OnDataPass onDataPass;

    public interface OnDataPass {
        void onChange(String uid);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDataPass = (OnDataPass) context;
    }

    private void clickListener() {
        /*adapter.OnUserClicked(uid ->
                onDataPass.onChange(uid));*/

            adapter.OnUserClicked(new UserAdapter.OnUserClicked() {
                @Override
                public void onClicked(String uid) {
                    if (uid == null) return;
                    onDataPass.onChange(uid);
                }
            });

    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);

        loadUserData();

        searchUser();

        clickListener();
    }


    private void searchUser() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                collectionReference.orderBy("name").startAt(query).endAt(query + "\uf8ff")
                        .get().addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                list.clear();
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    if (!snapshot.exists()) return;
                                    Users users = snapshot.toObject(Users.class);
                                    list.add(users);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if (newText.equals("")) {
                    loadUserData();
                }
                return false;
            }
        });
    }


    private void loadUserData() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users");
       /* reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) return;

                if (value == null) return;

                for (QueryDocumentSnapshot snapshot: value){
                    Users users = snapshot.toObject(Users.class);
                    list.add(users);

                }
                adapter.notifyDataSetChanged();
            }
        });*/
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    return;

                if (value == null)
                    return;

                list.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    Users users = snapshot.toObject(Users.class);
                    list.add(users);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initialize(View view) {
        searchView = view.findViewById(R.id.searchViewId);
        recyclerView = view.findViewById(R.id.searchRecyclerId);
        recyclerView.setHasFixedSize(true);
        adapter = new UserAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        collectionReference = FirebaseFirestore.getInstance().collection("Users");

    }
}