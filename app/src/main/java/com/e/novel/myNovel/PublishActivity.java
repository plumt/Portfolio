package com.e.novel.myNovel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e.novel.Model.NovelModel;
import com.e.novel.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PublishActivity extends AppCompatActivity {

    EditText title_edit, content_edit;
    TextView title_length, content_length;
    Button btn_result, btn_update, btn_delete, btn_lock;
    String key, count, title, contents;
    Intent intent;
    boolean update, lock;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        title_edit = (EditText) findViewById(R.id.title_edit);
        content_edit = (EditText) findViewById(R.id.content_edit);
        title_length = (TextView) findViewById(R.id.title_length);
        content_length = (TextView) findViewById(R.id.content_length);
        btn_result = (Button) findViewById(R.id.btn_result);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_lock = (Button) findViewById(R.id.btn_lock);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        intent = getIntent();
        count = intent.getStringExtra("count");
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");
        key = intent.getStringExtra("key");
        update = intent.getBooleanExtra("update", false);
        lock = intent.getBooleanExtra("lock", true);

        if (update) {
            title_edit.setText(title);
            title_length.setText(title_edit.getText().toString().length() + " / 30");
            content_edit.setText(contents);
            content_length.setText(content_edit.getText().toString().length() + " / 1000");
            btn_result.setVisibility(View.INVISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);
            if (!lock) {
                btn_lock.setBackgroundResource(R.drawable.lock_close);
            }
        }

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(title_edit.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(content_edit.getWindowToken(), 0);
                if (lock) {
                    btn_lock.setBackgroundResource(R.drawable.lock_close);
                    lock = false;
                } else {
                    btn_lock.setBackgroundResource(R.drawable.lock_open);
                    lock = true;
                }
            }
        });


        content_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                content_length.setText(content_edit.getText().toString().length() + " / 1000");
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
                title_length.setText(title_edit.getText().toString().length() + " / 30");
            }
        });

        btn_update.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_delete.setEnabled(false);
                        btn_update.setEnabled(false);
                        imm.hideSoftInputFromWindow(title_edit.getWindowToken(), 0);
                        imm.hideSoftInputFromWindow(content_edit.getWindowToken(), 0);
                        btn_result.performClick();
                    }
                });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(title_edit.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(content_edit.getWindowToken(), 0);
                deleteNovel();
            }
        });

        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(title_edit.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(content_edit.getWindowToken(), 0);
                if (title_edit.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else if (content_edit.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else if (update) {
                    updateNovel();
                } else {
                    insertNovel();
                }
            }
        });
    }

    void deleteNovel() {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NovelModel.Novel novel = snapshot.getValue(NovelModel.Novel.class);
                    if (novel.count.equals(count)) {
                        String key2 = snapshot.getKey();
                        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").child(key2).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, intent);
                                finish();
                                overridePendingTransition(R.anim.fromright, R.anim.toleft);
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

    void updateNovel() {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NovelModel.Novel novel = snapshot.getValue(NovelModel.Novel.class);
                    if (novel.count.equals(count)) {
                        String key2 = snapshot.getKey();
                        Map<String, Object> update = new HashMap<>();
                        update.put("novel_content", content_edit.getText().toString());
                        update.put("novel_title", title_edit.getText().toString());
                        update.put("open", lock);
                        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").child(key2).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, intent);
                                finish();
                                overridePendingTransition(R.anim.fromright, R.anim.toleft);
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

    void insertNovel() {
        NovelModel.Novel novel = new NovelModel.Novel();
        novel.novel_content = content_edit.getText().toString();
        novel.open = lock;
        novel.view = "0";
        novel.novel_title = title_edit.getText().toString();
        novel.count = count;
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").push().setValue(novel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String, Object> update = new HashMap<>();
                update.put("date", ServerValue.TIMESTAMP);
                FirebaseDatabase.getInstance().getReference().child("novels").child(key).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.fromright, R.anim.toleft);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }
}
