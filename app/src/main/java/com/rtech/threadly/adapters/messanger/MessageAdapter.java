package com.rtech.threadly.adapters.messanger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.activities.PostActivity;
import com.rtech.threadly.constants.MessageStateEnum;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.constants.TypeConstants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.Messanger.MessageClickCallBack;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.MessageManager;
import com.rtech.threadly.utils.MessengerUtils;
import com.rtech.threadly.utils.PreferenceUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;
import com.rtech.threadly.workers.MessageMediaHandlerWorker;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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

                holder.status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
                if (!list.get(position).getMsg().isEmpty()){
                    holder.sent_caption.setVisibility(View.VISIBLE);
                    holder.sent_caption.setText(list.get(position).getMsg());
                }else{
                    holder.sent_caption.setVisibility(View.GONE);
                }
                if(list.get(position).getPostLink()!=null){
                    Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.sent_MediaImageView);
                }else{
                    Glide.with(context).load(new File(list.get(position).getMediaLocalPath())).placeholder(R.drawable.post_placeholder).into(holder.sent_MediaImageView);
                }

                if(((list.get(position).getMediaUploadState().equals(MessageStateEnum.FAILED.toString())||
                        list.get(position).getMediaUploadState().equals(MessageStateEnum.SUCCESS.toString()))&&list.get(position).getDeliveryStatus()==0)&&list.get(position).getPostLink()!=null){
                    holder.cancelBtn.setTag("retry");
                    holder.cancelBtn.setImageResource(R.drawable.retry_icon);
                    holder.progress_circular.setVisibility(View.GONE);
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.sent_MediaImageView.setOnClickListener(v-> openMedia(list.get(position), TypeConstants.IMAGE));
                    holder.sent_MediaImageView.setOnLongClickListener(v -> {
                        showActionMenu(holder.sent_MediaImageView,position);
                        return true;
                    });
                }
                else if(list.get(position).getMediaUploadState().equals(MessageStateEnum.FAILED.toString())){
                    holder.progress_circular.setVisibility(View.GONE);
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.cancelBtn.setTag("retry");
                    holder.cancelBtn.setImageResource(R.drawable.retry_icon);
                }
                else if(!list.get(position).getMediaUploadState().equals(MessageStateEnum.SUCCESS.toString())||list.get(position).getPostLink()==null){

                    holder.progress_circular.setVisibility(View.VISIBLE);
                    holder.cancelBtn.setImageResource(R.drawable.cross_light);
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.progress_circular.setMax((int)list.get(position).getTotalSize());
                    holder.progress_circular.setProgress((int)list.get(position).getUploadedSize(),true);
                    holder.cancelBtn.setTag("cancel");
                }else{
                    holder.progress_circular.setVisibility(View.GONE);
                    holder.cancelBtn.setVisibility(View.GONE);
                    holder.sent_MediaImageView.setOnClickListener(v-> openMedia(list.get(position), TypeConstants.IMAGE));
                    holder.sent_MediaImageView.setOnLongClickListener(v -> {
                        showActionMenu(holder.sent_MediaImageView,position);
                        return true;
                    });
                }
                holder.cancelBtn.setOnClickListener((v)->{
                    if(holder.cancelBtn.getTag().equals("cancel")){
                        handelMediaSendCancel(position,holderView);
                    }else{
                        holder.cancelBtn.setTag("cancel");
                        holder.cancelBtn.setImageResource(R.drawable.cross_light);
                        holder.progress_circular.setVisibility(View.VISIBLE);
                        StartResendingMessage(list.get(position).getMessageUid());

                    }

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
                holder.received_MediaImageView.setOnLongClickListener(v -> {

                    showActionMenu(holder.received_MediaImageView,position);
                    return true;
                });


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

                holder.status_img.setImageResource(deliveryStatus==0?R.drawable.msg_pending:deliveryStatus==1?R.drawable.single_tick:deliveryStatus==2?R.drawable.double_tick_recieved:R.drawable.double_tick_viewed);
                if (!list.get(position).getMsg().isEmpty()){
                    holder.sent_caption.setVisibility(View.VISIBLE);
                    holder.sent_caption.setText(list.get(position).getMsg());
                }else{
                    holder.sent_caption.setVisibility(View.GONE);
                }

                if(list.get(position).getPostLink()!=null){
                    Glide.with(context).load(list.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.sent_MediaImageView);
                }else{
                    Glide.with(context).load(new File(list.get(position).getMediaLocalPath())).placeholder(R.drawable.post_placeholder).into(holder.sent_MediaImageView);
                }

                if(list.get(position).getMediaUploadState().equals(MessageStateEnum.FAILED.toString())){
                    holder.progress_circular.setVisibility(View.GONE);
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.cancelBtn.setTag("retry");
                    holder.cancelBtn.setImageResource(R.drawable.retry_icon);
                    holder.playIcon.setVisibility(View.GONE);
                }
                else if(!list.get(position).getMediaUploadState().equals(MessageStateEnum.SUCCESS.toString())||list.get(position).getPostLink()==null){
                    holder.progress_circular.setVisibility(View.VISIBLE);
                    holder.cancelBtn.setImageResource(R.drawable.cross_light);
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.progress_circular.setMax((int)list.get(position).getTotalSize());
                    holder.progress_circular.setProgress((int)list.get(position).getUploadedSize(),true);
                    holder.cancelBtn.setTag("cancel");
                    holder.playIcon.setVisibility(View.GONE);
                }else{
                    holder.progress_circular.setVisibility(View.GONE);
                    holder.cancelBtn.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.VISIBLE);
                    holder.sent_MediaImageView.setOnClickListener(v-> openMedia(list.get(position), TypeConstants.VIDEO));
                    holder.sent_MediaImageView.setOnLongClickListener(v -> {
                        showActionMenu(holder.sent_MediaImageView,position);
                        return true;
                    });
                }
                holder.cancelBtn.setOnClickListener((v)->{
                    if(holder.cancelBtn.getTag().equals("cancel")){
                        handelMediaSendCancel(position,holderView);
                    }else{
                        holder.cancelBtn.setTag("cancel");
                        holder.cancelBtn.setImageResource(R.drawable.cross_light);
                        holder.progress_circular.setVisibility(View.VISIBLE);
                        StartResendingMessage(list.get(position).getMessageUid());

                    }

                });




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
                holder.received_MediaImageView.setOnLongClickListener(v -> {
                    showActionMenu(holder.received_MediaImageView,position);
                    return true;
                });

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
                holder.sent_MediaImageView.setOnLongClickListener(v -> {
                    showActionMenu(holder.sent_MediaImageView,position);
                    return true;
                });

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
                holder.received_MediaImageView.setOnLongClickListener(v -> {
                    showActionMenu(holder.received_MediaImageView,position);
                    return true;
                });

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

                holder.sent_msg_layout.setOnLongClickListener(v -> {


                    showActionMenu(holder.sent_msg_layout,position);

                    return true;
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

                holder.recMsg.setOnLongClickListener(v -> {
                    showActionMenu(holder.recMsg,position);
                    return true;
                });


            }
        }



    }

    private void StartResendingMessage(String msgUid) {

        Executors.newSingleThreadExecutor().execute(()->{
            MessageSchema schema=DataBase.getInstance().MessageDao().getMessageWithUid(msgUid);
            @SuppressLint("RestrictedApi") Data data=new Data.Builder()
                    .put("path",schema.getMediaLocalPath())
                    .put("messageUid",msgUid).build();
            Core.getWorkManager().enqueue(new OneTimeWorkRequest.Builder(MessageMediaHandlerWorker.class).setInputData(data).build());
        });

    }

    private void handelMediaSendCancel(int position, RecyclerView.ViewHolder holderView) {
        MessageManager.CancelMessageMediaUploadRequest(list.get(position).getMessageUid());
        if(holderView instanceof ImageMessageViewHolder){
            ImageMessageViewHolder holder=(ImageMessageViewHolder) holderView;
            holder.cancelBtn.setImageResource(R.drawable.retry_icon);
            holder.cancelBtn.setTag("retry");
            holder.progress_circular.setVisibility(View.GONE);

        }else{
            VideoMessageViewHolder holder=(VideoMessageViewHolder) holderView;
            holder.cancelBtn.setImageResource(R.drawable.retry_icon);
            holder.cancelBtn.setTag("retry");
            holder.progress_circular.setVisibility(View.GONE);
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
        ImageView received_MediaImageView,sent_MediaImageView,cancelBtn;
        TextView rec_caption,sent_caption;
        ProgressBar progress_circular;
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
            progress_circular=itemView.findViewById(R.id.progress_circular);
            cancelBtn=itemView.findViewById(R.id.cancelBtn);

        }
    }
    public static class VideoMessageViewHolder extends RecyclerView.ViewHolder{
        ImageView senderProfile,status_img,playIcon;
        LinearLayout rec_msg_layout,sent_msg_layout;
        ImageView received_MediaImageView,sent_MediaImageView,cancelBtn;
        TextView rec_caption,sent_caption;
        ProgressBar progress_circular;
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
            progress_circular=itemView.findViewById(R.id.progress_circular);
            cancelBtn=itemView.findViewById(R.id.cancelBtn);
            playIcon=itemView.findViewById(R.id.playIcon);
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
    private void showActionMenu(View v,int position){
        MessageSchema messageSchema =list.get(position);
        if(messageSchema.getSenderId().equals(PreferenceUtil.getUUID())){
            //role is as of sender
            ActionMenuForSenderRole(v,position);
        }else{
            //role is as of receiver
            ActionMenuForReceiverRole(v,position);

        }

    }
    private void ActionMenuForSenderRole(View v,int position){
        PopupMenu actionMenu=new PopupMenu(context,v);
        actionMenu.getMenuInflater().inflate(R.menu.message_action_menu_sender,actionMenu.getMenu());
        actionMenu.setOnMenuItemClickListener(item -> {
            int itemId=item.getItemId();
            if(itemId==R.id.ReplyBtn){
                //reply action
                return true;
            }else if(itemId==R.id.ForwardBtn){
                //forward action
                showForwardMenu(list.get(position));

                return true;
            }else if(itemId==R.id.DeleteForYouBtn){
                //delete for me action
                deleteForMe(list.get(position));
                return true;
            }else{
                // unSend action
                unSendMessage(list.get(position));
                return true;
            }

        });

        actionMenu.show();
    }
    private void ActionMenuForReceiverRole(View v,int position){
        PopupMenu actionMenu=new PopupMenu(context,v);
        actionMenu.getMenuInflater().inflate(R.menu.message_action_menu_receiver,actionMenu.getMenu());
        actionMenu.setOnMenuItemClickListener(item -> {
            int itemId=item.getItemId();
            if(itemId==R.id.ReplyBtn){
                //reply action
                return true;
            }else if(itemId==R.id.ForwardBtn){
                //forward action
                showForwardMenu(list.get(position));

                return true;
            }else if(itemId==R.id.DeleteForYouBtn){
                //delete for me action
                deleteForMe(list.get(position));
                return true;
            }

            return true;


        });

        actionMenu.show();
    }
    private void deleteForMe(MessageSchema messageSchema) {
        MessengerUtils.deleteMsg(messageSchema.getMessageUid());
        String Role=messageSchema.getSenderId().equals(PreferenceUtil.getUUID())?"sender":"receiver";
        MessageManager.DeleteMessageForLoggedInUser(messageSchema.getMessageUid(), Role, new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String err) {


            }
        });

    }
    private void unSendMessage(MessageSchema messageSchema){
        MessengerUtils.deleteMsg(messageSchema.getMessageUid());
        MessageManager.unSendMessage(messageSchema.getMessageUid(), messageSchema.getReceiverId(), new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String err) {

            }
        });


    }



    private void showForwardMenu(MessageSchema Message){
        ArrayList<UsersModel> selectedUsers=new ArrayList<>();
        BottomSheetDialog shareBottomSheet=new BottomSheetDialog(context,R.style.TransparentBottomSheet);
        shareBottomSheet.setContentView(R.layout.post_share_layout);
        AppCompatButton sendBtn=shareBottomSheet.findViewById(R.id.sendBtn);
        RelativeLayout actionButtons_rl=shareBottomSheet.findViewById(R.id.actionButtons_rl);
        ImageView search_btn=shareBottomSheet.findViewById(R.id.search_btn);
        EditText search_edit_text=shareBottomSheet.findViewById(R.id.search_edit_text);
        ImageView suggestUsersBtn=shareBottomSheet.findViewById(R.id.suggestUsersBtn);
        RecyclerView Users_List_recyclerView=shareBottomSheet.findViewById(R.id.Users_List_recyclerView);
        LinearLayout Story_add_ll_btn=shareBottomSheet.findViewById(R.id.Story_add_ll_btn);
        ProgressBar progressBar=shareBottomSheet.findViewById(R.id.progressBar);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,3);
        Users_List_recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<UsersModel> usersModelList=new ArrayList<>();
        UsersShareSheetGridAdapter adapter=new UsersShareSheetGridAdapter(context, usersModelList, new OnUserSelectedListener() {
            @Override
            public void onSelect(UsersModel model) {
                if(selectedUsers.contains(model)){
                    selectedUsers.remove(model);
                }else{
                    selectedUsers.add(model);
                }
                assert actionButtons_rl != null;
                if(selectedUsers.isEmpty()){
                    actionButtons_rl.setVisibility(View.VISIBLE);
                    assert sendBtn != null;
                    sendBtn.setVisibility(View.GONE);

                }else{
                    actionButtons_rl.setVisibility(View.GONE);
                    assert sendBtn != null;
                    sendBtn.setVisibility(View.VISIBLE);
                }

            }
        });
        Users_List_recyclerView.setAdapter(adapter);



        MessageAbleUsersViewModel messageAbleUsersViewModel=new ViewModelProvider((AppCompatActivity)context).get(MessageAbleUsersViewModel.class);
        messageAbleUsersViewModel.getUsersList().observe((AppCompatActivity) context, new Observer<ArrayList<UsersModel>>() {
            @Override
            public void onChanged(ArrayList<UsersModel> usersModels) {
                if(usersModels.isEmpty()){
                    Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show();

                }else{
                    usersModelList.clear();
                    usersModelList.addAll(usersModels);
                    adapter.notifyDataSetChanged();

                }
                progressBar.setVisibility(View.GONE);

            }
        });

        //send btn action
        sendBtn.setOnClickListener(v->{
            if(!selectedUsers.isEmpty()){
                for(UsersModel model:selectedUsers){
                    try {
                        Core.sendCtoS(model.getUuid(),Message.getMsg(),Message.getType(),Message.getPostLink(),Message.getPostId(),Message.getMsg());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                selectedUsers.clear();
                sendBtn.setVisibility(View.GONE);
                actionButtons_rl.setVisibility(View.VISIBLE);

            }
            shareBottomSheet.dismiss();
        });

        shareBottomSheet.show();

    }

}
