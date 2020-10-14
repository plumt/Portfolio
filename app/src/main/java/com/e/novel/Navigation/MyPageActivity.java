package com.e.novel.Navigation;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.novel.Model.MailModel;
import com.e.novel.Model.NovelModel;
import com.e.novel.Model.UserModel;
import com.e.novel.R;
import com.e.novel.myNovel.FinishNovelActivity;
import com.e.novel.myNovel.NowNovelActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MyPageActivity extends AppCompatActivity {

    RelativeLayout relativeLayout1, relativeLayout2;
    Button insert_writer, btn_now_novel, btn_finish_novel;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(), myname;
    TextView profile_name, profile_comment, like_txt, fan_txt, view_txt, mail_txt;
    ImageView profile_image;
    int total_view = 0, likes = 0, mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        relativeLayout1 = (RelativeLayout) findViewById(R.id.relative1);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relative2);
        insert_writer = (Button) findViewById(R.id.insert_writer);
        btn_now_novel = (Button) findViewById(R.id.btn_now_novel);
        btn_finish_novel = (Button) findViewById(R.id.btn_finish_novel);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_comment = (TextView) findViewById(R.id.profile_comment);
        like_txt = (TextView) findViewById(R.id.like_txt);
        fan_txt = (TextView) findViewById(R.id.fan_txt);
        view_txt = (TextView) findViewById(R.id.view_txt);
        mail_txt = (TextView) findViewById(R.id.mail_txt);

        insert_writer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertWriter();
            }
        });


        btn_now_novel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent now_novel = new Intent(getApplicationContext(), NowNovelActivity.class);
                now_novel.putExtra("name", myname);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivityForResult(now_novel, 1, activityOptions.toBundle());
            }
        });

        btn_finish_novel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent now_novel = new Intent(getApplicationContext(), FinishNovelActivity.class);
                now_novel.putExtra("name", myname);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivityForResult(now_novel, 1, activityOptions.toBundle());
            }
        });

        mail_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mail > 0) {
                    Intent mailactivity = new Intent(getApplicationContext(), MyMailActivity.class);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                    startActivityForResult(mailactivity, 1, activityOptions.toBundle());
                }
            }
        });
        setting1();
        setting2();
    }

    void profile() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    fan_txt.setText(String.valueOf(userModel.fan.size()) + "명");
                    btn_now_novel.setText("연재 작품 ( " + userModel.now_write.size() + " 개 )");
                    btn_finish_novel.setText("완결 작품 ( " + userModel.finish_write.size() + " 개 )");
                    profile_name.setText(userModel.userName);
                    Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                    if (userModel.comment != null) {
                        profile_comment.setText("˝ " + userModel.comment + " ˝");
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void profile_writer() {
        total_view = likes = 0;
        FirebaseDatabase.getInstance().getReference().child("novels").orderByChild("uid/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NovelModel novelModel = snapshot.getValue(NovelModel.class);
                    if (novelModel.like != null) {
                        likes += Integer.parseInt(novelModel.like);
                    }
                    String key = snapshot.getKey();
                    FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                NovelModel.Novel novel = snapshot1.getValue(NovelModel.Novel.class);
                                if (novel.view != null) {
                                    total_view += Integer.parseInt(novel.view);
                                }
                            }
                            view_txt.setText(total_view + "회");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                like_txt.setText(likes + "개");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void insertWriter() {
        Map<String, Object> put_writer = new HashMap<>();
        put_writer.put("writer", true);
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(put_writer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();
                relativeLayout1.setVisibility(View.GONE);
                relativeLayout2.setVisibility(View.VISIBLE);
                profile();
                profile_writer();
            }
        });
    }

    void setting1() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                myname = userModel.userName;
                if (userModel.writer) {
                    relativeLayout2.setVisibility(View.VISIBLE);
                    profile();
                    profile_writer();
                } else {
                    relativeLayout1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setting2() {
        FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("massage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mail = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MailModel.Mail mailModel = snapshot.getValue(MailModel.Mail.class);
                    if (!mailModel.read) {
                        mail++;
                    }
                }
                mail_txt.setText(mail + "개");
                if (mail > 0) {
                    mail_txt.setPaintFlags(mail_txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    mail_txt.setPaintFlags(0);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setting1();
            setting2();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }
}
