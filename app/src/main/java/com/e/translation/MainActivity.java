package com.e.translation;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String clientId = "1xEmfrzp6LXgFRox_yAI", clientSecret = "wd8tODeJVm", result, source = "auto", target = "en", temp;
    Button btn;
    EditText edit;
    TextView txt, length;
    ImageView image, copy, delete;
    Spinner spinner, spinner2;
    private AdView mAdView;
    String[] source_language_1 = {"언어감지", "영어", "한국어", "일본어", "중국어 간체", "중국어 번체", "스페인어", "프랑스어", "베트남어", "태국어", "인도네시아어", "독일어", "러시아어", "이탈리아어"};
    String[] source_language_2 = {"auto", "en", "ko", "ja", "zh-CN", "zh-TW", "es", "fr", "vi", "th", "id", "de", "ru", "it"};
    String[] target_language_1 = {"영어", "한국어", "일본어", "중국어 간체", "중국어 번체", "스페인어", "프랑스어", "베트남어", "태국어", "인도네시아어", "독일어", "러시아어", "이탈리아어"};
    String[] target_language_2 = {"en", "ko", "ja", "zh-CN", "zh-TW", "es", "fr", "vi", "th", "id", "de", "ru", "it"};
    int select1 = 0, select2 = 0;
    DBHelper myHelper;
    SQLiteDatabase sqlDB;
    Intent intent;

    public class NaverTranslateTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder output = new StringBuilder();
            try {
                // 번역문 인코딩
                String text = URLEncoder.encode(edit.getText().toString(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                // 파파고 API와 연결
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // 번역 문장을 파라미터로 전송
                String postParams = "source=" + source + "&target=" + target + "&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                // 번역 결과 받아옴
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    output.append(inputLine);
                }
                br.close();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "번역을 할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
            result = output.toString();
            return null;
        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            try {
                if (element.getAsJsonObject().get("errorMessage") != null) {
                    Toast.makeText(getApplicationContext(), "번역을 할 수 없습니다", Toast.LENGTH_SHORT).show();
                } else if (element.getAsJsonObject().get("message") != null) {
                    // 번역 결과 출력
                    String str = element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();
                    if (edit.getText().toString().equals(str)) {
                        Toast.makeText(getApplicationContext(), "번역을 할 수 없습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean pass = true;
                        txt.setText(str);
                        Cursor history_cursor;
                        sqlDB = myHelper.getReadableDatabase();
                        history_cursor = sqlDB.rawQuery("SELECT *FROM history;", null);
                        while (history_cursor.moveToNext()) {
                            if (edit.getText().toString().equals(history_cursor.getString(1)) && txt.getText().toString().equals(history_cursor.getString(2))) {
                                pass = false;
                            }
                        }
                        sqlDB = myHelper.getWritableDatabase();
                        if (pass) {
                            String in = edit.getText().toString(), re = txt.getText().toString();
                            in = in.replace("'","＇");
                            re = re.replace("'","＇");
                            String sel1 = String.valueOf(select1), sel2 = String.valueOf(select2);
                            sqlDB.execSQL("INSERT INTO history VALUES (" + null + ",'" + in + "','" + re + "','" + sel1 + "','" +sel2 + "');");
                        }

                        history_cursor.close();
                        sqlDB.close();
                    }
                }
            } catch (ExceptionInInitializerError e) {
                Toast.makeText(getApplicationContext(), "번역을 할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class NaverTranslateTask2 extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder output = new StringBuilder();
            try {
                // 번역문 인코딩
                String text = URLEncoder.encode(edit.getText().toString(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/detectLangs";
                // 파파고 API와 연결
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // 번역 문장을 파라미터로 전송
                String postParams = "query=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                // 번역 결과 받아옴
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    output.append(inputLine);
                }
                br.close();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "번역을 할 수 없습니다1", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            result = output.toString();
            return null;
        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            try {
                if (element.getAsJsonObject().get("errorMessage") != null) {
//                    Log.e("번역 오류", "번역 오류가 발생했습니다.");
                    Toast.makeText(getApplicationContext(), "번역을 할 수 없습니다2", Toast.LENGTH_SHORT).show();
                } else if (element.getAsJsonObject().get("langCode") != null) {
                    // 번역 결과 출력
                    source = element.getAsJsonObject().get("langCode").getAsString();
                    switch (source) {
                        case "en":
                            select1 = 1;
                            break;
                        case "ko":
                            select1 = 2;
                            break;
                        case "ja":
                            select1 = 3;
                            break;
                        case "zh-CN":
                            select1 = 4;
                            break;
                        case "zh-TW":
                            select1 = 5;
                            break;
                        case "es":
                            select1 = 6;
                            break;
                        case "fr":
                            select1 = 7;
                            break;
                        case "vi":
                            select1 = 8;
                            break;
                        case "th":
                            select1 = 9;
                            break;
                        case "id":
                            select1 = 10;
                            break;
                        case "de":
                            select1 = 11;
                            break;
                        case "ru":
                            select1 = 12;
                            break;
                        case "it":
                            select1 = 13;
                            break;
                    }
                    NaverTranslateTask asyncTask = new NaverTranslateTask();
                    asyncTask.execute();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "번역을 할 수 없습니다3", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        edit = (EditText) findViewById(R.id.edit);
        txt = (TextView) findViewById(R.id.txt);
        length = (TextView) findViewById(R.id.length);
        image = (ImageView) findViewById(R.id.image);
        copy = (ImageView) findViewById(R.id.copy);
        delete = (ImageView) findViewById(R.id.delete);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        mAdView = findViewById(R.id.adView);
        intent = getIntent();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        myHelper = new DBHelper(this);
        sqlDB = myHelper.getReadableDatabase();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, target_language_1);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, source_language_1);
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(adapter2);
        txt.setMovementMethod(new ScrollingMovementMethod());

        if(intent.getStringExtra("input") != null){
            edit.setText(intent.getStringExtra("input"));
            txt.setText(intent.getStringExtra("result"));
            spinner.setSelection(Integer.parseInt(intent.getStringExtra("select2")));
            spinner2.setSelection(Integer.parseInt(intent.getStringExtra("select1")));
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                target = target_language_2[position];
                select2 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                source = source_language_2[position];
                select1 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText("");
                txt.setText("");
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getText().toString().trim().length() > 0) {
                    temp = edit.getText().toString();
                    edit.setText(txt.getText().toString());
                    txt.setText(temp);
                    int tmp = select1 - 1;
                    select1 = select2 + 1;
                    select2 = tmp;
                    spinner.setSelection(select2);
                    spinner2.setSelection(select1);
                    NaverTranslateTask asyncTask = new NaverTranslateTask();
                    asyncTask.execute();
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(txt.getText());
                Toast.makeText(getApplicationContext(), "복사했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setText("");
                if(edit.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(), "번역할 글자를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (source.equals("auto")) {
                    NaverTranslateTask2 asyncTask2 = new NaverTranslateTask2();
                    asyncTask2.execute();
                } else {
                    NaverTranslateTask asyncTask = new NaverTranslateTask();
                    asyncTask.execute();
                }
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                length.setText(edit.getText().toString().length() + " / 500");
            }
        });
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