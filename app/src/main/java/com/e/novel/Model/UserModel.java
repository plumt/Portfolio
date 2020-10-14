package com.e.novel.Model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    public String userName;
    public String profileImageUrl;
    public String uid;
    public String gender;
    public String comment;
    public boolean writer;
    public Map<String,Boolean> now_write = new HashMap<>();
    public Map<String,Boolean> finish_write = new HashMap<>();
    public Map<String,Boolean> fan = new HashMap<>();
}
