package com.e.randomchat.chat;

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
import com.e.randomchat.LoginActivity;
import com.e.randomchat.R;
import com.e.randomchat.alert.OutRoomActivity;
import com.e.randomchat.profile.UserProfileAlertActivity;
import com.e.randomchat.model.ChatModel;
import com.e.randomchat.model.NotificationModel;
import com.e.randomchat.model.UserModel;
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

public class RandomMessageActivity extends AppCompatActivity {
    private ImageView button;
    private EditText editText;
    private String uid, chatRoomUid, destinationUid, massage_txt;
    private RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private UserModel destinationUserModel;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;
    private TextView title_name;
    int peopleCount = 0;
    boolean first = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_message);
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
        first = getIntent().getBooleanExtra("first", false);
        button = (ImageView) findViewById(R.id.message_button);
        editText = (EditText) findViewById(R.id.message_edittext);
        title_name = (TextView) findViewById(R.id.title_name);
        recyclerView = (RecyclerView) findViewById(R.id.message_recycle);
        button.setEnabled(false);
        editText.setEnabled(false);


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
                editText.setEnabled(false);
                AddFriend();
            }
        });
        checkCheckRoom();
    }

    void AddFriend() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        notificationManager.cancelAll();
        ChatModel chatModel = new ChatModel();
        chatModel.users.put(uid, true);
        chatModel.users.put(destinationUid, true);
        chatModel.date = ServerValue.TIMESTAMP;
        chatModel.user = uid;

        if (chatRoomUid == null) {
            FirebaseDatabase.getInstance().getReference().child("randoms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    checkCheckRoom();
                }
            });
        } else {
            if (!editText.getText().toString().isEmpty() || first) {
                final ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                if (first) {
                    comment.message = "안녕하세요";
                }
                massage_txt = editText.getText().toString();
                editText.setText("");
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("randoms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> stringObjectMap = new HashMap<>();
                        stringObjectMap.put("date", ServerValue.TIMESTAMP);
                        stringObjectMap.put("user", uid);
                        FirebaseDatabase.getInstance().getReference().child("randoms").child(chatRoomUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sendGcm();
                                first = false;
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
            notificationModel.data.text = massage_txt;
            notificationModel.data.title = userName;
            notificationModel.data.uid = uid;

            if (first) {
                notificationModel.notification.text = "새로운 메시지가 도착했습니다";
                notificationModel.notification.title = "(랜덤채팅)";
                notificationModel.data.text = "새로운 메시지가 도착했습니다";
                notificationModel.data.title = "(랜덤채팅)";
            }

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

            Request request = new Request.Builder().header("Content-Type", "application/json")
                    .addHeader("Authorization", "key=AAAAXQvUWkI:APA91bGyaeXRjPoj6Q6CHCzWpp59YePOkEoJungmIldeheRMQjBr0r9tnKSz7BKL09LaQNZR4b3c2X6XM5ohD5v4yamNQCNtTn2B1YQs4RUlZwBQ18y-WUZ722PpLi81FYS1kg66vrFr")
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
        FirebaseDatabase.getInstance().getReference().child("randoms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destinationUid) && chatModel.users.get(destinationUid) == true) {
                        chatRoomUid = item.getKey();
                        recyclerView.setLayoutManager(new LinearLayoutManager(RandomMessageActivity.this));
                        recyclerView.setAdapter(new RandomRecyclerViewAdapter());
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


    class RandomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel.Comment> comments;

        public RandomRecyclerViewAdapter() {
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
            databaseReference = FirebaseDatabase.getInstance().getReference().child("randoms").child(chatRoomUid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        ChatModel.Comment chatmodel = item.getValue(ChatModel.Comment.class);
                        if (chatmodel.out != null) {
                            FirebaseDatabase.getInstance().getReference().child("randoms").child(chatRoomUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "채팅방이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                    editText.setText("채팅방이 삭제되었습니다");
                                    button.setEnabled(false);
                                    editText.setEnabled(false);
//                                    return;
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
                        FirebaseDatabase.getInstance().getReference().child("randoms").child(chatRoomUid).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    FirebaseDatabase.getInstance().getReference().child("randoms").child(chatRoomUid).updateChildren(readuser);
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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
                    messageViewHolder.textView_name.setText(destinationUserModel.userName);
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


                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(13);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_count_right);


                messageViewHolder.imageView_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile = new Intent(RandomMessageActivity.this, UserProfileAlertActivity.class);
                        profile.putExtra("uid", destinationUid);
                        profile.putExtra("key", chatRoomUid);
                        startActivityForResult(profile, 1);
                    }
                });
            }
            if (comments.get(comments.size() - 1).uid.equals(destinationUid)) {
                button.setEnabled(true);
                editText.setEnabled(true);
            } else {
                button.setEnabled(false);
                editText.setEnabled(false);
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_time.setText(time);
        }

        void setReadCounter(final int position, final TextView textView) {
            if (peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("randoms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
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
                boolean out = data.getBooleanExtra("banced", false);
                if (out) {
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
                Intent profile = new Intent(RandomMessageActivity.this, UserProfileAlertActivity.class);
                profile.putExtra("uid", destinationUid);
                profile.putExtra("key", chatRoomUid);
                startActivityForResult(profile, 1);
                break;
            case R.id.out:
                Intent out = new Intent(RandomMessageActivity.this, OutRoomActivity.class);
                out.putExtra("uid", destinationUid);
                out.putExtra("key", chatRoomUid);
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
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        notificationManager.cancelAll();
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
