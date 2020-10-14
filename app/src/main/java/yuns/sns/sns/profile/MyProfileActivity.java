package yuns.sns.sns.profile;

import android.app.Activity;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import yuns.sns.R;
import yuns.sns.sns.model.UserModel;

public class MyProfileActivity extends Activity {
    private static final int PICK_FROM_ALBUM = 10;
    private Uri imageUri;
    Button cancle_btn, result_btn, delete_btn, update_btn;
    ImageView profile_image;
    EditText profile_name, profile_comment;
    TextView comment_length, name_warning;
    LinearLayout liner1, liner2;
    String name, comment, myurl, gender, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    InputMethodManager imm;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        cancle_btn = (Button) findViewById(R.id.cancle_btn);
        result_btn = (Button) findViewById(R.id.result_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        update_btn = (Button) findViewById(R.id.update_btn);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_name = (EditText) findViewById(R.id.profile_name);
        profile_comment = (EditText) findViewById(R.id.profile_comment);
        comment_length = (TextView) findViewById(R.id.comment_length);
        name_warning = (TextView) findViewById(R.id.name_warning);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner2 = (LinearLayout) findViewById(R.id.liner2);
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
                comment_length.setText(profile_comment.getText().toString().length() + " / 20");
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
                    profile_name.setBackgroundResource(R.drawable.profile_name_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else {
                    profile_name.setBackgroundResource(R.drawable.profile_name);
                    name_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                result_btn.setEnabled(false);
                cancle_btn.setEnabled(false);
                if (profile_name.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "올바른 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    result_btn.setEnabled(true);
                    cancle_btn.setEnabled(true);
                    return;
                } else if (name.equals(profile_name.getText().toString()) && comment != null && comment.equals(profile_comment.getText().toString())) {
                    finish();
                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                    return;
                }
                insert();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                delete_btn.setEnabled(false);
                update_btn.setEnabled(false);
                delete();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                delete_btn.setEnabled(false);
                update_btn.setEnabled(false);
                Intent photo = new Intent(Intent.ACTION_PICK);
                photo.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(photo, PICK_FROM_ALBUM);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                profile_image.setEnabled(false);
                update_btn.setEnabled(true);
                liner1.setVisibility(View.GONE);
                liner2.setVisibility(View.VISIBLE);
                if (myurl.equals("https://firebasestorage.googleapis.com/v0/b/sns-55e69.appspot.com/o/man.png?alt=media&token=ebc3c563-4d9f-4c04-a08e-f24646627c52") ||
                        myurl.equals("https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/woman.png?alt=media&token=84de3df3-8b0c-4990-badc-4512bdee7360")) {
                    delete_btn.setEnabled(false);
                    delete_btn.setTextColor(Color.parseColor("#9f9f9f"));
                } else {
                    delete_btn.setEnabled(true);
                    delete_btn.setTextColor(Color.parseColor("#86B2D8"));
                }

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

    void insert() {
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("userName", profile_name.getText().toString());
        stringObjectMap.put("comment", profile_comment.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "프로필이 수정되었습니다", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });
    }

    void delete() {
        FirebaseStorage.getInstance().getReference().child("userImages").child(myUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Map<String, Object> stringObjectMap = new HashMap<>();
                if (gender.equals("남성")) {
                    stringObjectMap.put("profileImageUrl", "https://firebasestorage.googleapis.com/v0/b/sns-55e69.appspot.com/o/man.png?alt=media&token=ebc3c563-4d9f-4c04-a08e-f24646627c52");
                    myurl = "https://firebasestorage.googleapis.com/v0/b/sns-55e69.appspot.com/o/man.png?alt=media&token=ebc3c563-4d9f-4c04-a08e-f24646627c52";
                } else {
                    stringObjectMap.put("profileImageUrl", "https://firebasestorage.googleapis.com/v0/b/randomchat-9f7ce.appspot.com/o/woman.png?alt=media&token=84de3df3-8b0c-4990-badc-4512bdee7360");
                    myurl = "https://firebasestorage.googleapis.com/v0/b/sns-55e69.appspot.com/o/woman.png?alt=media&token=3845f683-1636-4db8-805b-a3f967aaeba6";
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "프로필 이미지가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        try {
                            Glide.with(getApplicationContext()).load(myurl).apply(new RequestOptions().circleCrop()).into(profile_image);
                            liner1.setVisibility(View.VISIBLE);
                            liner2.setVisibility(View.GONE);
                            update_btn.setEnabled(false);
                            delete_btn.setEnabled(false);
                            delete_btn.setTextColor(Color.parseColor("#9f9f9f"));
                            profile_image.setEnabled(true);
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }

    void keyboardDown(){
        imm.hideSoftInputFromWindow(profile_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(profile_comment.getWindowToken(), 0);
    }
    void update() {
        delete_btn.setTextColor(Color.parseColor("#9f9f9f"));
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
                        liner1.setVisibility(View.VISIBLE);
                        liner2.setVisibility(View.GONE);
                        profile_image.setEnabled(true);
                        update_btn.setEnabled(true);
                        delete_btn.setEnabled(true);
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
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);

    }
}