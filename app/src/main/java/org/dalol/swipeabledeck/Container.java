package org.dalol.swipeabledeck;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Filippo on 8/20/2016.
 */
public class Container extends LinearLayout {
    LinkedHashMap<Integer, View> pointers = new LinkedHashMap<Integer, View>();
    ArrayList<View> views = new ArrayList<View>();

    public Container(Context context) {
        super(context);
        initialize(context);

    }

    public Container(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {

    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        views = LayoutUtil.flattenLayout(this, false);
        for (View foo : views) {
            Rect rect = new Rect();
            foo.getGlobalVisibleRect(rect);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            for (View v : views) {
                Rect r = new Rect();
                v.getGlobalVisibleRect(r);
                if (event.getX() > r.left && event.getX() < r.right
                        && event.getY() > r.top
                        && event.getY() < r.bottom) {
                    pointers.put(event.getPointerId(0), v);
                    pointers.get(event.getPointerId(0)).onTouchEvent(event);
                    break;
                }
            }
        }
        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            int pid = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int index = event.findPointerIndex(pid);

            for (View v : views) {

                Rect r = new Rect();
                v.getGlobalVisibleRect(r);
                if (event.getX(index) > r.left
                        && event.getX(index) < r.right
                        && event.getY(index) > r.top
                        && event.getY(index) < r.bottom) {


                    pointers.put(pid, v);
                    MotionEvent copy = MotionEvent.obtain(event);
                    copy.setAction(MotionEvent.ACTION_DOWN);
                    copy.setLocation(event.getX(index), event.getY(index));
                    pointers.get(pid).onTouchEvent(copy);
                }
            }
        }
        if (action == MotionEvent.ACTION_POINTER_UP) {
            int pid = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int index = event.findPointerIndex(pid);

            if (pointers.get(pid) != null) // If the touch was outside any view
            {
                MotionEvent copy = MotionEvent.obtain(event);
                copy.setAction(MotionEvent.ACTION_UP);
                pointers.get(pid).onTouchEvent(copy);
                pointers.remove(pid);
            }
        }

        if (action == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                int pid = event.getPointerId(i);
                MotionEvent copy = MotionEvent.obtain(event);
                copy.setLocation(event.getX(i), event.getY(i));

                if (pointers.get(pid) == null) continue; // If the touch was outside any view
                pointers.get(pid).onTouchEvent(copy);
            }
        }

        if (action == MotionEvent.ACTION_UP) {
            if (pointers.get(event.getPointerId(0)) != null) {
                pointers.get(event.getPointerId(0)).onTouchEvent(event);
                pointers.remove(event.getPointerId(0));
            }
        }
        return true;
    }


    // This is the LayoutUtil.flattenLayout method
}