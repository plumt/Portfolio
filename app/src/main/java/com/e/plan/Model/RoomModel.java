package com.e.plan.Model;

import java.util.HashMap;
import java.util.Map;

public class RoomModel {
    public String roomUid;
    public String roomdName;
    public String password;
    public Map<String, Boolean> users = new HashMap<>();

    public Map<String, Memo> memos = new HashMap<>();

    public static class Memo {
        public String title;
        public String memo;
        public String uid;
        public String date;
        public String name;
    }

    public Map<String, Calender> calender = new HashMap<>();

    public static class Calender {
        public String date;
        public String uid;
        public String name;
        public String memo;
    }
}
