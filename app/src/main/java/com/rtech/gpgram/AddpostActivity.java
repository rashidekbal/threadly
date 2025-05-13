package com.rtech.gpgram;

import android.app.ComponentCaller;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.BitmapKt;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddpostActivity extends AppCompatActivity {
    LinearLayout overlayLayout;
    ImageView preview_image,camera_action,gallery_action,discard_btn;
    EditText caption;
    int cameraImageRequestCode=100,galleryImageRequest=200;
    File imageFile;
    Boolean isImageCaptured=false;
    AppCompatButton post_btn;
    String api=BuildConfig.BASE_URL.concat("/posts/createPost");
    SharedPreferences loginInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addpost);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        camera_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera,cameraImageRequestCode);

            }
        });

        gallery_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_PICK);
                gallery.setAction(MediaStore.ACTION_PICK_IMAGES);
                startActivityForResult(gallery,galleryImageRequest);
            }
        });

       discard_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               preview_image.setImageBitmap(null);
               overlayLayout.setVisibility(View.VISIBLE);
               isImageCaptured=false;
           }
       });


       post_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(isImageCaptured) {
                   String captiontext = caption.getText().toString();
                   if (captiontext.isEmpty()) captiontext = "null";
                   upload_toApi(imageFile, captiontext);
               }else{
                   Toast.makeText(getApplicationContext(),"please add photo",Toast.LENGTH_SHORT).show();
               }


           }
       });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){


            //image from camera
            if(requestCode==100){
                overlayLayout.setVisibility(View.GONE);
                Bitmap image=(Bitmap) (data.getExtras().get("data"));
                try {
                    imageFile = new File(getExternalFilesDir(null), "captured.jpg");

                    FileOutputStream out = new FileOutputStream(imageFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    isImageCaptured=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                preview_image.setImageBitmap(image);
            }
            //image from gallery
            else if (requestCode==200) {
                overlayLayout.setVisibility(View.GONE);
                Uri imageUri = data.getData();
                imageFile=makeFileFromUri(imageUri);
                isImageCaptured=true;
                preview_image.setImageURI(imageUri);
            }
        }
        
    }

    private void init(){
        overlayLayout=findViewById(R.id.add_post_overlay);
        preview_image=findViewById(R.id.preview_imageView);
        camera_action=findViewById(R.id.camera_action_btn);
        gallery_action=findViewById(R.id.gallery_action_btn);
        discard_btn=findViewById(R.id.close_btn);
        post_btn=findViewById(R.id.post_btn);
        caption=findViewById(R.id.caption_field);
        loginInfo=getSharedPreferences("loginInfo",MODE_PRIVATE);
        AndroidNetworking.initialize(AddpostActivity.this);


    }
    public void upload_toApi(File image,String caption){

        AndroidNetworking.upload(api).addHeaders("Authorization","Bearer ".concat(loginInfo.getString("token",null)))
                .addMultipartFile("image",image)
                .addMultipartParameter("caption",caption)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_SHORT).show();
              finish();

            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getApplicationContext(),"failed".concat(anError.getErrorBody()),Toast.LENGTH_SHORT).show();

            }
        });

    }
// convert from uri to file
    public File makeFileFromUri(Uri uri){

        File file = new File(getCacheDir(), "temp_image.jpg");
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}