package com.example.android.coordinatedeffort.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.coordinatedeffort.R;
import com.example.android.coordinatedeffort.SimpleAdapter;
import com.example.android.coordinatedeffort.behaviors.SlidingCardBehavior;

/**
 * Custom container to centralize the common logic for the sliding
 * card views. Includes state accessors that the behavior will
 * access to properly do a layout pass.
 */

@CoordinatorLayout.DefaultBehavior(SlidingCardBehavior.class)
public class SlidingCardLayout extends FrameLayout {

    private int mHeaderViewHeight;

    public SlidingCardLayout(Context context) {
        this(context, null);
    }

    public SlidingCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.widget_card, this);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        TextView header = (TextView) findViewById(R.id.header);

        SimpleAdapter adapter = new SimpleAdapter(list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingCardLayout, defStyleAttr, 0);
        header.setBackgroundColor(a.getColor(R.styleable.SlidingCardLayout_android_colorBackground, Color.BLACK));
        header.setText(a.getText(R.styleable.SlidingCardLayout_android_text));
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            mHeaderViewHeight = findViewById(R.id.header).getMeasuredHeight();
        }
    }

    public int getHeaderHeight() {
        return mHeaderViewHeight;
    }
}
