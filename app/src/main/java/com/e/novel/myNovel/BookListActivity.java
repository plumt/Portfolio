package com.e.novel.myNovel;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.novel.Model.NovelModel;
import com.e.novel.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    String key, novel_title, comment;
    Intent intent;
    TextView title, novel_comment, weektxt;
    private AdView mAdView;
    int count = 1;
    Integer[] numBtnIds = {R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat, R.id.sun, R.id.random};
    TextView[] numButtons = new TextView[8];
    boolean[] numcehck = new boolean[8];
    boolean finished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        intent = getIntent();
        key = intent.getStringExtra("key");
        comment = intent.getStringExtra("comment");
        novel_title = intent.getStringExtra("title");
        finished = intent.getBooleanExtra("finish", false);

        title = (TextView) findViewById(R.id.title);
        weektxt = (TextView) findViewById(R.id.weektxt);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        novel_comment = (TextView) findViewById(R.id.novel_comment);
        novel_comment.setMovementMethod(new ScrollingMovementMethod());
        title.setText(novel_title);
        setting();
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(BookListActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        if (finished) {
            toolbar.setVisibility(View.INVISIBLE);
        }
        if (comment == null) {
            relativeLayout.setVisibility(View.GONE);
        }
        novel_comment.setText(comment);

        for (int i = 0; i < numBtnIds.length; i++) {
            numButtons[i] = (TextView) findViewById(numBtnIds[i]);
            numcehck[i] = false;
        }
    }

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("novels").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NovelModel novelModel = dataSnapshot.getValue(NovelModel.class);
                if (novelModel.finish) {
                    weektxt.setText("완결");
                    for (int i = 0; i < 8; i++) {
                        numButtons[i].setVisibility(View.INVISIBLE);
                    }
                } else {
                    weektxt.setText("연재 주기");
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

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NovelModel.Novel> novels = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("novels").child(key).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    novels.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        novels.add(snapshot.getValue(NovelModel.Novel.class));
                    }
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

            if (novels.size() == 0) {
                count = 1;
            } else {
                count = Integer.parseInt(novels.get(novels.size() - 1).count) + 1;
            }

            customViewHolder.title.setText(novels.get(novels.size() - position - 1).novel_title);
            customViewHolder.count.setText("#" + novels.get(novels.size() - position - 1).count);
            customViewHolder.view_txt.setText(novels.get(novels.size() - position - 1).view);


            if (!novels.get(novels.size() - position - 1).open) {
                customViewHolder.count.setText(customViewHolder.count.getText().toString() + "\n비공개");
                customViewHolder.title.setTextColor(Color.argb(35, 0, 0, 0));
                customViewHolder.count.setTextColor(Color.argb(35, 0, 0, 0));
            }

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent publish = new Intent(getApplicationContext(), PublishActivity.class);
                    publish.putExtra("key", key);
                    publish.putExtra("title", novels.get(novels.size() - position - 1).novel_title);
                    publish.putExtra("contents", novels.get(novels.size() - position - 1).novel_content);
                    publish.putExtra("count", novels.get(novels.size() - position - 1).count);
                    publish.putExtra("update", true);
                    publish.putExtra("lock", novels.get(novels.size() - position - 1).open);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                    startActivityForResult(publish, 1, activityOptions.toBundle());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.memu_top_novel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.novel:
                Intent publish = new Intent(getApplicationContext(), PublishActivity.class);
                publish.putExtra("key", key);
                publish.putExtra("count", String.valueOf(count));
                startActivityForResult(publish, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
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
