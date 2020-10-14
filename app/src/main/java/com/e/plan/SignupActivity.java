package com.e.plan;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.plan.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 10;
    private Uri imageUri;
    Button btn_signup;
    ImageView image_signup;
    EditText edit_email, edit_password1, edit_password2, edit_name;
    TextView email_warning, password_length_warning, password_equal_warning, name_warning;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        image_signup = (ImageView) findViewById(R.id.signup_image);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password1 = (EditText) findViewById(R.id.edit_password1);
        edit_password2 = (EditText) findViewById(R.id.edit_password2);
        edit_name = (EditText) findViewById(R.id.edit_name);
        email_warning = (TextView) findViewById(R.id.email_warning);
        name_warning = (TextView) findViewById(R.id.name_warning);
        password_length_warning = (TextView) findViewById(R.id.password_length_warning);
        password_equal_warning = (TextView) findViewById(R.id.password_equal_warning);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        image_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent image = new Intent(Intent.ACTION_PICK);
                image.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(image, PICK_FROM_ALBUM);
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
                    email_warning.setText("이메일 형식으로 입력해주세요");
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
                    Toast.makeText(getApplicationContext(), "회원 정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (edit_email.getText().toString().equals("") ||
                        edit_email.getText().toString().isEmpty() ||
                        edit_email.getText().toString().length() < 10 ||
                        edit_email.getText().toString().trim().length() < 10 ||
                        !Patterns.EMAIL_ADDRESS.matcher(edit_email.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "올바른 이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (edit_password1.getText().toString().equals("") ||
                        edit_password1.getText().toString().isEmpty() ||
                        edit_password1.getText().length() < 6 ||
                        edit_password1.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "올바른 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (!edit_password1.getText().toString().equals(edit_password2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    btn_signup.setClickable(true);
                    return;
                } else if (edit_name.getText().toString().equals("") ||
                        edit_name.getText().toString().isEmpty() ||
                        edit_name.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "올바른 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
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
                                        userModel.profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/plan-5f34a.appspot.com/o/person.png?alt=media&token=3b546e2e-7fe2-4b27-9ab4-723206009b92";
                                        userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignupActivity.this, "가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
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
                                                userModel.profileImageUrl = imageUrl.getResult().toString();
                                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(SignupActivity.this, "가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
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

    void keyboardDown(){
        imm.hideSoftInputFromWindow(edit_email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edit_password1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edit_password2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edit_name.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
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
