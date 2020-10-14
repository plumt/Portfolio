package com.e.novel.Book;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.e.novel.R;

public class BookViewActivity extends AppCompatActivity {

    TextView title, content;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        title.setMovementMethod(new ScrollingMovementMethod());
        content.setMovementMethod(new ScrollingMovementMethod());
        intent = getIntent();
        title.setText(intent.getStringExtra("title"));
        content.setText(intent.getStringExtra("content"));
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }
}
