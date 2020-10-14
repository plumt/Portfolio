package com.e.randomchat.alert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.randomchat.R;
import com.e.randomchat.model.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class OutRoomActivity extends AppCompatActivity {
    TextView title, txt;
    LinearLayout liner1;
    Button cancle, result;
    String key, uid, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banced_alert);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        title = (TextView) findViewById(R.id.title);
        txt = (TextView) findViewById(R.id.txt);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);

        intent = getIntent();
        uid = intent.getStringExtra("uid");
        key = intent.getStringExtra("key");


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
                if (key != null) {
                    deleteRoom();
                    return;
                }
                outRoom();
            }
        });

        txt.setVisibility(View.VISIBLE);
        title.setText("방 나가기");
        txt.setText("채팅방을 나가면 대화내용이 모두 삭제되며\n채팅목록에서도 사라집니다");
        txt.setTextSize((float) 14);
        liner1.setVisibility(View.GONE);
    }

    void outRoom() {
        FirebaseDatabase.getInstance().getReference().child("randoms").orderByChild("users/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(uid)) {
                        key = item.getKey();
                        deleteRoom();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void deleteRoom() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("random").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("random").child(myUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
}
