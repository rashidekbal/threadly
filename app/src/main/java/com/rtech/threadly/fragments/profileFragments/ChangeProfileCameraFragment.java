package com.rtech.threadly.fragments.profileFragments;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.ListenableFuture;
import com.rtech.threadly.R;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.databinding.FragmentChangeProfileCameraBinding;
import com.rtech.threadly.interfaces.CameraFragmentInterface;
import com.rtech.threadly.utils.PermissionManagementUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class ChangeProfileCameraFragment extends Fragment {
    FragmentChangeProfileCameraBinding mainXml;
    private int permissionCode=101;
    private String[] permission={Manifest.permission.CAMERA};
    Boolean is_front=true;
    boolean is_flash=false;
    ImageCapture imageCapture;
    AppCompatActivity activity;
    CameraFragmentInterface callback;


    public ChangeProfileCameraFragment(CameraFragmentInterface callback) {
        this.callback=callback;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
mainXml= FragmentChangeProfileCameraBinding.inflate(inflater,container,false);
      activity=(AppCompatActivity) requireActivity();
      activity.setSupportActionBar(mainXml.toolbar);
      activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(is_front){
            mainXml.toggleFlash.setVisibility(View.GONE);
        }else{
            mainXml.toggleFlash.setVisibility(View.VISIBLE);
            if(!is_flash){
                mainXml.toggleFlash.setImageResource(R.drawable.flash_off_icon);
            }else {
                mainXml.toggleFlash.setImageResource(R.drawable.flash_on_icon);

            }
        }
        checkPermissionAndStart();
        setOnclickListeners();
        return mainXml.getRoot();
    }

    private void setOnclickListeners() {
        mainXml.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               activity.onBackPressed();
            }
        });

        mainXml.toggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_front=!is_front;
                StartMain();
            }
        });
        mainXml.toggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_flash=!is_flash;
                StartMain();

            }
        });
        mainXml.captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File photo=new File(activity.getFilesDir(),"threadly"+System.currentTimeMillis()+".png");
                ImageCapture.OutputFileOptions outputFileOptions=new ImageCapture.OutputFileOptions.Builder(photo).build();
                imageCapture.takePicture(
                        outputFileOptions,
                        getMainExecutor(),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                callback.onCapture(photo.getAbsolutePath(),"image");

                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                ReUsableFunctions.ShowToast("Error capturing image");

                            }
                        }
                );
            }
        });

    }

    private void checkPermissionAndStart(){
        if(!PermissionManagementUtil.isAllPermissionGranted(Threadly.getGlobalContext(),permission)){
            PermissionManagementUtil.requestPermission(requireActivity(),permission,permissionCode);

        }else{
            StartMain();
        }

    }

    private void StartMain() {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture=ProcessCameraProvider.getInstance(requireContext());
        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    if(is_front){
                        mainXml.toggleFlash.setVisibility(View.GONE);
                    }else{
                        mainXml.toggleFlash.setVisibility(View.VISIBLE);
                        if(!is_flash){
                            mainXml.toggleFlash.setImageResource(R.drawable.flash_off_icon);
                        }else {
                            mainXml.toggleFlash.setImageResource(R.drawable.flash_on_icon);

                        }
                    }
                    ProcessCameraProvider cameraProvider=cameraProviderListenableFuture.get();
                    cameraProvider.unbindAll();
                    CameraSelector cameraSelector=is_front?CameraSelector.DEFAULT_FRONT_CAMERA:CameraSelector.DEFAULT_BACK_CAMERA;
                    Preview preview=new Preview.Builder().build();
                    preview.setSurfaceProvider(mainXml.surfaceProvider.getSurfaceProvider());
                    imageCapture=new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).setFlashMode(!is_front ?is_flash?ImageCapture.FLASH_MODE_ON:ImageCapture.FLASH_MODE_OFF:ImageCapture.FLASH_MODE_OFF).build();


                    cameraProvider.bindToLifecycle(requireActivity(),cameraSelector,preview,imageCapture);




                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
        },getMainExecutor());


    }

    private Executor getMainExecutor() {
        return ContextCompat.getMainExecutor(requireContext());
    }


}