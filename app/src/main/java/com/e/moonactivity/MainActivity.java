package com.e.moonactivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    Button btn_left, btn_right;
    TextView txt;
    ImageView image;
    String lunAge;
    int year, month, date, lastday;
    String[] moon_time = new String[35];
    String[] moon_time_up = new String[35];
    LocalDate today, next, prev;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_left = (Button) findViewById(R.id.btn_left);
        btn_right = (Button) findViewById(R.id.btn_right);
        txt = (TextView) findViewById(R.id.txt);
        image = (ImageView) findViewById(R.id.image);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        today = LocalDate.now();        // 이번달
        next = today.plusMonths(1);     // 다음달
        prev = today.minusMonths(1);    // 이전달

        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next = today;
                today = prev;
                prev = prev.minusMonths(1);
                setting();
                date = 1;
            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev = today;
                today = next;
                next = next.plusMonths(1);
                setting();
                date = 1;
            }
        });
        setting();
    }

    void setting() {
        for (int i = 1; i < moon_time.length; i++) {
            moon_time[i] = moon_time_up[i] = null;
        }
        btn_right.setText(next.getMonthValue() + "월");
        btn_left.setText(prev.getMonthValue() + "월");
        txt.setText(today.getYear() + "년 " + today.getMonthValue() + "월");
        date = today.getDayOfMonth();
        month = today.getMonthValue();
        year = today.getYear();
        lastday = today.lengthOfMonth();
        NaverTranslateTask asyncTask = new NaverTranslateTask();
        asyncTask.execute();
    }

    public class NaverTranslateTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
                for (int i = 1; i <= lastday; i++) {
                    moon_time_up[i] = moon_time[i] = null;
                    String dates, months;
                    if (i < 10) {
                        dates = "0" + i;
                    } else {
                        dates = String.valueOf(i);
                    }
                    if (month < 10) {
                        months = "0" + month;
                    } else {
                        months = String.valueOf(month);
                    }
                    StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/LunPhInfoService/getLunPhInfo"); /*URL*/
                    urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=eM8YNJmbJ%2BJOQHAKzZVgE4wqg2wO6Vo4nJKtP9um5s%2BkAKR%2BvqX7XuBrFYuBVGLiqvXbc%2BefHP95Plgo04CTkQ%3D%3D"); /*Service Key*/
                    urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(year), "UTF-8")); /*연*/
                    urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(months, "UTF-8")); /*월*/
                    urlBuilder.append("&" + URLEncoder.encode("solDay", "UTF-8") + "=" + URLEncoder.encode(dates, "UTF-8")); /*일*/
                    URL url = new URL(urlBuilder.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");
                    BufferedReader rd;

                    if (conn.getResponseCode() == 200 && conn.getResponseCode() <= 300) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    final StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                    conn.disconnect();
                    int index1 = sb.lastIndexOf("<lunAge>");
                    int index2 = sb.lastIndexOf("</lunAge>");
                    lunAge = sb.substring(index1 + 8, index2);
                    double lunAge2 = Double.parseDouble(lunAge);
                    lunAge = String.valueOf(Math.round(lunAge2));
                    moon_time_up[i] = lunAge;
                    moon_time[i] = String.valueOf(lunAge2);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView.setAdapter(new RecyclerViewAdapter());
        }
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            recyclerView.setAdapter(new RecyclerViewAdapter());
        }
    };

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<DateModel> dateList;

        public RecyclerViewAdapter() {
            dateList = new ArrayList<>();
            for (int i = 1; i <= lastday; i++) {
                DateModel dateModel = new DateModel();
                dateModel.year = String.valueOf(year);
                dateModel.month = String.valueOf(month);
                dateModel.day = String.valueOf(i);
                DayOfWeek weeks = LocalDate.of(year, month, i).getDayOfWeek();
                dateModel.moon = moon_time[i];
                dateModel.moon_up = moon_time_up[i];
                switch (String.valueOf(weeks)) {
                    case "MONDAY":
                        dateModel.week = "월";
                        break;
                    case "TUESDAY":
                        dateModel.week = "화";
                        break;
                    case "WEDNESDAY":
                        dateModel.week = "수";
                        break;
                    case "THURSDAY":
                        dateModel.week = "목";
                        break;
                    case "FRIDAY":
                        dateModel.week = "금";
                        break;
                    case "SATURDAY":
                        dateModel.week = "토";
                        break;
                    case "SUNDAY":
                        dateModel.week = "일";
                        break;
                }
                dateList.add(dateModel);
            }
            notifyDataSetChanged();
            recyclerView.scrollToPosition(date - 1);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
            return new RecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;

            customViewHolder.date_txt.setText(dateList.get(position).month + "월 " + dateList.get(position).day + "일 (" + dateList.get(position).week + ")");
            if (dateList.get(position).week.equals("토")) {
                customViewHolder.date_txt.setTextColor(Color.argb(100, 0, 0, 255));
            } else if (dateList.get(position).week.equals("일")) {
                customViewHolder.date_txt.setTextColor(Color.argb(100, 255, 0, 0));
            } else {
                customViewHolder.date_txt.setTextColor(Color.argb(100, 0, 0, 0));
            }
            if (dateList.get(position).moon != null) {
                customViewHolder.moon_txt.setText("월령 : " + dateList.get(position).moon);
            }
            try {
                int drawable = R.drawable.moon5;
                switch (dateList.get(position).moon_up) {
                    case "0":
                        drawable = R.drawable.moon0;
                        break;
                    case "1":
                        drawable = R.drawable.moon1;
                        break;
                    case "2":
                        drawable = R.drawable.moon2;
                        break;
                    case "3":
                        drawable = R.drawable.moon3;
                        break;
                    case "4":
                        drawable = R.drawable.moon4;
                        break;
                    case "5":
                        drawable = R.drawable.moon5;
                        break;
                    case "6":
                        drawable = R.drawable.moon6;
                        break;
                    case "7":
                        drawable = R.drawable.moon7;
                        break;
                    case "8":
                        drawable = R.drawable.moon8;
                        break;
                    case "9":
                        drawable = R.drawable.moon9;
                        break;
                    case "10":
                        drawable = R.drawable.moon10;
                        break;
                    case "11":
                        drawable = R.drawable.moon11;
                        break;
                    case "12":
                        drawable = R.drawable.moon12;
                        break;
                    case "13":
                        drawable = R.drawable.moon13;
                        break;
                    case "14":
                        drawable = R.drawable.moon14;
                        break;
                    case "15":
                        drawable = R.drawable.moon15;
                        break;
                    case "16":
                        drawable = R.drawable.moon16;
                        break;
                    case "17":
                        drawable = R.drawable.moon17;
                        break;
                    case "18":
                        drawable = R.drawable.moon18;
                        break;
                    case "19":
                        drawable = R.drawable.moon19;
                        break;
                    case "20":
                        drawable = R.drawable.moon20;
                        break;
                    case "21":
                        drawable = R.drawable.moon21;
                        break;
                    case "22":
                        drawable = R.drawable.moon22;
                        break;
                    case "23":
                        drawable = R.drawable.moon23;
                        break;
                    case "24":
                        drawable = R.drawable.moon24;
                        break;
                    case "25":
                        drawable = R.drawable.moon25;
                        break;
                    case "26":
                        drawable = R.drawable.moon26;
                        break;
                    case "27":
                        drawable = R.drawable.moon27;
                        break;
                    case "28":
                        drawable = R.drawable.moon28;
                        break;
                    case "29":
                        drawable = R.drawable.moon29;
                        break;
                }
                customViewHolder.image.setImageResource(drawable);
            } catch (Exception e) {

            }
        }

        @Override
        public int getItemCount() {
            return dateList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView date_txt;
            public TextView moon_txt;
            public ImageView image;

            public CustomViewHolder(View view) {
                super(view);
                date_txt = (TextView) view.findViewById(R.id.date_txt);
                moon_txt = (TextView) view.findViewById(R.id.moon_txt);
                image = (ImageView) view.findViewById(R.id.image);
            }
        }
    }
}
