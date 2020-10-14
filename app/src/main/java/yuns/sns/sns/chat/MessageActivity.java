package yuns.sns.sns.chat;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import yuns.sns.R;
import yuns.sns.sns.LoginActivity;
import yuns.sns.sns.SnsActivity;
import yuns.sns.sns.alert.RoomOutWarningAlertActivity;
import yuns.sns.sns.model.ChatModel;
import yuns.sns.sns.model.NotificationModel;
import yuns.sns.sns.model.UserModel;
import yuns.sns.sns.profile.FriendProfileActivity;

public class MessageActivity extends AppCompatActivity {

    private ImageView button;
    private EditText editText;
    private String uid, chatRoomUid, destinationUid, massage_txt;
    private RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private UserModel destinationUserModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private TextView title_name;
    int peopleCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(this, LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinationUid = getIntent().getStringExtra("destinationUid");
        button = (ImageView) findViewById(R.id.message_button);
        editText = (EditText) findViewById(R.id.message_edittext);
        title_name = (TextView) findViewById(R.id.title_name);
        recyclerView = (RecyclerView) findViewById(R.id.message_recycle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                NoBanced();
                button.setEnabled(true);
            }
        });
        checkCheckRoom();
    }

    void NoBanced() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        notificationManager.cancelAll();
        final ChatModel chatModel = new ChatModel();
        chatModel.users.put(uid, true);
        chatModel.users.put(destinationUid, true);
        chatModel.date = ServerValue.TIMESTAMP;
        chatModel.user = uid;
        if (chatRoomUid == null) {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    checkCheckRoom();
                }
            });
        } else {
            if (!editText.getText().toString().isEmpty()) {

                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;
                massage_txt = editText.getText().toString();
                editText.setText("");
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> stringObjectMap = new HashMap<>();
                        stringObjectMap.put("date", ServerValue.TIMESTAMP);
                        stringObjectMap.put("user", uid);

                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sendGcm();
                            }
                        });
                    }
                });
            }
        }
    }

    void sendGcm() {
        Gson gson = new Gson();
        FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                destinationUserModel = dataSnapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (destinationUserModel.pushToken != null) {

            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.to = destinationUserModel.pushToken;
            notificationModel.notification.title = userName;
            notificationModel.notification.text = massage_txt;
            notificationModel.notification.uid = destinationUid;
            notificationModel.data.title = userName;
            notificationModel.data.text = massage_txt;
            notificationModel.data.uid = uid;

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

            Request request = new Request.Builder().header("Content-Type", "application/json")
                    .addHeader("Authorization", "key=AAAAJi0rsNY:APA91bFUS_FbW6CEBVKPVboAmydQkLxndPMeQ2PiIMgx-uxpbDfRwdqEhP4y12t8Iz8nxopaC9I2mfxMiwx89UEnCOgkVJHzNZCWJGAfUNGG3gHLjH2B1EJinC-VJv-jXYchGwX6ct70")
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(requestBody)
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }

    }

    void checkCheckRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destinationUid)) {
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                        break;
                    }
                }
                button.performClick();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinationUserModel = dataSnapshot.getValue(UserModel.class);
                    title_name.setText(destinationUserModel.userName);
                    if (chatRoomUid != null) {
                        getMessageList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        ChatModel.Comment chatModel = item.getValue(ChatModel.Comment.class);
                        if (chatModel.out != null) {
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "채팅방이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                    editText.setText("채팅방이 삭제되었습니다");
                                    button.setEnabled(false);
                                    editText.setEnabled(false);
                                    return;
                                }
                            });
                        } else {
                            String key = item.getKey();
                            ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                            ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                            comment_motify.readUsers.put(uid, true);
                            readUsersMap.put(key, comment_motify);
                            comments.add(comment_origin);
                        }

                    }
                    if (comments.size() == 0) {
                        return;
                    }

                    if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1);
                            }
                        });
                    } else {
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size() - 1);
                    }
                    Map<String, Object> readuser = new HashMap<>();
                    readuser.put("user", null);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).updateChildren(readuser);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(0);
                    notificationManager.cancelAll();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(13);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_count_left);
            } else {
                try {
                    Glide.with(holder.itemView.getContext())
                            .load(destinationUserModel.profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(messageViewHolder.imageView_profile);
                } catch (Exception e) {

                }
                if (destinationUserModel.userName.length() < 4) {
                    messageViewHolder.textView_name.setTextSize((float) 12.5);
                } else if (destinationUserModel.userName.length() < 5) {
                    messageViewHolder.textView_name.setTextSize(12);
                } else if (destinationUserModel.userName.length() < 6) {
                    messageViewHolder.textView_name.setTextSize((float) 10.5);
                } else {
                    messageViewHolder.textView_name.setTextSize(10);
                }

                messageViewHolder.textView_name.setText(destinationUserModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(13);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_count_right);

                messageViewHolder.imageView_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile = new Intent(MessageActivity.this, FriendProfileActivity.class);
                        profile.putExtra("uid", destinationUid);
                        profile.putExtra("nomal", true);
                        startActivityForResult(profile, 1);
                    }
                });
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_time.setText(time);
        }

        void setReadCounter(final int position, final TextView textView) {
            if (peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peopleCount = users.size();
                        int count = peopleCount - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                int count = peopleCount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public TextView textView_time;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_count_left;
            public TextView textView_count_right;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_text);
                textView_name = (TextView) view.findViewById(R.id.messageItem_text_name);
                textView_time = (TextView) view.findViewById(R.id.messageitem_text_time);
                imageView_profile = (ImageView) view.findViewById(R.id.messageItem_image_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_liner_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_liner_main);
                textView_count_left = (TextView) view.findViewById(R.id.messageItem_textview_count_left);
                textView_count_right = (TextView) view.findViewById(R.id.messageItem_textview_count_right);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                boolean banced = data.getBooleanExtra("banced", false);
                if (banced) {
                    finish();
                    overridePendingTransition(R.anim.fromright, R.anim.toleft);
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                boolean banced = data.getBooleanExtra("banced", false);
                if (banced) {
                    finish();
                    overridePendingTransition(R.anim.fromright, R.anim.toleft);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_top_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                overridePendingTransition(R.anim.fromright, R.anim.toleft);
                break;
            }
            case R.id.profile:
                Intent profile = new Intent(MessageActivity.this, FriendProfileActivity.class);
                profile.putExtra("uid", destinationUid);
                profile.putExtra("nomal", true);
                startActivityForResult(profile, 1);
                break;
            case R.id.out:
                Intent out = new Intent(MessageActivity.this, RoomOutWarningAlertActivity.class);
                out.putExtra("chatRoomUid", chatRoomUid);
                startActivityForResult(out, 2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            databaseReference.removeEventListener(valueEventListener);
        } catch (Exception e) {

        }
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            databaseReference.removeEventListener(valueEventListener);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkCheckRoom();
    }
}
