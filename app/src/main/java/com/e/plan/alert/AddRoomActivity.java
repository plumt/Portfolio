package com.e.plan.alert;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.plan.Model.RoomModel;
import com.e.plan.Model.UserModel;
import com.e.plan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddRoomActivity extends AppCompatActivity {

    LinearLayout liner1, liner2, liner3, liner4, liner5;
    EditText serch_tag, room_tag, room_name, serch_password, room_password;
    TextView title, name_warning, tag_warning, tag_warning2, password_warning, password_warning2;
    String number, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(), select, room_key, NEW, tag;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        title = (TextView) findViewById(R.id.title);
        name_warning = (TextView) findViewById(R.id.name_warning);
        tag_warning = (TextView) findViewById(R.id.tag_warning);
        tag_warning2 = (TextView) findViewById(R.id.tag_warning2);
        password_warning = (TextView) findViewById(R.id.password_warning);
        password_warning = (TextView) findViewById(R.id.password_warning);
        password_warning2 = (TextView) findViewById(R.id.password_warning2);
        Button come_in_room = (Button) findViewById(R.id.come_in_room);
        Button add_room = (Button) findViewById(R.id.add_room);
        Button cansle_btn1 = (Button) findViewById(R.id.cansle_btn1);
        Button cansle_btn2 = (Button) findViewById(R.id.cansle_btn2);
        Button result_btn1 = (Button) findViewById(R.id.result_btn1);
        Button result_btn2 = (Button) findViewById(R.id.result_btn2);
        Button delete_btn = (Button) findViewById(R.id.delete_btn);
        Button change_btn = (Button) findViewById(R.id.change_btn);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner2 = (LinearLayout) findViewById(R.id.liner2);
        liner3 = (LinearLayout) findViewById(R.id.liner3);
        liner4 = (LinearLayout) findViewById(R.id.liner4);
        liner5 = (LinearLayout) findViewById(R.id.liner5);
        serch_tag = (EditText) findViewById(R.id.serch_tag);
        room_tag = (EditText) findViewById(R.id.room_tag);
        room_name = (EditText) findViewById(R.id.room_name);
        serch_password = (EditText) findViewById(R.id.serch_password);
        room_password = (EditText) findViewById(R.id.room_password);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        select = intent.getStringExtra("select");
        NEW = intent.getStringExtra("NEW");
        tag = intent.getStringExtra("tag");
        try {
            if (number.equals("1")) {
                title.setText("방(#1) 참여하기");
            } else if (number.equals("2")) {
                title.setText("방(#2) 참여하기");
            } else if (number.equals("3")) {
                title.setText("방(#3) 참여하기");
            }
        } catch (Exception e) {
            finish();
            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
        }
        if (!select.equals("0") && !select.equals(number) && !NEW.equals("NEW")) {
            liner_visible(5);

        } else if (!select.equals("0") && select.equals(number) && !NEW.equals("NEW")) {
            liner_visible(4);
        }

        come_in_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner_visible(2);
                keyboardDown();
            }
        });

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner_visible(3);
                keyboardDown();
            }
        });
        cansle_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner_visible(1);
                serch_tag.setText("");
                serch_password.setText("");
                nomal();
                keyboardDown();
            }
        });
        cansle_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liner_visible(1);
                room_name.setText("");
                room_tag.setText("");
                room_password.setText("");
                nomal();
                keyboardDown();
            }
        });

        result_btn1.setOnClickListener(new View.OnClickListener() { // 방 참가
            @Override
            public void onClick(View v) {
                keyboardDown();
                if (serch_tag.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "방 태그는 6자리를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                roomIn(number);
            }
        });

        result_btn2.setOnClickListener(new View.OnClickListener() { // 방 생성
            @Override
            public void onClick(View v) {
                keyboardDown();
                if (room_name.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "방 이름은 1자리 이상 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (room_tag.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "방 태그는 6자리를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                createRoom(number);
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() { // 방 삭제
            @Override
            public void onClick(View v) {
                keyboardDown();
                roomDelete(number);
            }
        });

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                Map<String, Object> stringObjectMap = new HashMap<>();
                stringObjectMap.put("select", number);
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        room_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (room_name.getText().toString().trim().length() < 1) {
                    room_name.setBackgroundResource(R.drawable.edittext_red);
                    name_warning.setVisibility(View.VISIBLE);
                } else {
                    room_name.setBackgroundResource(R.drawable.edittext_login);
                    name_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        room_tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (room_tag.getText().toString().trim().length() < 6) {
                    room_tag.setBackgroundResource(R.drawable.edittext_red);
                    tag_warning2.setVisibility(View.VISIBLE);
                } else {
                    room_tag.setBackgroundResource(R.drawable.edittext_login);
                    tag_warning2.setVisibility(View.INVISIBLE);
                }
            }
        });

        serch_tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (serch_tag.getText().toString().trim().length() < 6) {
                    serch_tag.setBackgroundResource(R.drawable.edittext_red);
                    tag_warning.setVisibility(View.VISIBLE);
                } else {
                    serch_tag.setBackgroundResource(R.drawable.edittext_login);
                    tag_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        serch_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (serch_password.getText().toString().trim().length() < 1) {
                    serch_password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    serch_password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        room_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (room_password.getText().toString().trim().length() < 1) {
                    room_password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning2.setVisibility(View.VISIBLE);
                } else {
                    room_password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void nomal(){
        serch_tag.setBackgroundResource(R.drawable.edittext_login);
        room_name.setBackgroundResource(R.drawable.edittext_login);
        room_tag.setBackgroundResource(R.drawable.edittext_login);
        room_password.setBackgroundResource(R.drawable.edittext_login);
        serch_password.setBackgroundResource(R.drawable.edittext_login);
        name_warning.setVisibility(View.INVISIBLE);
        tag_warning.setVisibility(View.INVISIBLE);
        tag_warning2.setVisibility(View.INVISIBLE);
        password_warning.setVisibility(View.INVISIBLE);
        password_warning2.setVisibility(View.INVISIBLE);
    }

    void liner_visible(int num) {
        liner1.setVisibility(View.GONE);
        liner2.setVisibility(View.GONE);
        liner3.setVisibility(View.GONE);
        liner4.setVisibility(View.GONE);
        liner5.setVisibility(View.GONE);
        switch (num) {
            case 1:
                liner1.setVisibility(View.VISIBLE);
                break;
            case 2:
                liner2.setVisibility(View.VISIBLE);
                break;
            case 3:
                liner3.setVisibility(View.VISIBLE);
                break;
            case 4:
                liner4.setVisibility(View.VISIBLE);
                break;
            case 5:
                liner5.setVisibility(View.VISIBLE);
                break;
        }
    }

    void keyboardDown() {
        imm.hideSoftInputFromWindow(room_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(room_tag.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(serch_tag.getWindowToken(), 0);
    }

    void roomDelete(final String room) {
        FirebaseDatabase.getInstance().getReference().child("room").orderByChild("users/" + myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    RoomModel roomModel = item.getValue(RoomModel.class);
                    if (roomModel.roomUid.equals(tag)) {
                        room_key = item.getKey();
                        break;
                    }
                }

                FirebaseDatabase.getInstance().getReference().child("room").child(room_key).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        final int peopleCount = users.size();

                        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("room" + room).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("room" + room + "_name").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                                Map<String, Object> stringObjectMap = new HashMap<>();
                                                if (userModel.room1 != null) {
                                                    stringObjectMap.put("select", "1");
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);
                                                } else if (userModel.room2 != null) {
                                                    stringObjectMap.put("select", "2");
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);

                                                } else if (userModel.room3 != null) {
                                                    stringObjectMap.put("select", "3");
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("select").removeValue();
                                                }
                                                if (peopleCount == 1) {
                                                    FirebaseDatabase.getInstance().getReference().child("room").child(room_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "참여 중인 방을 삭제하였습니다", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                                            return;
                                                        }
                                                    });
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("room").child(room_key).child("users").child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "참여 중인 방을 삭제하였습니다", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                                            return;
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void roomIn(final String room) {
        FirebaseDatabase.getInstance().getReference().child("room").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean pass = true;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    RoomModel roomModel = item.getValue(RoomModel.class);
                    if (roomModel.roomUid.equals(serch_tag.getText().toString()) && !roomModel.users.containsKey(myUid) && roomModel.password.equals(serch_password.getText().toString())) {
                        pass = false;
                        Map<String, Object> stringObjectMap1 = new HashMap<>();
                        stringObjectMap1.put("select", number);
                        stringObjectMap1.put("room" + room, serch_tag.getText().toString());
                        stringObjectMap1.put("room" + room + "_name", roomModel.roomdName);
                        room_key = item.getKey();
                        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> stringObjectMap2 = new HashMap<>();
                                stringObjectMap2.put(myUid, true);
                                FirebaseDatabase.getInstance().getReference().child("room").child(room_key).child("users").updateChildren(stringObjectMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "새로운 방에 입장하였습니다", Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                        return;
                                    }
                                });
                            }
                        });
                        break;
                    } else if (roomModel.roomUid.equals(serch_tag.getText().toString()) && roomModel.users.containsKey(myUid)) {
                        pass = false;
                        Toast.makeText(getApplicationContext(), "이미 참여 중인 방입니다", Toast.LENGTH_SHORT).show();
                        return;
                    } else if(roomModel.roomUid.equals(serch_tag.getText().toString()) && !roomModel.password.equals(serch_password.getText().toString())){
                        Toast.makeText(getApplicationContext(), "패스워드가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                if (pass) {
                    Toast.makeText(getApplicationContext(), "존재하지 않는 방입니다", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void createRoom(final String room) {
        FirebaseDatabase.getInstance().getReference().child("room").addListenerForSingleValueEvent(new ValueEventListener() {
            boolean check = true;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RoomModel roomModel = snapshot.getValue(RoomModel.class);
                    if (roomModel.roomUid.equals(room_tag.getText().toString())) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    final RoomModel roomModel = new RoomModel();
                    roomModel.roomUid = room_tag.getText().toString();
                    roomModel.roomdName = room_name.getText().toString().trim();
                    roomModel.password = room_password.getText().toString().trim();
                    roomModel.users.put(myUid, true);
                    FirebaseDatabase.getInstance().getReference().child("room").push().setValue(roomModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, Object> stringObjectMap = new HashMap<>();
                            stringObjectMap.put("room" + room, room_tag.getText().toString());
                            stringObjectMap.put("select", room);
                            stringObjectMap.put("room" + room + "_name", room_name.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);
                            Toast.makeText(getApplicationContext(), "새로운 방을 등록하였습니다", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                            return;
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 코드입니다", Toast.LENGTH_SHORT).show();
                    room_tag.setText("");
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
