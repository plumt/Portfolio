package com.e.novel.SerchNovels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.novel.Book.SelectNovelActivity;
import com.e.novel.Model.NovelModel;
import com.e.novel.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MoreNovelActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    TextView more_count, title;
    private AdView mAdView;
    Intent intent;
    String more;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_novel);
        more_count = (TextView) findViewById(R.id.more_count);
        title = (TextView) findViewById(R.id.title);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        intent = getIntent();
        more = intent.getStringExtra("more");
        if (more.equals("recently")) {
            title.setText("최신 등록 작품");
        } else if (more.equals("mostlike")) {
            title.setText("최다 좋아요 작품");
        } else if (more.equals("mostview")) {
            title.setText("최다 조회수 작품");
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(MoreNovelActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NovelModel> novels = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("novels").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    novels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NovelModel novelModel = snapshot.getValue(NovelModel.class);
                        if (novelModel.open) {
                            novels.add(snapshot.getValue(NovelModel.class));
                        }
                        if (novels.size() >= 50) {
                            break;
                        }
                    }
                    if (more.equals("recently")) {
                        Collections.sort(novels, comparators1);
                    } else if (more.equals("mostlike")) {
                        Collections.sort(novels, comparators2);
                    } else if (more.equals("mostview")) {
                        Collections.sort(novels, comparators3);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;

            more_count.setText(String.valueOf(novels.size()));

            switch (novels.get(position).category_str) {
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
            String str;
            if(novels.get(position).finish){
                str = "(완) " + novels.get(position).title;
                SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                customViewHolder.title.setText(ssb);
            } else{
                str = "(연) " + novels.get(position).title;
                SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#0000FF")), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                customViewHolder.title.setText(ssb);
            }
            customViewHolder.name.setText(novels.get(position).userName);

            if (more.equals("recently")) {
                long unixTime = (long) novels.get(position).date;
                Date date = new Date(unixTime);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                customViewHolder.date.setText("최근 업데이트 : " + simpleDateFormat.format(date));
            } else if (more.equals("mostlike")) {
                customViewHolder.date.setText("♥ " + novels.get(position).like);
            } else if(more.equals("mostview")){
                customViewHolder.date.setText("조회수 : " + novels.get(position).view);
            }


            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent select = new Intent(getApplicationContext(), SelectNovelActivity.class);
                    select.putExtra("key", novels.get(position).key);
                    select.putExtra("title", novels.get(position).title);
                    select.putExtra("comment", novels.get(position).comment);
                    select.putExtra("category", novels.get(position).category_str);
                    select.putExtra("uid", novels.get(position).myuid);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                    startActivityForResult(select, 1, activityOptions.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return novels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView name;
            public TextView date;
            public ImageView bookimage;

            public CustomViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                name = (TextView) view.findViewById(R.id.name);
                date = (TextView) view.findViewById(R.id.date);
                bookimage = (ImageView) view.findViewById(R.id.bookimage);
            }
        }
    }

    Comparator<NovelModel> comparators1 = new Comparator<NovelModel>() {
        @Override
        public int compare(NovelModel item1, NovelModel item2) {
            return String.valueOf(item2.date).compareTo(String.valueOf(item1.date));
        }
    };

    Comparator<NovelModel> comparators2 = new Comparator<NovelModel>() {
        @Override
        public int compare(NovelModel item1, NovelModel item2) {
            if (Integer.parseInt(item1.like) > Integer.parseInt(item2.like)) {
                return -1;
            } else if (Integer.parseInt(item1.like) < Integer.parseInt(item2.like)) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    Comparator<NovelModel> comparators3 = new Comparator<NovelModel>() {
        @Override
        public int compare(NovelModel item1, NovelModel item2) {
            if (Integer.parseInt(item1.view) > Integer.parseInt(item2.view)) {
                return -1;
            } else if (Integer.parseInt(item1.view) < Integer.parseInt(item2.view)) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }
}
