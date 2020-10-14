package com.e.note.alert;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.e.note.R;

public class MemoPasswordActivity extends AppCompatActivity {
    TextView title, password_warning;
    EditText password;
    Button result, cancle;
    String memo, date, pass;
    boolean write, first;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        title = (TextView) findViewById(R.id.title);
        password_warning = (TextView) findViewById(R.id.password_warning);
        password = (EditText) findViewById(R.id.input_password);
        result = (Button) findViewById(R.id.result);
        cancle = (Button) findViewById(R.id.cancle);
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        intent = getIntent();
        pass = intent.getStringExtra("pass");
        date = intent.getStringExtra("date");
        memo = intent.getStringExtra("memo");
        write = intent.getBooleanExtra("write", false);
        first = intent.getBooleanExtra("first", false);


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password.getText().toString().trim().length() < 1) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                    password_warning.setText("패스워드는 한 글자 이상 입력해 주세요");
                } else {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                if (password.getText().toString().equals(pass)) {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                    intent.putExtra("pass", pass);
                    intent.putExtra("memo", memo);
                    intent.putExtra("date", date);
                    intent.putExtra("write", false);
                    intent.putExtra("first", false);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                } else {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                    password_warning.setText("올바른 비밀번호를 입력해 주세요");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
