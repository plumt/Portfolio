package com.e.plan.alert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e.plan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SerchPasswordActivity extends AppCompatActivity {

    Button cansle_btn, serch_btn;
    EditText input_email;
    TextView email_warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_password);

        cansle_btn = (Button) findViewById(R.id.cansle_btn);
        serch_btn = (Button) findViewById(R.id.serch_btn);
        input_email = (EditText) findViewById(R.id.input_email);
        email_warning = (TextView) findViewById(R.id.email_warning);
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        cansle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        input_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (input_email.getText().toString().equals("")) {
                    input_email.setBackgroundResource(R.drawable.edittext_login);
                    email_warning.setVisibility(View.INVISIBLE);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    input_email.setBackgroundResource(R.drawable.edittext_red);
                    email_warning.setText("이메일 형식으로 입력해주세요");
                    email_warning.setVisibility(View.VISIBLE);
                } else if (input_email.getText().toString().length() < 10 || input_email.getText().toString().trim().length() < 10) {
                    input_email.setBackgroundResource(R.drawable.edittext_red);
                    email_warning.setVisibility(View.VISIBLE);
                } else {
                    input_email.setBackgroundResource(R.drawable.edittext_login);
                    email_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        serch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
                if (input_email.getText().toString().trim().length() < 10) {
                    Toast.makeText(getApplicationContext(), "10자리 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "이메일 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                final AutoCompleteTextView email = new AutoCompleteTextView(SerchPasswordActivity.this);
                email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                String emailAdress = input_email.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(emailAdress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "메일을 전송하였습니다", Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                } else {
                                    Toast.makeText(getApplicationContext(), "올바른 메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
