package com.rtech.threadly.interfaces;

import com.rtech.threadly.models.Posts_Model;

import java.util.ArrayList;

public interface Post_fragmentSetCallback {
    void openPostFragment(ArrayList<Posts_Model> postsArray, int position);


    void openEditor();

}
