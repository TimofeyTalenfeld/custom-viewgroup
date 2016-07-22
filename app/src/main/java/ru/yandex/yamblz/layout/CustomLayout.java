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

    private Context context;

    public CustomLayout(Context context) {
        super(context);
        this.context = context.getApplicationContext();
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context.getApplicationContext();
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context.getApplicationContext();
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
                height = Math.max(height, child.getMeasuredHeight());
            }

        }

        if(specialChildIndex != -1) {
            View specialChild = getChildAt(specialChildIndex);

            measureChild(specialChild, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(specialChild.getLayoutParams().height, MeasureSpec.EXACTLY));
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int deviceWidth = getDevicWidth();

        int left = getPaddingLeft();
        int top = getPaddingTop();

        for(int i = 0; i < getChildCount(); ++i) {

            View child = getChildAt(i);

            if(child.getVisibility() == GONE) {
                continue;
            }

            int right = left + child.getMeasuredWidth();

            if(right > deviceWidth) {
                throw new IllegalStateException("Too much width on layout's children!");
            }

            child.layout(left, top, right, top + child.getMeasuredHeight());

            left = right;

        }

        Log.d(getClass().getCanonicalName(), "onLayout()");
    }

    private int getDevicWidth() {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        return deviceDisplay.x;
    }

    public static class LayoutParams {
        public LayoutParams(Context context, AttributeSet attr) {
            TypedArray arr = context.obtainStyledAttributes(attr, R.styleable.CustomLayout);
        }
    }
}
