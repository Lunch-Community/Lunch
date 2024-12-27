package com.lazytong.launcher.model;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.Objects;

public class Notification implements Parcelable {
    public Icon icon;
    public String appName;
    public String title;
    public String content;
    public String key;
    public PendingIntent intent;
    public static final Creator<Notification> CREATOR = new Creator<>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
    public Notification() {

    }
    public long time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    public Notification(Icon icon, String appName, String title, String content, long time, String key, PendingIntent intent) {
        this.icon = icon;
        this.appName = appName;
        this.title = title;
        this.content = content;
        this.time = time;
        this.key = key;
        this.intent = intent;
    }

    protected Notification(Parcel in) {
        icon = in.readParcelable(Icon.class.getClassLoader());
        content = in.readString();
        appName = in.readString();
        title = in.readString();
        time = in.readLong();
        key = in.readString();
        intent = in.readParcelable(PendingIntent.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(icon, i);
        parcel.writeString(content);
        parcel.writeString(appName);
        parcel.writeString(title);
        parcel.writeLong(time);
        parcel.writeString(key);
        parcel.writeParcelable(intent,i);
    }
}
