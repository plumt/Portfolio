package com.e.novel.Alert;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.novel.Model.NovelModel;
import com.e.novel.Model.UserModel;
import com.e.novel.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Map;

public class CreateNovelActivity extends AppCompatActivity {

    EditText novel_name, novel_comment;
    TextView text_warning, comment_warning, title_txt;
    Button create_btn, btn_lock, btn_delete, btn_update;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(), category, myname, title, comment, key;
    Integer[] numBtnIds = {R.id.fantasy, R.id.sf, R.id.game, R.id.drama, R.id.detective, R.id.mystery, R.id.heroism, R.id.romance, R.id.comic};
    Button[] numButtons = new Button[9];
    Intent intent;
    LinearLayout linearLayout, relative2;
    boolean lock, update, finish;
    Integer[] numBtnIds2 = {R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat, R.id.sun, R.id.random};
    TextView[] numButtons2 = new TextView[8];
    boolean[] numcehck = new boolean[8];
    SwitchButton switchButton;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_novel);
        novel_name = (EditText) findViewById(R.id.novel_name);
        novel_comment = (EditText) findViewById(R.id.novel_comment);
        text_warning = (TextView) findViewById(R.id.text_warning);
        comment_warning = (TextView) findViewById(R.id.comment_warning);
        title_txt = (TextView) findViewById(R.id.title);
        create_btn = (Button) findViewById(R.id.create_btn);
        btn_lock = (Button) findViewById(R.id.btn_lock);
        btn_delete = (Button) findViewById(R.id.delete_btn);
        btn_update = (Button) findViewById(R.id.update_btn);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        relative2 = (LinearLayout) findViewById(R.id.relative2);
        switchButton = (SwitchButton) findViewById(R.id.sawtooth);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        intent = getIntent();
        myname = intent.getStringExtra("name");
        comment = intent.getStringExtra("comment");
        title = intent.getStringExtra("title");
        category = intent.getStringExtra("category");
        update = intent.getBooleanExtra("update", false);
        lock = intent.getBooleanExtra("lock", false);
        finish = intent.getBooleanExtra("finish", false);
        key = intent.getStringExtra("key");

        for (int i = 0; i < numBtnIds.length; i++) {
            numButtons[i] = (Button) findViewById(numBtnIds[i]);
        }
        for (int i = 0; i < numBtnIds.length; i++) {
            final int index;
            index = i;
            numButtons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(novel_name.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(novel_comment.getWindowToken(), 0);
                    for (int j = 0; j < numBtnIds.length; j++) {
                        numButtons[j].setBackgroundResource(R.drawable.button1);
                    }
                    numButtons[index].setBackgroundResource(R.drawable.button2);
                    category = numButtons[index].getTag().toString();

                }
            });
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                imm.hideSoftInputFromWindow(novel_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(novel_comment.getWindowToken(), 0);
            }
        });

        for (int i = 0; i < numBtnIds2.length; i++) {
            numButtons2[i] = (TextView) findViewById(numBtnIds2[i]);
            numcehck[i] = false;
        }
        for (int i = 0; i < numBtnIds2.length; i++) {
            final int index;
            index = i;
            numButtons2[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(novel_name.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(novel_comment.getWindowToken(), 0);
                    if (numBtnIds2[index] == R.id.random) {
                        for (int j = 0; j < numBtnIds2.length - 1; j++) {
                            numButtons2[j].setBackgroundResource(R.drawable.text_round1);
                            numButtons2[j].setTextColor(Color.parseColor("#5A000000"));
                            numcehck[j] = false;
                        }
                    } else {
                        numButtons2[7].setBackgroundResource(R.drawable.text_round1);
                        numButtons2[7].setTextColor(Color.parseColor("#5A000000"));
                        numcehck[7] = false;
                    }
                    numButtons2[index].setBackgroundResource(R.drawable.text_round2);
                    numButtons2[index].setTextColor(Color.BLACK);
                    numcehck[index] = true;
                }
            });
        }

        if (finish) {
            switchButton.setChecked(true);
            relative2.setVisibility(View.GONE);
        } else {
            switchButton.setChecked(false);
            relative2.setVisibility(View.VISIBLE);
        }

        if (update) {
            novel_name.setEnabled(false);
            switchButton.setVisibility(View.VISIBLE);
            novel_comment.setText(comment);
            novel_name.setText(title);
            title_txt.setText("작품 수정");
            linearLayout.setVisibility(View.VISIBLE);
            create_btn.setVisibility(View.INVISIBLE);
            if (lock) {
                btn_lock.setBackgroundResource(R.drawable.lock_open);
            } else {
                btn_lock.setBackgroundResource(R.drawable.lock_close);
            }
            switch (category) {
                case "fantasy":
                    numButtons[0].performClick();
                    break;
                case "sf":
                    numButtons[1].performClick();
                    break;
                case "game":
                    numButtons[2].performClick();
                    break;
                case "drama":
                    numButtons[3].performClick();
                    break;
                case "detective":
                    numButtons[4].performClick();
                    break;
                case "mystery":
                    numButtons[5].performClick();
                    break;
                case "heroism":
                    numButtons[6].performClick();
                    break;
                case "romance":
                    numButtons[7].performClick();
                    break;
                case "comic":
                    numButtons[8].performClick();
                    break;
            }
            setting();
        } else {
            numButtons2[7].setBackgroundResource(R.drawable.text_round2);
            numButtons2[7].setTextColor(Color.BLACK);
            numcehck[7] = true;
        }

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(novel_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(novel_comment.getWindowToken(), 0);
                if (lock) {
                    btn_lock.setBackgroundResource(R.drawable.lock_close);
                    lock = false;
                } else {
                    btn_lock.setBackgroundResource(R.drawable.lock_open);
                    lock = true;
                }
            }
        });


        novel_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (novel_name.getText().toString().trim().length() < 1) {
                    novel_name.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setVisibility(View.VISIBLE);
                } else {
                    novel_name.setBackgroundResource(R.drawable.edittext_login);
                    text_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        novel_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (novel_comment.getText().toString().trim().length() < 1) {
                    novel_comment.setBackgroundResource(R.drawable.edittext_red);
                    comment_warning.setVisibility(View.VISIBLE);
                } else {
                    novel_comment.setBackgroundResource(R.drawable.edittext_login);
                    comment_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(novel_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(novel_comment.getWindowToken(), 0);
                create_btn.setEnabled(false);
                if (novel_name.getText().toString().trim().length() < 1) {
                    novel_name.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setVisibility(View.VISIBLE);
                    create_btn.setEnabled(true);
                } else if (novel_comment.getText().toString().trim().length() < 1) {
                    novel_comment.setBackgroundResource(R.drawable.edittext_red);
                    comment_warning.setVisibility(View.VISIBLE);
                    create_btn.setEnabled(true);
                } else if (category == null) {
                    Toast.makeText(getApplicationContext(), "장르를 선택해 주세요", Toast.LENGTH_SHORT).show();
                    create_btn.setEnabled(true);
                } else {
                    create_novel();
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(novel_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(novel_comment.getWindowToken(), 0);
                btn_delete.setEnabled(false);
                btn_update.setEnabled(false);
                delete_novel();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(novel_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(novel_comment.getWindowToken(), 0);
                btn_delete.setEnabled(false);
                btn_update.setEnabled(false);
                if (novel_name.getText().toString().trim().length() < 1) {
                    novel_name.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setVisibility(View.VISIBLE);
                    btn_delete.setEnabled(true);
                    btn_update.setEnabled(true);
                } else if (novel_comment.getText().toString().trim().length() < 1) {
                    novel_comment.setBackgroundResource(R.drawable.edittext_red);
                    comment_warning.setVisibility(View.VISIBLE);
                    btn_delete.setEnabled(true);
                    btn_update.setEnabled(true);
                } else if (category == null) {
                    Toast.makeText(getApplicationContext(), "장르를 선택해 주세요", Toast.LENGTH_SHORT).show();
                    btn_delete.setEnabled(true);
                    btn_update.setEnabled(true);
                } else {
                    update_novel();
                }
            }
        });
    }

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NovelModel novelModel = dataSnapshot.getValue(NovelModel.class);
                String[] weeks = {"월", "화", "수", "목", "금", "토", "일", "자유"};
                for (int i = 0; i < 8; i++) {
                    if (novelModel.week.containsKey(weeks[i])) {
                        numButtons2[i].setBackgroundResource(R.drawable.text_round2);
                        numButtons2[i].setTextColor(Color.BLACK);
                        numcehck[i] = true;
                    } else {
                        numcehck[i] = false;
                        numButtons2[i].setBackgroundResource(R.drawable.text_round1);
                        numButtons2[i].setTextColor(Color.parseColor("#5A000000"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void delete_novel() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel.now_write.containsKey(key)) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("now_write").child(key).removeValue();
                } else if (userModel.finish_write.containsKey(key)) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("finish_write").child(key).removeValue();
                }
                FirebaseDatabase.getInstance().getReference().child("novels").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "작품을 삭제했습니다", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void update_novel() {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("category_bool").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("week").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Object> update_week = new HashMap<>();
                        for (int i = 0; i < numBtnIds2.length; i++) {
                            if (numcehck[i]) {
                                update_week.put(numButtons2[i].getText().toString(), true);
                            }
                        }
                        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("week").updateChildren(update_week).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> update = new HashMap<>();
                                if (switchButton.isChecked()) {
                                    update.put("finish", true);
                                } else {
                                    update.put("finish", false);
                                }
                                update.put("title", novel_name.getText().toString().trim());
                                update.put("category_str", category);
                                update.put("open", lock);
                                update.put("comment", novel_comment.getText().toString().trim());
                                FirebaseDatabase.getInstance().getReference().child("novels").child(key).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        final Map<String, Object> update2 = new HashMap<>();
                                        update2.put(category, true);
                                        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("category_bool").updateChildren(update2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Map<String, Object> finish = new HashMap<>();
                                                finish.put(key, true);
                                                if (switchButton.isChecked()) {
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("finish_write").updateChildren(finish).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("now_write").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getApplicationContext(), "작품을 수정했습니다", Toast.LENGTH_SHORT).show();
                                                                    setResult(RESULT_OK, intent);
                                                                    finish();
                                                                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("now_write").updateChildren(finish).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("finish_write").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getApplicationContext(), "작품을 수정했습니다", Toast.LENGTH_SHORT).show();
                                                                    setResult(RESULT_OK, intent);
                                                                    finish();
                                                                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    void create_novel() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel.finish_write.containsKey(novel_name.getText().toString().trim()) || userModel.now_write.containsKey(novel_name.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "이미 같은 이름의 작품이 있습니다", Toast.LENGTH_SHORT).show();
                    create_btn.setEnabled(true);
                } else {
                    NovelModel novelModel = new NovelModel();
                    novelModel.category_bool.put(category, true);
                    novelModel.uid.put(myUid, true);
                    novelModel.open = lock;
                    novelModel.myuid = myUid;
                    novelModel.category_str = category;
                    novelModel.date = ServerValue.TIMESTAMP;
                    novelModel.title = novel_name.getText().toString().trim();
                    novelModel.userName = myname;
                    novelModel.like = "0";
                    novelModel.view = "0";
                    novelModel.finish = false;
                    novelModel.comment = novel_comment.getText().toString().trim();
                    for (int i = 0; i < numBtnIds2.length; i++) {
                        if (numcehck[i]) {
                            novelModel.week.put(numButtons2[i].getText().toString(), true);
                        }
                    }

                    FirebaseDatabase.getInstance().getReference().child("novels").push().setValue(novelModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance().getReference().child("novels").orderByChild("uid/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        NovelModel novelModel2 = snapshot.getValue(NovelModel.class);
                                        if (novelModel2.title.equals(novel_name.getText().toString().trim())) {
                                            key = snapshot.getKey();
                                            Map<String, Object> addkey = new HashMap<>();
                                            addkey.put("key", key);
                                            FirebaseDatabase.getInstance().getReference().child("novels").child(key).updateChildren(addkey).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Map<String, Object> now_write = new HashMap<>();
                                                    now_write.put(key, true);
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("now_write").updateChildren(now_write).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "새로운 작품을 등록했습니다", Toast.LENGTH_SHORT).show();
                                                            setResult(RESULT_OK, intent);
                                                            finish();
                                                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
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
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
