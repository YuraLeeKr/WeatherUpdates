package com.example.weatherscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    EditText City, Country;
    TextView Result;
    private final String url ="https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "bb3cba20f3473754d3dc493bac1511b3";
    DecimalFormat df = new DecimalFormat("#.##");

    //hi hello
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        City = findViewById(R.id.City);
        Country = findViewById(R.id.Country);
        Result = findViewById(R.id.Result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void getweatherDetails(View view) {
        Log.d("MainActivity", "Button clicked"); // Log message to indicate button click
        String tempUrl = "";
        String city = City.getText().toString().trim();
        String country = Country.getText().toString().trim();
        if (city.equals("")) {
            Result.setText("Please fill in the City field");
        } else {
            if(country.equals("")) {
                tempUrl = url + "?q=" + city + "&appid=" + appid;
            }
            else{
                tempUrl = url + "?q=" + city +","+ country + "&appid=" + appid;
            }
        }

            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        // Get the city name
                        String cityName = jsonResponse.optString("name", "Unknown City");

                        // Continue parsing other fields as needed
                        JSONArray weatherArray = jsonResponse.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String description = weatherObject.optString("description", "No description");

                        JSONObject mainObject = jsonResponse.getJSONObject("main");
                        double temperature = mainObject.optDouble("temp", 0.0);
                        double feelsLike = mainObject.optDouble("feels_like", 0.0);
                        int pressure = mainObject.optInt("pressure", 0);
                        int humidity = mainObject.optInt("humidity", 0);

                        // Convert temperature from Kelvin to Celsius
                        double tempCelsius = temperature - 273.15;
                        double feelsLikeCelsius = feelsLike - 273.15;
                        // Construct output string
                        String output = "Weather in " + cityName
                                + "\nDescription: " + description
                                + "\nTemperature: " + df.format(tempCelsius) + "°C"
                                + "\nFeels Like: " + df.format(feelsLikeCelsius) + "°C"
                                + "\nPressure: " + pressure + " hPa"
                                + "\nHumidity: " + humidity + "%";

                        // Update UI
                        Result.setText(output);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this, volleyError.toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

