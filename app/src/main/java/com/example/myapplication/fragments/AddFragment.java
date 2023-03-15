package com.example.myapplication.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;

import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.GalleryAdapter;
import com.example.myapplication.model.GalleryImages;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFragment extends Fragment {

    private EditText editText;
    private ImageView imageView, next, back;
    private RecyclerView recyclerView;

    private GalleryAdapter adapter;
    private List<GalleryImages> list;

    Dialog dialog;

    Uri picUri;

    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText = view.findViewById(R.id.addEditTextId);
        imageView = view.findViewById(R.id.addImageViewId);
        next = view.findViewById(R.id.nextId);
        back = view.findViewById(R.id.backButtonId);
        recyclerView = view.findViewById(R.id.addRecyclerView);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //creating a loading dialog
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.dialog_bg, null));
        dialog.setCancelable(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        adapter = new GalleryAdapter(list);

        recyclerView.setAdapter(adapter);

        clickListener();

        next.setOnClickListener(view1 -> uploadToStorage());

    }



    private void uploadToStorage() {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Post Images/")
                .child(user.getUid()).child("" + System.currentTimeMillis());

        dialog.show();

        storageReference.putFile(picUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        uploadData(uri.toString()));
                    }
                    else dialog.dismiss();
                });
    }

    private void clickListener() {
        adapter.SendImage(uri -> {
            Log.e("picUri", uri.toString());
            picUri = uri;
            Glide.with(getContext())
                    .load(picUri)
                    .into(imageView);
        });
        imageView.setVisibility(View.VISIBLE);

    }

    private void uploadData(String imageUrl) {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid()).collection("Post Images");


        String descriptio = editText.getText().toString();
        String id = reference.document().getId();

        // for likes count creating list
        List<String> list = new ArrayList<>();
        //this list for comments

        Map<String, Object> map = new HashMap<>();
        map.put("description", descriptio);
        map.put("id", id);
        map.put("imageUrl", imageUrl);
        map.put("timeStamp",FieldValue.serverTimestamp());

        map.put("profileImage",String.valueOf(user.getPhotoUrl()));
        map.put("name", user.getDisplayName());
        map.put("likes",list);
        map.put("comment","");
        map.put("uid" ,user.getUid());

        reference.document(id).set(map)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        dialog.dismiss();

                        System.out.println();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Dexter.withContext(getContext())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download");
                                    if (file.exists()) {
                                        File[] files = file.listFiles();
                                        assert files != null;
                                        list.clear();
                                        for (File file1 : files) {
                                            if (file1.getAbsolutePath().endsWith(".jpg") || file1.getAbsolutePath().endsWith(".png")) {
                                                list.add(new GalleryImages(Uri.fromFile(file1)));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });
    }

}