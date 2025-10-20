package com.rtech.threadly.adapters.messanger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.activities.PostActivity;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.constants.TypeConstants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.Messanger.MessageClickCallBack;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<MessageSchema> list;
    String profile;
    MessageClickCallBack messageClickCallBack;
    int TYPE_TEXT=1;
    int TYPE_IMAGE=2;
    int TYPE_VIDEO=3;
    int TYPE_POST=4;
    int TYPE_STORY=5;
    int TYPE_DELETED=-1;
    ArrayList<String> selectedMsgUUid=new ArrayList<>();

    public MessageAdapter(Context context, List<MessageSchema> list,String profile,MessageClickCallBack callback) {
        this.context = context;
        this.list = list;
        this.profile=profile;
        this.messageClickCallBack=callback;
    }

    @Override
    public int getItemViewType(int position) {
        String type=list.get(position).getType();
        if(list.get(position).isDeleted()){
            return TYPE_DELETED;

        }
        switch (type){

            case "image":
                return TYPE_IMAGE;
            case "video":
                return TYPE_VIDEO;
            case"post":
                return TYPE_POST;
            case "story":
                return TYPE_STORY;
            default:
                return TYPE_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        switch (viewType){
            case 2: return new ImageMessageViewHolder(layoutInflater.inflate(R.layout.image_message_card,parent,false));
            case 3: return new VideoMessageViewHolder(layoutInflater.inflate(R.layout.video_message_card,parent,false));
            case 4: return new PostMessageViewHolder(layoutInflater.inflate(R.layout.post_message_card,parent,false));
            case -1: return new DeletedMessageViewHolder(layoutInflater.inflate(R.layout.deleted_msg_view,parent,false));
            default:
                return new TextMessageviewHolder(layoutInflater.inflate(R.layout.text_msg_card,parent,false));
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderView, @SuppressLint("RecyclerView") int position) {

        //-------------------   ImageMessageViewHolder -------------------//
        if(holderView instanceof ImageMessageViewHolder){
            ImageMessageViewHolder holder=(ImageMessageViewHolder) holderView;
            //if i had sent the media
            if(list.get(position).getSenderId().equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){
                int deliveryStatus=list.get(position).getDeliveryStatus();
                holder.senderProfile.setVisibility(View.GONE);
              holder.rec_msg_layout.setVisibility(View.GONE);
              holder.sent_msg_layout.setVisibility(View.VISIBLE);
              Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.sent_MediaImageView);
              holder.status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
              if (!list.get(position).getMsg().isEmpty()){
                  holder.sent_caption.setVisibility(View.VISIBLE);
                  holder.sent_caption.setText(list.get(position).getMsg());
              }else{
                  holder.sent_caption.setVisibility(View.GONE);
              }
              holder.sent_MediaImageView.setOnClickListener(v-> {
                  openMedia(list.get(position), TypeConstants.IMAGE);
              });


            }
            else{
                holder.sent_msg_layout.setVisibility(View.GONE);
                holder.rec_msg_layout.setVisibility(View.VISIBLE);
                
                // if i had  received the message

                if(position>0){
                    // if not first message
                    if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                        //if sender is same as previous message
                        holder.senderProfile.setVisibility(View.GONE);}
                    else {
                        //if sender is changed from previous
                        holder.senderProfile.setVisibility(View.VISIBLE);
                        Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.senderProfile);

                    }

                }else{
                    //if first message
                    holder.senderProfile.setVisibility(View.VISIBLE);
                    Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into( holder.senderProfile);

                }
                Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.received_MediaImageView);
                if(!list.get(position).getMsg().isEmpty()){
                    holder.rec_caption.setVisibility(View.VISIBLE);
                    holder.rec_caption.setText(list.get(position).getMsg());

                }else {
                    holder.rec_caption.setVisibility(View.GONE);
                }

                holder.received_MediaImageView.setOnClickListener(v-> openMedia(list.get(position), TypeConstants.IMAGE));



            }


        }









        //-------------------   VideoMessageViewHolder -------------------//
        else if(holderView instanceof VideoMessageViewHolder){
            VideoMessageViewHolder holder=(VideoMessageViewHolder) holderView;
            //if i had sent the media
            if(list.get(position).getSenderId().equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){
                int deliveryStatus=list.get(position).getDeliveryStatus();
                holder.senderProfile.setVisibility(View.GONE);
                holder.rec_msg_layout.setVisibility(View.GONE);
                holder.sent_msg_layout.setVisibility(View.VISIBLE);
                Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.sent_MediaImageView);
                holder.status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
                if (!list.get(position).getMsg().isEmpty()){
                    holder.sent_caption.setVisibility(View.VISIBLE);
                    holder.sent_caption.setText(list.get(position).getMsg());
                }else{
                    holder.sent_caption.setVisibility(View.GONE);
                }

                //on click of video message
                holder.sent_MediaImageView.setOnClickListener(v-> openMedia(list.get(position), TypeConstants.VIDEO));


            }else{
                holder.sent_msg_layout.setVisibility(View.GONE);
                holder.rec_msg_layout.setVisibility(View.VISIBLE);

                // if i had  received the message

                if(position>0){
                    // if not first message
                    if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                        //if sender is same as previous message
                        holder.senderProfile.setVisibility(View.GONE);}
                    else {
                        //if sender is changed from previous
                        holder.senderProfile.setVisibility(View.VISIBLE);
                        Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.senderProfile);

                    }

                }else{
                    //if first message
                    holder.senderProfile.setVisibility(View.VISIBLE);
                    Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into( holder.senderProfile);

                }
                Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.received_MediaImageView);
                if(!list.get(position).getMsg().isEmpty()){
                    holder.rec_caption.setVisibility(View.VISIBLE);
                    holder.rec_caption.setText(list.get(position).getMsg());

                }else {
                    holder.rec_caption.setVisibility(View.GONE);
                }
                //on click of media message
                holder.received_MediaImageView.setOnClickListener(v-> openMedia(list.get(position), TypeConstants.VIDEO));


            }


        }













        //-------------------   PostMessageViewHolder -------------------//
        else if (holderView instanceof PostMessageViewHolder) {
            PostMessageViewHolder holder=(PostMessageViewHolder) holderView;
            //if i had sent the post
            if(list.get(position).getSenderId().equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){
                int deliveryStatus=list.get(position).getDeliveryStatus();
                holder.senderProfile.setVisibility(View.GONE);
                holder.rec_msg_layout.setVisibility(View.GONE);
                holder.sent_msg_layout.setVisibility(View.VISIBLE);
                Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.sent_MediaImageView);
                holder.status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
                if (!list.get(position).getMsg().isEmpty()||!list.get(position).getMsg().isBlank()){
                    holder.sent_caption.setVisibility(View.VISIBLE);
                    holder.sent_caption.setText(list.get(position).getMsg());
                }else{
                    holder.sent_caption.setVisibility(View.GONE);
                }

                //on click of video message i had sent
                holder.sent_MediaImageView.setOnClickListener(v-> openPost(list.get(position)));


            }
            else{
                holder.sent_msg_layout.setVisibility(View.GONE);
                holder.rec_msg_layout.setVisibility(View.VISIBLE);

                // if i had  received the post

                if(position>0){
                    // if not first message
                    if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                        //if sender is same as previous message
                        holder.senderProfile.setVisibility(View.GONE);}
                    else {
                        //if sender is changed from previous
                        holder.senderProfile.setVisibility(View.VISIBLE);
                        Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.senderProfile);

                    }

                }else{
                    //if first message
                    holder.senderProfile.setVisibility(View.VISIBLE);
                    holder.received_MediaImageView.setOnLongClickListener(v->{ ReUsableFunctions.ShowToast("long press detected");return true;});
                    Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into( holder.senderProfile);

                }
                Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.received_MediaImageView);
                if(!list.get(position).getMsg().isEmpty()||!list.get(position).getMsg().isBlank()){
                    holder.rec_caption.setVisibility(View.VISIBLE);
                    holder.rec_caption.setText(list.get(position).getMsg());

                }else {
                    holder.rec_caption.setVisibility(View.GONE);
                }
                //on click of media message i have received
                holder.received_MediaImageView.setOnClickListener(v->openPost(list.get(position)));


            }

        }









        //-------------------   DeletedMessageViewHolder -------------------//
        else if (holderView instanceof DeletedMessageViewHolder) {
            DeletedMessageViewHolder holder=(DeletedMessageViewHolder) holderView;
            if(list.get(position).getSenderId().equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){
                //if i had sent
                holder.sentMsg.setVisibility(View.VISIBLE);
                holder.recMsg.setVisibility(View.GONE);
            }else{
                //if i had received
                holder.sentMsg.setVisibility(View.GONE);
                holder.recMsg.setVisibility(View.VISIBLE);


            }
        }








        //-------------------   TextMessageViewHolder -------------------//
        else{
              TextMessageviewHolder holder=(TextMessageviewHolder) holderView;

              //layout background condition checking

                if(position==0){
                    //exclusive for position 0
                    if(list.get(position).getSenderId().equals(ReUsableFunctions.getMyUuid())){
                        if(list.size()-1>=position+1){
                            //if the list contains another message item
                            if(list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if the pos+1 is having same sender in this case other user then
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_start_bg));


                            }else{
                                //if pos+1 is not the other user
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_solo_bg));
                            }
                        }else{
                            //if no second message exists
                            holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_solo_bg));
                        }
                    }

                    //if received by user
                    else{
                        if(list.size()-1>=position+1){
                            //if the list contains another message item
                            if(list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if the pos+1 is having same sender in this case other user then
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_start));


                            }else{
                                //if pos+1 is not the other user
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_solo));
                            }
                        }else{
                            //if no second message exists
                            holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_solo));
                        }

                    }




                }else {
                    //if position is greater than 0
                    if(list.get(position).getSenderId().equals(ReUsableFunctions.getMyUuid())){
                        //if current user id sender
                        if(list.size()-1>=position+1){
                            //if position+1 exists
                            //we do double checking for both front and back
                            if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if  message sender is same as previous and next message sender is same
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_mid_bg));

                            }

                            if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&!list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if  message sender is same as previous but next message is not by the same user
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_end_bg));

                            }
                            if(!list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&!list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if both side is different
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_solo_bg));

                            }
                            if(!list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if  message sender is same as previous and next message sender is same
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_start_bg));

                            }


                        }else{
                            //if no next message available
                            if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                                //since no next message available check if same sender as before
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_end_bg));

                            }else{
                                //if solo message
                                holder.sent_msg_layout.setBackground(AppCompatResources.getDrawable(context,R.drawable.sent_msg_solo_bg));
                            }

                        }

                    }else{
                        //if other user is sender or current user is receiver
                        if(list.size()-1>=position+1){
                            //if position+1 exists
                            //we do double checking for both front and back
                            if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if  message sender is same as previous and next message sender is same
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_mid_bg));

                            }

                            if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&!list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if  message sender is same as previous but next message is not by the same user
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_end));

                            }
                            if(!list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&!list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if both side is different
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_solo));

                            }
                            if(!list.get(position).getSenderId().equals(list.get(position-1).getSenderId())&&list.get(position).getSenderId().equals(list.get(position+1).getSenderId())){
                                //if  message sender is same as previous and next message sender is same
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_start));

                            }


                        }else{
                            //if no next message available
                            if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                                //since no next message available check if same sender as before
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_end));

                            }else{
                                //if solo message
                                holder.recMsg.setBackground(AppCompatResources.getDrawable(context,R.drawable.received_msg_bg_solo));
                            }

                        }


                    }
                }



                // if selected for action ------//
            if(selectedMsgUUid.contains(list.get(position).getMessageUid())){
                holder.itemView.setBackground(AppCompatResources.getDrawable(
                        context,R.drawable.selected_item_overlay
                ));
            }else{
                holder.itemView.setBackground(AppCompatResources.getDrawable(
                        context,R.drawable.curved_small_square_box
                ));
            }







            //------------- actual logic --------------//
            if(list.get(position).getSenderId().equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){

                //if i had sent
                int deliveryStatus=list.get(position).getDeliveryStatus();
                holder.senderProfile.setVisibility(View.GONE);
                holder.recMsg.setVisibility(View.GONE);
                holder.sent_msg_layout.setVisibility(View.VISIBLE);
                holder.sentMsg.setText(list.get(position).getMsg());
                holder.status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
                holder.sent_msg_layout.setLongClickable(true);
                holder.sent_msg_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!selectedMsgUUid.isEmpty()){
                            if(selectedMsgUUid.contains(list.get(position).getMessageUid())){
                                selectedMsgUUid.remove(list.get(position).getMessageUid());
                            }else{
                                selectedMsgUUid.add(list.get(position).getMessageUid());
                            }

                            messageClickCallBack.longPress(position);

                        }
                    }
                });
                holder.sent_msg_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                         if(selectedMsgUUid.contains(list.get(position).getMessageUid())){
                             selectedMsgUUid.remove(list.get(position).getMessageUid());
                         }else{
                             selectedMsgUUid.add(list.get(position).getMessageUid());
                         }

                         messageClickCallBack.longPress(position);
                        return true;
                    }
                });
            }

            else {
                if(position>0){
                    if(list.get(position).getSenderId().equals(list.get(position-1).getSenderId())){
                        holder.senderProfile.setVisibility(View.INVISIBLE);}
                    else {
                        holder.senderProfile.setVisibility(View.VISIBLE);
                        Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into( holder.senderProfile);

                    }
                } else {
                    holder.senderProfile.setVisibility(View.VISIBLE);
                    Glide.with(context).load(profile).placeholder(R.drawable.blank_profile).circleCrop().into( holder.senderProfile);

                }

                holder.recMsg.setVisibility(View.VISIBLE);
                holder.sent_msg_layout.setVisibility(View.GONE);
                holder.recMsg.setText(list.get(position).getMsg());
                holder.recMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!selectedMsgUUid.isEmpty()){
                            if(selectedMsgUUid.contains(list.get(position).getMessageUid())){
                                selectedMsgUUid.remove(list.get(position).getMessageUid());
                            }else{
                                selectedMsgUUid.add(list.get(position).getMessageUid());
                            }

                            messageClickCallBack.longPress(position);

                        }
                    }
                });
                holder.recMsg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(selectedMsgUUid.contains(list.get(position).getMessageUid())){
                            selectedMsgUUid.remove(list.get(position).getMessageUid());
                        }else{
                            selectedMsgUUid.add(list.get(position).getMessageUid());
                        }

                        messageClickCallBack.longPress(position);
                        return true;
                    }
                });


            }
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void openPost(MessageSchema messageData){
        Intent postPageIntent=new Intent(context, PostActivity.class);
        postPageIntent.putExtra("postid",messageData.getPostId());
        context.startActivity(postPageIntent);


    }



    public static class TextMessageviewHolder extends RecyclerView.ViewHolder {
        TextView recMsg,sentMsg;
        ImageView senderProfile,status_img;
        ConstraintLayout sent_msg_layout;
        public TextMessageviewHolder(@NonNull View itemView) {
            super(itemView);
            recMsg=itemView.findViewById(R.id.rec_text_msg);
            sentMsg=itemView.findViewById(R.id.sent_text_msg);
            senderProfile=itemView.findViewById(R.id.senderProfile);
            status_img=itemView.findViewById(R.id.status_img);
            sent_msg_layout=itemView.findViewById(R.id.sent_msg_layout);

        }
    }
    public static class ImageMessageViewHolder extends RecyclerView.ViewHolder{
        ImageView senderProfile,status_img;
        LinearLayout rec_msg_layout,sent_msg_layout;
        ImageView received_MediaImageView,sent_MediaImageView;
        TextView rec_caption,sent_caption;
        public ImageMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderProfile=itemView.findViewById(R.id.senderProfile);
            status_img=itemView.findViewById(R.id.status_img);
            rec_msg_layout=itemView.findViewById(R.id.rec_msg_layout);
            received_MediaImageView=itemView.findViewById(R.id.received_MediaImageView);
            rec_caption=itemView.findViewById(R.id.rec_caption);
            sent_msg_layout=itemView.findViewById(R.id.sent_msg_layout);
            sent_MediaImageView=itemView.findViewById(R.id.sent_MediaImageView);
            sent_caption=itemView.findViewById(R.id.sent_caption);

        }
    }
    public static class VideoMessageViewHolder extends RecyclerView.ViewHolder{
        ImageView senderProfile,status_img;
        LinearLayout rec_msg_layout,sent_msg_layout;
        ImageView received_MediaImageView,sent_MediaImageView;
        TextView rec_caption,sent_caption;
        public VideoMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderProfile=itemView.findViewById(R.id.senderProfile);
            status_img=itemView.findViewById(R.id.status_img);
            rec_msg_layout=itemView.findViewById(R.id.rec_msg_layout);
            received_MediaImageView=itemView.findViewById(R.id.received_MediaImageView);
            rec_caption=itemView.findViewById(R.id.rec_caption);
            sent_msg_layout=itemView.findViewById(R.id.sent_msg_layout);
            sent_MediaImageView=itemView.findViewById(R.id.sent_MediaImageView);
            sent_caption=itemView.findViewById(R.id.sent_caption);
        }
    }
    public static class PostMessageViewHolder extends RecyclerView.ViewHolder{
        ImageView senderProfile,status_img;
        LinearLayout rec_msg_layout,sent_msg_layout;
        ImageView received_MediaImageView,sent_MediaImageView;
        TextView rec_caption,sent_caption;
        public PostMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderProfile=itemView.findViewById(R.id.senderProfile);
            status_img=itemView.findViewById(R.id.status_img);
            rec_msg_layout=itemView.findViewById(R.id.rec_msg_layout);
            received_MediaImageView=itemView.findViewById(R.id.received_MediaImageView);
            rec_caption=itemView.findViewById(R.id.rec_caption);
            sent_msg_layout=itemView.findViewById(R.id.sent_msg_layout);
            sent_MediaImageView=itemView.findViewById(R.id.sent_MediaImageView);
            sent_caption=itemView.findViewById(R.id.sent_caption);
        }
    }
    public static class DeletedMessageViewHolder extends RecyclerView.ViewHolder{
        TextView recMsg,sentMsg;
        public DeletedMessageViewHolder(@NonNull View itemView){
            super(itemView);
            recMsg=itemView.findViewById(R.id.rec_text_msg);
            sentMsg=itemView.findViewById(R.id.sent_text_msg);
        }

    }

    private void openMedia(MessageSchema messageSchema,String type){
        InputMethodManager imm=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        AppCompatActivity activity=(AppCompatActivity) context;
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),0);
        messageClickCallBack.onItemClicked(messageSchema,type);

    }

}
