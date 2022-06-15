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

public class GetCompanies extends AppCompatActivity {

    ProgressBar progressBarGetCities;

    ListView lvCities;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_companies);

        progressBarGetCities=findViewById(R.id.progressBarGetCities);
        lvCities=findViewById(R.id.lvCities);

        userId = getIntent().getStringExtra("userId");

        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        lvCities.setAdapter(citiesAdapter);


        new GetCities().execute();


    }

    class GetCities extends AsyncTask<Void, Void, ArrayList<String>>{

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

                String queryCities = "select name from recruiter";
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

                    Intent intent = new Intent(getApplicationContext(), ApplyJobActivity.class);

                    intent.putExtra("company_name", strings.get(position));
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                }
            });

        }
    }
}