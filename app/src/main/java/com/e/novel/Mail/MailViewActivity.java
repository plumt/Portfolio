package com.e.novel.Mail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.novel.Model.MailModel;
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

public class MailViewActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    TextView massage_txt, title_warning, mail_warning, title_txt;
    Button delete_btn, bance_btn, answer_btn, cancle, result;
    EditText mail_title, mail_comment;
    Intent intent;
    String uid, key, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_view);
        massage_txt = (TextView) findViewById(R.id.massage_txt);
        title_txt = (TextView) findViewById(R.id.title_txt);
        title_warning = (TextView) findViewById(R.id.title_warning);
        mail_warning = (TextView) findViewById(R.id.mail_warning);
        mail_title = (EditText) findViewById(R.id.mail_title);
        mail_comment = (EditText) findViewById(R.id.mail_comment);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        bance_btn = (Button) findViewById(R.id.bance_btn);
        answer_btn = (Button) findViewById(R.id.answer_btn);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        intent = getIntent();
        massage_txt.setMovementMethod(new ScrollingMovementMethod());
        massage_txt.setText(intent.getStringExtra("massage"));
        title_txt.setMovementMethod(new ScrollingMovementMethod());
        title_txt.setText(intent.getStringExtra("title"));
        uid = intent.getStringExtra("uid");
        key = intent.getStringExtra("key");

        mail_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mail_title.getText().toString().trim().length() < 1) {
                    mail_title.setBackgroundResource(R.drawable.edittext_red);
                    title_warning.setVisibility(View.VISIBLE);
                } else {
                    mail_title.setBackgroundResource(R.drawable.edittext_login);
                    title_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        mail_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mail_comment.getText().toString().trim().length() < 1) {
                    mail_comment.setBackgroundResource(R.drawable.edittext_red);
                    mail_warning.setVisibility(View.VISIBLE);
                } else {
                    mail_comment.setBackgroundResource(R.drawable.edittext_login);
                    mail_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        bance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banced();
            }
        });

        answer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mail_comment.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mail_title.getWindowToken(), 0);
                linearLayout.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);
                mail_title.setText("");
                mail_comment.setText("");
                mail_title.setBackgroundResource(R.drawable.edittext_login);
                mail_comment.setBackgroundResource(R.drawable.edittext_login);
                title_warning.setVisibility(View.INVISIBLE);
                mail_warning.setVisibility(View.INVISIBLE);
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mail_comment.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mail_title.getWindowToken(), 0);
                if (mail_title.getText().toString().trim().length() < 1) {
                    mail_title.setBackgroundResource(R.drawable.edittext_red);
                    title_warning.setVisibility(View.VISIBLE);
                } else if (mail_comment.getText().toString().trim().length() < 1) {
                    mail_comment.setBackgroundResource(R.drawable.edittext_red);
                    mail_warning.setVisibility(View.VISIBLE);
                } else {
                    sendMail();
                }
            }
        });
    }

    void sendMail() {
        FirebaseDatabase.getInstance().getReference().child("mails").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MailModel mailModels = dataSnapshot.getValue(MailModel.class);
                if (mailModels != null && mailModels.banced != null && mailModels.banced.containsKey(myUid)) {
                    Toast.makeText(getApplicationContext(), "쪽지를 보낼 수 없습니다", Toast.LENGTH_SHORT).show();
                    cancle.performClick();
                } else {
                    MailModel.Mail mailModel = new MailModel.Mail();
                    mailModel.date = ServerValue.TIMESTAMP;
                    mailModel.read = false;
                    mailModel.uid = myUid;
                    mailModel.massage = mail_comment.getText().toString().trim();
                    mailModel.title = mail_title.getText().toString().trim();
                    FirebaseDatabase.getInstance().getReference().child("mails").child(uid).child("massage").push().setValue(mailModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "상대에게 쪽지를 보냈습니다", Toast.LENGTH_SHORT).show();
                            cancle.performClick();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void delete() {
        FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("massage").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "쪽지를 삭제했습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });
    }

    void banced() {
        Map<String, Object> update = new HashMap<>();
        update.put(uid, true);
        FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("banced").updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("massage").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MailModel.Mail mailModel = snapshot.getValue(MailModel.Mail.class);
                            if (mailModel.uid.equals(uid)) {
                                String key2 = snapshot.getKey();
                                FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("massage").child(key2).removeValue();
                            }
                        }
                        Toast.makeText(getApplicationContext(), "상대를 차단했습니다", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}
