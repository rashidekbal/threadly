package com.rtech.gpgram.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OutputFileOptions;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.util.concurrent.ListenableFuture;
import com.rtech.gpgram.R;
import com.rtech.gpgram.databinding.ActivityAddPostBinding;
import com.rtech.gpgram.fragments.AddPostMainFragment;
import com.rtech.gpgram.interfaces.AddPostMainFragmentOptionsClickInterface;

import java.io.File;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;

public class AddPostActivity extends AppCompatActivity {
    ActivityAddPostBinding mainXml;
    boolean isBackCamera=false;
    boolean isFlashOn=false;
    ImageCapture imageCaptureProvider;
    VideoCapture<Recorder> videoCaptureProvider;
    Recorder recorder;
    private final static int permissionCode=786;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainXml=ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

      checkPermissions();





        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount()==0){
                   finish();
                };
            }
        });










    }


    private void checkPermissions() {
      String cameraPermission=Manifest.permission.CAMERA;
      String AudioPermission=Manifest.permission.RECORD_AUDIO;
      String[] getPermission={cameraPermission,AudioPermission};
      if(ContextCompat.checkSelfPermission(this,cameraPermission)!=PackageManager.PERMISSION_GRANTED||ActivityCompat.shouldShowRequestPermissionRationale(this,AudioPermission)){
          ActivityCompat.requestPermissions(this,getPermission,permissionCode);

      } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,cameraPermission)) {
          AlertDialog.Builder dialog=new AlertDialog.Builder(this);
          dialog.setMessage("Camera And Audio_RECORDING permission is required for using camera feature");
          dialog.setTitle("Permission required");
          dialog.setCancelable(false);
          dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  ActivityCompat.requestPermissions(AddPostActivity.this,new String[]{cameraPermission,AudioPermission},permissionCode);
                  dialog.dismiss();
              }
          });
          dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();

              }
          });
          dialog.show();

      }
     else{
         openMainFragment();

      }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==permissionCode&&grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
               openMainFragment();

            }
            else if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)||!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)){
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("Permission required");
                dialog.setMessage("grant the required permission for Desired Activity");
                dialog.setCancelable(false);
                dialog.setPositiveButton("settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent setting=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri=Uri.fromParts("package",getPackageName(),null);
                        setting.setData(uri);
                        startActivity(setting);
                        dialog.dismiss();

                    }
                });
                dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                });
                dialog.show();

            }

        }

    }

    private void fragmentManager(Fragment fragmentPage){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(mainXml.fragmentHolder.getId(),fragmentPage).addToBackStack(null).commit();

    }
    private void openMainFragment(){
        fragmentManager(new AddPostMainFragment(new AddPostMainFragmentOptionsClickInterface() {

        }));
    }

}