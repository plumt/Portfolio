package yuns.sns.sns.model;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

import yuns.sns.R;

public class UserModel {
    public String userName;
    public String profileImageUrl;
    public String uid;
    public String tag;
    public String pushToken;
    public String gender;
    public String comment;
    public Map<String,String> friend = new HashMap<>();
    public Map<String,String> banced = new HashMap<>();
}
