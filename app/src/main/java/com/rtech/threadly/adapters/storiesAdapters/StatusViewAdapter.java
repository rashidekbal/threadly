package com.rtech.threadly.adapters.storiesAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.StoryOpenCallback;
import com.rtech.threadly.models.StoriesModel;

import java.util.ArrayList;

public class StatusViewAdapter extends RecyclerView.Adapter<StatusViewAdapter.viewHolder> {
    Context context;
    ArrayList<StoriesModel> list;
    StoryOpenCallback callback;
    public StatusViewAdapter(Context c, ArrayList<StoriesModel> list, StoryOpenCallback callback){
        this.context=c;
        this.list=list;
        setHasStableIds(true);
        this.callback=callback;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public StatusViewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.status_card,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewAdapter.viewHolder holder, int position) {
        holder.storyLayout.setOnClickListener(v->{

            callback.openStoryOf(list.get(position).userid,list.get(position).userProfile,list,position);
        });
        if(list.get(position).isSeen){
            holder.dpBorder.setBackground(AppCompatResources.getDrawable(context,R.drawable.circle_grey));
        }else{
            holder.dpBorder.setBackground(AppCompatResources.getDrawable(context,R.drawable.red_circle));
        }
        Glide.with(context).load(list.get(position).userProfile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.profileImg);

        holder.userid.setText(list.get(position).userid);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView userid;
        ImageView profileImg;
        CardView dpBorder;
        LinearLayout storyLayout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg=itemView.findViewById(R.id.profile_img);
            userid=itemView.findViewById(R.id.userid);
            dpBorder=itemView.findViewById(R.id.StoryOuterBorder_color);
            storyLayout=itemView.findViewById(R.id.storyLayout);
        }
    }
}
