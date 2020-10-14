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

public class AddSchedule extends AppCompatActivity {

    TextView schedul_warning;
    LinearLayout liner1, liner2;
    EditText input_schdule;
    String date, cal_date, cal_name, cal_memo, room, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        Button result_btn = (Button) findViewById(R.id.result_btn);
        Button cansle_btn = (Button) findViewById(R.id.cansle_btn);
        Button delete_btn = (Button) findViewById(R.id.delete_btn);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner2 = (LinearLayout) findViewById(R.id.liner2);
        input_schdule = (EditText) findViewById(R.id.input_schedule);
        schedul_warning = (TextView) findViewById(R.id.schedul_warning);
        Intent add_schedule = getIntent();
        date = add_schedule.getStringExtra("date");
        cal_date = add_schedule.getStringExtra("cal_date");
        cal_name = add_schedule.getStringExtra("cal_name");
        cal_memo = add_schedule.getStringExtra("cal_memo");
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (date == null) {
            liner1.setVisibility(View.GONE);
            liner2.setVisibility(View.VISIBLE);
        }
        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_schdule.getWindowToken(), 0);
                if (input_schdule.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "일정을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                add_scheduleMemo();
            }
        });

        cansle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        input_schdule.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(input_schdule.getText().toString().trim().length() < 1){
                    input_schdule.setBackgroundResource(R.drawable.edittext_red);
                    schedul_warning.setVisibility(View.VISIBLE);
                } else{
                    input_schdule.setBackgroundResource(R.drawable.edittext_login);
                    schedul_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_schedule();
            }
        });
    }

    void delete_schedule() {
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
                                    FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("calender").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                final RoomModel.Calender cal = snapshot1.getValue(RoomModel.Calender.class);
                                                if (cal.uid.equals(myUid) && cal.date.equals(cal_date) && cal.memo.equals(cal_memo)) {
                                                    String key = snapshot1.getKey();
                                                    FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("calender").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Intent intent = getIntent();
                                                            boolean del = intent.getBooleanExtra("del", true);
                                                            intent.putExtra("del", del);
                                                            setResult(RESULT_OK, intent);
                                                            Toast.makeText(getApplicationContext(), "일정을 삭제하였습니다", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
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

    void add_scheduleMemo() {
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
                                final RoomModel roomModel = item.getValue(RoomModel.class);
                                if (roomModel.roomUid.equals(room)) {
                                    String roomkey = item.getKey();
                                    RoomModel.Calender calender = new RoomModel.Calender();
                                    calender.memo = input_schdule.getText().toString();
                                    calender.name = userModel.userName;
                                    calender.uid = myUid;
                                    calender.date = date;
                                    FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("calender").push().setValue(calender).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "일정을 등록했습니다", Toast.LENGTH_SHORT).show();
                                            finish();
                                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
