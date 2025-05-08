package com.rtech.gpgram;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rtech.gpgram.fragments.homeFragment;
import com.rtech.gpgram.fragments.notificationFragment;
import com.rtech.gpgram.fragments.profileFragment;
import com.rtech.gpgram.fragments.searchFragment;

public class HomeActivity extends AppCompatActivity {
SharedPreferences loginInfo;
SharedPreferences.Editor prefEditor;
Button logOut_btn;
BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });



        init();

//        TextView textView=findViewById(R.id.textView);
//        String username=loginInfo.getString("username","madherchod");
//        textView.setText(username);
//        logOut_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                prefEditor.clear().apply();
//                Intent loginPage=new Intent(HomeActivity.this, LoginActivity.class);
//                loginPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(loginPage);
//                finish();
//
//            }
//        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.home){
                    addFragment(new homeFragment());



                } else if (item.getItemId()==R.id.search) {

                    addFragment(new searchFragment());

                } else if (item.getItemId()==R.id.add_post) {
                    Toast.makeText(HomeActivity.this,"opening new intent ",Toast.LENGTH_SHORT).show();


                } else if (item.getItemId()==R.id.notification) {
                    addFragment(new notificationFragment());

                }else if (item.getItemId()==R.id.profile){

                    addFragment(new profileFragment());
                }

                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.home);



    }
    protected void init(){
//        loginInfo=getSharedPreferences("loginInfo", MODE_PRIVATE);
//        prefEditor=loginInfo.edit();
//        logOut_btn=findViewById(R.id.logout);
    bottomNavigationView=findViewById(R.id.bottom_navigation);


    }

    private void addFragment(Fragment fragment){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentHolder,fragment);
        transaction.commit();

    }

}