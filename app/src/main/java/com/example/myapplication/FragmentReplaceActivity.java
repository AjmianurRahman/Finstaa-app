package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.myapplication.fragments.CommentFragment;
import com.example.myapplication.fragments.CreatAccountFragment;
import com.example.myapplication.fragments.ForgotPassword;
import com.example.myapplication.fragments.LoginFragment;

public class FragmentReplaceActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_replace);


        frameLayout = findViewById(R.id.frameLayout);

        boolean isComment = getIntent().getBooleanExtra("isComment", false);
        if (isComment){
            setFragment(new CommentFragment());
        }else{
            setFragment(new LoginFragment() );
        }


    }

    public void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
       if (fragment instanceof CreatAccountFragment){
           fragmentTransaction.addToBackStack(null);
       }
       if(fragment instanceof ForgotPassword){
           fragmentTransaction.addToBackStack(null);
       }

       if (fragment instanceof CommentFragment){
           String id = getIntent().getStringExtra("id");
           String uid = getIntent().getStringExtra("uid");

           Bundle bundle = new Bundle();
           bundle.putString("id", id);
           bundle.putString("uid", uid);
           fragment.setArguments(bundle);

       }

        fragmentTransaction.replace(frameLayout.getId(),fragment).commit();
    }
}