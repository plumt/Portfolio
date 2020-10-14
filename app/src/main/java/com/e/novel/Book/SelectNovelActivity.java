package com.e.novel.Book;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.novel.Alert.WriterProfileActivity;
import com.e.novel.Model.NovelModel;
import com.e.novel.R;
import com.e.novel.myNovel.BookListActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectNovelActivity extends AppCompatActivity {

    ImageView like_btn, image, profile;
    TextView title_txt, like_txt, view_txt, comment_txt, weektxt;
    public RecyclerView recyclerView;
    private AdView mAdView;
    String key, title, category, uid, comment, like, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Intent intent;
    boolean like_click = false;
    Integer[] numBtnIds = {R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat, R.id.sun, R.id.random};
    TextView[] numButtons = new TextView[8];
    boolean[] numcehck = new boolean[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_novel);
        title_txt = (TextView) findViewById(R.id.title);
        comment_txt = (TextView) findViewById(R.id.comment);
        like_txt = (TextView) findViewById(R.id.like_txt);
        view_txt = (TextView) findViewById(R.id.view_txt);
        weektxt = (TextView) findViewById(R.id.weektxt);
        like_btn = (ImageView) findViewById(R.id.like_image);
        image = (ImageView) findViewById(R.id.image);
        profile = (ImageView) findViewById(R.id.profile);

        intent = getIntent();
        key = intent.getStringExtra("key");
        title = intent.getStringExtra("title");
        comment = intent.getStringExtra("comment");
        category = intent.getStringExtra("category");
        uid = intent.getStringExtra("uid");

        for (int i = 0; i < numBtnIds.length; i++) {
            numButtons[i] = (TextView) findViewById(numBtnIds[i]);
            numcehck[i] = false;
        }

        title_txt.setText(title);
        comment_txt.setText("˝ " + comment + " ˝");
        comment_txt.setMovementMethod(new ScrollingMovementMethod());

        switch (category) {
            case "fantasy":
                image.setImageResource(R.drawable.books1);
                break;
            case "sf":
                image.setImageResource(R.drawable.books7);
                break;
            case "game":
                image.setImageResource(R.drawable.books3);
                break;
            case "drama":
                image.setImageResource(R.drawable.books4);
                break;
            case "detective":
                image.setImageResource(R.drawable.books5);
                break;
            case "mystery":
                image.setImageResource(R.drawable.books6);
                break;
            case "romance":
                image.setImageResource(R.drawable.books2);
                break;
            case "heroism":
                image.setImageResource(R.drawable.books8);
                break;
            case "comic":
                image.setImageResource(R.drawable.books9);
                break;
        }

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setting();

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like_click) {
                    clickLike(-1);
                    like_click = false;
                    like_btn.setBackgroundResource(R.drawable.heart);
                } else {
                    clickLike(+1);
                    like_click = true;
                    like_btn.setBackgroundResource(R.drawable.heart2);
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent write_profile = new Intent(getApplicationContext(), WriterProfileActivity.class);
                write_profile.putExtra("uid", uid);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fab_open, R.anim.fab_close);
                startActivity(write_profile, activityOptions.toBundle());
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectNovelActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
    }

    void clickLike(final int num) {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NovelModel novelModel = dataSnapshot.getValue(NovelModel.class);
                int count = Integer.parseInt(novelModel.like) + num;
                like_txt.setText(String.valueOf(count));
                Map<String, Object> update = new HashMap<>();
                update.put("like", String.valueOf(count));
                FirebaseDatabase.getInstance().getReference().child("novels").child(key).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (num == -1) {
                            FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("likeUser").child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "작품 추천을 취소하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Map<String, Object> update2 = new HashMap<>();
                            update2.put(myUid, true);
                            FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("likeUser").updateChildren(update2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "작품을 추천하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NovelModel novelModel = dataSnapshot.getValue(NovelModel.class);
                like = novelModel.like;
                like_txt.setText(like);
                if (novelModel.likeUser.containsKey(myUid)) {
                    like_click = true;
                    like_btn.setBackgroundResource(R.drawable.heart2);
                } else {
                    like_click = false;
                    like_btn.setBackgroundResource(R.drawable.heart);
                }

                if (novelModel.finish) {
                    weektxt.setText("완결");
                    for (int i = 0; i < 8; i++) {
                        numButtons[i].setVisibility(View.INVISIBLE);
                    }
                } else {
                    weektxt.setText("연재 주기 : ");
                    String[] weeks = {"월", "화", "수", "목", "금", "토", "일", "자유"};
                    for (int i = 0; i < 8; i++) {
                        if (novelModel.week.containsKey(weeks[i])) {
                            numButtons[i].setBackgroundResource(R.drawable.text_round2);
                            numButtons[i].setTextColor(Color.BLACK);
                            numcehck[i] = true;
                        } else {
                            numcehck[i] = false;
                            numButtons[i].setBackgroundResource(R.drawable.text_round1);
                            numButtons[i].setTextColor(Color.parseColor("#5A000000"));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void addView(final String count, final String book_title, final String book_content) {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NovelModel.Novel novel = snapshot.getValue(NovelModel.Novel.class);
                    final String key2;
                    if (novel.count.equals(count)) {
                        key2 = snapshot.getKey();
                        FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                NovelModel.Novel views = dataSnapshot.getValue(NovelModel.Novel.class);
                                int view = Integer.parseInt(views.view) + 1;
                                Map<String, Object> update = new HashMap<>();
                                update.put("view", String.valueOf(view));
                                FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").child(key2).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent book = new Intent(getApplicationContext(), BookViewActivity.class);
                                        book.putExtra("title", book_title);
                                        book.putExtra("content", book_content);
                                        startActivityForResult(book, 1);
                                    }
                                });
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

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NovelModel.Novel> novels = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    novels.clear();
                    int view = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NovelModel.Novel novel = snapshot.getValue(NovelModel.Novel.class);
                        if (novel.open) {
                            if (novel.view != null) {
                                view += Integer.parseInt(novel.view);
                            }
                            novels.add(snapshot.getValue(NovelModel.Novel.class));
                        }
                    }
                    Map<String, Object> update = new HashMap<>();
                    update.put("view", String.valueOf(view));
                    FirebaseDatabase.getInstance().getReference().child("novels").child(key).updateChildren(update);
                    view_txt.setText(String.valueOf(view));
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;

            customViewHolder.title.setText(novels.get(novels.size() - position - 1).novel_title);
            customViewHolder.count.setText("#" + novels.get(novels.size() - position - 1).count);
            customViewHolder.view_txt.setText(novels.get(novels.size() - position - 1).view);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!uid.equals(myUid)) {
                        addView(novels.get(novels.size() - position - 1).count, novels.get(novels.size() - position - 1).novel_title, novels.get(novels.size() - position - 1).novel_content);
                    } else {
                        Intent book = new Intent(getApplicationContext(), BookViewActivity.class);
                        book.putExtra("title", novels.get(novels.size() - position - 1).novel_title);
                        book.putExtra("content", novels.get(novels.size() - position - 1).novel_content);
                        startActivityForResult(book, 1);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return novels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView count;
            public ImageView bookimage;
            public TextView view_txt;
            public ImageView view_image;
            public TextView like_txt;
            public ImageView like_image;


            public CustomViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                count = (TextView) view.findViewById(R.id.count);
                view_txt = (TextView) view.findViewById(R.id.view_txt);
                like_txt = (TextView) view.findViewById(R.id.like_txt);
                bookimage = (ImageView) view.findViewById(R.id.bookimage);
                view_image = (ImageView) view.findViewById(R.id.view_image);
                like_image = (ImageView) view.findViewById(R.id.like_image);
                bookimage.setVisibility(View.GONE);
                like_image.setVisibility(View.GONE);
                like_txt.setVisibility(View.GONE);
                count.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            recyclerView.setAdapter(new RecyclerViewAdapter());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }
}
