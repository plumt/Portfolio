package com.e.novel.Navigation;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.novel.Book.SelectNovelActivity;
import com.e.novel.Model.NovelModel;
import com.e.novel.R;
import com.google.firebase.auth.FirebaseAuth;
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

public class LikeActivity extends Fragment {
    public RecyclerView recyclerView;
    TextView like_count;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd");
    private OnFragmentInteractionListener mListener;

    public LikeActivity() {
    }

    public static LikeActivity newInstance(String param1, String param2) {
        LikeActivity fragment = new LikeActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_like, container, false);
        like_count = (TextView) view.findViewById(R.id.like_count);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerViewAdapter());

        return view;
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NovelModel> novels = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("novels").orderByChild("likeUser/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    novels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NovelModel novel = snapshot.getValue(NovelModel.class);
                        if (novel.open) {
                            novels.add(novel);
                        }
                    }
                    Collections.sort(novels, comparators);
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
            like_count.setText(String.valueOf(novels.size()));

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
            customViewHolder.title.setText(novels.get(position).title);
            customViewHolder.name.setText(novels.get(position).userName);

            long unixTime = (long) novels.get(position).date;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            customViewHolder.date.setText("최근 업데이트 : " + simpleDateFormat.format(date));

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent select = new Intent(getContext(), SelectNovelActivity.class);
                    select.putExtra("key", novels.get(position).key);
                    select.putExtra("title", novels.get(position).title);
                    select.putExtra("comment", novels.get(position).comment);
                    select.putExtra("category", novels.get(position).category_str);
                    select.putExtra("uid", novels.get(position).myuid);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fromleft, R.anim.toright);
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

    Comparator<NovelModel> comparators = new Comparator<NovelModel>() {
        @Override
        public int compare(NovelModel item1, NovelModel item2) {
            return String.valueOf(item2.date).compareTo(String.valueOf(item1.date));
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
