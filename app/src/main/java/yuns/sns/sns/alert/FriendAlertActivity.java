package yuns.sns.sns.alert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import yuns.sns.R;
import yuns.sns.sns.model.UserModel;

public class FriendAlertActivity extends Activity {
    TextView title, comment, name;
    Button result, cansle;
    String friend_uid, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendalert);
        title = (TextView) findViewById(R.id.title_text);
        comment = (TextView) findViewById(R.id.comment_text);
        name = (TextView) findViewById(R.id.comment_name);
        result = (Button) findViewById(R.id.result_btn);
        cansle = (Button) findViewById(R.id.cansle_btn);
        imageView = (ImageView) findViewById(R.id.friend_image);
        title.setText("친구 추가");
        comment.setText("님을 추가하시겠습니까?");
        Intent intent = new Intent(this.getIntent());
        friend_uid = intent.getStringExtra("friend_uid");

        print();

        cansle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriend();
            }
        });

    }

    void print() {
        FirebaseDatabase.getInstance().getReference().child("users").child(friend_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    name.setText(userModel.userName);
                    String imageUrl = userModel.profileImageUrl;
                    Glide.with(FriendAlertActivity.this).load(imageUrl).into(imageView);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    void AddFriend() {
        FirebaseDatabase.getInstance().getReference().child("users").child(friend_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> addfriend = new HashMap<>();
                addfriend.put(myUid, "friend");
                FirebaseDatabase.getInstance().getReference().child("users").child(friend_uid).child("friend").updateChildren(addfriend).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "친구 추가를 완료하였습니다", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                        return;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
