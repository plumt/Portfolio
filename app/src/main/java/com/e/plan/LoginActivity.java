package com.e.plan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.e.plan.alert.SendMailActivity;
import com.e.plan.alert.SerchPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button login, signup;
    private EditText id, password;
    TextView find_password, id_warning, password_warning;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    InputMethodManager imm;
    int out = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        find_password = (TextView) findViewById(R.id.find_password);
        id_warning = (TextView) findViewById(R.id.text_warning);
        password_warning = (TextView) findViewById(R.id.password_warning);
        id = (EditText) findViewById(R.id.login_id);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_button);
        signup = (Button) findViewById(R.id.login_signup);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();


        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (id.getText().toString().equals("")) {
                    id.setBackgroundResource(R.drawable.edittext_login);
                    id_warning.setVisibility(View.INVISIBLE);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    id.setBackgroundResource(R.drawable.edittext_red);
                    id_warning.setText("이메일 형식으로 입력해주세요");
                    id_warning.setVisibility(View.VISIBLE);
                } else if (id.getText().toString().length() < 10 || id.getText().toString().trim().length() < 10) {
                    id.setBackgroundResource(R.drawable.edittext_red);
                    id_warning.setVisibility(View.VISIBLE);
                } else {
                    id.setBackgroundResource(R.drawable.edittext_login);
                    id_warning.setVisibility(View.INVISIBLE);
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
                if (password.getText().toString().equals("")) {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                } else if (password.getText().toString().length() < 6 || password_warning.getText().toString().trim().length() < 6) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                login.setEnabled(false);
                signup.setEnabled(false);
                find_password.setEnabled(false);
                if (id.getText().toString().isEmpty() || id.getText().toString().equals("") || password.getText().toString().isEmpty() || password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "로그인 정보를 모두 기입해 주세요", Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                    signup.setEnabled(true);
                    find_password.setEnabled(true);
                    return;
                } else if (password.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "비밀번호는 6자리 이상 입력해 주세요", Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                    signup.setEnabled(true);
                    find_password.setEnabled(true);
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(id.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 올바르지 않습니다", Toast.LENGTH_SHORT).show();
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

        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                Intent serch = new Intent(LoginActivity.this, SerchPasswordActivity.class);
                startActivity(serch);
            }
        });

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (id.getText().toString().equals("")) {
                    id.setBackgroundResource(R.drawable.edittext_login);
                    id_warning.setVisibility(View.INVISIBLE);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    id.setBackgroundResource(R.drawable.edittext_red);
                    id_warning.setText("이메일 형식으로 입력해주세요");
                    id_warning.setVisibility(View.VISIBLE);
                } else if (id.getText().toString().length() < 10) {
                    id.setBackgroundResource(R.drawable.edittext_red);
                    id_warning.setText("10자리 이상 입력해주세요");
                    id_warning.setVisibility(View.VISIBLE);
                } else if (id.getText().toString().trim().length() < 10) {
                    id.setBackgroundResource(R.drawable.edittext_red);
                    id_warning.setText("10자리 이상 입력해주세요");
                    id_warning.setVisibility(View.VISIBLE);
                } else {
                    id.setBackgroundResource(R.drawable.edittext_login);
                    id_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password.getText().toString().equals("")) {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                } else if (password.getText().toString().length() < 6) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                } else if (password.getText().toString().trim().length() < 6) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void keyboardDown() {
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(id.getWindowToken(), 0);

    }

    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "아이디 및 비밀번호를 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                            login.setEnabled(true);
                            find_password.setEnabled(true);
                            signup.setEnabled(true);
                        } else {
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
        Intent intent = new Intent(LoginActivity.this, PlanActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
        startActivity(intent, activityOptions.toBundle());
        id.setText("");
        password.setText("");
        login.setEnabled(true);
        find_password.setEnabled(true);
        signup.setEnabled(true);
        finish();
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
