package com.e.note.model;

import java.util.HashMap;
import java.util.Map;

public class MemoModel {

    public Map<String,Memo> memos = new HashMap<>();
    public Map<String,Boolean> user = new HashMap<>();
    public Object date;
    public String name;
    public String key;
    public String color;
    public String password;

    public static class Memo{
        public String memo;
        public Object date;
        public String password;
    }
}
