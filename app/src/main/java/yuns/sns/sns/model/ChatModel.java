package yuns.sns.sns.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public Map<String,Comment> comments = new HashMap<>();
    public Map<String,Boolean> users = new HashMap<>();
    public Object date;
    public String user;
    public static class Comment{
        public String uid;
        public String message;
        public String out;
        public Object timestamp;
        public Map<String,Object> readUsers = new HashMap<>();
    }
}
