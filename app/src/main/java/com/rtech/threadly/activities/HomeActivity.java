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
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rtech.threadly.R;
import com.rtech.threadly.constants.HomeActivityFragmentsIdEnum;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityHomeBinding;
import com.rtech.threadly.fragments.CustomPostFeed.CustomPostFeedFragment;
import com.rtech.threadly.fragments.ReelsFragment;
import com.rtech.threadly.fragments.profileFragments.ChangeProfileCameraFragment;
import com.rtech.threadly.fragments.profileFragments.ChangeProfileImageSelector;
import com.rtech.threadly.fragments.profileFragments.EditBioFragment;
import com.rtech.threadly.fragments.profileFragments.EditNameFragment;
import com.rtech.threadly.fragments.profileFragments.EditProfileMainFragment;
import com.rtech.threadly.fragments.profileFragments.UsernameEditFragment;
import com.rtech.threadly.fragments.homeFragment;
import com.rtech.threadly.fragments.profileFragments.profileFragment;
import com.rtech.threadly.fragments.profileFragments.profileUploadFinalPreview;
import com.rtech.threadly.fragments.searchFragments.searchFragment;
import com.rtech.threadly.fragments.storiesFragment.ViewStoriesFragment;
import com.rtech.threadly.interfaces.FragmentItemClickInterface;
import com.rtech.threadly.interfaces.OnDestroyFragmentCallback;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.interfaces.StoriesBackAndForthInterface;
import com.rtech.threadly.interfaces.StoryOpenCallback;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ExplorePostsViewModel;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
SharedPreferences loginInfo;
    StoryOpenCallback storyOpenCallback;
OnDestroyFragmentCallback onDestroyStoriesFragmentCallback;
    ProfileViewModel profileViewModel;
    ExplorePostsViewModel explorePostsViewModel;
