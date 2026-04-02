package com.rtech.threadly.utils;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.HorizontalUsersListAdapter;
import com.rtech.threadly.adapters.UsersList_adapter_with_like_data;
import com.rtech.threadly.constants.StatsConstants;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.models.PostLiked_UserModel;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.network_managers.StoriesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostInteractedByViewerUtil {
    BottomSheetDialog bottomSheetDialog;
    ArrayList<UsersModel> listOfUsersProfile;
    ArrayList<PostLiked_UserModel> lisOfUsersViewed_and_liked_story_or_post;
    PostsManager postsManager;
    Context context;
    HorizontalUsersListAdapter adapter;
    UsersList_adapter_with_like_data usersListAdapterWithLikeData;
    ShimmerFrameLayout shimmer_comments_holder;
    RecyclerView recyclerView;
    TextView heading;
    int currentPostId;
    String currentType;
    StoriesManager storiesManager;


    public PostInteractedByViewerUtil(PostsManager postsManager, Context context) {
        this.context = context;
        this.postsManager = postsManager;
        this.listOfUsersProfile = new ArrayList<>();
        this.lisOfUsersViewed_and_liked_story_or_post=new ArrayList<>();
        setUpBottomSheetDialog();
    }
    public PostInteractedByViewerUtil(StoriesManager storiesManager, Context context) {
        this.context = context;
        this.storiesManager = storiesManager;
        this.listOfUsersProfile = new ArrayList<>();
        this.lisOfUsersViewed_and_liked_story_or_post=new ArrayList<>();
        setUpBottomSheetDialog();
    }

    private void setUpBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.TransparentBottomSheet);
        bottomSheetDialog.setContentView(R.layout.post_interaction_stats_general_bottomsheet_ui);
        FrameLayout frameLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetDialog.setCancelable(true);
        if (frameLayout != null) {
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setDraggable(true);
            bottomSheetBehavior.setState(STATE_EXPANDED);
            bottomSheetBehavior.setFitToContents(true);
        }
        shimmer_comments_holder = bottomSheetDialog.findViewById(R.id.shimmer_comments_holder);
        recyclerView = bottomSheetDialog.findViewById(R.id.recyclerView);
        heading = bottomSheetDialog.findViewById(R.id.type_text);

    }


    public void openViewer(String type, int postId) {
        if(type.equals(StatsConstants.STORY_VIEW_COUNT.toString())){
            lisOfUsersViewed_and_liked_story_or_post.clear();
            stateLoading();
            loadStoryViewersData(postId);
            heading.setText("Viewed by");
            return;
        }
        if((currentPostId!=-1&&currentType!=null)&&(currentType.equals(type)&&currentPostId==postId)){
            bottomSheetDialog.show();
            return;
        }
        listOfUsersProfile.clear();
        if (heading != null) {
            if (type.equals(StatsConstants.LIKE.toString())) heading.setText("Liked by");
            else heading.setText("Shared by");
        }
        stateLoading();
        if (type.equals(StatsConstants.LIKE.toString())) loadLikedByData(postId);
        else loadSharedByData(postId);
        currentPostId=postId;
        currentType=type;
    }
    private void loadSharedByData(int postId) {
        postsManager.getSharedByUserInfo(postId, new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                stateLoaded();
                JSONArray data = response.optJSONArray("data");
                if (data != null) {
                    extractData(data);
                }
                handleDataExtracted();

            }

            @Override
            public void onError(int errorCode, JSONObject errorObject) {
                handleError();
            }
        });

    }
    private void loadStoryViewersData(int storyId) {
        storiesManager.getStoryViewers(storyId, new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                stateLoaded();
                JSONArray data = response.optJSONArray("data");
                if (data != null) {
                    extractDataForStoryFormat(data);
                }
                handleDataExtractedForStory();

            }

            @Override
            public void onError(int errorCode, JSONObject errorObject) {
                handleError();
            }
        });

    }



    private void loadLikedByData(int postId) {
        postsManager.getLikedByUsersInfo(postId, new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                stateLoaded();
                JSONArray data = response.optJSONArray("data");
                if (data != null) {
                    extractData(data);
                }
                handleDataExtracted();

            }

            @Override
            public void onError(int errorCode, JSONObject errorObject) {
                handleError();
            }
        });

    }

    private void handleDataExtracted() {
        if (recyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new HorizontalUsersListAdapter(context, listOfUsersProfile);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    private void handleDataExtractedForStory() {
        if (recyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            usersListAdapterWithLikeData = new UsersList_adapter_with_like_data(context, lisOfUsersViewed_and_liked_story_or_post);
            recyclerView.setAdapter(usersListAdapterWithLikeData);
            usersListAdapterWithLikeData.notifyDataSetChanged();
        }
    }


    private void handleError() {
    }

    private void extractData(JSONArray data) {
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.optJSONObject(i);
            String username = obj.optString("username");
            String userid = obj.optString("userid");
            String uuid = obj.optString("uuid");
            String profilePic = obj.optString("profilepic");
            listOfUsersProfile.add(new UsersModel(uuid, username, userid, profilePic));

        }
    }
    private void extractDataForStoryFormat(JSONArray data) {
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.optJSONObject(i);
            String username = obj.optString("username");
            String userid = obj.optString("userid");
            String uuid = obj.optString("uuid");
            String profilePic = obj.optString("profilepic");
            int isLikedBy=obj.optInt("isLiked");
            lisOfUsersViewed_and_liked_story_or_post.add(new PostLiked_UserModel(uuid, username, userid, profilePic,isLikedBy));

        }
    }

    private void stateLoading() {
        if (shimmer_comments_holder != null && listOfUsersProfile.isEmpty()) {
            shimmer_comments_holder.setVisibility(View.VISIBLE);
        }
        bottomSheetDialog.show();



    }

    private void stateLoaded() {
        if (shimmer_comments_holder != null) {
            shimmer_comments_holder.setVisibility(View.GONE);
        }


    }
}
