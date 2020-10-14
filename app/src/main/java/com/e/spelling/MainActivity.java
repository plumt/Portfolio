package com.e.spelling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonElement;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    EditText edit;
    TextView txt, length;
    Button btn, en2ko, ko2en;
    ImageView delete, copy;
    String clientId = "g0gdqCRDnsnFey8Sc9rp", clientSecret = "wIFgZoaHpw", result;
    boolean choice = true;
    DBHelper myHelper;
    SQLiteDatabase sqlDB;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = (EditText) findViewById(R.id.edit);
        txt = (TextView) findViewById(R.id.txt);
        length = (TextView) findViewById(R.id.length);
        btn = (Button) findViewById(R.id.btn);
        en2ko = (Button) findViewById(R.id.btn_en2ko);
        ko2en = (Button) findViewById(R.id.btn_ko2en);
        delete = (ImageView) findViewById(R.id.delete);
        copy = (ImageView) findViewById(R.id.copy);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        intent = getIntent();
        myHelper = new DBHelper(this);
        sqlDB = myHelper.getReadableDatabase();
        if (intent.getStringExtra("input") != null) {
            edit.setText(intent.getStringExtra("input"));
            txt.setText(intent.getStringExtra("result"));
            String select = intent.getStringExtra("select");
            if (select.equals("1")) {
                en2ko.setBackgroundResource(R.drawable.button2);
                ko2en.setBackgroundResource(R.drawable.button3);
                choice = true;
            } else {
                en2ko.setBackgroundResource(R.drawable.button3);
                ko2en.setBackgroundResource(R.drawable.button2);
                choice = false;
            }
        }
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

        en2ko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                en2ko.setBackgroundResource(R.drawable.button2);
                ko2en.setBackgroundResource(R.drawable.button3);
                choice = true;
            }
        });

        ko2en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                en2ko.setBackgroundResource(R.drawable.button3);
                ko2en.setBackgroundResource(R.drawable.button2);
                choice = false;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText("");
                txt.setText("");
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
                if (choice) {
                    NaverTranslateTask asyncTask = new NaverTranslateTask();
                    asyncTask.execute();
                } else {
                    kr2en();
                }
            }
        });
    }

    void kr2en() {

        char[] arrChoSung = {0x3131, 0x3132, 0x3134, 0x3137, 0x3138,
                0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
                0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
        char[] arrJungSung = {0x314f, 0x3150, 0x3151, 0x3152,
                0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a,
                0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
                0x3163};
        char[] arrJongSung = {0x0000, 0x3131, 0x3132, 0x3133,
                0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c,
                0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
                0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
        String[] arrChoSungEng = {"r", "R", "s", "e", "E",
                "f", "a", "q", "Q", "t", "T", "d", "w",
                "W", "c", "z", "x", "v", "g"};
        String[] arrJungSungEng = {"k", "o", "i", "O",
                "j", "p", "u", "P", "h", "hk", "ho", "hl",
                "y", "n", "nj", "np", "nl", "b", "m", "ml",
                "l"};
        String[] arrJongSungEng = {"", "r", "R", "rt",
                "s", "sw", "sg", "e", "f", "fr", "fa", "fq",
                "ft", "fx", "fv", "fg", "a", "q", "qt", "t",
                "T", "d", "w", "c", "z", "x", "v", "g"};
        String[] arrSingleJaumEng = {"r", "R", "rt",
                "s", "sw", "sg", "e", "E", "f", "fr", "fa", "fq",
                "ft", "fx", "fv", "fg", "a", "q", "Q", "qt", "t",
                "T", "d", "w", "W", "c", "z", "x", "v", "g"};
        String word = edit.getText().toString(), result = "", resultEng = "";
        for (int i = 0; i < word.length(); i++) {
            char chars = (char) (word.charAt(i) - 0xAC00);
            if (chars >= 0 && chars <= 11172) {
                int chosung = chars / (21 * 28);
                int jungsung = chars % (21 * 28) / 28;
                int jongsung = chars % (21 * 28) % 28;
                result = result + arrChoSung[chosung] + arrJungSung[jungsung];
                if (jongsung != 0x0000) {
                    result = result + arrJongSung[jongsung];
                }
                resultEng = resultEng + arrChoSungEng[chosung] + arrJungSungEng[jungsung];
                if (jongsung != 0x0000) {
                    resultEng = resultEng + arrJongSungEng[jongsung];
                }
            } else {
                result = result + ((char) (chars + 0xAC00));
                if (chars >= 34097 && chars <= 34126) {
                    int jaum = (chars - 34097);
                    resultEng = resultEng + arrSingleJaumEng[jaum];
                } else if (chars >= 34127 && chars <= 34147) {
                    int moum = (chars - 34127);
                    resultEng = resultEng + arrJungSungEng[moum];
                } else {
                    resultEng = resultEng + ((char) (chars + 0xAC00));
                }
            }
        }
        try {
            txt.setText(resultEng);
            boolean pass = true;
            Cursor history_cursor;
            sqlDB = myHelper.getReadableDatabase();
            history_cursor = sqlDB.rawQuery("SELECT *FROM history;", null);
            while (history_cursor.moveToNext()) {
                if (edit.getText().toString().trim().equals(history_cursor.getString(1))) {
                    pass = false;
                }
            }
            if (edit.getText().toString().trim().length() < 1 || edit.getText().toString().equals("") || resultEng.equals(edit.getText().toString())) {
                pass = false;
            }
            sqlDB = myHelper.getWritableDatabase();
            if (pass) {
                String in = edit.getText().toString().replace("'", "＇"), re = txt.getText().toString().replace("'", "＇"), select = "0";
                if (choice) {
                    select = "1";
                }
                sqlDB.execSQL("INSERT INTO history VALUES (" + null + ",'" + in + "','" + re + "','" + select + "');");
            }
            history_cursor.close();
            sqlDB.close();
        } catch (Exception e) {

        }
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
            String apiURL = "https://openapi.naver.com/v1/search/errata?query=" + text;
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
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result);
                String str = element.getAsJsonObject().get("errata").getAsString();
                txt.setText(str);
                boolean pass = true;
                Cursor history_cursor;
                sqlDB = myHelper.getReadableDatabase();
                history_cursor = sqlDB.rawQuery("SELECT *FROM history;", null);
                while (history_cursor.moveToNext()) {
                    if (edit.getText().toString().trim().equals(history_cursor.getString(1))) {
                        pass = false;
                    }
                }
                if (edit.getText().toString().trim().length() < 1 || edit.getText().toString().equals("") || str.equals(edit.getText().toString())) {
                    pass = false;
                }
                sqlDB = myHelper.getWritableDatabase();
                if (pass) {
                    String in = edit.getText().toString().replace("'", "＇"), re = txt.getText().toString().replace("'", "＇"), select = "0";
                    if (choice) {
                        select = "1";
                    }
                    sqlDB.execSQL("INSERT INTO history VALUES (" + null + ",'" + in + "','" + re + "','" + select + "');");
                }
                history_cursor.close();
                sqlDB.close();
            } catch (Exception e) {
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
