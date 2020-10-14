package com.e.plan.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.plan.LoginActivity;
import com.e.plan.Model.RoomModel;
import com.e.plan.Model.UserModel;
import com.e.plan.R;
import com.e.plan.alert.AddSchedule;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;

public class CalenderFragment extends Fragment {
    Button add_schedule;
    MaterialCalendarView materialCalendarView;
    public RecyclerView recyclerView;
    Integer y, m, d;
    List<String> result = new ArrayList<String>();
    private AdView mAdView;
    String room;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_calender, container, false);
        add_schedule = (Button) view.findViewById(R.id.add_schedule);
        recyclerView = (RecyclerView) view.findViewById(R.id.calender_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(getActivity(), LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            getActivity().finish();
        }

        Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH) + 1;
        d = calendar.get(Calendar.DATE);

        recyclerView.setAdapter(new CalenderFragmentRecyclerViewAdapter());


        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_schedule = new Intent(getActivity(), AddSchedule.class);
                add_schedule.putExtra("date", y.toString() + "," + m.toString() + "," + d.toString());
                startActivity(add_schedule);
            }
        });

        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                y = date.getYear();
                m = date.getMonth() + 1;
                d = date.getDay();
                recyclerView.setAdapter(new CalenderFragmentRecyclerViewAdapter());
            }
        });


        materialCalendarView.setCurrentDate(new Date(System.currentTimeMillis()));
        calender_setting();

        return view;
    }


    class CalenderFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<RoomModel.Calender> roomModels;

        public CalenderFragmentRecyclerViewAdapter() {
            roomModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            if (userModel.select != null) {
                                switch (userModel.select) {
                                    case "1":
                                        room = userModel.room1;
                                        break;
                                    case "2":
                                        room = userModel.room2;
                                        break;
                                    case "3":
                                        room = userModel.room3;
                                }
                            }

                            FirebaseDatabase.getInstance().getReference().child("room").orderByChild("users/" + myUid).equalTo(true).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                                        final RoomModel roomModel = item.getValue(RoomModel.class);
                                        if (roomModel.roomUid.equals(room)) {
                                            String roomkey = item.getKey();
                                            FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("calender").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    roomModels.clear();
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        RoomModel.Calender calender = snapshot.getValue(RoomModel.Calender.class);
                                                        if (calender.date.equals(y.toString() + "," + m.toString() + "," + d)) {
                                                            roomModels.add(calender);
                                                        }
                                                        result.add(calender.date);

                                                    }
                                                    try {
                                                        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
                                                    } catch (Exception e) {

                                                    }
                                                    if (roomModels.size() == 0) {
                                                        return;
                                                    }
                                                    notifyDataSetChanged();
                                                    recyclerView.scrollToPosition(roomModels.size() - 1);
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            CustomViewHolder customViewHolder = ((CustomViewHolder) holder);
            customViewHolder.write_name.setText(roomModels.get(position).name);
            customViewHolder.write_memo.setText(roomModels.get(position).memo);
            customViewHolder.write_date.setText(roomModels.get(position).date);

            customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (myUid.equals(roomModels.get(position).uid)) {
                            Intent del_schedule = new Intent(getActivity(), AddSchedule.class);
                            del_schedule.putExtra("cal_date", roomModels.get(position).date);
                            del_schedule.putExtra("cal_name", roomModels.get(position).name);
                            del_schedule.putExtra("cal_memo", roomModels.get(position).memo);
                            startActivityForResult(del_schedule, 1);
                        }
                    } catch (Exception e) {

                    }
                    return false;
                }
            });


        }

        @Override
        public int getItemCount() {
            return roomModels.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView write_name, write_memo, write_date;

            public CustomViewHolder(View view) {
                super(view);
                write_name = (TextView) view.findViewById(R.id.write_name);
                write_memo = (TextView) view.findViewById(R.id.write_memo);
                write_date = (TextView) view.findViewById(R.id.write_date);
            }

        }
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        List<String> Time_Result;

        ApiSimulator(List<String> time) {
            Time_Result = time;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            try {
                for (int i = 0; i < Time_Result.size(); i++) {
                    //     CalendarDay day = CalendarDay.from(calendar);
                    String[] time = Time_Result.get(i).split(",");
                    int year = Integer.parseInt(time[0]);
                    int month = Integer.parseInt(time[1]);
                    int dayy = Integer.parseInt(time[2]);

                    calendar.set(year, month - 1, dayy);
                    CalendarDay day = CalendarDay.from(calendar);
                    dates.add(day);
                }
            } catch (Exception e) {

            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            try {
                if (getActivity().isFinishing()) {
                    return;
                }
            } catch (Exception e) {

            }

            try {
                materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays, getActivity()));
            } catch (Exception e) {

            }
        }
    }


    void calender_setting() {
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2100, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());
    }

    public class SundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    public class SaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            final Drawable drawable;
            drawable = getResources().getDrawable(R.drawable.more);
            //     view.addSpan(new StyleSpan(Typeface.BOLD_ITALIC));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.setSelectionDrawable(drawable);
//            view.addSpan(new ForegroundColorSpan(Color.GREEN));
        }

        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }

    public class EventDecorator implements DayViewDecorator {

        private Drawable drawable;
        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates, Activity context) {
            try {
                this.color = color;
                drawable = context.getResources().getDrawable(R.drawable.more);
                this.dates = new HashSet<>(dates);
            } catch (Exception e) {

            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            //view.setSelectionDrawable(drawable);
            view.addSpan(new DotSpan(5, color)); // 날자밑에 점
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean del = data.getBooleanExtra("del", false);
            if (del) {
                result.clear();
                materialCalendarView.removeDecorators();
                recyclerView.setAdapter(new CalenderFragmentRecyclerViewAdapter());
                new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
                calender_setting();
                try {
                    new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(getActivity(), LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            getActivity().finish();
        }
    }
}
