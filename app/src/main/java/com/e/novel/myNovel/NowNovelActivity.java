package com.e.novel.myNovel;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.novel.Alert.CreateNovelActivity;
import com.e.novel.Model.NovelModel;
import com.e.novel.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NowNovelActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    TextView note_count, title;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(), myname;
    private AdView mAdView;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_novel);
        mAdView = findViewById(R.id.adView);
        note_count = (TextView) findViewById(R.id.note_count);
        title = (TextView) findViewById(R.id.title);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        intent = getIntent();
        myname = intent.getStringExtra("name");
        title.setText("연재 작품 관리");
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(NowNovelActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NovelModel> novels = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("novels").orderByChild("uid/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    novels.clear();
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NovelModel novelModel = snapshot.getValue(NovelModel.class);
                        if(!novelModel.finish) {
                            novels.add(novelModel);
                        }
                    }
                    notifyDataSetChanged();
                    if (novels.size() == 0) {
                        note_count.setText("0");
                    }
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

            FirebaseDatabase.getInstance().getReference().child("novels").child(novels.get(novels.size() - position - 1).key).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int view = 0;
                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                        NovelModel.Novel novel = snapshot1.getValue(NovelModel.Novel.class);
                        if (novel.view != null) {
                            view += Integer.parseInt(novel.view);
                        }
                    }
                    customViewHolder.view_txt.setText(String.valueOf(view));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            note_count.setText(String.valueOf(novels.size()));
            switch (novels.get(novels.size() - position - 1).category_str) {
                case "fantasy":
                    customViewHolder.bookimage.setImageResource(R.drawable.books1);
                    break;
                case "sf":
                    customViewHolder.bookimage.setImageResource(R.drawable.books7);
                    break;
                case "game":
                    customViewHolder.bookimage.setImageResource(R.drawable.books3);
                    break;
                case "drama":
                    customViewHolder.bookimage.setImageResource(R.drawable.books4);
                    break;
                case "detective":
                    customViewHolder.bookimage.setImageResource(R.drawable.books5);
                    break;
                case "mystery":
                    customViewHolder.bookimage.setImageResource(R.drawable.books6);
                    break;
                case "romance":
                    customViewHolder.bookimage.setImageResource(R.drawable.books2);
                    break;
                case "heroism":
                    customViewHolder.bookimage.setImageResource(R.drawable.books8);
                    break;
                case "comic":
                    customViewHolder.bookimage.setImageResource(R.drawable.books9);
                    break;
            }
            customViewHolder.title.setText(novels.get(novels.size() - position - 1).title);
            if (!novels.get(novels.size() - position - 1).open) {
                customViewHolder.title.setTextColor(Color.argb(35, 0, 0, 0));
            }

            customViewHolder.like_txt.setText(novels.get(novels.size() - position - 1).like);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent booklist = new Intent(getApplicationContext(), BookListActivity.class);
                    booklist.putExtra("title", novels.get(novels.size() - position - 1).title);
                    booklist.putExtra("key", novels.get(novels.size() - position - 1).key);
                    booklist.putExtra("comment", novels.get(novels.size() - position - 1).comment);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                    startActivity(booklist, activityOptions.toBundle());
                }
            });

            customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent update = new Intent(getApplicationContext(), CreateNovelActivity.class);
                    update.putExtra("name", novels.get(novels.size() - position - 1).userName);
                    update.putExtra("comment", novels.get(novels.size() - position - 1).comment);
                    update.putExtra("title", novels.get(novels.size() - position - 1).title);
                    update.putExtra("category", novels.get(novels.size() - position - 1).category_str);
                    update.putExtra("key", novels.get(novels.size() - position - 1).key);
                    update.putExtra("update", true);
                    update.putExtra("lock", novels.get(novels.size() - position - 1).open);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fab_open, R.anim.fab_close);
                    startActivityForResult(update, 1, activityOptions.toBundle());
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return novels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView bookimage;
            public TextView view_txt;
            public ImageView view_image;
            public TextView like_txt;
            public ImageView like_image;

            public CustomViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                bookimage = (ImageView) view.findViewById(R.id.bookimage);
                view_txt = (TextView) view.findViewById(R.id.view_txt);
                view_image = (ImageView) view.findViewById(R.id.view_image);
                like_txt = (TextView) view.findViewById(R.id.like_txt);
                like_image = (ImageView) view.findViewById(R.id.like_image);
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
                Intent new_novel = new Intent(getApplicationContext(), CreateNovelActivity.class);
                new_novel.putExtra("name", myname);
                startActivityForResult(new_novel, 1);
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
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }
}
