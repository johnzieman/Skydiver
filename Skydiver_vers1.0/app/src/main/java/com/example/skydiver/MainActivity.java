package com.example.skydiver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final String URL_DATA = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=573c2716058e22a7cf5c762c084846e1&units=metric";
    private EditText editText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textViewInfo);
    }

    public void searchCity(View view) {
        String cityName = editText.getText().toString().trim();
        if(cityName != null){
            DownloadTask task = new DownloadTask();
            String url = String.format(URL_DATA,cityName);
            task.execute(url);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(strings[0]);
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.readLine();
                    while (line != null){
                        stringBuilder.append(line);
                        line = reader.readLine();
                    }
                    return stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(s == null){
                    textView.setText("Not found");
                }else {

                    String city = jsonObject.getString("name");
                    String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    String descriptionDetails = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    String temp = jsonObject.getJSONObject("main").getString("temp");
                    //Log.i("test", city+description+descriptionDetails+temp); //some test checkings
                    String weather = String.format("City: %s\nTemperature : %s°C\nDetails: %s, %s", city, temp, description, descriptionDetails);
                    textView.setText(weather);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
