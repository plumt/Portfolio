package yuns.sns.sns.alert;

import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
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
import yuns.sns.sns.model.ChatModel;
import yuns.sns.sns.model.UserModel;

public class AddFriendActivity extends Activity {
    TextView output_txt, output_txt2, tag_warning;
    Button serch_btn, cansle_btn, serch_btn2, cansle_btn2;
    EditText input_edit;
    ImageView friend_image;
    LinearLayout liner1, liner2;
    Boolean pass = true;
    int choice;
    String chatRoomUid, uid, name, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        serch_btn = (Button) findViewById(R.id.serch_btn);
        serch_btn2 = (Button) findViewById(R.id.serch_btn2);
        cansle_btn = (Button) findViewById(R.id.cansle_btn);
        cansle_btn2 = (Button) findViewById(R.id.cansle_btn2);
        input_edit = (EditText) findViewById(R.id.friend_tag);
        output_txt = (TextView) findViewById(R.id.friend_select);
        output_txt2 = (TextView) findViewById(R.id.friend_select2);
        tag_warning = (TextView) findViewById(R.id.tag_warning);
        friend_image = (ImageView) findViewById(R.id.friend_image);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner2 = (LinearLayout) findViewById(R.id.liner2);
        friend_image.setBackground(new ShapeDrawable(new OvalShape()));
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        cansle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(input_edit.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        input_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (input_edit.getText().toString().trim().length() < 6) {
                    input_edit.setBackgroundResource(R.drawable.edittext_red);
                    tag_warning.setVisibility(View.VISIBLE);
                } else {
                    input_edit.setBackgroundResource(R.drawable.edittext_login);
                    tag_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        serch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(input_edit.getWindowToken(), 0);
                if (input_edit.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "6자리를 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    serch_btn.setEnabled(false);
                    cansle_btn.setEnabled(false);
                    SerchFriend(input_edit.getText().toString());
                }
            }
        });

        cansle_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_edit.getWindowToken(), 0);
                serch_btn.setEnabled(true);
                cansle_btn.setEnabled(true);
                liner1.setVisibility(View.VISIBLE);
                liner2.setVisibility(View.GONE);
                input_edit.setVisibility(View.VISIBLE);
                tag_warning.setVisibility(View.INVISIBLE);
                output_txt.setVisibility(View.GONE);
                friend_image.setVisibility(View.GONE);
                output_txt2.setVisibility(View.GONE);
            }
        });
        serch_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_edit.getWindowToken(), 0);
                if (choice == 1) {
                    addFriend();
                } else if (choice == 2) {
                    banced();
                } else if (choice == 3) {
                    noBanced();
                } else {
                    cansle_btn.performClick();
                }
            }
        });


    }

    void SerchFriend(final String addFriend) {
        pass = true;
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.uid.equals(myUid) && userModel.tag.equals(addFriend)) {
                        Toast.makeText(getApplicationContext(), "태그를 확인해 주세요", Toast.LENGTH_SHORT).show();
                        serch_btn.setEnabled(true);
                        cansle_btn.setEnabled(true);
                        return;
                    } else if (userModel.tag.equals(addFriend)) {
                        liner1.setVisibility(View.GONE);
                        liner2.setVisibility(View.VISIBLE);
                        input_edit.setVisibility(View.GONE);
                        tag_warning.setVisibility(View.GONE);
                        output_txt.setVisibility(View.VISIBLE);
                        friend_image.setVisibility(View.VISIBLE);
                        output_txt2.setVisibility(View.VISIBLE);
                        try {
                            uid = userModel.uid;
                            name = userModel.userName;
                            output_txt.setText(userModel.userName);
                            String imageUrl = userModel.profileImageUrl;
                            Glide.with(AddFriendActivity.this).load(imageUrl).apply(new RequestOptions().circleCrop()).into(friend_image);
                        } catch (Exception e) {
                        }
                        if (userModel.banced.containsKey(myUid)) {
                            choice = 3;
                            serch_btn2.setText("해제");
                            output_txt2.setText("님을 차단 해제하시겠습니까?");
                        } else if (!userModel.friend.containsKey(myUid)) {
                            choice = 1;
                            serch_btn2.setText("추가");
                            output_txt2.setText("님을 추가하시겠습니까?");
                        } else {
                            choice = 2;
                            serch_btn2.setText("차단");
                            output_txt2.setText("님을 차단하시겠습니까?");
                        }
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "친구를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                serch_btn.setEnabled(true);
                cansle_btn.setEnabled(true);
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
                Toast.makeText(getApplicationContext(), "친구 차단을 해제하였습니다", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                return;
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
                        comment.uid = uid;
                        comment.message = "";
                        comment.out = "out";
                        comment.timestamp = ServerValue.TIMESTAMP;
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        frined_delete();
                                        return;
                                    }
                                });
                            }
                        });
                        break;
                    }
                }
                frined_delete();
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
                        try {
                            Toast.makeText(getApplicationContext(), name + "님을 차단했습니다", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {

                        }
                        finish();
                        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                        return;
                    }
                });
            }
        });
    }

    void addFriend() {

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> addfriend = new HashMap<>();
                addfriend.put(myUid, "friend");
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("friend").updateChildren(addfriend).addOnCompleteListener(new OnCompleteListener<Void>() {
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