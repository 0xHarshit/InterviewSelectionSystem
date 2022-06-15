package com.example.interviewselection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class RecruiterHome extends AppCompatActivity {

    ProgressBar progressBarGetCities;

    ListView lvCities;

    String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_home);

        progressBarGetCities=findViewById(R.id.progressBarGetCities);
        lvCities=findViewById(R.id.lvCities);

        companyId = getIntent().getStringExtra("companyId");

        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        lvCities.setAdapter(citiesAdapter);


        new GetApplicants().execute();


    }

    class GetApplicants extends AsyncTask<Void, Void, ArrayList<String>> {

        ArrayAdapter<String> citiesAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            citiesAdapter= (ArrayAdapter<String>) lvCities.getAdapter();
            progressBarGetCities.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            ArrayList <String> cities = new ArrayList<String>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/interview?useSSL=true", "harshit", "Parking@123");
                Statement statementLogin = connection.createStatement();

                String queryCities = String.format( "select enrollment from applications where company_id=%s", companyId);
                ResultSet resultSetCities = statementLogin.executeQuery(queryCities);

                while(resultSetCities.next())
                {
                    String city = resultSetCities.getString(1);
                    cities.add(city);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return cities;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            progressBarGetCities.setVisibility(View.GONE);

            citiesAdapter.addAll(strings);

            lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getApplicationContext(), ShowApplicant.class);

                    intent.putExtra("enrollment", strings.get(position));
                    intent.putExtra("companyId", companyId);
                    startActivity(intent);

                }
            });

        }
    }
}