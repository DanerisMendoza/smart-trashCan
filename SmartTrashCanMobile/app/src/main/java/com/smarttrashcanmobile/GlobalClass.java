package com.smarttrashcanmobile;

import android.app.Application;
import android.widget.TextView;

public class GlobalClass extends Application {
    TextView textViewStatus, textViewMode;



    public void setTextViewStatus(TextView tv) {
        this.textViewStatus = tv;
    }
    public void setTextViewMode(TextView tv) {
        this.textViewMode = tv;
    }


    public TextView getTextViewStatus() {
        return textViewStatus;
    }
    public TextView getTextViewMode() {return textViewMode;}
}
