package com.rtech.threadly.adapters.messanger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.POJO.ConvMessageCounter;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.models.UsersModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.viewHolder> {
    Context context;
    ArrayList<HistorySchema> list;
    OnUserSelectedListener onUserSelectedListener;
    List<ConvMessageCounter>unreadCountList;

    public HistoryListAdapter(Context context, OnUserSelectedListener onUserSelectedListener, ArrayList<HistorySchema> list,List<ConvMessageCounter>conterList) {
        this.unreadCountList=conterList;
        this.context = context;
        this.onUserSelectedListener = onUserSelectedListener;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.message_profile_card,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int rawPosition) {
        int position=holder.getLayoutPosition();
        setUpUnreadCount(holder,position);
        holder.username_text.setText(list.get(position).getUsername());
        holder.mediaSendCameraBtn.setVisibility(View.GONE);
        holder.mediaHint_layout.setVisibility(View.GONE);
        Glide.with(context).load(list.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.userProfile_img);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserSelectedListener.onSelect(new UsersModel(list.get(position).getUuid(),list.get(position).getUsername(),list.get(position).getUserId(),list.get(position).getProfilePic()));
            }
        });
        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // implement logic for deleting of conversation also sync with server
                return false;
            }
        });



    }

    private void setUpUnreadCount(viewHolder holder, int position) {
        if(unreadCountList.isEmpty()){
            holder.recent.setVisibility(View.GONE);
            return ;
        }
        int index=findCounterObjectIndex(getConversationId(position));
        if(index==-1||unreadCountList.get(index).unreadCount==0) {holder.recent.setVisibility(View.GONE);
        return;}
        holder.recent.setVisibility(View.VISIBLE);
        holder.recent.setText(unreadCountList.get(index).unreadCount.toString()+" new message's ");
    }

    private int findCounterObjectIndex(String conversationId) {
       for(int i=0;i<unreadCountList.size();i++){
           if(unreadCountList.get(i).conversationId.equals(conversationId))return i;
       }
       return -1;
    }

    private String getConversationId(int position){
        return list.get(position).getConversationId();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parent;
ImageView userProfile_img,mediaSendCameraBtn,secondThumbnail,firstThumbnail;
TextView username_text,recent,unReadCounter;
RelativeLayout mediaHint_layout,mainLayout;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile_img=itemView.findViewById(R.id.profile);
            mediaSendCameraBtn=itemView.findViewById(R.id.sendMedia_btn);
            username_text=itemView.findViewById(R.id.username);
            secondThumbnail=itemView.findViewById(R.id.secondThumbnail);
            firstThumbnail=itemView.findViewById(R.id.firstThumbnail);
            mediaHint_layout=itemView.findViewById(R.id.mediaHint_layout);
            mainLayout=itemView.findViewById(R.id.mainLayout);
            unReadCounter=itemView.findViewById(R.id.unReadCounter);
            recent=itemView.findViewById(R.id.recent);

        }
    }
}