boolean isFirstLaunch=true;

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

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},115);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(0); // clear light status flag
        window.setStatusBarColor(Color.BLACK); // or any dark color you're using

        init();
        Executors.newSingleThreadExecutor().execute(() -> {
            profileViewModel.getProfileLiveData();
            profileViewModel.getUserPostsLiveData();
        });



        onDestroyStoriesFragmentCallback= () -> {
            binding.bottomNavigation.setVisibility(View.VISIBLE);
            binding.cardView.setBackgroundColor(getResources().getColor(R.color.white));

        };

        storyOpenCallback = (userid, profilePic, list, position) -> {
            ViewStoriesFragment fragment=new ViewStoriesFragment(onDestroyStoriesFragmentCallback, new StoriesBackAndForthInterface() {
                @Override
                public void previous(int position2) {
                    if(position>0) {
                        storyOpenCallback.openStoryOf(list.get(position - 1).userid, list.get(position - 1).userProfile, list, position - 1);
                    }else{
//                            getSupportFragmentManager().popBackStack();
                        addFragment(new homeFragment(storyOpenCallback),HomeActivityFragmentsIdEnum.HOME.toString());
                        binding.bottomNavigation.setVisibility(View.VISIBLE);
                        binding.cardView.setBackgroundColor(getResources().getColor(R.color.white));

                    }
                }

                @Override
                public void next(int position2, int size) {
                    if(position<list.size()-1){
                        storyOpenCallback.openStoryOf(list.get(position+1).userid,list.get(position+1).userProfile,list,position+1);
                    }else{
//                            getSupportFragmentManager().popBackStack();
                        addFragment(new homeFragment(storyOpenCallback),HomeActivityFragmentsIdEnum.HOME.toString());
                        binding.bottomNavigation.setVisibility(View.VISIBLE);
                binding.cardView.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                }
            });
//                ReUsableFunctions.ShowToast("opening for " +userid);
            Bundle bundle=new Bundle();
            bundle.putString("userId",userid);
            bundle.putString("profilePic",profilePic);
            fragment.setArguments(bundle);
            addFragment(fragment,HomeActivityFragmentsIdEnum.VIEW_STORIES_FRAGMENT.toString());
            binding.bottomNavigation.setVisibility(View.INVISIBLE);
            binding.cardView.setBackgroundColor(Color.BLACK);


        };
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

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.home){
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                addFragment(
                        new homeFragment(
//                                    (userid,profilePic) -> {
//                        ViewStoriesFragment fragment=new ViewStoriesFragment(() -> {
//                            binding.bottomNavigation.setVisibility(View.VISIBLE);
//                            binding.cardView.setBackgroundColor(Color.WHITE);
//                        }, new StoriesBackAndForthInterface() {
//                            @Override
//                            public void previous(int position) {
//
//                            }
//
//                            @Override
//                            public void next(int position, int size) {
//
//                            }
//                        });
//                        Bundle bundle=new Bundle();
//                        bundle.putString("userId",userid);
//                        bundle.putString("profilePic",profilePic);
//                        fragment.setArguments(bundle);
//                        addFragment(fragment);
//                        binding.bottomNavigation.setVisibility(View.INVISIBLE);
//                        binding.cardView.setBackgroundColor(Color.BLACK);
//                    }
                                storyOpenCallback
                        ), HomeActivityFragmentsIdEnum.HOME.toString());
                currentFragment=item.getItemId();






            } else if (item.getItemId()==R.id.search) {

                if(currentFragment!=R.id.search){
                    if(doesThisFragmentExistInStack(HomeActivityFragmentsIdEnum.SEARCH.toString())){
                      getSupportFragmentManager().popBackStack(HomeActivityFragmentsIdEnum.SEARCH.toString(),0);
                    }else{

                        addFragment(new searchFragment(),HomeActivityFragmentsIdEnum.SEARCH.toString());
                    }

                }
                currentFragment=item.getItemId();


            } else if (item.getItemId()==R.id.add_post) {

                startActivity(new Intent(HomeActivity.this, AddPostActivity.class).putExtra("title","New Post"));


            } else if (item.getItemId()==R.id.reels) {
                if(currentFragment!=R.id.reels){
                    if(doesThisFragmentExistInStack(HomeActivityFragmentsIdEnum.REELS.toString())){
                        getSupportFragmentManager().popBackStack(HomeActivityFragmentsIdEnum.REELS.toString(),0);
                    }else{
                        addFragment(new ReelsFragment(),HomeActivityFragmentsIdEnum.REELS.toString());
                    }
                    currentFragment=item.getItemId();
               }

            }else if (item.getItemId()==R.id.profile){
                if(currentFragment!=R.id.profile){
                    if(doesThisFragmentExistInStack(HomeActivityFragmentsIdEnum.PROFILE.toString())){
                        getSupportFragmentManager().popBackStack(HomeActivityFragmentsIdEnum.PROFILE.toString(),0);
                    }else{
                        addFragment(new profileFragment(new Post_fragmentSetCallback() {

                            @Override
                            public void openPostFragment(ArrayList<Posts_Model> postsArray, int position) {
                                ArrayList<ExtendedPostModel> postArrayList=new ArrayList<>();
                                for(Posts_Model model:postsArray){
                                    postArrayList.add(new ExtendedPostModel(model.getCONTENT_TYPE(),
                                            model.getPostId(),
                                            model.getUserId(),
                                            model.getUsername(),
                                            model.getUserDpUrl(),
                                            model.getPostUrl(),
                                            model.getCaption(),
                                            model.getCreatedAt(),
                                            model.getLikedBy(),
                                            model.getLikeCount(),
                                            model.getCommentCount(),
                                            model.getShareCount(),
                                            model.getIsliked()?1:0,
                                            model.isVideo(),
                                            model.isFollowed()));
                                }
                                CustomPostFeedFragment customPostFeedFragment=new CustomPostFeedFragment();
                                Bundle data=new Bundle();
                                data.putParcelableArrayList("postList",postArrayList);
                                data.putInt("position",position);
                                customPostFeedFragment.setArguments(data);
                                addFragment(customPostFeedFragment,HomeActivityFragmentsIdEnum.CUSTOM_POST_FEED_FRAGMENT.toString());
                            }

                            @Override
                            public void openEditor() {

                                addFragment(new EditProfileMainFragment(new FragmentItemClickInterface() {
                                    @Override
                                    public void onItemClick(@Nullable  View v) {

                                        assert v != null;
                                        if(v.getId()==R.id.name_layout){

                                            addFragment(new EditNameFragment(),HomeActivityFragmentsIdEnum.EDIT_NAME_FRAGMENT.toString());

                                        }else if(v.getId()==R.id.username_layout){
                                            addFragment(new UsernameEditFragment(),HomeActivityFragmentsIdEnum.USERNAME_EDIT_FRAGMENT.toString());


                                        }else if(v.getId()==R.id.bio_layout){
                                            addFragment(new EditBioFragment(),HomeActivityFragmentsIdEnum.BIO_EDIT_FRAGMENT.toString());

                                        } else if (v.getId()==R.id.openCameraButton) {
                                            addFragmentNoBackStack(new ChangeProfileCameraFragment((filePath, mediaType) -> {
                                                Bundle bundle =new Bundle();
                                                bundle.putString("path",filePath);
                                                profileUploadFinalPreview fragment=new profileUploadFinalPreview();
                                                fragment.setArguments(bundle);
                                                addFragmentNoBackStack(fragment);
                                            }));

                                        }else if(v.getId()==R.id.pictureSelector_gallery_btn){
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                addFragment(new ChangeProfileImageSelector(),HomeActivityFragmentsIdEnum.CHANGE_PROFILE_PIC_FRAGMENT.toString());
                                            }else{
                                                ReUsableFunctions.ShowToast("something went wrong");
                                            }

                                        }

                                    }

                                    @Override
                                    public void onFragmentDestroy() {
                                        binding.bottomNavigation.setVisibility(View.VISIBLE);

                                    }
                                }),HomeActivityFragmentsIdEnum.PROFILE_EDITOR_MAIN.toString());
                                binding.bottomNavigation.setVisibility(View.INVISIBLE);

                            }

                        }),HomeActivityFragmentsIdEnum.PROFILE.toString());
                    }

                }
                currentFragment=item.getItemId();


            }

            return true;
        });
        binding.bottomNavigation.setSelectedItemId(R.id.home);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            int backstackEntryCount=getSupportFragmentManager().getBackStackEntryCount();
            if(backstackEntryCount==0) {
             HomeActivity.super.onBackPressed();
            }



        });





    }

    private boolean doesThisFragmentExistInStack(String string) {
        FragmentManager manager=getSupportFragmentManager();
        int entryCount=manager.getBackStackEntryCount();
        for(int i=0;i<entryCount;i++){
            String entryID=manager.getBackStackEntryAt(i).getName();
            if(entryID==null)return false;
            if(entryID.equals(string)){
                return true;
            }
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int backstackEntryCount=getSupportFragmentManager().getBackStackEntryCount();
            String entryID=getSupportFragmentManager().getBackStackEntryAt(backstackEntryCount-1).getName();
            assert entryID != null;
                if(entryID.equals(HomeActivityFragmentsIdEnum.HOME.toString())){
                    binding.bottomNavigation.setSelectedItemId(R.id.home);

                }else if(entryID.equals(HomeActivityFragmentsIdEnum.REELS.toString())){
                    binding.bottomNavigation.setSelectedItemId(R.id.reels);

                }else if(entryID.equals(HomeActivityFragmentsIdEnum.PROFILE.toString())){
                    binding.bottomNavigation.setSelectedItemId(R.id.profile);

                }else if(entryID.equals(HomeActivityFragmentsIdEnum.SEARCH.toString())){

                    binding.bottomNavigation.setSelectedItemId(R.id.search);

                }




    }

    protected void init(){
        loginInfo= Core.getPreference();
        profileViewModel=new ViewModelProvider(this).get(ProfileViewModel.class);
        explorePostsViewModel=new ViewModelProvider(this).get(ExplorePostsViewModel.class);
        explorePostsViewModel.loadExploreFeed();




    }

    private void addFragment(Fragment fragment,String fragmentId ){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentHolder,fragment);
        transaction.addToBackStack(fragmentId);
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
    protected void onResume() {
        super.onResume();
        ExoplayerUtil.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExoplayerUtil.release();



    }

}



