package com.rtech.threadly.activities.Messanger;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.SocketIo.SocketEmitterEvents;
import com.rtech.threadly.SocketIo.SocketManager;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.activities.HomeActivity;
import com.rtech.threadly.adapters.messanger.MessageAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityMessangerMainMessagePageBinding;
import com.rtech.threadly.fragments.PostAddCameraFragment;
import com.rtech.threadly.fragments.common_ui_pages.Media_Capture_finalizer_fragment;
import com.rtech.threadly.interfaces.CameraFragmentInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.interfaces.general_ui_callbacks.OnCapturedMediaFinalizedCallback;
import com.rtech.threadly.network_managers.MessageManager;
import com.rtech.threadly.utils.PermissionManagementUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessagesViewModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class MessengerMainMessagePageActivity extends AppCompatActivity {
    ActivityMessangerMainMessagePageBinding mainXml;
    Bundle userdata;
    String uuid;
    String conversationId;
    MessagesViewModel messagesViewModel;
    List<MessageSchema> msgList;
    MessageAdapter messageAdapter;
    String sendType="text";
    String mediaLink;
    String selectedMediaType;

    OnCapturedMediaFinalizedCallback onCapturedMediaFinalizedCallback=new OnCapturedMediaFinalizedCallback() {
        @Override
        public void OnFinalized(String filePath, String mediaType) {
            sendType="media";
            getSupportFragmentManager().popBackStack("Id_camera_page",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            selectedMediaType=mediaType;
            mainXml.mediaSendLayout.setVisibility(View.VISIBLE);
            Glide.with(MessengerMainMessagePageActivity.this).load(new File(filePath)).placeholder(R.drawable.post_placeholder).into(mainXml.MediaPreviewImage);
            mainXml.uploadProgress.setVisibility(View.VISIBLE);
            mainXml.cameraBtn.setVisibility(View.GONE);
            mainXml.addonsSection.setVisibility(View.GONE);
            mainXml.sendBtn.setVisibility(View.VISIBLE);
            mainXml.discardBtn.setVisibility(View.VISIBLE);
            mainXml.sendBtn.setEnabled(false);
            MessageManager.UploadMsgMedia(new File(filePath), new NetworkCallbackInterfaceWithProgressTracking() {
                @Override
                public void onSuccess(JSONObject response) {
                    mediaLink=response.optJSONObject("data").optString("link");
                    mainXml.uploadProgress.setVisibility(View.GONE);
                    mainXml.sendBtn.setEnabled(true);

                }

                @Override
                public void onError(String err) {
                    mainXml.uploadProgress.setVisibility(View.GONE);
                    ReUsableFunctions.ShowToast("something went wrong ..");
                }

                @Override
                public void progress(long bytesUploaded, long totalBytes) {
                    mainXml.uploadProgress.setMax((int) totalBytes);
                    mainXml.uploadProgress.setProgress((int) bytesUploaded);


                }
            });
            ReUsableFunctions.ShowToast("media finalized"+" type "+mediaType);

        }

        @Override
        public void discard() {
            getSupportFragmentManager().popBackStack();
            ReUsableFunctions.ShowToast("media discarded");
        }
    };
    CameraFragmentInterface cameraFragmentInterface= (filePath, mediaType) -> {
        Media_Capture_finalizer_fragment fragment=new Media_Capture_finalizer_fragment(onCapturedMediaFinalizedCallback);
        Bundle bundle=new Bundle();
        bundle.putString("filePath",filePath);
        bundle.putString("mediaType",mediaType);
        fragment.setArguments(bundle);
        changeFragment(fragment,"Id_mediaFinalizer_page");

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mainXml=ActivityMessangerMainMessagePageBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());
        // ** THIS IS THE UPDATED LISTENER **
        ViewCompat.setOnApplyWindowInsetsListener(mainXml.main, (v, insets) -> {
            // Inset for the status bar and navigation bar
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Inset for the software keyboard (IME)
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            // Calculate the bottom padding
            // Use the larger of the navigation bar height or the keyboard height
            int bottomPadding = Math.max(systemBars.bottom, imeInsets.bottom);

            // Apply the insets as padding to the root view.
            // This will push the footer (and everything else) up when the keyboard appears.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

            // We've handled the insets, so return an empty set
            return WindowInsetsCompat.CONSUMED;
        });

        init();
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if(getSupportFragmentManager().getBackStackEntryCount()==0){
                mainXml.fragmentContainer.setVisibility(View.GONE);
            }
        });

    }

    private void init(){
        mainXml.mediaSendLayout.setVisibility(View.GONE);
        mainXml.fragmentContainer.setVisibility(View.GONE);
        msgList=new ArrayList<>();
        userdata=getIntent().getExtras();
        messagesViewModel=new ViewModelProvider(this).get(MessagesViewModel.class);
        if(userdata==null){
            ReUsableFunctions.ShowToast("something went wrong...");
            finish();
        }
        uuid=userdata.getString("uuid");
        conversationId=uuid+ Core.getPreference().getString(SharedPreferencesKeys.UUID,null);
        setUpRecyclerView();
        setUserData();

    }
    private void setUserData(){
        mainXml.username.setText(userdata.getString("username"));
        mainXml.userId.setText(userdata.getString("userid"));
        Glide.with(this).load(userdata.getString("profilePic")).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profile);
        messagesViewModel.getMessages(conversationId).observe(this, messageSchemas -> {
            if(!messageSchemas.isEmpty()){
                msgList.clear();
                msgList.addAll(messageSchemas);
                messageAdapter.notifyItemChanged(msgList.size()-1);
                mainXml.RecyclerView.scrollToPosition(msgList.size()-1);
            }


        });
        messagesViewModel.getConversationUnreadMsg_count(conversationId,Core.getPreference().getString(SharedPreferencesKeys.UUID,"null")).observe(this, integer -> {
            if(integer>0){
                //new unread message arrived or already exists
                // update local db as seen and send message to global db to set as seen
                try {
                    if(SocketManager.getInstance().getSocket().connected()){
                        SocketEmitterEvents.UpdateSeenMsg_status(uuid,Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"));
                    }else{
                        MessageManager.setSeenMessage(uuid,Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().dao().updateMessagesSeen(conversationId,Core.getPreference().getString(SharedPreferencesKeys.UUID,"null")));
            }
        });



        mainXml.backBtn.setOnClickListener(v-> {
            if (Objects.equals(userdata.getString("src"), "notification")) {
                startActivity(new Intent(Threadly.getGlobalContext(), HomeActivity.class));

            }
            finish();

        });
        mainXml.msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    mainXml.cameraBtn.setVisibility(View.GONE);
                    mainXml.addonsSection.setVisibility(View.GONE);
                    mainXml.sendBtn.setVisibility(View.VISIBLE);


                }else{
                    mainXml.sendBtn.setVisibility(TextView.GONE);
                    mainXml.addonsSection.setVisibility(View.VISIBLE);
                    mainXml.cameraBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mainXml.sendBtn.setOnClickListener(v->{
            if(sendType.equals("text")){
                ReUsableFunctions.ShowToast("text");
                String msg=mainXml.msgEditText.getText().toString().trim();
                if(!msg.isEmpty()){
                    try {
                        Core.sendCtoS(uuid,msg,"text","null",-1);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    mainXml.msgEditText.setText("");
                }
            }else if(sendType.equals("media")){
                ReUsableFunctions.ShowToast("media type send");
                String msg=mainXml.msgEditText.getText().toString().trim();
                if(!mediaLink.isEmpty()){
                    try {
                        Core.sendCtoS(uuid,msg,selectedMediaType,mediaLink,-1);
                        sendType="text";
                        mainXml.mediaSendLayout.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });




        mainXml.cameraBtn.setOnClickListener(v -> {

            if( ActivityCompat.shouldShowRequestPermissionRationale(MessengerMainMessagePageActivity.this,Manifest.permission.CAMERA)){
                ReUsableFunctions.ShowToast("please provide camera permission in settings");
                return ;

            }
            if(PermissionManagementUtil.isAllPermissionGranted(MessengerMainMessagePageActivity.this,new String[]{Manifest.permission.CAMERA})){
                ReUsableFunctions.ShowToast("camera btn clicked ");
                mainXml.fragmentContainer.setVisibility(View.VISIBLE);
                changeFragment(new PostAddCameraFragment(cameraFragmentInterface),"Id_camera_page");
            }else{
                PermissionManagementUtil.requestPermission(MessengerMainMessagePageActivity.this,new String[]{Manifest.permission.CAMERA},205);

            }

        });
        mainXml.mediaBtn.setOnClickListener(v->showMediaSelector());
    }
    private void setUpRecyclerView(){
        messageAdapter=new MessageAdapter(this,msgList,userdata.getString("profilePic"));
        mainXml.RecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mainXml.RecyclerView.setAdapter(messageAdapter);
    }

    private void showMediaSelector(){
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(this,R.style.TransparentBottomSheet);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(R.layout.media_picker_grid_view_sample_post);
        FrameLayout frameLayout=bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if(frameLayout!=null){
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior=BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setState(STATE_EXPANDED);
            bottomSheetBehavior.setFitToContents(true);
            bottomSheetBehavior.setDraggable(false);
        }




        bottomSheetDialog.show();
    }

    private void changeFragment(Fragment frag,String fragmentId){
        getSupportFragmentManager().beginTransaction().replace(mainXml.fragmentContainer.getId(),frag)
                .addToBackStack(fragmentId)
                .commit();
    }


}