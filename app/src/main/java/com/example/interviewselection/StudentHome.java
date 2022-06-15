package com.example.interviewselection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StudentHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        Button btnApply = findViewById(R.id.apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GetCompanies.class);
                String userId = getIntent().getStringExtra("userId");
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }
}