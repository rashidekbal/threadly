package com.rtech.threadly.adapters.messanger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<MessageSchema> list;
    String profile;

    public MessageAdapter(Context context, List<MessageSchema> list,String profile) {
        this.context = context;
        this.list = list;
        this.profile=profile;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.text_msg_card,parent,false);
        return new TextMessageviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(list.get(position).getSenderId().equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){
            //if i had sent
            int deliveryStatus=list.get(position).getDeliveryStatus();
            ((TextMessageviewHolder)holder).senderProfile.setVisibility(View.GONE);
            ((TextMessageviewHolder)holder).recMsg.setVisibility(View.GONE);
            ((TextMessageviewHolder)holder).sent_msg_layout.setVisibility(View.VISIBLE);
            ((TextMessageviewHolder)holder).sentMsg.setText(list.get(position).getMsg());
            ((TextMessageviewHolder)holder).status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
        }else {
            if(position>0){
                if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                    ((TextMessageviewHolder)holder).senderProfile.setVisibility(View.GONE);}
                else {
                    ((TextMessageviewHolder)holder).senderProfile.setVisibility(View.VISIBLE);
                    Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into( ((TextMessageviewHolder)holder).senderProfile);

                }
            } else {
                ((TextMessageviewHolder)holder).senderProfile.setVisibility(View.VISIBLE);
                Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into( ((TextMessageviewHolder)holder).senderProfile);

            }

            ((TextMessageviewHolder)holder).recMsg.setVisibility(View.VISIBLE);
            ((TextMessageviewHolder)holder).sent_msg_layout.setVisibility(View.GONE);
            ((TextMessageviewHolder)holder).recMsg.setText(list.get(position).getMsg());


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TextMessageviewHolder extends RecyclerView.ViewHolder {
        TextView recMsg,sentMsg;
        ImageView senderProfile,status_img;
        LinearLayout sent_msg_layout;
        public TextMessageviewHolder(@NonNull View itemView) {
            super(itemView);
            recMsg=itemView.findViewById(R.id.rec_text_msg);
            sentMsg=itemView.findViewById(R.id.sent_text_msg);
            senderProfile=itemView.findViewById(R.id.senderProfile);
            status_img=itemView.findViewById(R.id.status_img);
            sent_msg_layout=itemView.findViewById(R.id.sent_msg_layout);

        }
    }
}
