package com.e.novel.Alert;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.novel.Model.MailModel;
import com.e.novel.Model.NovelModel;
import com.e.novel.Model.UserModel;
import com.e.novel.R;
import com.e.novel.SerchNovels.MoreWriterActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WriterProfileActivity extends AppCompatActivity {

    RelativeLayout relativeLayout1, relativeLayout2;
    LinearLayout linear2;
    ImageView profile_image, image;
    TextView like_txt, view_txt, fan_txt, profile_name, profile_comment, title_warning, mail_warning, title;
    ImageButton add_btn, mail_btn, minus_btn;
    Button result, cancle;
    EditText mail_title, mail_comment;
    Intent intent;
    String uid, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    int like = 0, view = 0;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_profile);
        relativeLayout1 = (RelativeLayout) findViewById(R.id.relative1);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relative2);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        image = (ImageView) findViewById(R.id.image);
        like_txt = (TextView) findViewById(R.id.like_txt);
        view_txt = (TextView) findViewById(R.id.view_txt);
        fan_txt = (TextView) findViewById(R.id.fan_txt);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_comment = (TextView) findViewById(R.id.profile_comment);
        title_warning = (TextView) findViewById(R.id.title_warning);
        mail_warning = (TextView) findViewById(R.id.mail_warning);
        title = (TextView) findViewById(R.id.title);
        add_btn = (ImageButton) findViewById(R.id.add_btn);
        mail_btn = (ImageButton) findViewById(R.id.mail_btn);
        minus_btn = (ImageButton) findViewById(R.id.minus_btn);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        mail_title = (EditText) findViewById(R.id.mail_title);
        mail_comment = (EditText) findViewById(R.id.mail_comment);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        intent = getIntent();
        uid = intent.getStringExtra("uid");

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

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mail_title.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mail_comment.getWindowToken(), 0);
                image.setVisibility(View.VISIBLE);
                title.setText("작가 프로필");
                relativeLayout1.setVisibility(View.VISIBLE);
                relativeLayout2.setVisibility(View.GONE);
                mail_title.setText("");
                mail_comment.setText("");
                mail_title.setBackgroundResource(R.drawable.edittext_login);
                mail_comment.setBackgroundResource(R.drawable.edittext_login);
                title_warning.setVisibility(View.INVISIBLE);
                mail_warning.setVisibility(View.INVISIBLE);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fan(true);
            }
        });
        minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fan(false);
            }
        });

        mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setVisibility(View.INVISIBLE);
                title.setText("쪽지 보내기");
                relativeLayout1.setVisibility(View.GONE);
                relativeLayout2.setVisibility(View.VISIBLE);
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mail_title.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mail_comment.getWindowToken(), 0);
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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moreNovel = new Intent(getApplicationContext(), MoreWriterActivity.class);
                moreNovel.putExtra("uid", uid);
                moreNovel.putExtra("name", profile_name.getText().toString().trim());
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(moreNovel, activityOptions.toBundle());
                finish();
            }
        });

        setting1();
        setting2();
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
                            Toast.makeText(getApplicationContext(), "작가에게 쪽지를 보냈습니다", Toast.LENGTH_SHORT).show();
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

    void fan(boolean f) {
        if (f) {
            Map<String, Object> update = new HashMap<>();
            update.put(myUid, true);
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("fan").updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "팬으로 등록하였습니다", Toast.LENGTH_SHORT).show();
                    setting1();
                    add_btn.setClickable(false);
                    minus_btn.setClickable(true);
                    add_btn.setVisibility(View.GONE);
                    minus_btn.setVisibility(View.VISIBLE);
                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("fan").child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "팬 등록을 취소하였습니다", Toast.LENGTH_SHORT).show();
                    setting1();
                    add_btn.setClickable(true);
                    minus_btn.setClickable(false);
                    add_btn.setVisibility(View.VISIBLE);
                    minus_btn.setVisibility(View.GONE);
                }
            });
        }
    }

    void setting1() {
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    if (myUid.equals(uid)) {
                        linear2.setVisibility(View.GONE);
                    } else if (!userModel.fan.containsKey(myUid)) {
                        linear2.setVisibility(View.VISIBLE);
                        add_btn.setClickable(true);
                        minus_btn.setClickable(false);
                        add_btn.setVisibility(View.VISIBLE);
                        minus_btn.setVisibility(View.GONE);
                    } else {
                        linear2.setVisibility(View.VISIBLE);
                        add_btn.setClickable(false);
                        minus_btn.setClickable(true);
                        add_btn.setVisibility(View.GONE);
                        minus_btn.setVisibility(View.VISIBLE);
                    }
                    profile_name.setText(userModel.userName);
                    profile_comment.setText("˝ " + userModel.comment + " ˝");
                    fan_txt.setText(userModel.fan.size() + "명");
                    Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setting2() {
        FirebaseDatabase.getInstance().getReference().child("novels").orderByChild("uid/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                like = view = 0;
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NovelModel novelModel = snapshot.getValue(NovelModel.class);
                    if (novelModel.like != null) {
                        like += Integer.parseInt(novelModel.like);
                    }
                    if (novelModel.view != null) {
                        view += Integer.parseInt(novelModel.view);
                    }
                }
                like_txt.setText(like + "개");
                view_txt.setText(view + "회");
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
