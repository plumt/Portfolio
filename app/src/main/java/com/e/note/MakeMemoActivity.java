package com.e.note;

import android.app.ActivityOptions;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.note.model.MemoModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MakeMemoActivity extends AppCompatActivity {
    EditText memotxt, password;
    Button result, cancle, result2;
    TextView memowarning, memolength, memotxt2, title, password_warning;
    CheckBox checkbox;
    boolean write, first;
    ImageView copy;
    String key, name, memo, write_date, pass, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 M월 d일 (E요일) a K시 mm분", Locale.KOREAN);
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_memo);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        memotxt = (EditText) findViewById(R.id.memotxt);
        password = (EditText) findViewById(R.id.password);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        copy = (ImageView) findViewById(R.id.copy);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        result2 = (Button) findViewById(R.id.result2);
        memowarning = (TextView) findViewById(R.id.memo_warning);
        password_warning = (TextView) findViewById(R.id.password_warning);
        title = (TextView) findViewById(R.id.title);
        memolength = (TextView) findViewById(R.id.memo_length);
        memotxt2 = (TextView) findViewById(R.id.memotxt2);
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        name = intent.getStringExtra("name");
        write_date = intent.getStringExtra("date");
        write = intent.getBooleanExtra("write", false);
        first = intent.getBooleanExtra("first", false);
        memo = intent.getStringExtra("memo");
        pass = intent.getStringExtra("pass");
        if (pass != null) {
            checkbox.setChecked(true);
        }
        setting();
        memotxt2.setMovementMethod(new ScrollingMovementMethod());
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(memotxt.getWindowToken(), 0);
                Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("key", key);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
                startActivity(intent, activityOptions.toBundle());
                finish();
            }
        });
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(memotxt.getWindowToken(), 0);
                if (memotxt.getText().toString().trim().length() < 1) {
                    memowarning.setVisibility(View.VISIBLE);
                    memotxt.setBackgroundResource(R.drawable.memo_red);
                    return;
                } else if (checkbox.isChecked() && password.getText().toString().trim().length() < 1) {
                    password_warning.setVisibility(View.VISIBLE);
                    password.setBackgroundResource(R.drawable.memo_red);
                    return;
                } else if (memo != null && memo.equals(memotxt.getText().toString()) && (pass == null && !checkbox.isChecked() || checkbox.isChecked() && pass != null && password.getText().toString().equals(pass))) {
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.memo_text);
                    memowarning.setVisibility(View.INVISIBLE);
                    memotxt.setBackgroundResource(R.drawable.memo_text);
                    first = false;
                    write = false;
                    setting();
                } else {
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.memo_text);
                    memowarning.setVisibility(View.INVISIBLE);
                    memotxt.setBackgroundResource(R.drawable.memo_text);
                    if (first) {
                        makeMemo();
                    } else {
                        updateMemo();
                    }
                }
            }
        });

        result2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(memotxt.getWindowToken(), 0);
                write = true;
                memotxt.setText(memotxt2.getText().toString());
                setting();
            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox.isChecked()) {
                    password.setEnabled(true);
                } else {
                    password.setEnabled(false);
                    password.setText("");
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.memo_text);
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(memotxt2.getText());
                Toast.makeText(getApplicationContext(), "메모를 복사했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        memotxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (memotxt.getText().toString().trim().length() < 1) {
                    memowarning.setVisibility(View.VISIBLE);
                    memotxt.setBackgroundResource(R.drawable.memo_red);
                } else {
                    memowarning.setVisibility(View.INVISIBLE);
                    memotxt.setBackgroundResource(R.drawable.memo_text);
                }
                memolength.setText(memotxt.getText().toString().length() + " / 1000");
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password.getText().toString().trim().length() < 1) {
                    password_warning.setVisibility(View.VISIBLE);
                    password.setBackgroundResource(R.drawable.memo_red);
                } else {
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.memo_text);
                }
            }
        });
    }

    void setting() {
        if (write) {
            result.setVisibility(View.VISIBLE);
            result2.setVisibility(View.GONE);
            memotxt.setVisibility(View.VISIBLE);
            memotxt2.setVisibility(View.GONE);
            copy.setVisibility(View.INVISIBLE);
            memolength.setVisibility(View.VISIBLE);
            memowarning.setVisibility(View.INVISIBLE);
            title.setText("메모 작성");
            checkbox.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            password_warning.setVisibility(View.INVISIBLE);
            cancle.setText("취소");
        } else {
            memotxt2.setText(memo);
            if (checkbox.isChecked()) {
                password.setText(pass);
                password.setEnabled(true);
            } else {
                password.setText("");
                password.setEnabled(false);
            }
            result.setVisibility(View.GONE);
            result2.setVisibility(View.VISIBLE);
            memotxt.setVisibility(View.GONE);
            memotxt2.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);
            memolength.setVisibility(View.INVISIBLE);
            memowarning.setVisibility(View.INVISIBLE);
            long unixTime = Long.parseLong(write_date);
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            title.setText(simpleDateFormat.format(date));
            checkbox.setVisibility(View.INVISIBLE);
            password.setVisibility(View.INVISIBLE);
            password_warning.setVisibility(View.INVISIBLE);
            cancle.setText("뒤로");
        }
    }

    void makeMemo() {
        final MemoModel.Memo memoModel = new MemoModel.Memo();
        memoModel.date = ServerValue.TIMESTAMP;
        memoModel.memo = memotxt.getText().toString().trim();
        if (checkbox.isChecked()) {
            memoModel.password = password.getText().toString().trim();
        }
        FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).child("memos").push().setValue(memoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "메모 작성 완료!", Toast.LENGTH_SHORT).show();
                write = false;
                memo = memotxt.getText().toString();
                if (checkbox.isChecked()) {
                    pass = password.getText().toString();
                }
                if (first) {
                    Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("key", key);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
                    startActivity(intent, activityOptions.toBundle());
                    finish();
                    return;
                }
                setting();
            }
        });
    }

    void updateMemo() {
        FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).child("memos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MemoModel memoModel = snapshot.getValue(MemoModel.class);
                    if (String.valueOf(memoModel.date).equals(write_date)) {
                        String key2 = snapshot.getKey();
                        Map<String, Object> stringObjectMap = new HashMap<>();
                        stringObjectMap.put("memo", memotxt.getText().toString());
                        if (checkbox.isChecked()) {
                            stringObjectMap.put("password", password.getText().toString());
                            pass = password.getText().toString();
                        } else {
                            stringObjectMap.put("password", null);
                            pass = null;
                        }
                        FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).child("memos").child(key2).updateChildren(stringObjectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "메모 수정 완료!", Toast.LENGTH_SHORT).show();
                                write = false;
                                memo = memotxt.getText().toString();
                                setting();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("key", key);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
        startActivity(intent, activityOptions.toBundle());
        finish();
    }
}
