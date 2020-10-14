package com.e.note.alert;

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

import com.e.note.R;
import com.e.note.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class SignupAlertActivity extends AppCompatActivity {
    EditText email, password1, password2, name;
    TextView email_warning, name_warning, password1_warning, password2_warning;
    Button signup, cancle;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_alert);
        email = (EditText) findViewById(R.id.signup_editemail);
        password1 = (EditText) findViewById(R.id.signup_editpassword1);
        password2 = (EditText) findViewById(R.id.signup_editpassword2);
        name = (EditText) findViewById(R.id.signup_editname);
        signup = (Button) findViewById(R.id.signup_signupbutton);
        cancle = (Button) findViewById(R.id.cancle);
        email_warning = (TextView) findViewById(R.id.text_warning);
        name_warning = (TextView) findViewById(R.id.name_warning);
        password1_warning = (TextView) findViewById(R.id.password_length_warning);
        password2_warning = (TextView) findViewById(R.id.password_equal_warning);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    email.setBackgroundResource(R.drawable.edittext_red);
                    email_warning.setText("이메일 형식으로 입력해 주세요");
                    email_warning.setVisibility(View.VISIBLE);
                } else if (email.getText().toString().trim().length() < 10) {
                    email.setBackgroundResource(R.drawable.edittext_red);
                    email_warning.setVisibility(View.VISIBLE);
                    email_warning.setText("이메일은 열 글자 이상 입력해 주세요");
                } else {
                    email.setBackgroundResource(R.drawable.edittext_login);
                    email_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password1.getText().toString().trim().length() < 6) {
                    password1.setBackgroundResource(R.drawable.edittext_red);
                    password1_warning.setVisibility(View.VISIBLE);
                } else {
                    password1.setBackgroundResource(R.drawable.edittext_login);
                    password1_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!password1.getText().toString().equals(password2.getText().toString())) {
                    password2.setBackgroundResource(R.drawable.edittext_red);
                    password2_warning.setVisibility(View.VISIBLE);
                } else {
                    password2.setBackgroundResource(R.drawable.edittext_login);
                    password2_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (name.getText().toString().trim().length() < 1) {
                    name.setBackgroundResource(R.drawable.edittext_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else {
                    name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                signup.setEnabled(false);
                if (email.getText().toString().trim().length() < 10 ||
                        !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email.setBackgroundResource(R.drawable.edittext_red);
                    email_warning.setText("올바른 이메일을 입력해 주세요");
                    email_warning.setVisibility(View.VISIBLE);
                    signup.setEnabled(true);
                    return;
                } else if (password1.getText().toString().trim().length() < 6) {
                    password1.setBackgroundResource(R.drawable.edittext_red);
                    password1_warning.setVisibility(View.VISIBLE);
                    signup.setEnabled(true);
                    return;
                } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                    password2.setBackgroundResource(R.drawable.edittext_red);
                    password2_warning.setVisibility(View.VISIBLE);
                    signup.setEnabled(true);
                    return;
                } else if (name.getText().toString().equals("") ||
                        name.getText().toString().isEmpty() ||
                        name.getText().toString().trim().length() < 1) {
                    name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                    signup.setEnabled(true);
                    return;
                }
                signUp();
            }
        });
    }

    void signUp() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(), password1.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            email.setBackgroundResource(R.drawable.edittext_login);
                            email_warning.setVisibility(View.INVISIBLE);
                            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(email.getText().toString()).build();
                            task.getResult().getUser().updateProfile(userProfileChangeRequest);
                            UserModel userModel = new UserModel();
                            userModel.uid = myUid;
                            userModel.userName = name.getText().toString().trim();
                            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                }
                            });


                        } else {
                            email.setBackgroundResource(R.drawable.edittext_red);
                            email_warning.setText("이미 가입된 계정입니다");
                            email_warning.setVisibility(View.VISIBLE);
                            signup.setEnabled(true);
                        }
                    }
                });
    }

    void keyboardDown() {
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(password1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(password2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
