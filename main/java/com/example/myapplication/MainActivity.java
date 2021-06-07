package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity {
    Bitmap myBitmap;
    private RequestQueue queue;
    private String imageURL;
    TextView textView, cnameInput, capitalNameInput, regionInput, subRegionInput, populationInput, borderInput, languageInput;
    private int count;
    private String country;
    Spinner spinner;
    Button button, clear;
    ImageView flagImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        textView = (TextView) findViewById(R.id.text);
        cnameInput = (TextView) findViewById(R.id.CnameInput);
        capitalNameInput = (TextView) findViewById(R.id.CapitalnameInput);
        regionInput = (TextView) findViewById(R.id.RegionnameInput);
        subRegionInput = (TextView) findViewById(R.id.SubregionnameInput);
        populationInput = (TextView) findViewById(R.id.PopulationnameInput);
        borderInput = (TextView) findViewById(R.id.BordernameInput);
        languageInput = (TextView) findViewById(R.id.LaguagenameInput);
        flagImage = (ImageView) findViewById(R.id.flag);
        button = (Button) findViewById(R.id.search);
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache(MainActivity.this);
                Toast.makeText(MainActivity.this, "Cache has been deleted!", Toast.LENGTH_SHORT).show();
                clear.setVisibility(View.GONE);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                clear.setVisibility(View.VISIBLE);
            }
        });
        spinner = (Spinner) findViewById(R.id.spinner);
        String[] countries = new String[]{"Afghanistan","Armenia","Azerbaijan","Bahrain","Bangladesh","Bhutan","Brunei Darussalam","Cambodia","China","Georgia","Hong Kong",
                "India","Indonesia","Iran (Islamic Republic of)","Iraq","Israel","Japan","Jordan","Kazakhstan","Kuwait","Kyrgyzstan","Lao People's Democratic Republic","Lebanon","Malaysia",
                "Mongolia","Nepal","Korea (Democratic People's Republic of)", "Oman","Pakistan",
                "Palestine, State of","Philippines","Qatar","Russian","Saudi Arabia","Singapore", "Sri Lanka","Syrian Arab Republic","Taiwan","Tajikistan","Thailand","Turkey","Timor-Leste",
                "United Arab Emirates","Uzbekistan","Viet Nam","Yemen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Please select one!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getData(){
        String url = ("https://restcountries.eu/rest/v2/region/asia");
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jSONArray = new JSONArray(response); count = 0;
                    for (int j = 0; j < jSONArray.length(); j++){
                        JSONObject jsn = jSONArray.getJSONObject(j);
                        String keyVal = jsn.getString("name");
                        if (keyVal.equals(country)){
                            String capital = jsn.getString("capital");
                            String region = jsn.getString("region");
                            String subregion = jsn.getString("subregion");
                            String population = jsn.getString("population");
                            String borders = jsn.getString("borders");
                            String languages = jsn.getString("languages");
                            String flag = jsn.getString("flag");
                            cnameInput.setText(keyVal);
                            capitalNameInput.setText(capital);
                            borderInput.setText(borders);
                            subRegionInput.setText(subregion);
                            regionInput.setText(region);
                            populationInput.setText(population);
                            System.out.println(flag);
                            Utils.fetchSvg(MainActivity.this, flag, flagImage);
                            //String lang = jsn.getString("languages");
                            JSONArray jsonArray = new JSONArray(languages);
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject lang = jsonArray.getJSONObject(i);
                                String keyLang = lang.getString("name");
                                languageInput.setText(keyLang);

                            }


                            //String imageURL = String.valueOf(flag);
                           // Glide.with(MainActivity.this).load(flag).diskCacheStrategy(DiskCacheStrategy.ALL).into(flagImage);


                            //image.equals(flag);
                            //URL.equals(flag);
                            //imageView.toString().equals(flag);
                           // imageView.setImageURI(Uri.parse(flag));

                            //Picasso.with(MainActivity.this).load("https://i.imgur.com/tGbaZCY.jpg").into(flagImage);

                            break;
                        }
                    }


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response", error.toString());
            }
        });
        queue.add(request);

    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }
}