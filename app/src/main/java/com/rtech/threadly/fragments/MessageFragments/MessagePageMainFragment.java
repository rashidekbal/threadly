package com.rtech.threadly.fragments.MessageFragments;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import com.rtech.threadly.databinding.FragmentMessagePageMainBinding;
import com.rtech.threadly.interfaces.Messanger.CameraOpenCallBackListener;
import com.rtech.threadly.network_managers.MessageManager;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessagesViewModel;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;


public class MessagePageMainFragment extends Fragment {
    FragmentMessagePageMainBinding mainXml;
    CameraOpenCallBackListener cameraOpenCallBackListener;
    Bundle userdata;
    String uuid;
    String conversationId;
    MessagesViewModel messagesViewModel;
    List<MessageSchema> msgList;
    MessageAdapter messageAdapter;
    public MessagePageMainFragment() {
        // Required empty public constructor
    }
    public MessagePageMainFragment(CameraOpenCallBackListener callBackListener){
        this.cameraOpenCallBackListener=callBackListener;
    }

    // In MessagePageMainFragment.java

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        // Your init() call remains here
        init();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mainXml=FragmentMessagePageMainBinding.inflate(inflater,container,false);
      //on header back btn pressed

        return mainXml.getRoot();
    }
    private void init(){
        msgList=new ArrayList<>();
        userdata=getArguments();
        messagesViewModel=new ViewModelProvider(requireActivity()).get(MessagesViewModel.class);
        if(userdata==null){
            ReUsableFunctions.ShowToast("something went wrong...");
            requireActivity().finish();
        }
        uuid=userdata.getString("uuid");
        conversationId=uuid+ Core.getPreference().getString(SharedPreferencesKeys.UUID,null);
        setUpRecyclerView();
        setUserData();
    }
    private void setUserData(){
        mainXml.username.setText(userdata.getString("username"));
        mainXml.userId.setText(userdata.getString("userid"));
        Glide.with(requireActivity()).load(userdata.getString("profilePic")).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profile);
        messagesViewModel.getMessages(conversationId).observe(getViewLifecycleOwner(), messageSchemas -> {
            if(!messageSchemas.isEmpty()){
                msgList.clear();
                msgList.addAll(messageSchemas);
                messageAdapter.notifyItemChanged(msgList.size()-1);
                mainXml.RecyclerView.scrollToPosition(msgList.size()-1);
            }


        });
        messagesViewModel.getConversationUnreadMsg_count(conversationId,Core.getPreference().getString(SharedPreferencesKeys.UUID,"null")).observe(getViewLifecycleOwner(), integer -> {
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
            requireActivity().finish();

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



        mainXml.sendBtn.setOnClickListener(
                v->{
                    String msg=mainXml.msgEditText.getText().toString().trim();
                    if(!msg.isEmpty()){
                        try {
                            Core.sendCtoS(uuid,msg);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        mainXml.msgEditText.setText("");
                    }
                }
        );
        mainXml.cameraBtn.setOnClickListener(v->cameraOpenCallBackListener.cameraBtnClicked());
        mainXml.mediaBtn.setOnClickListener(v->showMediaSelector());
    }


    private void setUpRecyclerView(){
        messageAdapter=new MessageAdapter(getActivity(),msgList,userdata.getString("profilePic"));
        mainXml.RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mainXml.RecyclerView.setAdapter(messageAdapter);
    }
    private void showMediaSelector(){
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(requireActivity(),R.style.TransparentBottomSheet);
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
}