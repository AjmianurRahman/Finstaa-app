package com.example.myapplication.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.FragmentReplaceActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CreatAccountFragment extends Fragment implements View.OnClickListener {

    private EditText name, email, pass, conPass;
    private Button signIn;
    private TextView backToSignIn;

    List<String> list = new ArrayList<>();
    List<String> list2 = new ArrayList<>();

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore fs;

    String memail, mpass;

    public CreatAccountFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_creat_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.NameId);
        email = view.findViewById(R.id.emailId2);
        pass = view.findViewById(R.id.PasswordId);
        conPass = view.findViewById(R.id.ConfirmPasswordId);
        signIn = view.findViewById(R.id.signUpId2);
        backToSignIn = view.findViewById(R.id.backToLoginId);

        mAuth = FirebaseAuth.getInstance();

        backToSignIn.setOnClickListener(this);
        signIn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backToLoginId) {
            if (((FragmentReplaceActivity) getActivity()) != null) {
                ((FragmentReplaceActivity) getActivity()).setFragment(new LoginFragment());
            }
        }

        if (view.getId() == R.id.signUpId2) {
            memail = email.getText().toString().trim();
            mpass = pass.getText().toString();
            if (name.getText().toString().isEmpty()) {
                name.setError("Enter Name");
                name.requestFocus();
                return;
            }


            if (memail.isEmpty()) {
                email.setError("Enter an email address");
                email.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(memail).matches()) {
                email.setError("Enter a valid email address");
                email.requestFocus();
                return;
            }

            //checking the validity of the password
            if (mpass.isEmpty()) {
                pass.setError("Enter a password");
                pass.requestFocus();
                return;
            }

            if (pass.length() < 6) {
                pass.setError("Password Length Must be 8 Digits");
                pass.requestFocus();
                return;
            }
            if (!conPass.getText().toString().isEmpty() && !mpass.equals(conPass.getText().toString())) {
                conPass.setError("Password dosn't match");
                conPass.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(memail, mpass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            user = mAuth.getCurrentUser();

                            //setting the user name for home feed image
                            String image = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fmedia.istockphoto.com%2Fid%2F1300845620%2Fvector%2Fuser-icon-flat-isolated-on-white-background-user-symbol-vector-illustration.jpg%3Fs%3D612x612%26w%3D0%26k%3D20%26c%3DyBeyba0hUkh14_jgv1OKqIH0CCSWU_4ckRkAoy2p73o%3D&imgrefurl=https%3A%2F%2Fwww.istockphoto.com%2Fillustrations%2Fperson-icon&tbnid=3ieVDLEJVcWu5M&vet=12ahUKEwjqjvTV1pr9AhXLmdgFHZJXAXsQMygAegUIARDoAQ..i&docid=nMa4EhgTujOH9M&w=612&h=612&q=person%20image%20icon&ved=2ahUKEwjqjvTV1pr9AhXLmdgFHZJXAXsQMygAegUIARDoAQ";
                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                            request.setDisplayName(name.getText().toString());
                            request.setPhotoUri(Uri.parse(image));
                            user.updateProfile(request.build());

                            loadDataToFirestore();
                            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            //varify email
                            user.sendEmailVerification();

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

    private void loadDataToFirestore() {

        fs = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();

        userData.put("name", name.getText().toString());
        userData.put("email", memail);
        userData.put("password", mpass);
        assert user != null;
        userData.put("uid", user.getUid());
        userData.put("profileImage", " ");
        userData.put("bio", " ");

        userData.put("followers", list);
        userData.put("following", list2);

        userData.put("number of Posts", 0);


        fs.collection("Users").document(user.getUid())
                .set(userData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(getActivity().getApplicationContext(), "Data uploaded to Firestore", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}