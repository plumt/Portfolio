package com.e.translation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private AdView mAdView;
    DBHelper myHelper;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        myHelper = new DBHelper(this);
        sqlDB = myHelper.getReadableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<historyModel> transhistory = new ArrayList<>();

        public RecyclerViewAdapter() {
            Cursor cursor;
            myHelper = new DBHelper(getApplicationContext());
            sqlDB = myHelper.getReadableDatabase();
            cursor = sqlDB.rawQuery("SELECT *FROM history;", null);
            while (cursor.moveToNext()) {
                historyModel historyModel = new historyModel();
                historyModel.input = cursor.getString(1);
                historyModel.result = cursor.getString(2);
                historyModel.select1 = cursor.getString(3);
                historyModel.select2 = cursor.getString(4);
                transhistory.add(historyModel);
            }
            cursor.close();
            sqlDB.close();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new RecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;
            final String input, result;
            input = transhistory.get(position).input.replace("＇","'");
            result = transhistory.get(position).result.replace("＇","'");
            customViewHolder.inputtxt.setText(input);
            customViewHolder.resulttxt.setText(result);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.putExtra("input", input);
                    intent.putExtra("result", result);
                    intent.putExtra("select1", transhistory.get(position).select1);
                    intent.putExtra("select2", transhistory.get(position).select2);
                    startActivity(intent);
                    finish();
                    // 애니메이션
                }
            });
        }

        @Override
        public int getItemCount() {
            return transhistory.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView inputtxt;
            public TextView resulttxt;

            public CustomViewHolder(View view) {
                super(view);
                inputtxt = (TextView) view.findViewById(R.id.input_txt);
                resulttxt = (TextView) view.findViewById(R.id.result_txt);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();                //애니메이션
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();                //애니메이션
    }
}
