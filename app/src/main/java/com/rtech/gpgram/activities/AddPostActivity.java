package com.rtech.gpgram.activities;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.rtech.gpgram.R;
import com.rtech.gpgram.constants.Permissions;
import com.rtech.gpgram.databinding.ActivityAddPostBinding;
import com.rtech.gpgram.fragments.AddPostMainFragment;
import com.rtech.gpgram.fragments.PostAddCameraFragment;
import com.rtech.gpgram.fragments.UploadPostFinalFragment;
import com.rtech.gpgram.interfaces.AddPostMainFragmentOptionsClickInterface;
import com.rtech.gpgram.interfaces.CameraFragmentInterface;
import com.rtech.gpgram.utils.PermissionManagementUtil;


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

// start running main function after permission check
      checkPermissionsAndStartMain();


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
        // call back for cameraFragment
        cameraFragmentInterface=new CameraFragmentInterface() {
            @Override
            public void onCapture(String filePath, String mediaType) {
                Bundle bundle=new Bundle();
                bundle.putString("filePath",filePath);
                bundle.putString("mediaType",mediaType);
                bundle.putString("from","camera");
                Fragment fragment=new UploadPostFinalFragment();
                fragment.setArguments(bundle);
                fragmentManager(fragment);



            }
        };






        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount()==0){
                    finish();
                }
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