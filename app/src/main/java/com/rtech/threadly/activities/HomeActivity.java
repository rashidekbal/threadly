package com.rtech.threadly.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationBarView;
import com.rtech.threadly.R;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityHomeBinding;
import com.rtech.threadly.fragments.ReelsFragment;
import com.rtech.threadly.fragments.profileFragments.ChangeProfileCameraFragment;
import com.rtech.threadly.fragments.profileFragments.ChangeProfileImageSelector;
import com.rtech.threadly.fragments.profileFragments.EditBioFragment;
import com.rtech.threadly.fragments.profileFragments.EditNameFragment;
import com.rtech.threadly.fragments.profileFragments.EditProfileMainFragment;
import com.rtech.threadly.fragments.profileFragments.UsernameEditFragment;
import com.rtech.threadly.fragments.homeFragment;
import com.rtech.threadly.fragments.post_fragment;
import com.rtech.threadly.fragments.profileFragments.profileFragment;
import com.rtech.threadly.fragments.profileFragments.profileUploadFinalPreview;
import com.rtech.threadly.fragments.searchFragment;
import com.rtech.threadly.fragments.storiesFragment.ViewStoriesFragment;
import com.rtech.threadly.interfaces.CameraFragmentInterface;
import com.rtech.threadly.interfaces.FragmentItemClickInterface;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.interfaces.StoryOpenCallback;
import com.rtech.threadly.utils.ExoplayerUtil;

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
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},115);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(0); // clear light status flag
            window.setStatusBarColor(Color.BLACK); // or any dark color you're using
        }

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
                    addFragment(new homeFragment((userid,profilePic) -> {
                        ViewStoriesFragment fragment=new ViewStoriesFragment(()->{
                            binding.bottomNavigation.setVisibility(View.VISIBLE);
                            binding.cardView.setBackgroundColor(Color.WHITE);
                        });
                        Bundle bundle=new Bundle();
                        bundle.putString("userId",userid);
                        bundle.putString("profilePic",profilePic);
                        fragment.setArguments(bundle);
                        addFragment(fragment);
                        binding.bottomNavigation.setVisibility(View.INVISIBLE);
                        binding.cardView.setBackgroundColor(Color.BLACK);
                    }));
                    currentFragment=item.getItemId();






                } else if (item.getItemId()==R.id.search) {
                    currentFragment=item.getItemId();
                    addFragment(new searchFragment());

                } else if (item.getItemId()==R.id.add_post) {
                    startActivity(new Intent(HomeActivity.this, AddPostActivity.class));


                } else if (item.getItemId()==R.id.reels) {
                    currentFragment=item.getItemId();
                    addFragment(new ReelsFragment());

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

                                        addFragment(new EditNameFragment());

                                    }else if(v.getId()==R.id.username_layout){
                                        addFragment(new UsernameEditFragment());


                                    }else if(v.getId()==R.id.bio_layout){
                                        addFragment(new EditBioFragment());

                                    } else if (v.getId()==R.id.openCameraButton) {
                                        addFragmentNoBackStack(new ChangeProfileCameraFragment(new CameraFragmentInterface() {
                                            @Override
                                            public void onCapture(String filePath, String mediaType) {
                                                Bundle bundle =new Bundle();
                                                bundle.putString("path",filePath);
                                                profileUploadFinalPreview fragment=new profileUploadFinalPreview();
                                                fragment.setArguments(bundle);
                                                addFragmentNoBackStack(fragment);
                                            }
                                        }));

                                    }else if(v.getId()==R.id.pictureSelector_gallery_btn){
                                        addFragment(new ChangeProfileImageSelector());

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
        loginInfo= Core.getPreference();




    }

    private void addFragment(Fragment fragment ){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentHolder,fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
    private void addFragmentNoBackStack(Fragment fragment ){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentHolder,fragment);
        transaction.commit();

    }
    private void addFragment(Fragment fragment,String url,int postid){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExoplayerUtil.release();
    }
}


    //    @Override
//    protected void onResume() {
//        super.onResume();
//        bottomNavigationView.setSelectedItemId(currentFragment);
//    }
