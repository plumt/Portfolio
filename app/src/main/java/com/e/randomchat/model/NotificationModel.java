package com.e.randomchat.model;

public class NotificationModel {

    public String to;
    public Notification notification = new Notification();
    public Data data = new Data();
    public static class Notification {
        public String title;
        public String text;
        public String uid;
    }
    public static class Data{
        public String title;
        public String text;
        public String uid;
    }
}
