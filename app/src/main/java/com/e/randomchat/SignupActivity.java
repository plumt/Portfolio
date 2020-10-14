package com.e.randomchat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.randomchat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 10;
    private EditText edit_email, edit_name, edit_password1, edit_password2;
    private Button btn_signup;
    private RadioGroup radioGroup;
    private ImageView image_signup;
    private Uri imageUri;
    private String gender = "남성";
    InputMethodManager imm;
    TextView email_warning, password_length_warning, password_equal_warning, name_warning;
    Integer y, m, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edit_email = (EditText) findViewById(R.id.signup_editemail);
        edit_name = (EditText) findViewById(R.id.signup_editname);
        email_warning = (TextView) findViewById(R.id.text_warning);
        password_equal_warning = (TextView) findViewById(R.id.password_equal_warning);
        password_length_warning = (TextView) findViewById(R.id.password_length_warning);
        name_warning = (TextView) findViewById(R.id.name_warning);
        edit_password1 = (EditText) findViewById(R.id.signup_editpassword1);
        edit_password2 = (EditText) findViewById(R.id.signup_editpassword2);
        btn_signup = (Button) findViewById(R.id.signup_signupbutton);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        image_signup = (ImageView) findViewById(R.id.signup_image);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        final Calendar calendar = Calendar.getInstance();

        image_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_man) {
                    gender = "남성";
                } else {
                    gender = "여성";
                }
            }
        });

        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edit_email.getText().toString().equals("")) {
                    edit_email.setBackgroundResource(R.drawable.edittext_login);
                    email_warning.setVisibility(View.INVISIBLE);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    edit_email.setBackgroundResource(R.drawable.edittext_red);
                    email_warning.setText("이메일 형식으로 입력해 주세요");
                    email_warning.setVisibility(View.VISIBLE);
                } else if (edit_email.getText().toString().length() < 10 || edit_email.getText().toString().trim().length() < 10) {
                    edit_email.setBackgroundResource(R.drawable.edittext_red);
                    email_warning.setVisibility(View.VISIBLE);
                } else {
                    edit_email.setBackgroundResource(R.drawable.edittext_login);
                    email_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        edit_password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edit_password1.getText().toString().equals("")) {
                    edit_password1.setBackgroundResource(R.drawable.edittext_login);
                    password_length_warning.setVisibility(View.INVISIBLE);
                } else if (edit_password1.getText().toString().length() < 6 || edit_password1.getText().toString().trim().length() < 6) {
                    edit_password1.setBackgroundResource(R.drawable.edittext_red);
                    password_length_warning.setVisibility(View.VISIBLE);
                } else {
                    edit_password1.setBackgroundResource(R.drawable.edittext_login);
                    password_length_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        edit_password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edit_password2.getText().toString().equals("")) {
                    edit_password2.setBackgroundResource(R.drawable.edittext_login);
                    password_equal_warning.setVisibility(View.INVISIBLE);
                } else if (!edit_password1.getText().toString().equals(edit_password2.getText().toString())) {
                    edit_password2.setBackgroundResource(R.drawable.edittext_red);
                    password_equal_warning.setVisibility(View.VISIBLE);
                } else {
                    edit_password2.setBackgroundResource(R.drawable.edittext_login);
                    password_equal_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edit_name.getText().toString().equals("")){
                    edit_name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                } else if(edit_name.getText().toString().length() < 1 || edit_name.getText().toString().trim().length() < 1){
                    edit_name.setBackgroundResource(R.drawable.edittext_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else {
                    edit_name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                btn_signup.setClickable(false);
                if (edit_email.getText().toString().isEmpty() &&
                        edit_password1.getText().toString().isEmpty() &&
                        edit_password2.getText().toString().isEmpty() &&
                        edit_name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "회원 정보를 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (edit_email.getText().toString().equals("") ||
                        edit_email.getText().toString().isEmpty() ||
                        edit_email.getText().toString().length() < 10 ||
                        edit_email.getText().toString().trim().length() < 10 ||
                        !Patterns.EMAIL_ADDRESS.matcher(edit_email.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "올바른 이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (edit_password1.getText().toString().equals("") ||
                        edit_password1.getText().toString().isEmpty() ||
                        edit_password1.getText().length() < 6 ||
                        edit_password1.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "올바른 패스워드를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (!edit_password1.getText().toString().equals(edit_password2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (edit_name.getText().toString().equals("") ||
                        edit_name.getText().toString().isEmpty() ||
                        edit_name.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "올바른 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                }
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edit_email.getText().toString().trim(), edit_password1.getText().toString().trim())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(edit_name.getText().toString()).build();
                                    task.getResult().getUser().updateProfile(userProfileChangeRequest);
                                    if (imageUri == null) {
                                        final UserModel userModel = new UserModel();
                                        userModel.userName = edit_name.getText().toString().trim();
                                        userModel.gender = gender;
                                        userModel.reward = "5";
                                        y = calendar.get(Calendar.YEAR);
                                        m = calendar.get(Calendar.MONTH) + 1;
                                        d = calendar.get(Calendar.DATE);
                                        userModel.date = y.toString() + "," + m.toString() + "," + d.toString();
                                        if (gender.equals("남성")) {
                                            userModel.who = "여성";
                                            userModel.profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/man.png?alt=media&token=759ed70f-854c-45f2-8d42-d36f0410fec1";
                                        } else {
                                            userModel.who = "남성";
                                            userModel.profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/woman.png?alt=media&token=84de3df3-8b0c-4990-badc-4512bdee7360";
                                        }
                                        userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                                finish();
                                                overridePendingTransition(R.anim.fromright, R.anim.toleft);
                                            }
                                        });
                                    } else {
                                        FirebaseStorage.getInstance().getReference().child("userImages").putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                                                while (!imageUrl.isComplete()) ;
                                                final UserModel userModel = new UserModel();
                                                userModel.userName = edit_name.getText().toString();
                                                userModel.gender = gender;
                                                userModel.reward = "5";
                                                userModel.profileImageUrl = imageUrl.getResult().toString();
                                                y = calendar.get(Calendar.YEAR);
                                                m = calendar.get(Calendar.MONTH) + 1;
                                                d = calendar.get(Calendar.DATE);
                                                userModel.date = y.toString() + "," + m.toString() + "," + d.toString();
                                                if (gender.equals("남성")) {
                                                    userModel.who = "여성";
                                                } else {
                                                    userModel.who = "남성";
                                                }
                                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(), "가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        overridePendingTransition(R.anim.fromright, R.anim.toleft);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "이미 가입된 계정입니다", Toast.LENGTH_SHORT).show();
                                    btn_signup.setClickable(true);
                                }
                            }
                        });
            }
        });
    }

    void keyboardDown() {
        imm.hideSoftInputFromWindow(edit_email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edit_password1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edit_password2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edit_name.getWindowToken(), 0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent login = new Intent(SignupActivity.this, LoginActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fromleft, R.anim.toright);
        startActivity(login, activityOptions.toBundle());
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            image_signup.setImageURI(data.getData());
            imageUri = data.getData();
        }
    }
}
