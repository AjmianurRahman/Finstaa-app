package com.example.myapplication.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatAdapter;
import com.example.myapplication.model.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.FirestoreGrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView imageView;
    private TextView name, status;
    private EditText editText;
    private ImageView send;
    RecyclerView recyclerView;

    ChatAdapter adapter;
    List<ChatModel> list;
    FirebaseUser user;

    String chatId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = FirebaseAuth.getInstance().getCurrentUser();

        imageView = findViewById(R.id.chatActivityDpId);
        name = findViewById(R.id.chatActivityNameId);
        status = findViewById(R.id.chatActivityStatusId);
        editText = findViewById(R.id.chatActivityEditId);
        send = findViewById(R.id.chatActivitySendId);

        send.setOnClickListener(this);

        recyclerView = findViewById(R.id.chatActivityRecyclerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new ChatAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadUserData();

        loadMessages();

    }

    void loadUserData() {
        String oppositeUid = getIntent().getStringExtra("oppositeUid");

        FirebaseFirestore.getInstance().collection("Users")
                .document(oppositeUid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) return;
                        if (!value.exists()) return;

                        boolean isOnline = value.getBoolean("online");
                        status.setText(isOnline ? "Online" : "Offline");

                        Glide.with(getApplicationContext())
                                .load(value.getString("profileImage"))
                                .into(imageView);

                        name.setText(value.getString("name"));
                    }
                });
    }

    void loadMessages() {
        chatId = getIntent().getStringExtra("id");
        String oppositeUid = getIntent().getStringExtra("oppositeUid");

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Messages")
                .document(chatId).collection("Messages");
        collectionReference.orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) return;
                if (value.isEmpty() || value == null) return;

                list.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    ChatModel model = snapshot.toObject(ChatModel.class);
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.chatActivitySendId) {
            String message = editText.getText().toString();
            if (message.isEmpty()) return;

            CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
            Map<String, Object> map = new HashMap<>();

            map.put("lastMessage", message);
            map.put("time", FieldValue.serverTimestamp());

            reference.document(chatId).update(map);
            //todo ---- ----------- ------
            //Messages

            CollectionReference mReference = FirebaseFirestore.getInstance().collection("Messages")
                    .document(chatId ).collection("Messages");

            String mPushId = mReference.document().getId();

            Map<String, Object> mMap = new HashMap<>();
            map.put("id", mPushId);
            map.put("time", FieldValue.serverTimestamp());
            map.put("senderId", user.getUid());
            map.put("message", "Hi");
            mReference.document(mPushId).set(mMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        editText.setText("");
                    }else {
                        Toast.makeText(ChatActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}