package com.e.note;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.note.alert.DeleteMemoActivity;
import com.e.note.alert.MemoPasswordActivity;
import com.e.note.model.MemoModel;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemoActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private AdView mAdView;
    Context context;
    int count = 0;
    FlexboxLayout flexboxLayout;
    TextView titlename;
    Intent intent;
    String key, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        mAdView = findViewById(R.id.adView);
        titlename = (TextView) findViewById(R.id.title_name);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        context = this;
        flexboxLayout = (FlexboxLayout) findViewById(R.id.flexboxLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        titlename.setText(intent.getStringExtra("name"));
        key = intent.getStringExtra("key");
        recyclerView = (RecyclerView) findViewById(R.id.memo_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(MemoActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<MemoModel.Memo> memos = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).child("memos").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    memos.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        memos.add(snapshot.getValue(MemoModel.Memo.class));
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
            return new RecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;
            createButton(memos.get(position).memo, memos.get(position).date, memos.get(position).password);
            customViewHolder.memotext.setText(memos.get(position).memo);
        }

        @Override
        public int getItemCount() {
            return memos.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView memotext;

            public CustomViewHolder(View view) {
                super(view);
                memotext = (TextView) view.findViewById(R.id.memo_text);
            }
        }
    }

    void createButton(final String txt, final Object date, final String pass) {
        final Button btn = new Button(context);
        btn.setPadding(50, 130, 50, 50);

        if (pass == null) {
            btn.setText(txt);
        } else {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.lock);
        }
        btn.setPadding(0, 0, 0, 150);

        btn.setMaxLines(5);

        LinearLayout.LayoutParams topButton = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
            topButton.width = size.x / 2 - 20;
            topButton.height = size.y / 5;
        } else {
            topButton.width = 400;
            topButton.height = 400;
        }
        count++;
        btn.setBackgroundResource(R.drawable.memo_btn);
        btn.setLayoutParams(topButton);
        flexboxLayout.addView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass != null) {
                    Intent intent = new Intent(getApplicationContext(), MemoPasswordActivity.class);
                    intent.putExtra("pass", pass);
                    intent.putExtra("memo", txt);
                    intent.putExtra("date", String.valueOf(date));
                    intent.putExtra("write", false);
                    intent.putExtra("first", false);
                    startActivityForResult(intent, 2);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MakeMemoActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("name", titlename.getText().toString());
                    intent.putExtra("memo", txt);
                    intent.putExtra("date", String.valueOf(date));
                    intent.putExtra("write", false);
                    intent.putExtra("first", false);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                    startActivity(intent, activityOptions.toBundle());
                    finish();
                }
            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                btn.setEnabled(false);
                Intent delete = new Intent(getApplicationContext(), DeleteMemoActivity.class);
                delete.putExtra("key", key);
                delete.putExtra("pass", pass);
                delete.putExtra("name", titlename.getText().toString());
                delete.putExtra("date", String.valueOf(date));
                startActivityForResult(delete, 1);
                btn.setEnabled(true);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_top_memo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent note = new Intent(getApplicationContext(), NoteActivity.class);
                ActivityOptions activityOptions1 = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
                startActivity(note, activityOptions1.toBundle());
                finish();
                break;

            case R.id.memo:
                Intent intent = new Intent(getApplicationContext(), MakeMemoActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("name", titlename.getText().toString());
                intent.putExtra("write", true);
                intent.putExtra("first", true);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(intent, activityOptions.toBundle());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean del = data.getBooleanExtra("del", false);
            if (del) {
                finish();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            String pass = data.getStringExtra("pass");
            String date = data.getStringExtra("date");
            String memo = data.getStringExtra("memo");
            boolean write = data.getBooleanExtra("write", false);
            boolean first = data.getBooleanExtra("first", false);
            Intent go = new Intent(getApplicationContext(), MakeMemoActivity.class);
            go.putExtra("key", key);
            go.putExtra("name", titlename.getText().toString());
            go.putExtra("pass", pass);
            go.putExtra("memo", memo);
            go.putExtra("date", date);
            go.putExtra("write", write);
            go.putExtra("first", first);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
            startActivity(go, activityOptions.toBundle());
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent note = new Intent(getApplicationContext(), NoteActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
        startActivity(note, activityOptions.toBundle());
        finish();
    }
}
