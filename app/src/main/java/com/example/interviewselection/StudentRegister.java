package com.example.interviewselection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentRegister extends AppCompatActivity {

    EditText etEnterName, etEnterEmail, etEnterEnrollment, etEnterBranch, etEnterCGPA, etCreatePass, etConfirmPass;

    Button btnRegister;

    ProgressBar progressBarUpdate;

    String name, email, enroll, branch, createPass, cgpa, confirmPass, userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_register);

        etEnterName = findViewById(R.id.etEnterName);
        etEnterEmail = findViewById(R.id.etEnterEmail);
        etEnterEnrollment = findViewById(R.id.etEnterEnrollment);
        etEnterBranch = findViewById(R.id.etEnterBranch);
        etEnterCGPA = findViewById(R.id.etEnterCGPA);
        etCreatePass = findViewById(R.id.etCreatePass);
        etConfirmPass = findViewById(R.id.etConfirmPass);

        btnRegister = findViewById(R.id.btnRegister);

        progressBarUpdate = findViewById(R.id.progressBarUpdate);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RegisterAsync().execute();
            }
        });



    }

    class RegisterAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBarUpdate.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            name = etEnterName.getText().toString();
            email = etEnterEmail.getText().toString();
            enroll = etEnterEnrollment.getText().toString();
            branch = etEnterBranch.getText().toString();
            cgpa = etEnterCGPA.getText().toString();
            createPass = etCreatePass.getText().toString();
            confirmPass = etConfirmPass.getText().toString();

            if(TextUtils.isEmpty(name) ||
                    TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty((enroll)) ||
                    TextUtils.isEmpty(branch) ||
                    TextUtils.isEmpty(cgpa) ||
                    TextUtils.isEmpty(createPass) ||
                    TextUtils.isEmpty(confirmPass)) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast nullEntry = Toast.makeText(getApplicationContext(), "Please Enter all fields!", Toast.LENGTH_SHORT);
                        nullEntry.show();
                    }
                });
            }

            else {

                boolean flagUserAlreadyExists = false, flagVehicleAlreadyExists = false;

                Connection connection = null;
                Statement statementCheck = null, statementCheck1 = null;
                ResultSet resultGet = null, resultCount = null;

                try {

                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/interview?useSSL=true", "harshit", "Parking@123");
                    statementCheck = connection.createStatement();
                    statementCheck1 = connection.createStatement();

                    String queryGet = String.format("select * from student;");
                    resultGet = statementCheck.executeQuery(queryGet);

                    String queryCount = String.format("select count(*) from student;");
                    resultCount = statementCheck1.executeQuery(queryCount);

                    while(resultGet.next()) {
                        if(email.equals(resultGet.getString("email"))){
                            flagUserAlreadyExists = true;
                            break;
                        }
                    }

                    connection.close();
                    statementCheck.close();
                    statementCheck1.close();
                    resultGet.close();
                    resultCount.close();

                } catch (Exception e) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "CheckEmail Exception!", Toast.LENGTH_SHORT);
                            toastPasswordIncorrect.show();
                        }
                    });


                    e.printStackTrace();
                }

                if (flagUserAlreadyExists) {
                    runOnUiThread(new Runnable() {

                        public void run() {

                            Toast alreadyExists = Toast.makeText(getApplicationContext(),
                                    "User with Email ID already exists! Login or use different Email.", Toast.LENGTH_SHORT);

                            alreadyExists.show();
                        }
                    });
                }

                else if (!createPass.equals(confirmPass)) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "Enter same password in both password fields", Toast.LENGTH_SHORT);
                            toastPasswordIncorrect.show();
                        }
                    });

                } else {

                    Connection connection1 = null;
                    Statement statementCreateNew = null;
                    int resultCreateNew;

                    try {

                        connection1 = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/interview?useSSL=true", "harshit", "Parking@123");
                        statementCreateNew = connection1.createStatement();
                        String queryInsert = String.format("INSERT into student " +
                                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');" ,
                                name, enroll, email, createPass, branch, cgpa);

                        resultCreateNew = statementCreateNew.executeUpdate(queryInsert);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (resultCreateNew == 0) {
                                    Toast toastFailure = Toast.makeText(getApplicationContext(), "Something Went Wrong! Please Try Again.", Toast.LENGTH_SHORT);
                                    toastFailure.show();
                                } else {
                                    Toast toastSuccess = Toast.makeText(getApplicationContext(), "Registration Successful!!", Toast.LENGTH_SHORT);
                                    toastSuccess.show();
                                }
                            }
                        });


                        try {
                            connection1.close();
                            statementCreateNew.close();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "InsertAsync Exception!", Toast.LENGTH_SHORT);
                                toastPasswordIncorrect.show();
                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {

                            etCreatePass.setText(null);
                            etConfirmPass.setText(null);
                        }
                    });

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBarUpdate.setVisibility(View.INVISIBLE);
        }
    }
}
