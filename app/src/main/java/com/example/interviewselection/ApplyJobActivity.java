package com.example.interviewselection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ApplyJobActivity extends AppCompatActivity {
    TextView enrollment, company, ctc, techstk, tvStatus;
    String company_name, userId;
    Integer company_id;
    String appStatus;
    String tCTC, tTS;
    Button applyButton;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);
        enrollment = findViewById(R.id.enrollment);
        applyButton = findViewById(R.id.applyButton);
        applyButton.setVisibility(View.INVISIBLE);
        company = findViewById(R.id.company);
        progressBar = findViewById(R.id.progressBar2);
        ctc = findViewById(R.id.ctc);
        techstk = findViewById(R.id.techstk);
        tvStatus = findViewById(R.id.app_status);
        userId = getIntent().getStringExtra("userId");
        company_name = getIntent().getStringExtra("company_name");

        new AsyncUpdateData().execute();


        String disEnroll = "Enrollment: " + userId;
        enrollment.setText(disEnroll);

        String disCompany = "Company: " + company_name;
        company.setText(disCompany);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncApplyAction().execute();
            }
        });


    }


    class AsyncUpdateData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/interview?useSSL=true", "harshit", "Parking@123");
                Statement statement = connection.createStatement();


                String queryCompanyDetails = String.format("select company_id, ctc, tech_stack from recruiter WHERE name='%s'", company_name);
                ResultSet resultCompany = statement.executeQuery(queryCompanyDetails);
                resultCompany.absolute(1);
                company_id = resultCompany.getInt(1);
                tCTC = "CTC: " + resultCompany.getString(2);
                tTS = "Tech Stack: " + resultCompany.getString(3);

                String queryCount = String.format("select count(*) from applications where company_id=%d and enrollment='%s'", company_id, userId);
                ResultSet appCount = statement.executeQuery(queryCount);
                appCount.absolute(1);
                int count = appCount.getInt(1);

                if (count == 0) {
                    appStatus = "NOT APPLIED";
                } else {
                    String queryStatus = String.format("select status from applications where company_id=%d and enrollment='%s'", company_id, userId);
                    ResultSet rs = statement.executeQuery(queryStatus);
                    rs.absolute(1);
                    appStatus = rs.getString(1);
                }


            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            ctc.setText(tCTC);
            techstk.setText(tTS);
            String tStatus = "Status: " + appStatus;
            tvStatus.setText(tStatus);

            if (appStatus.equals("NOT APPLIED")) {
                applyButton.setVisibility(View.VISIBLE);
            }
        }
    }

    class AsyncApplyAction extends  AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try
            {
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/interview?useSSL=true", "harshit", "Parking@123");
                Statement statement = connection.createStatement();
                String queryAdd = String.format("insert into applications (company_id, enrollment, status) values (%d, '%s', '%s')", company_id, userId, "APPLIED");
                statement.executeUpdate(queryAdd);
                applyButton.setVisibility(View.INVISIBLE);
                appStatus = "APPLIED";
            }
            catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            String tStatus = "Status: " + appStatus;
            tvStatus.setText(tStatus);

            if (appStatus.equals("NOT APPLIED")) {
                applyButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
