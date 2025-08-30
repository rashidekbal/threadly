package com.rtech.threadly.fragments;

import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY;
import static androidx.camera.core.ImageCapture.FLASH_MODE_OFF;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.ListenableFuture;
import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentPostAddCameraBinding;
import com.rtech.threadly.interfaces.CameraFragmentInterface;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class PostAddCameraFragment extends Fragment {
    FragmentPostAddCameraBinding mainXml;
    AppCompatActivity activity;
    boolean isBackCamera=false;
    boolean isFlashOn=false;
    ImageCapture imageCapture;
    Recorder recorder;
    VideoCapture<Recorder> videoCapture;
    ProfileViewModel profileViewModel;
    CameraFragmentInterface callback;


    public PostAddCameraFragment(CameraFragmentInterface callback) {
        this.callback=callback;
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       mainXml= FragmentPostAddCameraBinding.inflate(inflater, container, false);


        activity=(AppCompatActivity) requireActivity();
        profileViewModel=new ViewModelProvider(activity).get(ProfileViewModel.class);
        startCamera();
        setOnclickListeners();


        return mainXml.getRoot();
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(activity);
        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider=cameraProviderListenableFuture.get();
                    CameraSelector cameraSelector=isBackCamera?CameraSelector.DEFAULT_BACK_CAMERA:CameraSelector.DEFAULT_FRONT_CAMERA;
                    Preview preview=new Preview.Builder().build();
                    preview.setSurfaceProvider(mainXml.cameraLivePreview.getSurfaceProvider());
                    imageCapture=new ImageCapture.Builder().setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY).setFlashMode(FLASH_MODE_OFF).build();
                    recorder=new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build();
                    videoCapture=new VideoCapture.Builder(recorder).build();
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle((LifecycleOwner) activity,cameraSelector,preview,imageCapture,videoCapture);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },ContextCompat.getMainExecutor(activity));
    }

    private void setOnclickListeners(){
        //on close button click
        mainXml.closeButton.setOnClickListener((View v )-> activity.onBackPressed());

        //toggle flash
        mainXml.toggleFlash.setOnClickListener((View v)-> {
            {
                if(isFlashOn){
                    isFlashOn=false;
                    mainXml.toggleFlash.setImageResource(R.drawable.flash_off_icon);
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                }else{
                    isFlashOn=true;
                    mainXml.toggleFlash.setImageResource(R.drawable.flash_on_icon);
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);

                }
            }
        });
        // toggle camera
        mainXml.toggleCamera.setOnClickListener(v -> {
            isBackCamera=!isBackCamera;
            startCamera();
        });

        mainXml.captureBtn.setOnClickListener(v -> {
            mainXml.captureBtn.setEnabled(false);
            File photoFile=new File(activity.getFilesDir(),"threadly"+System.currentTimeMillis()+".png");
            ImageCapture.OutputFileOptions outputFileOptions=new ImageCapture.OutputFileOptions.Builder(photoFile).build();
            imageCapture.takePicture(
                    outputFileOptions,
                    ContextCompat.getMainExecutor(activity),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            mainXml.captureBtn.setEnabled(true);
                            callback.onCapture(photoFile.getAbsolutePath(), "image");
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            mainXml.captureBtn.setEnabled(true);

                        }
                    }
            );
        });


    }
}