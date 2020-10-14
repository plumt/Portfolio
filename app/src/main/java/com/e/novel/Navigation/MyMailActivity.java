package com.e.novel.Navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.novel.Mail.BancedActivity;
import com.e.novel.Mail.MailViewActivity;
import com.e.novel.Model.MailModel;
import com.e.novel.Model.UserModel;
import com.e.novel.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MyMailActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    TextView mail_count;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private AdView mAdView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mail);
        mail_count = (TextView) findViewById(R.id.mail_count);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyMailActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<MailModel.Mail> mails = new ArrayList<>();
        private List<String> keys = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("massage").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mails.clear();
                    keys.clear();
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mails.add(snapshot.getValue(MailModel.Mail.class));
                        keys.add(snapshot.getKey());
                    }
                    notifyDataSetChanged();
                    if (mails.size() == 0) {
                        mail_count.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;

            mail_count.setText(String.valueOf(mails.size()));

            FirebaseDatabase.getInstance().getReference().child("users").child(mails.get(mails.size() - position - 1).uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    try {
                        customViewHolder.chatitem_nametext.setText(userModel.userName);
                        Glide.with(customViewHolder.itemView.getContext())
                                .load(userModel.profileImageUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(customViewHolder.chatitem_image);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            long unixTime = (long) mails.get(mails.size() - position - 1).date;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            customViewHolder.chatitem_timetext.setText(simpleDateFormat.format(date));
            customViewHolder.chatitem_titleext.setText(mails.get(mails.size() - position - 1).title);

            if (mails.get(mails.size() - position - 1).read) {
                customViewHolder.circle.setVisibility(View.INVISIBLE);
                customViewHolder.chatitem_timetext.setTextColor(Color.argb(35, 0, 0, 0));
                customViewHolder.chatitem_titleext.setTextColor(Color.argb(35, 0, 0, 0));
                customViewHolder.chatitem_nametext.setTextColor(Color.argb(35, 0, 0, 0));

            } else {
                customViewHolder.circle.setVisibility(View.VISIBLE);
            }

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customViewHolder.circle.setVisibility(View.INVISIBLE);
                    customViewHolder.chatitem_timetext.setTextColor(Color.argb(35, 0, 0, 0));
                    customViewHolder.chatitem_titleext.setTextColor(Color.argb(35, 0, 0, 0));
                    customViewHolder.chatitem_nametext.setTextColor(Color.argb(35, 0, 0, 0));
                    read(keys.get(keys.size() - position - 1), mails.get(mails.size() - position - 1).uid, mails.get(mails.size() - position - 1).massage, mails.get(mails.size() - position - 1).title);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mails.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView chatitem_timetext;
            public TextView chatitem_titleext;
            public TextView circle;
            public TextView chatitem_nametext;
            public ImageView chatitem_image;

            public CustomViewHolder(View view) {
                super(view);
                chatitem_timetext = (TextView) view.findViewById(R.id.chatitem_timetext);
                chatitem_titleext = (TextView) view.findViewById(R.id.chatitem_titleext);
                circle = (TextView) view.findViewById(R.id.circle);
                chatitem_nametext = (TextView) view.findViewById(R.id.chatitem_nametext);
                chatitem_image = (ImageView) view.findViewById(R.id.chatitem_image);
            }
        }
    }

    void read(final String key, final String uid, final String massage, final String title) {
        Map<String, Object> update = new HashMap<>();
        update.put("read", true);
        FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("massage").child(key).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent mailView = new Intent(getApplicationContext(), MailViewActivity.class);
                mailView.putExtra("uid", uid);
                mailView.putExtra("massage", massage);
                mailView.putExtra("key", key);
                mailView.putExtra("title", title);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fab_open, R.anim.fab_close);
                startActivityForResult(mailView, 1, activityOptions.toBundle());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.banced:
                Intent new_novel = new Intent(getApplicationContext(), BancedActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivityForResult(new_novel, 1, activityOptions.toBundle());
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
