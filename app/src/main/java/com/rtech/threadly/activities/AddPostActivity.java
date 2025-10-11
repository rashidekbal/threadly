package com.rtech.threadly.activities;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.rtech.threadly.R;
import com.rtech.threadly.constants.Permissions;
import com.rtech.threadly.databinding.ActivityAddPostBinding;
import com.rtech.threadly.fragments.AddPostMainFragment;
import com.rtech.threadly.fragments.PostAddCameraFragment;
import com.rtech.threadly.fragments.UploadPostFinalFragment;
import com.rtech.threadly.interfaces.AddPostMainFragmentOptionsClickInterface;
import com.rtech.threadly.interfaces.CameraFragmentInterface;
import com.rtech.threadly.utils.PermissionManagementUtil;


public class AddPostActivity extends AppCompatActivity {
    ActivityAddPostBinding mainXml;
    private final static int permissionCode=786;
    AddPostMainFragmentOptionsClickInterface addPostMainFragmentOptionsClickInterface;
    CameraFragmentInterface cameraFragmentInterface;


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
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(0); // clear light status flag
        window.setStatusBarColor(Color.BLACK); // or any dark color you're using


        // call back for cameraFragment
        cameraFragmentInterface= (filePath, mediaType) -> {
            Bundle bundle=new Bundle();
            bundle.putString("filePath",filePath);
            bundle.putString("mediaType",mediaType);
            bundle.putString("from","camera");
            Fragment fragment=new UploadPostFinalFragment();
            fragment.setArguments(bundle);
            fragmentManager(fragment);



        };

      // call back for mainFragment


      addPostMainFragmentOptionsClickInterface=new AddPostMainFragmentOptionsClickInterface() {
          @Override
          public void openCamera() {
              fragmentManager(new PostAddCameraFragment(cameraFragmentInterface));

          }

          @Override
          public void itemPicked(String uri, String type) {
                Bundle bundle=new Bundle();
                bundle.putString("filePath",uri);
                bundle.putString("mediaType",type);
                bundle.putString("from","gallery");
                Fragment fragment=new UploadPostFinalFragment();
                fragment.setArguments(bundle);
                fragmentManager(fragment);

          }
      };

        // start running main function after permission check
        checkPermissionsAndStartMain();




        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if(getSupportFragmentManager().getBackStackEntryCount()==0){
                finish();
            }
        });









    }


    private void checkPermissionsAndStartMain() {
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
          if(!PermissionManagementUtil.isAllPermissionGranted(AddPostActivity.this,Permissions.AddPostPermissionsApi33AndAbove)){
              PermissionManagementUtil.requestPermission(AddPostActivity.this, Permissions.AddPostPermissionsApi33AndAbove,permissionCode);
          }else{
              openMainFragment();
          }

      }else{
          if (!PermissionManagementUtil.isAllPermissionGranted(AddPostActivity.this,Permissions.AddPostPermissionsApiBelow33)){
              PermissionManagementUtil.requestPermission(AddPostActivity.this,Permissions.AddPostPermissionsApiBelow33,permissionCode);
          }else{
              openMainFragment();
          }

      }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==permissionCode&&grantResults.length>0){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                if(PermissionManagementUtil.isAllPermissionGranted(AddPostActivity.this,Permissions.AddPostPermissionsApi33AndAbove)){
                    openMainFragment();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(AddPostActivity.this);
                    builder.setTitle(R.string.permission_required);
                    builder.setMessage("CAMERA AND FILES Permissions are required to add post, please allow the permissions in settings.");
                    builder.setPositiveButton("Go to Settings ", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                    builder.setNegativeButton("cancel", (dialog, which) -> finish());
                    builder.show();

                }
            }
            else{
                if(PermissionManagementUtil.isAllPermissionGranted(AddPostActivity.this,Permissions.AddPostPermissionsApiBelow33)){
                    openMainFragment();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(AddPostActivity.this);
                    builder.setTitle(R.string.permission_required);
                    builder.setMessage("CAMERA AND FILES Permissions are required to add post, please allow the permissions in settings.");
                    builder.setPositiveButton("Go to Settings ", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                    builder.setNegativeButton("cancel", (dialog, which) -> finish());
                    builder.show();
                }

            }


        }

    }

    private void fragmentManager(Fragment fragmentPage){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(mainXml.fragmentHolder.getId(),fragmentPage).addToBackStack(null).commit();

    }
    private void openMainFragment(){
        fragmentManager(new AddPostMainFragment(addPostMainFragmentOptionsClickInterface));
    }

}