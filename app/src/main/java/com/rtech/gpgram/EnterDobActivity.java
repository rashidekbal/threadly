package com.rtech.gpgram;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class EnterDobActivity extends AppCompatActivity {
EditText dob_field;
Calendar calendar;
AppCompatButton next_btn;
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
                        String dob = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dob_field.setText(dob);
                    },
                    year, month, day
            );

            // Disable future dates bro
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        Intent intent =getIntent();
        String token=intent.getStringExtra("token");
        String password=intent.getStringExtra("password");
        assert password != null;
        assert token != null;
        Toast.makeText(this, token.concat("").concat(password), Toast.LENGTH_SHORT).show();


    }
    private void init(){
        dob_field=findViewById(R.id.dob_field);
        calendar = Calendar.getInstance();
        next_btn=findViewById(R.id.next_btn);
    }
}