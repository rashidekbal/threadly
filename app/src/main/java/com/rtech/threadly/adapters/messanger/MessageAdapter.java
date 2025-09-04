package com.rtech.threadly.adapters.messanger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;

import org.w3c.dom.Text;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder>{
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
    public MessageAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.msg_card,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.viewHolder holder, int position) {

        if(list.get(position).getSenderId().equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){
            //if i had sent
            int deliveryStatus=list.get(position).getDeliveryStatus();
            holder.senderProfile.setVisibility(View.GONE);
            holder.recMsg.setVisibility(View.GONE);
            holder.sent_msg_layout.setVisibility(View.VISIBLE);
            holder.sentMsg.setText(list.get(position).getMsg());
            holder.status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
        }else {
            if(position>0){
                if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                    holder.senderProfile.setVisibility(View.GONE);}
                else {
                    holder.senderProfile.setVisibility(View.VISIBLE);
                    Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.senderProfile);

                }
            } else {
                holder.senderProfile.setVisibility(View.VISIBLE);
                Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.senderProfile);

            }

            holder.recMsg.setVisibility(View.VISIBLE);
            holder.sent_msg_layout.setVisibility(View.GONE);
            holder.recMsg.setText(list.get(position).getMsg());


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView recMsg,sentMsg;
        ImageView senderProfile,status_img;
        LinearLayout sent_msg_layout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            recMsg=itemView.findViewById(R.id.rec_text_msg);
            sentMsg=itemView.findViewById(R.id.sent_text_msg);
            senderProfile=itemView.findViewById(R.id.senderProfile);
            status_img=itemView.findViewById(R.id.status_img);
            sent_msg_layout=itemView.findViewById(R.id.sent_msg_layout);

        }
    }
}
