package com.e.plan.alert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.plan.Model.UserModel;
import com.e.plan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    ImageView profile_image;
    Button result_btn, insert_btn, delete_btn, cancle_btn;
    EditText myname;
    LinearLayout liner1, liner2;
    private Uri imageUri;
    Boolean flag = true;
    String name;
    Intent intent;
    TextView name_warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        result_btn = (Button) findViewById(R.id.result_btn);
        insert_btn = (Button) findViewById(R.id.insert_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        cancle_btn = (Button) findViewById(R.id.cancle_btn);
        myname = (EditText) findViewById(R.id.my_name);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner2 = (LinearLayout) findViewById(R.id.liner2);
        name_warning = (TextView) findViewById(R.id.name_warning);
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        intent = getIntent();
        name = intent.getStringExtra("name");
        profile();

        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(myname.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_btn.setEnabled(false);
                cancle_btn.setEnabled(false);
                imm.hideSoftInputFromWindow(myname.getWindowToken(), 0);
                if (myname.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                profile_save();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner1.setVisibility(View.GONE);
                liner2.setVisibility(View.VISIBLE);
                result_btn.setEnabled(false);
                cancle_btn.setEnabled(false);
            }
        });

        myname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (myname.getText().toString().trim().length() < 1) {
                    myname.setBackgroundResource(R.drawable.edittext_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else {
                    myname.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseStorage.getInstance().getReference().child("userImages").child(myUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> stringObjectMap = new HashMap<>();
                        stringObjectMap.put("profileImageUrl", "https://firebasestorage.googleapis.com/v0/b/plan-5f34a.appspot.com/o/person.png?alt=media&token=3b546e2e-7fe2-4b27-9ab4-723206009b92");
                        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (flag) {
                                    Toast.makeText(getApplicationContext(), "프로필 이미지가 수정되었습니다", Toast.LENGTH_SHORT).show();
                                    profile();
                                    delete_btn.setEnabled(false);
                                    delete_btn.setTextColor(Color.parseColor("#9f9f9f"));
                                } else {
                                    flag = true;
                                }
                                liner1.setVisibility(View.VISIBLE);
                                liner2.setVisibility(View.GONE);
                                liner1.setEnabled(true);
                                result_btn.setEnabled(true);
                                cancle_btn.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });

        insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photo = new Intent(Intent.ACTION_PICK);
                photo.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(photo, PICK_FROM_ALBUM);
            }
        });

    }

    void profile_save() {
        if (!name.equals(myname.getText().toString())) {
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("userName", myname.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);
            Toast.makeText(getApplicationContext(), "이름이 수정되었습니다", Toast.LENGTH_SHORT).show();
        }
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }

    void profile() {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                myname.setText(userModel.userName);
                Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                if (userModel.profileImageUrl.equals("https://firebasestorage.googleapis.com/v0/b/plan-5f34a.appspot.com/o/person.png?alt=media&token=3b546e2e-7fe2-4b27-9ab4-723206009b92")) {
                    delete_btn.setEnabled(false);
                    delete_btn.setTextColor(Color.parseColor("#9f9f9f"));
                } else {
                    delete_btn.setEnabled(true);
                    delete_btn.setTextColor(Color.parseColor("#86B2D8"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void insert() {
        flag = false;
        delete_btn.performClick();
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage.getInstance().getReference().child("userImages").child(myUid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                while (!imageUrl.isComplete()) ;
                Map<String, Object> stringObjectMap = new HashMap<>();
                stringObjectMap.put("profileImageUrl", imageUrl.getResult().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "프로필 이미지가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        liner1.setVisibility(View.VISIBLE);
                        liner2.setVisibility(View.GONE);
                        delete_btn.setEnabled(true);
                        delete_btn.setTextColor(Color.parseColor("#86B2D8"));
                        liner1.setEnabled(false);
                        result_btn.setEnabled(true);
                        cancle_btn.setEnabled(true);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile_image.setImageURI(data.getData());
            imageUri = data.getData();
            insert();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
