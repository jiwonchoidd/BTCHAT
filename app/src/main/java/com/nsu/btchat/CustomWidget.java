package com.nsu.btchat;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
public class CustomWidget extends LinearLayout {
    public  CustomWidget(Context context, AttributeSet attributeSet) {
        super(context);
        final Context tmpContext = context;
        // 커스텀 위젯으로 함
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.custom_widget, this);


    }
}
