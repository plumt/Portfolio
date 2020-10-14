package com.e.randomchat.profile;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.randomchat.R;
import com.e.randomchat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class MyProfileAlertActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 10;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button cancle, insert, result, delete, update;
    ImageView profile_image;
    EditText profile_edit, profile_comment_edit;
    TextView profile_name, profile_comment_length, name_warning, profile_gender;
    LinearLayout liner1, liner2, liner_comment;
    private Uri imageUri;
    String name, gender, myurl, comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_alert);

        cancle = (Button) findViewById(R.id.cancle);
        insert = (Button) findViewById(R.id.banced);
        result = (Button) findViewById(R.id.result);
        delete = (Button) findViewById(R.id.delete);
        update = (Button) findViewById(R.id.update);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_edit = (EditText) findViewById(R.id.profile_edit);
        profile_comment_edit = (EditText) findViewById(R.id.profile_comment_edit);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_gender = (TextView) findViewById(R.id.profile_gender);
        profile_comment_length = (TextView) findViewById(R.id.profile_comment_length);
        name_warning = (TextView) findViewById(R.id.name_warning);
        liner1 = (LinearLayout)findViewById(R.id.liner1);
        liner2 = (LinearLayout)findViewById(R.id.liner2);
        liner_comment = (LinearLayout)findViewById(R.id.liner_comment);

        liner_comment.setVisibility(View.GONE);
        profile_gender.setVisibility(View.GONE);
        profile_comment_edit.setVisibility(View.VISIBLE);
        profile_comment_length.setVisibility(View.VISIBLE);
        profile_name.setVisibility(View.GONE);
        profile_edit.setVisibility(View.VISIBLE);
        result.setVisibility(View.GONE);
        liner1.setVisibility(View.VISIBLE);
        insert.setText("확인");
        setting();

        profile_comment_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                profile_comment_length.setText(profile_comment_edit.getText().toString().length() + " / 25");

            }
        });

        profile_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(profile_edit.getText().toString().trim().length() < 1){
                    profile_edit.setBackgroundResource(R.drawable.profile_name_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else{
                    profile_edit.setBackgroundResource(R.drawable.profile_name);
                    name_warning.setVisibility(View.INVISIBLE);
                }
            }
        });


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fab_open,R.anim.fab_close);
            }
        });
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert.setEnabled(false);
                cancle.setEnabled(false);
                if(profile_edit.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(),"올바른 이름을 입력해 주세요",Toast.LENGTH_SHORT).show();
                    insert.setEnabled(true);
                    return;
                } else if(name.equals(profile_edit.getText().toString()) && comment != null && comment.equals(profile_comment_edit.getText().toString())){
                    finish();
                    overridePendingTransition(R.anim.fab_open,R.anim.fab_close);
                    return;
                }
                insert();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.setEnabled(true);
                delete.setTextColor(Color.parseColor("#86B2D8"));
                update.setEnabled(true);
                liner1.setVisibility(View.GONE);
                liner2.setVisibility(View.VISIBLE);
                profile_edit.setVisibility(View.GONE);
                profile_image.setEnabled(false);
                profile_name.setVisibility(View.VISIBLE);
                profile_name.setText("프로필 사진을 수정하시겠습니까?");

                if(myurl.equals("https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/man.png?alt=media&token=759ed70f-854c-45f2-8d42-d36f0410fec1") ||
                myurl.equals("https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/woman.png?alt=media&token=84de3df3-8b0c-4990-badc-4512bdee7360")){
                    delete.setEnabled(false);
                    delete.setTextColor(Color.parseColor("#9f9f9f"));
                }else{
                    delete.setEnabled(true);
                    delete.setTextColor(Color.parseColor("#86B2D8"));
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.setEnabled(false);
                update.setEnabled(false);
                delete();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.setEnabled(false);
                update.setEnabled(false);
                Intent photo = new Intent(Intent.ACTION_PICK);
                photo.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(photo, PICK_FROM_ALBUM);
            }
        });


    }

    void delete(){
        FirebaseStorage.getInstance().getReference().child("userImages").child(myUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Map<String, Object> stringObjectMap = new HashMap<>();
                if(gender.equals("남성")){
                    stringObjectMap.put("profileImageUrl","https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/man.png?alt=media&token=759ed70f-854c-45f2-8d42-d36f0410fec1");
                    myurl = "https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/man.png?alt=media&token=759ed70f-854c-45f2-8d42-d36f0410fec1";
                } else{
                    stringObjectMap.put("profileImageUrl","https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/woman.png?alt=media&token=84de3df3-8b0c-4990-badc-4512bdee7360");
                    myurl = "https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/woman.png?alt=media&token=84de3df3-8b0c-4990-badc-4512bdee7360";
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "프로필 이미지가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        try{
                            Glide.with(getApplicationContext()).load(myurl).apply(new RequestOptions().circleCrop()).into(profile_image);
                        } catch (Exception e){

                        }
                        liner1.setVisibility(View.VISIBLE);
                        liner2.setVisibility(View.GONE);
                        profile_edit.setVisibility(View.VISIBLE);
                        profile_comment_edit.setVisibility(View.VISIBLE);
                        profile_comment_length.setVisibility(View.VISIBLE);
                        profile_image.setEnabled(true);
                        profile_name.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    void update(){
        FirebaseStorage.getInstance().getReference().child("userImages").child(myUid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                while (!imageUrl.isComplete()) ;
                Map<String, Object> stringObjectMap = new HashMap<>();
                stringObjectMap.put("profileImageUrl", imageUrl.getResult().toString());
                myurl = imageUrl.getResult().toString();
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "프로필 이미지가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        try{
                            Glide.with(getApplicationContext()).load(imageUrl.getResult().toString()).apply(new RequestOptions().circleCrop()).into(profile_image);
                        } catch (Exception e){

                        }
                        liner1.setVisibility(View.VISIBLE);
                        liner2.setVisibility(View.GONE);
                        profile_edit.setVisibility(View.VISIBLE);
                        profile_comment_edit.setVisibility(View.VISIBLE);
                        profile_comment_length.setVisibility(View.VISIBLE);
                        profile_image.setEnabled(true);
                        profile_name.setVisibility(View.GONE);
                        delete.setEnabled(true);
                        update.setEnabled(true);
                    }
                });
            }
        });
    }

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    gender = userModel.gender;
                    myurl = userModel.profileImageUrl;
                    profile_edit.setText(userModel.userName);
                    profile_comment_edit.setText(userModel.comment);
                    name = userModel.userName;
                    comment = userModel.comment;
                    Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                } catch (Exception e) {
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void insert(){
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("userName", profile_edit.getText().toString());
        stringObjectMap.put("comment", profile_comment_edit.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"프로필이 수정되었습니다",Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fab_open,R.anim.fab_close);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile_image.setImageURI(data.getData());
            imageUri = data.getData();
            update();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open,R.anim.fab_close);

    }
}
