package yuns.sns.sns.alert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import yuns.sns.R;
import yuns.sns.sns.model.ChatModel;

public class RoomOutWarningAlertActivity extends AppCompatActivity {
    boolean alone;
    String chatRoomUid, uid, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button cancle, result;
    EditText input_email;
    TextView comment, title;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_password);
        title = (TextView) findViewById(R.id.title);
        comment = (TextView) findViewById(R.id.comment);
        input_email = (EditText) findViewById(R.id.input_email);
        cancle = (Button) findViewById(R.id.cansle_btn);
        result = (Button) findViewById(R.id.serch_btn);

        input_email.setVisibility(View.GONE);
        title.setText("채팅방 나가기");
        comment.setTextSize((float) 14);
        comment.setText("채팅방을 나가면 대화내용이 모두 삭제되며\n채팅목록에서도 사라집니다");
        comment.setVisibility(View.VISIBLE);

        intent = getIntent();
        chatRoomUid = intent.getStringExtra("chatRoomUid");
        alone = intent.getBooleanExtra("alone", false);
        uid = intent.getStringExtra("destinationUid");


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
                if (chatRoomUid != null) {
                    roomDelete();
                } else {
                    setchChatroomId();
                }
            }
        });
    }

    void setchChatroomId() {
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
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "채팅방이 삭제되었습니다", Toast.LENGTH_SHORT).show();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void roomDelete() {
        ChatModel.Comment comment = new ChatModel.Comment();
        comment.uid = myUid;
        comment.message = "";
        comment.out = "out";
        comment.timestamp = ServerValue.TIMESTAMP;
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                return;
            }
        });
    }
}
