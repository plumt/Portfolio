package com.e.novel.User;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.novel.Alert.SendMailActivity;
import com.e.novel.Alert.SerchPasswordActivity;
import com.e.novel.MainActivity;
import com.e.novel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button login, signup;
    private EditText id, password;
    private FirebaseAuth firebaseAuth;
    TextView find_password, text_warning, password_warning;
    int out = 0;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        find_password = (TextView) findViewById(R.id.find_password);
        text_warning = (TextView) findViewById(R.id.text_warning);
        password_warning = (TextView) findViewById(R.id.password_warning);
        id = (EditText) findViewById(R.id.login_id);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_button);
        signup = (Button) findViewById(R.id.login_signup);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    id.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setText("이메일 형식으로 입력해 주세요");
                    text_warning.setVisibility(View.VISIBLE);
                } else if (id.getText().toString().trim().length() < 10) {
                    text_warning.setText("이메일은 열 글자 이상 입력해 주세요");
                    id.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setVisibility(View.VISIBLE);
                } else {
                    id.setBackgroundResource(R.drawable.edittext_login);
                    text_warning.setVisibility(View.INVISIBLE);
                }
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
                if (password.getText().toString().trim().length() < 6) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                    password_warning.setText("패스워드는 여섯 글자 이상 입력해 주세요");
                } else {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardDown();
                Intent serch = new Intent(LoginActivity.this, SerchPasswordActivity.class);
                startActivity(serch);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                login.setEnabled(false);
                signup.setEnabled(false);
                find_password.setEnabled(false);
                if (!Patterns.EMAIL_ADDRESS.matcher(id.getText().toString()).matches() || id.getText().toString().trim().length() < 10) {
                    id.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setText("올바른 이메일을 입력해 주세요");
                    text_warning.setVisibility(View.VISIBLE);
                    login.setEnabled(true);
                    signup.setEnabled(true);
                    find_password.setEnabled(true);
                    return;
                } else if (password.getText().toString().trim().length() < 6) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                    password_warning.setText("패스워드는 여섯 글자 이상 입력해 주세요");
                    login.setEnabled(true);
                    signup.setEnabled(true);
                    find_password.setEnabled(true);
                    return;
                }
                loginEvent();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                Intent signup = new Intent(LoginActivity.this, SignupActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(signup, activityOptions.toBundle());
            }
        });

    }

    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            id.setBackgroundResource(R.drawable.edittext_red);
                            text_warning.setText("아이디 및 비밀번호를 다시 확인해 주세요");
                            text_warning.setVisibility(View.VISIBLE);
                            password.setBackgroundResource(R.drawable.edittext_red);
                            password_warning.setText("아이디 및 비밀번호를 다시 확인해 주세요");
                            password_warning.setVisibility(View.VISIBLE);

                            login.setEnabled(true);
                            find_password.setEnabled(true);
                            signup.setEnabled(true);
                        } else {
                            id.setBackgroundResource(R.drawable.edittext_login);
                            text_warning.setVisibility(View.INVISIBLE);
                            password.setBackgroundResource(R.drawable.edittext_login);
                            password_warning.setVisibility(View.INVISIBLE);
                            check();
                        }
                    }
                });
    }

    void check() {
        final FirebaseUser user_check = FirebaseAuth.getInstance().getCurrentUser();
        if (!user_check.isEmailVerified()) {
            Intent sendmail = new Intent(LoginActivity.this, SendMailActivity.class);
            startActivity(sendmail);
            login.setEnabled(true);
            find_password.setEnabled(true);
            signup.setEnabled(true);
            return;
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
        startActivity(intent, activityOptions.toBundle());
        id.setText("");
        password.setText("");
        login.setEnabled(true);
        find_password.setEnabled(true);
        signup.setEnabled(true);
        id.setBackgroundResource(R.drawable.edittext_login);
        text_warning.setVisibility(View.INVISIBLE);
        password.setBackgroundResource(R.drawable.edittext_login);
        password_warning.setVisibility(View.INVISIBLE);
        finish();
    }

    void keyboardDown() {
        imm.hideSoftInputFromWindow(login.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (out == 0) {
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            out = 1;
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        out = 0;
    }
}
