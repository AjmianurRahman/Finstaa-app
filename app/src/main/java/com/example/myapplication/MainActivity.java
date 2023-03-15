package com.example.myapplication;

import static com.example.myapplication.utils.Constents.PREF_DIRECTORY;
import static com.example.myapplication.utils.Constents.PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.myapplication.adapter.MyPagerAdapter;
import com.example.myapplication.fragments.SearchFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnDataPass {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    MyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initalize();

        addTabs();
    }

    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.search));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.add));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.bell_fill));
       // tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.user_fill));

        SharedPreferences preferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        String directory = preferences.getString(PREF_DIRECTORY,"");

        Bitmap bitmap = loadProfileImage(directory);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        tabLayout.addTab(tabLayout.newTab().setIcon(drawable));

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.getTabAt(0).setIcon(R.drawable.home_fill);

        adapter = new MyPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) );
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.home_fill);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.search_fill);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.plus);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.bell);
                        break;
                   /* case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.user);
                        break;*/

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.home);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.add);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.bell_fill);
                        break;
                   /* case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.user_fill);
                        break;*/

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.home);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.add);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.bell_fill);
                        break;
                   /* case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.user_fill);
                        break;*/

                }
            }
        });
    }

    private Bitmap loadProfileImage(String directory){
        try {
            File file = new File(directory, "profile.png");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    private void initalize() {
        viewPager = findViewById(R.id.viewPagerId);
        tabLayout = findViewById(R.id.tablayoutId);

    }

    public static String USER_UID;
    public static Boolean IS_SEARCHED_USER = false;

    @Override
    public void onChange(String uid) {
        viewPager.setCurrentItem(4);
        USER_UID = uid;
        IS_SEARCHED_USER = true;
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem()!=0){
            IS_SEARCHED_USER = false;
            viewPager.setCurrentItem(0);
        }else
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // you are active now
        updateStatus(true);
    }

    @Override
    protected void onPause() {
        updateStatus(false);
        super.onPause();
    }

    // to find if user is active input data to firebase
    void updateStatus(boolean status){
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);

        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(map);
    }
}