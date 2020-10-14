package com.e.randomchat.alert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.randomchat.R;
import com.e.randomchat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BancedAlertActivity extends AppCompatActivity {

    String uid, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button cancle, result;
    TextView profile_name, profile_name2;
    ImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banced_alert);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_name2 = (TextView) findViewById(R.id.profile_name2);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        uid = getIntent().getStringExtra("uid");
        if (uid != null) {
            setting();
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setEnabled(false);
                noBanced();
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
                    profile_name2.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void noBanced() {
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("banced").child(myUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                result.setEnabled(true);
                Toast.makeText(getApplicationContext(), "차단을 해제하였습니다", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
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
