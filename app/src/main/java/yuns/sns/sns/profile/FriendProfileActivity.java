package yuns.sns.sns.profile;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import yuns.sns.R;
import yuns.sns.sns.chat.MessageActivity;
import yuns.sns.sns.model.ChatModel;
import yuns.sns.sns.model.UserModel;

public class FriendProfileActivity extends Activity {
    private String uid, chatRoomUid, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(), name;
    Button banced_btn1, banced_btn2, chat_btn, cancle_btn, result_btn;
    ImageView profile_image;
    TextView profile_name, profile_name2, profile_tag, profile_gender, profile_comment;
    LinearLayout liner1, liner2, liner3, liner4;
    boolean nomal;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendprofile);
        banced_btn1 = (Button) findViewById(R.id.banced_btn1);
        banced_btn2 = (Button) findViewById(R.id.banced_btn2);
        chat_btn = (Button) findViewById(R.id.chat_btn);
        cancle_btn = (Button) findViewById(R.id.cancle_btn);
        result_btn = (Button) findViewById(R.id.result_btn);

        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_name2 = (TextView) findViewById(R.id.profile_name2);
        profile_tag = (TextView) findViewById(R.id.profile_tag);
        profile_gender = (TextView) findViewById(R.id.profile_gender);
        profile_comment = (TextView) findViewById(R.id.profile_comment);

        profile_image = (ImageView) findViewById(R.id.profile_image);
        liner4 = (LinearLayout) findViewById(R.id.liner4);
        liner3 = (LinearLayout) findViewById(R.id.liner3);
        liner2 = (LinearLayout) findViewById(R.id.liner2);
        liner1 = (LinearLayout) findViewById(R.id.liner1);

        intent = getIntent();
        uid = intent.getStringExtra("uid");
        nomal = intent.getBooleanExtra("nomal", false);

        if (nomal) {
            liner3.setVisibility(View.GONE);
            banced_btn2.setVisibility(View.VISIBLE);
        }

        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner1.setVisibility(View.VISIBLE);
                liner2.setVisibility(View.VISIBLE);
                liner3.setVisibility(View.VISIBLE);
                liner4.setVisibility(View.GONE);
                profile_name2.setVisibility(View.GONE);
                if (nomal) {
                    liner3.setVisibility(View.GONE);
                    banced_btn2.setVisibility(View.VISIBLE);
                }
            }
        });
        banced_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner1.setVisibility(View.GONE);
                liner2.setVisibility(View.GONE);
                liner3.setVisibility(View.GONE);
                liner4.setVisibility(View.VISIBLE);
                profile_name2.setVisibility(View.VISIBLE);
            }
        });

        banced_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banced_btn2.setVisibility(View.GONE);
                banced_btn1.performClick();
            }
        });

        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banced();
            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(FriendProfileActivity.this, MessageActivity.class);
                chat.putExtra("destinationUid", uid);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromleft, R.anim.toright);
                startActivity(chat, activityOptions.toBundle());
                finish();
            }
        });
        setting();
    }

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    profile_name.setText(userModel.userName);
                    name = userModel.userName;
                    profile_gender.setText(userModel.gender);
                    profile_tag.setText("#" + userModel.tag);
                    profile_comment.setText(userModel.comment);
                    if (userModel.comment == null) {
                        liner2.setVisibility(View.INVISIBLE);
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

    void banced() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(uid)) {
                        chatRoomUid = snapshot.getKey();
                        ChatModel.Comment comment = new ChatModel.Comment();
                        comment.uid = myUid;
                        comment.message = "";
                        comment.out = "out";
                        comment.timestamp = ServerValue.TIMESTAMP;
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).removeValue();
                            }
                        });
                        break;
                    }
                }
                frined_delete();
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    void frined_delete() {
        Map<String, Object> banced = new HashMap<>();
        banced.put(myUid, "banced");
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("banced").updateChildren(banced).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("friend").child(myUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
