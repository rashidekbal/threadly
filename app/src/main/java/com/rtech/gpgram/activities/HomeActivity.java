package com.rtech.gpgram.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rtech.gpgram.R;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.databinding.ActivityHomeBinding;
import com.rtech.gpgram.fragments.EditBioFragment;
import com.rtech.gpgram.fragments.EditProfileMainFragment;
import com.rtech.gpgram.fragments.UsernameEditFragment;
import com.rtech.gpgram.fragments.homeFragment;
import com.rtech.gpgram.fragments.notificationFragment;
import com.rtech.gpgram.fragments.post_fragment;
import com.rtech.gpgram.fragments.profileFragment;
import com.rtech.gpgram.fragments.searchFragment;
import com.rtech.gpgram.interfaces.FragmentItemClickInterface;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;

import java.security.Permission;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
SharedPreferences loginInfo;
SharedPreferences.Editor prefEditor;
int permissionCode=786;


int currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });



        init();
        binding.bottomNavigation.setItemIconTintList(null);
        Glide.with(HomeActivity.this).asBitmap()
                        .load(loginInfo.getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null")).error(R.drawable.blank_profile).placeholder(R.drawable.blank_profile).circleCrop()
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        Drawable drawable=new BitmapDrawable(HomeActivity.this.getResources(),resource);
                                        binding.bottomNavigation.getMenu().findItem(R.id.profile).setIcon(drawable);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }

                                    @Override
                                    public void onLoadStarted(@Nullable Drawable placeholder) {
                                        super.onLoadStarted(placeholder);
                                        binding.bottomNavigation.getMenu().findItem(R.id.profile).setIcon(placeholder);

                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        super.onLoadFailed(errorDrawable);
                                        binding.bottomNavigation.getMenu().findItem(R.id.profile).setIcon(errorDrawable);

                                    }
                                });

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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
                    startActivity(new Intent(HomeActivity.this, AddPostActivity.class));


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

                        @Override
                        public void openEditor() {

                            addFragment(new EditProfileMainFragment(new FragmentItemClickInterface() {
                                @Override
                                public void onItemClick(@Nullable  View v) {

                                    if(v.getId()==R.id.name_layout){

                                        addFragment(new com.rtech.gpgram.fragments.EditNameFragment());

                                    }else if(v.getId()==R.id.username_layout){
                                        addFragment(new UsernameEditFragment());


                                    }else if(v.getId()==R.id.bio_layout){
                                        addFragment(new EditBioFragment());

                                    }

                                }

                                @Override
                                public void onfragmentDestroy() {
                                    binding.bottomNavigation.setVisibility(View.VISIBLE);

                                }
                            }));
                            binding.bottomNavigation.setVisibility(View.INVISIBLE);

                        }

                    }));
                }

                return true;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.home);
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
        loginInfo=getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME, MODE_PRIVATE);




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


    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        bottomNavigationView.setSelectedItemId(currentFragment);
//    }
