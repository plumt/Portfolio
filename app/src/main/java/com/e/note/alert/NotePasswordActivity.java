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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.note.R;

public class NotePasswordActivity extends AppCompatActivity {
    TextView title, password_warning;
    EditText password;
    Button cancle, result;
    Intent intent;
    String pass, name, key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        password = (EditText) findViewById(R.id.input_password);
        title = (TextView) findViewById(R.id.title);
        password_warning = (TextView) findViewById(R.id.password_warning);
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        intent = getIntent();
        pass = intent.getStringExtra("pass");
        name = intent.getStringExtra("name");
        key = intent.getStringExtra("key");

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

        title.setText("노트 잠금");

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                if (pass.equals(password.getText().toString())) {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                    intent.putExtra("name", name);
                    intent.putExtra("key", key);
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
