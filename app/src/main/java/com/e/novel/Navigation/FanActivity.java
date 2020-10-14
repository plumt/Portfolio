package com.e.novel.Navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.novel.Alert.WriterProfileActivity;
import com.e.novel.Model.UserModel;
import com.e.novel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FanActivity extends Fragment {
    public RecyclerView recyclerView;
    TextView fan_count;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd");
    private OnFragmentInteractionListener mListener;

    public FanActivity() {
    }

    public static FanActivity newInstance(String param1, String param2) {
        FanActivity fragment = new FanActivity();
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
        View view = inflater.inflate(R.layout.activity_fan, container, false);
        fan_count = (TextView) view.findViewById(R.id.fan_count);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerViewAdapter());
        return view;
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<UserModel> users = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("users").orderByChild("fan/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users.clear();
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        users.add(snapshot.getValue(UserModel.class));
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banced, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;

            fan_count.setText(String.valueOf(users.size()));

            FirebaseDatabase.getInstance().getReference().child("users").child(users.get(position).uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    try {
                        customViewHolder.name.setText(userModel.userName);
                        Glide.with(customViewHolder.itemView.getContext())
                                .load(userModel.profileImageUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(customViewHolder.image);

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent write_profile = new Intent(getActivity(), WriterProfileActivity.class);
                    write_profile.putExtra("uid", users.get(position).uid);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fab_open, R.anim.fab_close);
                    startActivity(write_profile, activityOptions.toBundle());
                }
            });

        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView image;
            public Button nobanced;

            public CustomViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                image = (ImageView) view.findViewById(R.id.image);
                nobanced = (Button) view.findViewById(R.id.nobanced);
                nobanced.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
