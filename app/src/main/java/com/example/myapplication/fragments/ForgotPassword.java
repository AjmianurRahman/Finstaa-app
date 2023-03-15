package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.FragmentReplaceActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends Fragment implements View.OnClickListener {

    private EditText email;
    private Button recover;
    private TextView backToLogin;

    FirebaseAuth mAuth;

    public ForgotPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email =  view.findViewById(R.id.EmaildId3);
        recover = view.findViewById(R.id.recoverPassButtonId);
        backToLogin = view.findViewById(R.id.backToLogId);

        mAuth = FirebaseAuth.getInstance();

        recover.setOnClickListener(this);
        backToLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.recoverPassButtonId){
                String memail = email.getText().toString();
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

            mAuth.sendPasswordResetEmail(memail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "A Password reset email has been send", Toast.LENGTH_SHORT).show();
                                email.setText("");
                            }else {
                                Toast.makeText(getContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
        if(view.getId()==R.id.backToLogId){
            if (((FragmentReplaceActivity) getActivity()) != null) {
                ((FragmentReplaceActivity) getActivity()).setFragment(new LoginFragment());
            }
        }


    }
}