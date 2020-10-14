package com.e.dictionary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    EditText edit;
    TextView noresult;
    Button btn;
    String clientId = "7eMxvPP_pEUFUrXWRjZI", clientSecret = "13l0hiAuyy";
    String result;
    Uri uri;
    String[] description_str = new String[100];
    String[] link_str = new String[100];
    String[] image_str = new String[100];
    private AdView mAdView;
    DBHelper myHelper;
    SQLiteDatabase sqlDB;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = (EditText) findViewById(R.id.edit);
        noresult = (TextView) findViewById(R.id.noresult);
        btn = (Button) findViewById(R.id.btn);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.refresh);

        intent = getIntent();
        myHelper = new DBHelper(this);
        sqlDB = myHelper.getReadableDatabase();
        if (intent.getStringExtra("input") != null) {
            noresult.setVisibility(View.INVISIBLE);
            edit.setText(intent.getStringExtra("input"));
            NaverTranslateTask asyncTask = new NaverTranslateTask();
            asyncTask.execute();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noresult.setVisibility(View.INVISIBLE);
                NaverTranslateTask asyncTask = new NaverTranslateTask();
                asyncTask.execute();
            }
        });
    }

    public class NaverTranslateTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String text = null;
            try {
                text = URLEncoder.encode(edit.getText().toString().trim(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("검색어 인코딩 실패", e);
            }
            String apiURL = "https://openapi.naver.com/v1/search/encyc?query=" + text + "&display=" + 100;
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);
            result = get(apiURL, requestHeaders);
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private String get(String apiURL, Map<String, String> requestHeaders) {
            HttpURLConnection con = connect(apiURL);
            try {
                con.setRequestMethod("GET");
                for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                    con.setRequestProperty(header.getKey(), header.getValue());
                }
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                    return readBody(con.getInputStream());
                } else { // 에러 발생
                    return readBody(con.getErrorStream());
                }
            } catch (Exception e) {
                throw new RuntimeException("API 요청과 응답 실패", e);
            } finally {
                con.disconnect();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private String readBody(InputStream inputStream) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            try (BufferedReader lineReader = new BufferedReader(streamReader)) {
                StringBuilder responseBody = new StringBuilder();
                String line;
                while ((line = lineReader.readLine()) != null) {
                    responseBody.append(line);
                }
                return responseBody.toString();
            } catch (IOException e) {
                throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
            }
        }

        private HttpURLConnection connect(String apiURL) {
            try {
                URL url = new URL(apiURL);
                return (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e) {
                throw new RuntimeException("API URL이 잘못되었습니다. : " + apiURL, e);
            } catch (IOException e) {
                throw new RuntimeException("연결이 실패했습니다. : " + apiURL, e);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JsonParser parser2 = new JsonParser();
                JsonElement element2 = parser2.parse(result);
                if (Integer.parseInt(element2.getAsJsonObject().get("display").toString()) > 0) {
                    noresult.setVisibility(View.INVISIBLE);
                    int display = Integer.valueOf(element2.getAsJsonObject().get("display").toString());
                    for (int i = 0; i < display; i++) {
                        JsonParser parser = new JsonParser();
                        Object object = parser.parse(result);
                        JsonObject jsonObject = (JsonObject) object;
                        JsonArray array = jsonObject.getAsJsonArray("items");
                        JsonElement element = array.get(i);
                        String description = element.getAsJsonObject().get("description").toString();
                        String title = element.getAsJsonObject().get("title").toString();
                        String link = element.getAsJsonObject().get("link").toString();
                        String thumbnail = element.getAsJsonObject().get("thumbnail").toString();
                        description = description.replace("<b>", "");
                        description = description.replace("</b>", "");
                        description = description.substring(1, description.length() - 1);
                        title = title.replace("<b>", "");
                        title = title.replace("</b>", "");
                        title = title.substring(1, title.length() - 1);
                        link = link.replace("\"", "");
                        thumbnail = thumbnail.replace("\"", "");
                        link_str[i] = link;
                        description_str[i] = "<" + title + ">" + description;
                        if (!thumbnail.equals("")) {
                            image_str[i] = thumbnail;
                        }
                    }
                    boolean pass = true;
                    Cursor history_cursor;
                    sqlDB = myHelper.getReadableDatabase();
                    history_cursor = sqlDB.rawQuery("SELECT *FROM history;", null);
                    while (history_cursor.moveToNext()) {
                        if (edit.getText().toString().trim().equals(history_cursor.getString(1))) {
                            pass = false;
                        }
                    }
                    sqlDB = myHelper.getWritableDatabase();
                    if (pass) {
                        String in = edit.getText().toString().replace("'", "＇");
                        sqlDB.execSQL("INSERT INTO history VALUES (" + null + ",'" + in + "');");
                    }
                    history_cursor.close();
                    sqlDB.close();
                    recyclerView.setAdapter(new RecyclerViewAdapter());
                } else {
                    noresult.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(new RecyclerViewAdapter());
                }
            } catch (Exception e) {
                recyclerView.setAdapter(new RecyclerViewAdapter());
            }
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<SerchModel> dictionaryList;

        public RecyclerViewAdapter() {
            dictionaryList = new ArrayList<>();
            for (int i = 0; i < description_str.length; i++) {
                if (description_str[i] == null) {
                    continue;
                }
                SerchModel serchModel = new SerchModel();
                serchModel.description = description_str[i];
                serchModel.link = link_str[i];
                if (image_str[i] != null) {
                    serchModel.thumbnail = image_str[i];
                }
                dictionaryList.add(serchModel);
                image_str[i] = link_str[i] = description_str[i] = null;
            }
            notifyDataSetChanged();

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dictionary, parent, false);
            return new RecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;

            customViewHolder.txt.setText(dictionaryList.get(position).description);

            if (dictionaryList.get(position).thumbnail != null) {
                try {
                    String cut1 = dictionaryList.get(position).thumbnail.substring(dictionaryList.get(position).thumbnail.lastIndexOf("net/") + 4);
                    String cut2 = cut1.substring(cut1.lastIndexOf("?") + 1);
                    cut1 = cut1.replace(cut2, "");
                    final String customUrl = "https://dbscthumb-phinf.pstatic.net/" + cut1 + "type=m250&wm=N";
                    Glide.with(customViewHolder.image.getContext())
                            .asBitmap()
                            .load(customUrl)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                    customViewHolder.image.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    customViewHolder.image.setVisibility(View.GONE);

                                }
                            });

                } catch (Exception e) {

                }
            } else {
                customViewHolder.image.setVisibility(View.GONE);
                customViewHolder.txt.setTextSize(14);
            }

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uri = Uri.parse(dictionaryList.get(position).link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    // 애니메이션
                }
            });
        }

        @Override
        public int getItemCount() {
            return dictionaryList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView txt;
            public ImageView image;

            public CustomViewHolder(View view) {
                super(view);
                txt = (TextView) view.findViewById(R.id.txt);
                image = (ImageView) view.findViewById(R.id.image);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                edit.setText("");
                recyclerView.setAdapter(new RecyclerViewAdapter());
                noresult.setVisibility(View.VISIBLE);
                break;
            case R.id.history:
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
                finish();
                // 애니메이션
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
