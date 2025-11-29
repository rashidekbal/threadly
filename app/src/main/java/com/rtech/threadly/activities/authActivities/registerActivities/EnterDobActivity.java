package com.rtech.threadly.activities.authActivities.registerActivities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;

import java.util.Calendar;

public class EnterDobActivity extends AppCompatActivity {
EditText dob_field;
Calendar calendar;
AppCompatButton next_btn;
Intent getDataIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_dob);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        init();
        dob_field.setOnClickListener(view -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String dob = selectedYear  + "/" + (selectedMonth + 1) + "/" + selectedDay;
                        dob_field.setText(dob);
                    },
                    year, month, day
            );

            // Disable future dates bro
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
next_btn.setOnClickListener(v -> {
    next_btn.setEnabled(false);
    String dob=dob_field.getText().toString();
    if(dob.isEmpty()){
        Toast.makeText(EnterDobActivity.this, "Please enter a valid dob", Toast.LENGTH_SHORT).show();
        next_btn.setEnabled(true);
    }else{
        String token=getDataIntent.getStringExtra("token");
        String password=getDataIntent.getStringExtra("password");
        next_btn.setEnabled(true);
        Intent intent=new Intent(EnterDobActivity.this, NameEnterActivity.class);
        intent.putExtra("token",token);
        intent.putExtra("password",password);
        intent.putExtra("dob",dob);
        intent.putExtra("api",getDataIntent.getStringExtra("api"));
        startActivity(intent);

    }

});




    }
    private void init(){
        dob_field=findViewById(R.id.dob_field);
        calendar = Calendar.getInstance();
        next_btn=findViewById(R.id.next_btn);
        getDataIntent=getIntent();
    }
}