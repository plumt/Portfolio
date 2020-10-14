package com.e.note;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.note.alert.MakeNoteActivity;
import com.e.note.alert.NotePasswordActivity;
import com.e.note.alert.UpdateNoteActivity;
import com.e.note.model.MemoModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NoteActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private AdView mAdView;
    TextView note_count;
    int pass = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        recyclerView = (RecyclerView) findViewById(R.id.note_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
        note_count = (TextView) findViewById(R.id.note_count);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<MemoModel> notes = new ArrayList<>();

        public RecyclerViewAdapter() {
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).orderByChild("user/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notes.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        notes.add(snapshot.getValue(MemoModel.class));
                    }
                    if (notes.size() == 0) {
                        note_count.setText("0");
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            Map<String, MemoModel.Memo> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(notes.get(position).memos);
            note_count.setText(String.valueOf(notes.size()));

            customViewHolder.notetext.setText(notes.get(position).name);
            if (notes.get(position).password == null) {
                customViewHolder.notecount.setText(String.valueOf(notes.get(position).memos.size()));
            } else {
                customViewHolder.notecount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.note_lock, 0);
            }

            switch (notes.get(position).color) {
                case "1":
                    customViewHolder.itemView.setBackgroundResource(R.drawable.note_red);
                    break;
                case "2":
                    customViewHolder.itemView.setBackgroundResource(R.drawable.note_orange);
                    break;
                case "3":
                    customViewHolder.itemView.setBackgroundResource(R.drawable.note_yellow);
                    break;
                case "4":
                    customViewHolder.itemView.setBackgroundResource(R.drawable.note_green);
                    break;
                case "5":
                    customViewHolder.itemView.setBackgroundResource(R.drawable.note_sky);
                    break;
                case "6":
                    customViewHolder.itemView.setBackgroundResource(R.drawable.note_blue);
                    break;
                case "7":
                    customViewHolder.itemView.setBackgroundResource(R.drawable.note_purple);
                    break;
            }

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pass = 0;
                    if (notes.get(position).password != null) {
                        Intent intent = new Intent(v.getContext(), NotePasswordActivity.class);
                        intent.putExtra("name", notes.get(position).name);
                        intent.putExtra("key", notes.get(position).key);
                        intent.putExtra("pass", notes.get(position).password);
                        startActivityForResult(intent, 2);
                    } else {
                        Intent intent = new Intent(v.getContext(), MemoActivity.class);
                        intent.putExtra("name", notes.get(position).name);
                        intent.putExtra("key", notes.get(position).key);
                        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromleft, R.anim.toright);
                        startActivity(intent, activityOptions.toBundle());
                        finish();
                    }
                }
            });

            customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    pass = 0;
                    Intent intent = new Intent(v.getContext(), UpdateNoteActivity.class);
                    intent.putExtra("name", notes.get(position).name);
                    intent.putExtra("key", notes.get(position).key);
                    intent.putExtra("pass", notes.get(position).password);
                    intent.putExtra("choice", Integer.parseInt(notes.get(position).color));
                    startActivityForResult(intent, 1);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView notetext;
            public TextView notecount;

            public CustomViewHolder(View view) {
                super(view);
                notetext = (TextView) view.findViewById(R.id.note_text);
                notecount = (TextView) view.findViewById(R.id.note_count);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.memu_top_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        pass = 0;
        switch (item.getItemId()) {
            case R.id.logout:
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("pushToken").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                        finish();
                    }
                });
                break;

            case R.id.note:
                Intent intent = new Intent(getApplicationContext(), MakeNoteActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean recycle = data.getBooleanExtra("recycle", false);
            if (recycle) {
                recyclerView.setAdapter(new RecyclerViewAdapter());
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            String name = data.getStringExtra("name");
            String key = data.getStringExtra("key");
            Intent go = new Intent(getApplicationContext(), MemoActivity.class);
            go.putExtra("key", key);
            go.putExtra("name", name);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
            startActivity(go, activityOptions.toBundle());
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (pass == 0) {
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            pass = 1;
        } else if (pass == 1) {
            finish();
            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
        }
    }
}
