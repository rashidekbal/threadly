package com.rtech.threadly.interfaces;

import com.rtech.threadly.models.StoriesModel;

import java.util.ArrayList;

public interface StoryOpenCallback {
   void openStoryOf(String userid, String profilePic, ArrayList<StoriesModel> list,int position);
}
