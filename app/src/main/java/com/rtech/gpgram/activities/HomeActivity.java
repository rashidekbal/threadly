package com.rtech.gpgram.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rtech.gpgram.R;
import com.rtech.gpgram.fragments.homeFragment;
import com.rtech.gpgram.fragments.notificationFragment;
import com.rtech.gpgram.fragments.post_fragment;
import com.rtech.gpgram.fragments.profileFragment;
import com.rtech.gpgram.fragments.searchFragment;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;

public class HomeActivity extends AppCompatActivity {
SharedPreferences loginInfo;
SharedPreferences.Editor prefEditor;
BottomNavigationView bottomNavigationView;
int currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });



        init();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.home){
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    addFragment(new homeFragment());
                    currentFragment=item.getItemId();






                } else if (item.getItemId()==R.id.search) {
                    currentFragment=item.getItemId();
                    addFragment(new searchFragment());

                } else if (item.getItemId()==R.id.add_post) {
                    startActivity(new Intent(HomeActivity.this, AddpostActivity.class));


                } else if (item.getItemId()==R.id.notification) {
                    currentFragment=item.getItemId();
                    addFragment(new notificationFragment());

                }else if (item.getItemId()==R.id.profile){
                    currentFragment=item.getItemId();
                    addFragment(new profileFragment(new Post_fragmentSetCallback() {
                        @Override
                        public void openPostFragment(String url, int postid) {
                            addFragment(new post_fragment(), url, postid);
                        }
                    }));
                }

                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount() == 0) {

                 HomeActivity.super.onBackPressed();
                    }
                }


        });



    }
    protected void init(){
//        loginInfo=getSharedPreferences("loginInfo", MODE_PRIVATE);
//        prefEditor=loginInfo.edit();
//        logOut_btn=findViewById(R.id.logout);
    bottomNavigationView=findViewById(R.id.bottom_navigation);


    }

    private void addFragment(Fragment fragment ){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentHolder,fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }  private void addFragment(Fragment fragment,String url,int postid){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putString("url",url);
        bundle.putInt("postid",postid);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragmentHolder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        bottomNavigationView.setSelectedItemId(currentFragment);
//    }
}