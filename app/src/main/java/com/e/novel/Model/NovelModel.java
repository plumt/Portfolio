package com.e.novel.Model;

import java.util.HashMap;
import java.util.Map;

public class NovelModel {
    public String userName;
    public String title;
    public Map<String, Boolean> category_bool = new HashMap<>();
    public Map<String, Boolean> uid = new HashMap<>();
    public boolean open;
    public boolean finish;
    public String category_str;
    public Object date;
    public String key;
    public String like;
    public String myuid;
    public String comment;
    public String view;
    public Map<String, Boolean> likeUser = new HashMap<>();
    public Map<String, Boolean> week = new HashMap<>();
    public Map<String, Novel> novels = new HashMap<>();

    public static class Novel {
        public String view;
        public String novel_title;
        public String novel_content;
        public String count;
        public boolean open;
    }
}
