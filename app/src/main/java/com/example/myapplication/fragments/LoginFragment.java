package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText email, pass;
    private Button login;
    private TextView signup, forget;
    private CircleImageView googleSignIn;
    ConstraintLayout layout;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.EmaildId);
        pass = view.findViewById(R.id.PasswordId);
        login = view.findViewById(R.id.signUpId);
        forget = view.findViewById(R.id.forgotPassId);
        signup = view.findViewById(R.id.backToLoginId2);
        layout = view.findViewById(R.id.frameId2);
        googleSignIn = view.findViewById(R.id.googleSignIn);


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if (getActivity() != null)
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        googleSignIn.setOnClickListener(this);
        login.setOnClickListener(this);
        forget.setOnClickListener(this);
        signup.setOnClickListener(this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account;
        account = GoogleSignIn.getLastSignedInAccount(getActivity());


        Map<String, Object> userData = new HashMap<>();

        assert account != null;
        userData.put("name", account.getDisplayName());
        userData.put("email", account.getEmail());
        userData.put("password", "google do not provide");
        assert user != null;
        userData.put("nid", user.getUid());
        userData.put("profileImage", String.valueOf(account.getPhotoUrl()));
        userData.put("bio"," ");
        userData.put("followers", 0);
        userData.put("following", 0);
        userData.put("number of Posts", 0);

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (getActivity()!=null){
                                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }


                        } else {
                            Toast.makeText(getContext(), "Error:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backToLoginId2) {
            if (((FragmentReplaceActivity) getActivity()) != null) {
                ((FragmentReplaceActivity) getActivity()).setFragment(new CreatAccountFragment());
            }
        }
        if (view.getId() == R.id.forgotPassId) {

            if (((FragmentReplaceActivity) getActivity()) != null) {
                ((FragmentReplaceActivity) getActivity()).setFragment(new ForgotPassword());
            }
            //forgot pass
        }
        if (view.getId() == R.id.signUpId) {
            String memail = email.getText().toString().trim();
            String mpass = pass.getText().toString();
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

            mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        if (!user.isEmailVerified()) {
                            user.sendEmailVerification();
                            Snackbar.make(layout, "A Varification email has been sent \nplease varify yourself"
                                    , Snackbar.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }


                }
            });
        }
        if (view.getId() == R.id.googleSignIn) {
            signIn();
        }

    }

}