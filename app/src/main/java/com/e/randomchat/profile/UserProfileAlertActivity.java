package com.e.randomchat.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.randomchat.R;
import com.e.randomchat.model.ChatModel;
import com.e.randomchat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserProfileAlertActivity extends AppCompatActivity {
    String uid, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(), key, name;
    Button result, cancle, banced;
    TextView profile_name, profile_name2, profile_comment, profile_gender;
    ImageView profile_image;
    LinearLayout liner1, liner_comment;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_alert);

        result = (Button) findViewById(R.id.result);
        cancle = (Button) findViewById(R.id.cancle);
        banced = (Button) findViewById(R.id.banced);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_comment = (TextView) findViewById(R.id.profile_comment);
        profile_name2 = (TextView) findViewById(R.id.profile_name2);
        profile_gender = (TextView) findViewById(R.id.profile_gender);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner_comment = (LinearLayout) findViewById(R.id.liner_comment);

        intent = getIntent();
        uid = intent.getStringExtra("uid");
        key = intent.getStringExtra("key");

        if (uid != null && key != null) {
            setting();
        }

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner_comment.setVisibility(View.VISIBLE);
                profile_gender.setVisibility(View.VISIBLE);
                profile_name2.setVisibility(View.GONE);
                result.setVisibility(View.VISIBLE);
                liner1.setVisibility(View.GONE);
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner_comment.setVisibility(View.GONE);
                profile_gender.setVisibility(View.GONE);
                profile_name2.setVisibility(View.VISIBLE);
                result.setVisibility(View.GONE);
                liner1.setVisibility(View.VISIBLE);
            }
        });

        banced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banced.setEnabled(false);
                cancle.setEnabled(false);
                bancedUser();
            }
        });


    }

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    profile_name.setText(userModel.userName);
                    name = userModel.userName;
                    profile_comment.setText(userModel.comment);
                    profile_gender.setText(userModel.gender);
                    if (userModel.comment == null || userModel.comment.equals("")) {
                        liner_comment.setVisibility(View.INVISIBLE);
                    }
                    Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void bancedUser() {

        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("random").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("random").child(myUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> banced = new HashMap<>();
                        banced.put(myUid, "banced");
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("banced").updateChildren(banced).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ChatModel.Comment comment = new ChatModel.Comment();
                                comment.uid = myUid;
                                comment.message = "";
                                comment.out = "out";
                                comment.timestamp = ServerValue.TIMESTAMP;
                                FirebaseDatabase.getInstance().getReference().child("randoms").child(key).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference().child("randoms").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(), name + "님을 차단했습니다", Toast.LENGTH_SHORT).show();
                                                intent.putExtra("banced", true);
                                                setResult(RESULT_OK, intent);
                                                finish();
                                                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                                return;
                                            }
                                        });
                                    }
                                });

                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
