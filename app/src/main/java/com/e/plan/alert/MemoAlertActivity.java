package com.e.plan.alert;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MemoAlertActivity extends AppCompatActivity {

    TextView title_txt, memo_txt, date, name, memo_length, title_length;
    Button insert, delete, result;
    EditText title_edit, memo_edit;
    Integer y, m, d;
    Calendar calendar;
    LinearLayout liner, liner1, liner2;
    View view;
    boolean btn;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(), Save_title, Save_memo, room;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_alert);

        view = (View) findViewById(R.id.view);
        title_txt = (TextView) findViewById(R.id.title_txt);
        memo_txt = (TextView) findViewById(R.id.memo_txt);
        date = (TextView) findViewById(R.id.date);
        name = (TextView) findViewById(R.id.name);
        memo_length = (TextView) findViewById(R.id.memo_length);
        title_length = (TextView) findViewById(R.id.title_length);
        title_edit = (EditText) findViewById(R.id.title_edit);
        memo_edit = (EditText) findViewById(R.id.memo_edit);
        insert = (Button) findViewById(R.id.insert);
        delete = (Button) findViewById(R.id.delete);
        result = (Button) findViewById(R.id.result);
        liner = (LinearLayout) findViewById(R.id.liner);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner2 = (LinearLayout) findViewById(R.id.liner2);
        calendar = Calendar.getInstance();
        memo_txt.setMovementMethod(new ScrollingMovementMethod());
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        Intent memo = getIntent();
        String uid = memo.getStringExtra("uid");
        btn = memo.getBooleanExtra("btn", false);
        if (uid != null && uid.equals(myUid)) {
            insert.setEnabled(true);
            insert.setTextColor(Color.parseColor("#86B2D8"));
            delete.setEnabled(true);
            delete.setTextColor(Color.parseColor("#86B2D8"));
            liner.setVisibility(View.VISIBLE);
            liner1.setVisibility(View.VISIBLE);
        }
        if (btn) {
            liner2.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            result.setEnabled(true);
            result.setTextColor(Color.parseColor("#86B2D8"));
            inclick();
        } else {
            name.setText(memo.getStringExtra("name"));
            title_txt.setText(memo.getStringExtra("title"));
            memo_txt.setText(memo.getStringExtra("memo"));
            date.setText(memo.getStringExtra("date"));
        }

        memo_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                memo_length.setText(memo_edit.getText().toString().length() + " / 300");
            }
        });

        title_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                title_length.setText(title_edit.getText().toString().length() + " / 50");
            }
        });


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                if (title_edit.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요2", Toast.LENGTH_SHORT).show();
                } else if (memo_edit.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해 주세요2", Toast.LENGTH_SHORT).show();
                } else {
                    Save_memo = memo_txt.getText().toString().trim();
                    Save_title = title_txt.getText().toString().trim();
                    inclick();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                deleteMemo();
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                if (title_edit.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요1", Toast.LENGTH_SHORT).show();
                } else if (memo_edit.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해 주세요1", Toast.LENGTH_SHORT).show();
                } else {
                    if (btn) {
                        addMemo();
                    } else {
                        updateMemo();
                    }
                }
            }
        });
    }

    void keyboardDown() {
        imm.hideSoftInputFromWindow(title_edit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(memo_edit.getWindowToken(), 0);
    }

    void deleteMemo() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.uid.equals(myUid)) {
                        FirebaseDatabase.getInstance().getReference().child("room").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot item : dataSnapshot.getChildren()) {
                                    RoomModel roomModel = item.getValue(RoomModel.class);
                                    String room = null;
                                    switch (userModel.select) {
                                        case "1":
                                            room = userModel.room1;
                                            break;
                                        case "2":
                                            room = userModel.room2;
                                            break;
                                        case "3":
                                            room = userModel.room3;
                                    }
                                    if (roomModel.roomUid.equals(room)) {
                                        final String roomkey = item.getKey();
                                        FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("memo").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                    final RoomModel.Memo room = snapshot1.getValue(RoomModel.Memo.class);
                                                    if (room.uid.equals(myUid) && room.date.equals(date.getText().toString()) && room.title.equals(title_txt.getText().toString()) && room.memo.equals(memo_txt.getText().toString())) {
                                                        String key = snapshot1.getKey();
                                                        FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("memo").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent intent = getIntent();
                                                                boolean del = intent.getBooleanExtra("del", true);
                                                                intent.putExtra("del", del);
                                                                setResult(RESULT_OK, intent);
                                                                Toast.makeText(getApplicationContext(), "메모를 삭제하였습니다", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void updateMemo() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel.select != null) {
                    switch (userModel.select) {
                        case "1":
                            room = userModel.room1;
                            break;
                        case "2":
                            room = userModel.room2;
                            break;
                        case "3":
                            room = userModel.room3;
                    }
                    FirebaseDatabase.getInstance().getReference().child("room").orderByChild("users/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                RoomModel roomModel = item.getValue(RoomModel.class);

                                if (roomModel.roomUid.equals(room)) {
                                    final String roomkey = item.getKey();
                                    FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("memo").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                final RoomModel.Memo room = snapshot1.getValue(RoomModel.Memo.class);
                                                if (room.uid.equals(myUid) && room.name.equals(name.getText().toString()) && room.date.equals(date.getText().toString()) && room.title.equals(Save_title) && room.memo.equals(Save_memo)) {
                                                    Map<String, Object> stringObjectMap = new HashMap<>();
                                                    stringObjectMap.put("memo", memo_edit.getText().toString());
                                                    stringObjectMap.put("title", title_edit.getText().toString());
                                                    String key = snapshot1.getKey();
                                                    FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("memo").child(key).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getApplicationContext(), "메모가 수정되었습니다", Toast.LENGTH_SHORT).show();
                                                            name.setText(userModel.userName);
                                                            reclick();
                                                            return;
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "참여 중인 방이 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void addMemo() {
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH) + 1;
        d = calendar.get(Calendar.DATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel.select != null) {
                    switch (userModel.select) {
                        case "1":
                            room = userModel.room1;
                            break;
                        case "2":
                            room = userModel.room2;
                            break;
                        case "3":
                            room = userModel.room3;
                    }
                    FirebaseDatabase.getInstance().getReference().child("room").orderByChild("users/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                RoomModel roomModel = item.getValue(RoomModel.class);
                                if (roomModel.roomUid.equals(room)) {
                                    String roomkey = item.getKey();
                                    RoomModel.Memo memo = new RoomModel.Memo();
                                    memo.title = title_edit.getText().toString();
                                    memo.memo = memo_edit.getText().toString();
                                    memo.date = y + "-" + m + "-" + d;
                                    memo.uid = myUid;
                                    memo.name = userModel.userName;
                                    FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("memo").push().setValue(memo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "메모가 등록되었습니다", Toast.LENGTH_SHORT).show();
                                            btn = false;
                                            date.setText(y + "-" + m + "-" + d);
                                            name.setText(userModel.userName);
                                            reclick();
                                            return;
                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "참여 중인 방이 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void inclick() {
        insert.setEnabled(false);
        delete.setEnabled(false);
        insert.setTextColor(Color.parseColor("#E6BDBDBD"));
        delete.setTextColor(Color.parseColor("#E6BDBDBD"));
        liner.setVisibility(View.GONE);
        liner1.setVisibility(View.GONE);
        liner2.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        title_edit.setVisibility(View.VISIBLE);
        memo_edit.setVisibility(View.VISIBLE);
        memo_length.setVisibility(View.VISIBLE);
        title_length.setVisibility(View.VISIBLE);
        title_txt.setVisibility(View.GONE);
        memo_txt.setVisibility(View.GONE);
        title_edit.setText(title_txt.getText().toString());
        memo_edit.setText(memo_txt.getText().toString());
        result.setEnabled(true);
        result.setTextColor(Color.parseColor("#86B2D8"));
    }

    void reclick() {
        insert.setEnabled(true);
        delete.setEnabled(true);
        insert.setTextColor(Color.parseColor("#86B2D8"));
        delete.setTextColor(Color.parseColor("#86B2D8"));
        liner.setVisibility(View.VISIBLE);
        liner1.setVisibility(View.VISIBLE);
        liner2.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        title_edit.setVisibility(View.GONE);
        memo_edit.setVisibility(View.GONE);
        memo_length.setVisibility(View.INVISIBLE);
        title_length.setVisibility(View.INVISIBLE);
        title_txt.setVisibility(View.VISIBLE);
        memo_txt.setVisibility(View.VISIBLE);
        title_txt.setText(title_edit.getText().toString());
        memo_txt.setText(memo_edit.getText().toString());
        insert.setVisibility(View.VISIBLE);
        result.setEnabled(false);
        result.setTextColor(Color.parseColor("#E6BDBDBD"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
