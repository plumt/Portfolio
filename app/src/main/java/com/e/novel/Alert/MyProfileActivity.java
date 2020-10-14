package com.e.novel.Alert;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.novel.Model.UserModel;
import com.e.novel.R;
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

public class MyProfileActivity extends AppCompatActivity {

    private Uri imageUri;
    private static final int PICK_FROM_ALBUM = 10;
    LinearLayout linear1, linear2;
    EditText profile_name, profile_comment;
    TextView name_warning, comment_warning;
    ImageView profile_image;
    Button cancle, result, delete, update;
    String name, comment, myurl, gender, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Intent intent;
    InputMethodManager imm;
    boolean imageChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        profile_name = (EditText) findViewById(R.id.profile_name);
        profile_comment = (EditText) findViewById(R.id.profile_comment);
        name_warning = (TextView) findViewById(R.id.name_warning);
        comment_warning = (TextView) findViewById(R.id.comment_warning);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        delete = (Button) findViewById(R.id.delete);
        update = (Button) findViewById(R.id.update);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        profile_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (profile_comment.getText().toString().trim().length() < 1) {
                    profile_comment.setBackgroundResource(R.drawable.edittext_red);
                    comment_warning.setVisibility(View.VISIBLE);
                } else {
                    profile_comment.setBackgroundResource(R.drawable.edittext_login);
                    comment_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        profile_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (profile_name.getText().toString().trim().length() < 1) {
                    profile_name.setBackgroundResource(R.drawable.edittext_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else {
                    profile_name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setEnabled(false);
                cancle.setEnabled(false);
                profile_image.setEnabled(false);
                imm.hideSoftInputFromWindow(profile_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(profile_comment.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(profile_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(profile_comment.getWindowToken(), 0);
                result.setEnabled(false);
                cancle.setEnabled(false);
                profile_image.setEnabled(false);
                if (profile_name.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "올바른 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    result.setEnabled(true);
                    cancle.setEnabled(true);
                    return;
                } else if (!imageChange && name.equals(profile_name.getText().toString()) && comment != null && comment.equals(profile_comment.getText().toString())) {
                    finish();
                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                    return;
                }
                insert();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(profile_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(profile_comment.getWindowToken(), 0);
                result.setEnabled(false);
                cancle.setEnabled(false);
                profile_image.setEnabled(false);
                linear1.setVisibility(View.GONE);
                linear2.setVisibility(View.VISIBLE);
                if (myurl.equals("https://firebasestorage.googleapis.com/v0/b/novel-52026.appspot.com/o/man.png?alt=media&token=3974938f-3db6-4486-9937-9cea73c31ffe") ||
                        myurl.equals("https://firebasestorage.googleapis.com/v0/b/novel-52026.appspot.com/o/woman.png?alt=media&token=ced70947-d4c3-4f61-9fe2-60af7d54f8eb")) {
                    delete.setEnabled(false);
                    delete.setTextColor(Color.parseColor("#9f9f9f"));
                } else {
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
        setting();
    }

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    myurl = userModel.profileImageUrl;
                    gender = userModel.gender;
                    profile_name.setText(userModel.userName);
                    profile_comment.setText(userModel.comment);
                    comment = userModel.comment;
                    name = userModel.userName;
                    Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void update() {
        imageChange = true;
        delete.setTextColor(Color.parseColor("#9f9f9f"));
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
                        try {
                            Glide.with(getApplicationContext()).load(imageUrl.getResult().toString()).apply(new RequestOptions().circleCrop()).into(profile_image);
                        } catch (Exception e) {

                        }
                        linear1.setVisibility(View.VISIBLE);
                        linear2.setVisibility(View.GONE);
                        profile_image.setEnabled(true);
                        update.setEnabled(true);
                        delete.setEnabled(true);
                        result.setEnabled(true);
                        cancle.setEnabled(true);
                    }
                });
            }
        });
    }

    void insert() {
        imageChange = true;
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("userName", profile_name.getText().toString());
        stringObjectMap.put("comment", profile_comment.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Map<String, Object> update = new HashMap<>();
                update.put("userName", profile_name.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("novels").orderByChild("myuid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            FirebaseDatabase.getInstance().getReference().child("novels").child(key).updateChildren(update);
                        }
                        Toast.makeText(getApplicationContext(), "프로필이 수정되었습니다", Toast.LENGTH_SHORT).show();
                        result.setEnabled(true);
                        cancle.setEnabled(true);
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    void delete() {
        imageChange = true;
        FirebaseStorage.getInstance().getReference().child("userImages").child(myUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Map<String, Object> stringObjectMap = new HashMap<>();
                if (gender.equals("남성")) {
                    stringObjectMap.put("profileImageUrl", "https://firebasestorage.googleapis.com/v0/b/novel-52026.appspot.com/o/man.png?alt=media&token=3974938f-3db6-4486-9937-9cea73c31ffe");
                    myurl = "https://firebasestorage.googleapis.com/v0/b/novel-52026.appspot.com/o/man.png?alt=media&token=3974938f-3db6-4486-9937-9cea73c31ffe";
                } else {
                    stringObjectMap.put("profileImageUrl", "https://firebasestorage.googleapis.com/v0/b/novel-52026.appspot.com/o/woman.png?alt=media&token=ced70947-d4c3-4f61-9fe2-60af7d54f8eb");
                    myurl = "https://firebasestorage.googleapis.com/v0/b/novel-52026.appspot.com/o/woman.png?alt=media&token=ced70947-d4c3-4f61-9fe2-60af7d54f8eb";
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "프로필 이미지가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        try {
                            Glide.with(getApplicationContext()).load(myurl).apply(new RequestOptions().circleCrop()).into(profile_image);
                            linear1.setVisibility(View.VISIBLE);
                            linear2.setVisibility(View.GONE);
                            update.setEnabled(true);
                            delete.setEnabled(true);
                            result.setEnabled(true);
                            cancle.setEnabled(true);
                            delete.setTextColor(Color.parseColor("#9f9f9f"));
                            profile_image.setEnabled(true);
                        } catch (Exception e) {

                        }
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
            update();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
