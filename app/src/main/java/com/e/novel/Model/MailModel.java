package com.e.novel.Model;

import java.util.HashMap;
import java.util.Map;

public class MailModel {
    public Map<String, Boolean> banced = new HashMap<>();
    public Map<String, Mail> mails = new HashMap<>();

    public static class Mail {
        public String uid;
        public String title;
        public String massage;
        public Object date;
        public boolean read;
    }
}
