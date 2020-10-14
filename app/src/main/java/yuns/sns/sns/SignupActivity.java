package yuns.sns.sns;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import yuns.sns.R;
import yuns.sns.sns.model.UserModel;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 10;
    private EditText email, name, password1, password2, tag;
    private Button signup, tag_check;
    private RadioGroup radioGroup;
    private ImageView profile;
    private Uri imageUri;
    private String gender = "남성";
    private Boolean tag_check_pass = false;
    TextView text_warning, password_length_warning, password_equal_warning, tag_warning, name_warning;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email = (EditText) findViewById(R.id.signup_editemail);
        name = (EditText) findViewById(R.id.signup_editname);
        text_warning = (TextView) findViewById(R.id.text_warning);
        password_equal_warning = (TextView) findViewById(R.id.password_equal_warning);
        password_length_warning = (TextView) findViewById(R.id.password_length_warning);
        tag_warning = (TextView) findViewById(R.id.tag_warning);
        name_warning = (TextView) findViewById(R.id.name_warning);
        password1 = (EditText) findViewById(R.id.signup_editpassword1);
        password2 = (EditText) findViewById(R.id.signup_editpassword2);
        tag = (EditText) findViewById(R.id.signup_edittag);
        signup = (Button) findViewById(R.id.signup_signupbutton);
        tag_check = (Button) findViewById(R.id.signup_tagcheck);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        profile = (ImageView) findViewById(R.id.signup_image);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        profile.setOnClickListener(new View.OnClickListener() {
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

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (email.getText().toString().equals("")) {
                    email.setBackgroundResource(R.drawable.edittext_login);
                    text_warning.setVisibility(View.INVISIBLE);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()) {
                    email.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setText("이메일 형식으로 입력해 주세요");
                    text_warning.setVisibility(View.VISIBLE);
                } else if (email.getText().toString().length() < 10 || email.getText().toString().trim().length() < 10) {
                    email.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setVisibility(View.VISIBLE);
                } else {
                    email.setBackgroundResource(R.drawable.edittext_login);
                    text_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password1.getText().toString().equals("")) {
                    password1.setBackgroundResource(R.drawable.edittext_login);
                    password_length_warning.setVisibility(View.INVISIBLE);
                } else if (password1.getText().toString().length() < 6 || password1.getText().toString().trim().length() < 6) {
                    password1.setBackgroundResource(R.drawable.edittext_red);
                    password_length_warning.setVisibility(View.VISIBLE);
                } else {
                    password1.setBackgroundResource(R.drawable.edittext_login);
                    password_length_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password2.getText().toString().equals("")) {
                    password2.setBackgroundResource(R.drawable.edittext_login);
                    password_equal_warning.setVisibility(View.INVISIBLE);
                } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                    password2.setBackgroundResource(R.drawable.edittext_red);
                    password_equal_warning.setVisibility(View.VISIBLE);
                } else {
                    password2.setBackgroundResource(R.drawable.edittext_login);
                    password_equal_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (tag.getText().toString().equals("")) {
                    tag.setBackgroundResource(R.drawable.edittext_login);
                    tag_warning.setVisibility(View.INVISIBLE);
                    tag_check.setEnabled(false);
                } else if (tag.getText().toString().length() < 6 || tag.getText().toString().trim().length() < 6) {
                    tag.setBackgroundResource(R.drawable.edittext_red);
                    tag_warning.setVisibility(View.VISIBLE);
                    tag_check.setEnabled(false);
                } else {
                    tag.setBackgroundResource(R.drawable.edittext_login);
                    tag_warning.setVisibility(View.INVISIBLE);
                    tag_check.setEnabled(true);
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
                if (name.getText().toString().equals("")) {
                    name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                } else if (name.getText().toString().length() < 1 || name.getText().toString().trim().length() < 1) {
                    name.setBackgroundResource(R.drawable.edittext_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else {
                    name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                }


            }
        });


        tag_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag_check.setEnabled(false);
                if (tag.getText().toString().trim().length() < 6 || tag.getText().toString().isEmpty()) {
                    Toast.makeText(SignupActivity.this, "태그는 6자리를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    tag_check.setEnabled(true);
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel.tag.equals(tag.getText().toString())) {
                                Toast.makeText(SignupActivity.this, "이미 존재하는 태그입니다", Toast.LENGTH_SHORT).show();
                                tag_check.setEnabled(true);
                                return;
                            }
                        }
                        Toast.makeText(SignupActivity.this, "사용 가능한 태그입니다", Toast.LENGTH_SHORT).show();
                        tag_check.setEnabled(false);
                        tag.setEnabled(false);
                        tag_check_pass = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardDown();
                signup.setEnabled(false);
                if (email.getText().toString().isEmpty() &&
                        tag.getText().toString().isEmpty() &&
                        password1.getText().toString().isEmpty() &&
                        password2.getText().toString().isEmpty() &&
                        name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "회원 정보를 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    return;
                } else if (email.getText().toString().equals("") ||
                        email.getText().toString().isEmpty() ||
                        email.getText().toString().length() < 10 ||
                        email.getText().toString().trim().length() < 10 ||
                        !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "올바른 이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    return;
                } else if (password1.getText().toString().equals("") ||
                        password1.getText().toString().isEmpty() ||
                        password1.getText().length() < 6 ||
                        password1.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "올바른 패스워드를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    return;
                } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    return;
                } else if (name.getText().toString().equals("") ||
                        name.getText().toString().isEmpty() ||
                        name.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "올바른 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    return;
                } else if (tag.getText().toString().equals("") ||
                        tag.getText().toString().isEmpty() ||
                        tag.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "올바른 태그를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    return;
                } else if (!tag_check_pass) {
                    Toast.makeText(getApplicationContext(), "중복체크를 확인해 주세요", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    return;
                }


                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(), password1.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final String uid = task.getResult().getUser().getUid();
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                                    task.getResult().getUser().updateProfile(userProfileChangeRequest);
                                    if (imageUri == null) {
                                        final UserModel userModel = new UserModel();
                                        userModel.userName = name.getText().toString();
                                        userModel.tag = tag.getText().toString();
                                        userModel.gender = gender;


                                        if (gender.equals("남성")) {
                                            userModel.profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/sns-55e69.appspot.com/o/man.png?alt=media&token=ebc3c563-4d9f-4c04-a08e-f24646627c52";
                                        } else {
                                            userModel.profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/sns-55e69.appspot.com/o/woman.png?alt=media&token=3845f683-1636-4db8-805b-a3f967aaeba6";
                                        }
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

                                        FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                                                while (!imageUrl.isComplete()) ;
                                                final UserModel userModel = new UserModel();
                                                userModel.userName = name.getText().toString();
                                                userModel.tag = tag.getText().toString();
                                                userModel.gender = gender;
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
                                    Toast.makeText(SignupActivity.this, "이미 존재하는 계정입니다", Toast.LENGTH_SHORT).show();
                                    signup.setEnabled(true);
                                }
                            }
                        });
            }
        });

    }

    void keyboardDown(){
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(password1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(password2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tag.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData());
            imageUri = data.getData();
        }
    }
}
