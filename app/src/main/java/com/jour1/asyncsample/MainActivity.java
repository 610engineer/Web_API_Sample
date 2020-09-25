package com.jour1.asyncsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvCityList = findViewById(R.id.lvCityList);
        List<Map<String,String>> cityList = new ArrayList<>();
        Map<String,String> city = new HashMap<>();
        city.put("name","Osaka");
        city.put("id","1853908");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","Gifu");
        city.put("id","1863640");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","Nagoya");
        city.put("id","1856057");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","Tokyo");
        city.put("id","1850147");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","Fukuoka");
        city.put("id","1863967");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","Kyoto");
        city.put("id","1857910");
        cityList.add(city);

        String[] from={"name"};
        int[] to = {android.R.id.text1};
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,cityList,
                android.R.layout.simple_expandable_list_item_1,from,to);
        lvCityList.setAdapter(adapter);
        lvCityList.setOnItemClickListener(new ListItemClickListener());
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position , long id){
            Map<String, String> item = (Map<String,String>) parent.getItemAtPosition(position);
            String cityName = item.get("name");
            String cityId = item.get("id");
            TextView tvCityName = findViewById(R.id.tvCityName);
            tvCityName.setText(cityName + " weather :");

            TextView tvWeatherTelop = findViewById(R.id.tvWeatherTelop);
            TextView tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
            WeatherInfoReceiver receiver = new WeatherInfoReceiver(tvWeatherTelop,tvWeatherDesc);
            receiver.execute(cityId);
        }
    }

    private class WeatherInfoReceiver extends AsyncTask<String,String,String>{
        private TextView _tvWeatherTelop;
        private TextView _tvWeatherDesc;

        public WeatherInfoReceiver(TextView tvWeatherTelop, TextView tvWeatherDesc){
            _tvWeatherDesc = tvWeatherDesc;
            _tvWeatherTelop = tvWeatherTelop;
        }
        @Override
        public String doInBackground(String...params){
            String id = params[0];
            String urlStr = "http://api.openweathermap.org/data/2.5/weather?id=" + id +"&appid=3b81ba55fe173c0a2efde5ce46ca07f1";
            Log.d("Tag","urlStrの中身は" + urlStr);
            String result="";

            //HTTP
            HttpURLConnection con = null;
            InputStream is = null;
            try{
                URL url = new URL(urlStr);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                is = con.getInputStream();
                result = is2String(is);
                //Log.d("TAG", "resultの中身は"+ result);
            }
            catch(MalformedURLException ex){
                Log.d("TAG", "失敗");
            }
            catch(IOException ex){
                Log.d("TAG", "失敗");

            }
            finally {
                if(con != null){
                    con.disconnect();
                }
                if(is != null){
                    try{
                        is.close();
                    }
                    catch(IOException ex){

                    }
                }
            }
            return result;
        }

        private String is2String(InputStream is) throws IOException{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            char[] b = new char[1024];
            int line;
            while(0 <= (line = reader.read(b))){
                sb.append(b, 0 , line);
            }
            return sb.toString();
        }

        @Override
        public void onPostExecute(String result){
            String telop ="";
            String desc = "お天気詳細";
            Log.d("TAG", "resultの中身は"+ result);
            try{
                JSONObject rootJSON = new JSONObject(result);
                JSONArray weatherArray = rootJSON.getJSONArray("weather");
                JSONObject weatherObj = weatherArray.getJSONObject(0);
                telop = weatherObj.getString("main");
            }
            catch(JSONException ex){
            }
            _tvWeatherTelop.setText(telop);
            _tvWeatherDesc.setText(desc);
        }
    }
}