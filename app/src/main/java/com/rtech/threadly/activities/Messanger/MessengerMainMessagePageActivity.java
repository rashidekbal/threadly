package com.rtech.threadly.activities.Messanger;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.ActivityMessangerMainMessagePageBinding;
import com.rtech.threadly.fragments.MessageFragments.MediaSendMessageFragment;
import com.rtech.threadly.fragments.MessageFragments.MessagePageMainFragment;
import com.rtech.threadly.fragments.PostAddCameraFragment;
import com.rtech.threadly.interfaces.CameraFragmentInterface;
import com.rtech.threadly.interfaces.Messanger.CameraOpenCallBackListener;
import com.rtech.threadly.interfaces.general_ui_callbacks.OnCapturedMediaFinalizedCallback;
import com.rtech.threadly.utils.ReUsableFunctions;

public class MessengerMainMessagePageActivity extends AppCompatActivity {
    ActivityMessangerMainMessagePageBinding mainXml;
    Bundle userdata;
    OnCapturedMediaFinalizedCallback onCapturedMediaFinalizedCallback=new OnCapturedMediaFinalizedCallback() {
        @Override
        public void OnFinalized(String filePath, String mediaType) {

        }

        @Override
        public void discard() {

        }
    };
    CameraOpenCallBackListener cameraOpenCallBackListener=new CameraOpenCallBackListener() {
        @Override
        public void cameraBtnClicked() {
            changeFragment(new PostAddCameraFragment(new CameraFragmentInterface() {
                @Override
                public void onCapture(String filePath, String mediaType) {

                    getSupportFragmentManager().popBackStack();
                }
            }));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mainXml=ActivityMessangerMainMessagePageBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());

        init();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount()==0){
                    finish();
                }
            }
        });
    }

    private void init(){
        userdata=getIntent().getExtras();
        if(userdata==null){
            ReUsableFunctions.ShowToast("something went wrong..");
            finish();
        }else{
            Fragment messagePageMainFragment=new MessagePageMainFragment(cameraOpenCallBackListener);
            messagePageMainFragment.setArguments(userdata);
            changeFragment(messagePageMainFragment);
        }

    }
    private void changeFragment(Fragment frag){
        getSupportFragmentManager().beginTransaction().replace(mainXml.fragmentContainer.getId(),frag)
                .addToBackStack(null)
                .commit();
    }

}