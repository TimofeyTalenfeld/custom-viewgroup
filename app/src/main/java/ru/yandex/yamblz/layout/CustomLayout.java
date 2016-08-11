package ru.yandex.yamblz.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import ru.yandex.yamblz.R;

/**
 * Created by root on 7/22/16.
 */
public class CustomLayout extends ViewGroup {

    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.d(getClass().getCanonicalName(), "onMeasure()");

        int height = 0;
        int width = 0;
        int specialChildIndex = -1;

        for(int i = 0; i < getChildCount(); ++i) {

            View child = getChildAt(i);

            if(child.getVisibility() == GONE) {
                continue;
            }

            if(child.getLayoutParams().width == WindowManager.LayoutParams.MATCH_PARENT) {
                if(specialChildIndex != -1) {
                    throw new IllegalStateException("CustomLayout can contain only one child with match_parent width!");
                }
                specialChildIndex = i;
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                width += child.getMeasuredWidth();
            }
            height = Math.max(height, child.getMeasuredHeight());

        }

        if(specialChildIndex != -1) {
            View specialChild = getChildAt(specialChildIndex);

            measureChild(specialChild,
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(specialChild.getLayoutParams().height, MeasureSpec.EXACTLY));

            width += specialChild.getMeasuredWidth();
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = getPaddingLeft();
        int top = getPaddingTop();

        for(int i = 0; i < getChildCount(); ++i) {

            View child = getChildAt(i);

            if(child.getVisibility() == GONE) {
                continue;
            }

            int right = left + child.getMeasuredWidth();

            child.layout(left, top, right, top + child.getMeasuredHeight());

            left = right;

        }

        Log.d(getClass().getCanonicalName(), "onLayout()");
    }

    public static class LayoutParams {
        public LayoutParams(Context context, AttributeSet attr) {
            TypedArray arr = context.obtainStyledAttributes(attr, R.styleable.CustomLayout);
        }
    }
}
