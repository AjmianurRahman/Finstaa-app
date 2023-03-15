package com.example.myapplication.Chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatUserAdapter;
import com.example.myapplication.model.ChatUserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatUserActivity extends AppCompatActivity {

    private EditText search;
    private RecyclerView recyclerView;
    ChatUserAdapter adapter;
    List<ChatUserModel> list;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);


        search = findViewById(R.id.chatEdittextId);
        recyclerView = findViewById(R.id.chatUserRecyclerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new ChatUserAdapter(this, list);
        recyclerView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();

        fetchUserData();

        clickListener();
    }

    void fetchUserData(){
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Messages");
        collectionReference.whereArrayContains("uid", user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error!= null) return;
                        if (value.isEmpty()) return;

                        list.clear();
                        for (QueryDocumentSnapshot snapshot: value){
                            ChatUserModel model = snapshot.toObject(ChatUserModel.class);
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    void clickListener(){
        adapter.OnStartChat(new ChatUserAdapter.OnstartChat() {
            @Override
            public void clicked(int position, List<String> uids, String chatId) {
                String oppositeUid;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (uids.get(0).equalsIgnoreCase(user.getUid())) {
                    oppositeUid = uids.get(0);
                } else {
                    oppositeUid = uids.get(1);
                }
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("oppositeUid",oppositeUid );
                intent.putExtra("id",chatId);
                startActivity(intent);

            }
        });
    }
}