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

public class ShowApplicant extends AppCompatActivity {
    TextView enrollment, company, ctc, techstk, tvStatus;
    String company_id, userId;
    String appStatus;
    String tCTC, tTS, tCompany;
    Button acceptButton, rejectButton;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_applicant);
        enrollment = findViewById(R.id.enrollment);
        acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setVisibility(View.INVISIBLE);
        rejectButton = findViewById(R.id.rejectButton);
        rejectButton.setVisibility(View.INVISIBLE);
        company = findViewById(R.id.company);
        progressBar = findViewById(R.id.progressBar2);
        ctc = findViewById(R.id.ctc);
        techstk = findViewById(R.id.techstk);
        tvStatus = findViewById(R.id.app_status);
        userId = getIntent().getStringExtra("enrollment");
        company_id = getIntent().getStringExtra("companyId");

        new AsyncUpdateData().execute();

        String disEnroll = "Enrollment: " + userId;
        enrollment.setText(disEnroll);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] params = {"ACCEPTED"};
                new AsyncAcceptAction().execute(params);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] params = {"REJECTED"};
                new AsyncAcceptAction().execute(params);
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


                String queryCompanyDetails = String.format("select name, cgpa, branch from student WHERE enrollment='%s'", userId);
                ResultSet resultCompany = statement.executeQuery(queryCompanyDetails);
                resultCompany.absolute(1);
                tCompany = "Name: " + resultCompany.getString(1);
                tCTC = "CGPA: " + resultCompany.getString(2);
                tTS = "Branch: " + resultCompany.getString(3);

                String queryStatus = String.format("select status from applications where company_id=%s and enrollment='%s'", company_id, userId);
                ResultSet rs = statement.executeQuery(queryStatus);
                rs.absolute(1);
                appStatus = rs.getString(1);


            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            company.setText(tCompany);
            ctc.setText(tCTC);
            techstk.setText(tTS);
            String tStatus = "Status: " + appStatus;
            tvStatus.setText(tStatus);

            if (appStatus.equals("APPLIED")) {
                acceptButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
            }
        }
    }

    class AsyncAcceptAction extends  AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String action = strings[0];
            try
            {
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/interview?useSSL=true", "harshit", "Parking@123");
                Statement statement = connection.createStatement();
                String queryAdd = String.format("update applications set status='%s' where company_id=%s and enrollment='%s'", action, company_id, userId);
                statement.executeUpdate(queryAdd);
                acceptButton.setVisibility(View.INVISIBLE);
                rejectButton.setVisibility(View.INVISIBLE);
                appStatus = action;
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

            if (appStatus.equals("APPLIED")) {
                acceptButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
