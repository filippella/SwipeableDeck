package org.dalol.swipeabledeck;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by Filippo on 8/19/2016.
 */
public class SwipeableDeckView extends RelativeLayout implements View.OnTouchListener {

    private static final float MIN_DISTANCE = 150f;
    private boolean isDeckCreated;
    private int deckViewHeight;
    private int deckVieWidth;
    private float mDx, mDy;
    private float mInitialX;
    private View child;
    private ObjectAnimator animator;

    GestureDetector gdt;


    private static final String imagesLink[] = {
            "https://scontent-lhr3-1.xx.fbcdn.net/v/t1.0-9/1003285_483043595098299_1117604655_n.jpg?oh=e23b2091b2e90d3f11e2062a763453bc&oe=58470168",
            "https://scontent-lhr3-1.xx.fbcdn.net/v/t1.0-9/1507023_10153690634025634_251822057_n.jpg?oh=366b5c2e921cce49eaf2ee1c1a912e3c&oe=5811531A",

            "https://scontent-lhr3-1.xx.fbcdn.net/v/t1.0-9/13076856_10209280307155334_6415517330023236988_n.jpg?oh=e61924f477629bed1836081c6a423c9a&oe=581503B7",
            "https://avatars2.githubusercontent.com/u/8240260?v=3&s=400"
    };

    private boolean touchedDown;
    private int mActivePointerId;
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerIndex = 0;
    private boolean cardTouchingDown;
    private boolean untilAnimationDone;
    private boolean isAnimating;
    private int paddingLeft;
    private int paddingTop;
    private float CARD_SPACING = 15f;
    private ImageView imageView;

    public SwipeableDeckView(Context context) {
        this(context, null, 0);
    }

    public SwipeableDeckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeableDeckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeableDeckView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ActionBar,
                0, 0);
        try {
            //do whatever
        } finally {
            a.recycle();
        }
        ViewCompat.setTranslationZ(this, Float.MAX_VALUE);
        setClipToPadding(false);
        setClipChildren(false);
        setWillNotDraw(false);

        gdt = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isDeckCreated) return;
        isDeckCreated = true;
        deckVieWidth = getWidth();
        deckViewHeight = getHeight();

        createInitialDecks();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private void createInitialDecks() {
        removeAllViews();
        Context context = getContext();

        child = LayoutInflater.from(getContext()).inflate(R.layout.test_card, this, false);
        imageView = (ImageView) child.findViewById(R.id.imageView);
//
//        child = new ImageView(context);
//       // child.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.card_bg));
//        child.setBackgroundColor(Color.rgb(new Random().nextInt(255), new Random().nextInt(255)
//                , new Random().nextInt(255)));

        int itemWidth = deckVieWidth - (paddingLeft + getPaddingRight());
        int itemHeight = deckVieWidth - (paddingTop + getPaddingBottom());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();

        child.setY(paddingTop);

        Log.d("Padding top", "padding top size 1 - > " + paddingTop);

        //LayoutParams params = new LayoutParams(itemWidth, itemHeight);
        //params.topMargin = paddingTop;
        //child.setPadding(10, 10,10 ,10);
        child.setOnTouchListener(this);

        if (true) {
            imageView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.EXACTLY | itemHeight);

        //addViewInLayout(child, -1, params, true);
        addView(child, -1, params);

        for(int i = 0; i < 0; i++) {
            ImageView testChild = new ImageView(context);
            testChild.setBackgroundColor(Color.rgb(new Random().nextInt(255), new Random().nextInt(255)
            , new Random().nextInt(255)));

            LayoutParams testChildparams = new LayoutParams(itemWidth,
                    itemHeight);
            testChild.setPadding(10, 10,10 ,10);
            testChild.setOnTouchListener(this);
//            testChild.setScaleX(0.9f);
//            testChild.setScaleY(0.9f);

            int topViewIndex = getChildCount() - 1;

            int mViewSpacing = getResources().getDimensionPixelSize(R.dimen.default_val);

            int distanceToViewAbove = Math.abs((i+1) * mViewSpacing);

            int newPositionX = (getWidth() - testChild.getMeasuredWidth()) / 2;
            int newPositionY = distanceToViewAbove + getPaddingTop();

//            testChild.layout(
//                    newPositionX,
//                    getPaddingTop(),
//                    newPositionX + testChild.getMeasuredWidth(),
//                    getPaddingTop() + testChild.getMeasuredHeight());

            testChild.setY(newPositionY);

            //float offset = (int) (((2 - 1) * CARD_SPACING) - ((i+1) * CARD_SPACING));
            testChildparams.topMargin = newPositionY;

            int diff = child.getWidth() - testChild.getWidth();

            Log.d("Width of", "Child foddv- - > " + diff);

            //testChildparams.topMargin = (paddingTop + (int) (CARD_SPACING*(i*1)));

            addViewInLayout(testChild, i, testChildparams, true);
        }

        ImageView viewById = (ImageView) child.findViewById(R.id.imageView2);
//        viewById.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mInitialX = event.getRawX();
//                return false;
//            }
//        });
        viewById.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Filippo!!", Toast.LENGTH_SHORT).show();
            }
        });


        //addView(child, params);
        Picasso.with(context).load(imagesLink[new Random().nextInt(imagesLink.length)]).into(imageView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        //int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >>   MotionEvent.ACTION_POINTER_ID_SHIFT;

        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        Log.d("test", "MotionEvent down detected - > " + pointerId);


        int eventAction = event.getAction();

        float rawX = event.getRawX();
        float rawY = event.getRawY();



        float centerX = (getWidth() / 2);

        //float viewCenter = v.getX()  (getWidth() / 2)  (v.getX() + v.getRight()) / 2;
        //Log.d("test", "center of the view - > " + getValueOfX(v));

        procesRotation(v);

        Log.d("TouchEvents", "Is returning -> onTouch " + touchedDown + " action > " + eventAction );

        if(isAnimating || pointerId != mActivePointerId) {
            touchedDown = true;
            cardTouchingDown = true;

            untilAnimationDone = true;
            return false;
        }

       gdt.onTouchEvent(event) ;

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mInitialX = rawX;
                cardTouchingDown = true;

                mActivePointerId = event.getPointerId(0);

                //requestDisallowInterceptTouchEvent(true);

                if (animator != null) {
                    animator.cancel();
                }

                mDx = rawX - v.getX();
                mDy = rawY - v.getY();
                return true;
            case MotionEvent.ACTION_MOVE:

//                final int pointerIndex = Compat.getPointerIndex(ev.getAction());
//                final int pointerId = ev.getPointerId(pointerIndex);

                v.setX(rawX - mDx);
                v.setY(rawY - mDy);




//                if (Math.abs(deltaX) > MIN_DISTANCE) {
//                    if (rawX > mInitialX) {
//                        //rotate clockwise
//                    }
//
//                    // Right to left swipe action
//                    else {
//                        //rotate anti clockwise
//                    }
//                }


                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:



                touchedDown = false;

                //requestDisallowInterceptTouchEvent(false);

                //v1 = mInitialX;

                    float deltaX = rawX - mInitialX;

//                    if (Math.abs(deltaX) > MIN_DISTANCE) {
//                        // Left to Right swipe action
//                        if (rawX > mInitialX) {
//                            Toast.makeText(getContext(), "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show();
//                        }
//
//                        // Right to left swipe action
//                        else {
//                            Toast.makeText(getContext(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } else {
//                        Toast.makeText(getContext(), "Just A TAP!!!", Toast.LENGTH_SHORT).show();
//                        // consider as something else - a screen tap for example
//                    }
                break;
        }

//        mActivePointerIndex = event
//                .findPointerIndex(mActivePointerId != INVALID_POINTER_ID ? mActivePointerId
//                        : 0);

        animateToOriginal();

        return !isAnimating;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
       // boolean dispatchTouchEvent = super.dispatchTouchEvent(ev);
       // Log.d("TouchEvents", "Is returning -> dispatchTouchEvent " + dispatchTouchEvent );
       // boolean onTouchEvent = child.dispatchTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mInitialX = ev.getRawX();
                break;
        }

        return child.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean onTouchEvent = super.onTouchEvent(event);
        Log.d("TouchEvents", "Is returning -> onTouchEvent " + onTouchEvent);
        child.onTouchEvent(event);
        return onTouchEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean touchEvent = super.onInterceptTouchEvent(ev);
//        Log.d("TouchEvents", "Is returning -> onInterceptTouchEvent " + touchEvent + " touchedDown > " + touchedDown );
//        //Log.d("TouchEvents", "Is returning -> touchedDown " + touchedDown);
//        if(untilAnimationDone) return true;
//
//        Log.d("TouchEvents", "Is returning touchEvent -> " + touchEvent);
        Log.d("TouchEvents", "Is returning onInterceptTouchEvent -> " + untilAnimationDone);
        return untilAnimationDone;
    }

//    private float getValueOfX(View v) {
//        return v.getX();
//    }

//    float getActiveX(MotionEvent ev) {
//        try {
//            return ev.getX(mActivePointerIndex);
//        } catch (Exception e) {
//            return ev.getX();
//        }
//    }
//
//    float getActiveY(MotionEvent ev) {
//        try {
//            return ev.getY(mActivePointerIndex);
//        } catch (Exception e) {
//            return ev.getY();
//        }
//    }



    private void procesRotation(View v) {
        float vX = 35 * 2.f * ((v.getX() - getPaddingLeft())/getWidth());

        //float rotation = 15 * 2.f * (vX - child.getWidth()/2) / getWidth();
        v.setRotation(vX);
    }

    private void animateToOriginal() {

        Log.d("Padding top", "padding top size 2 - > " + paddingTop);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", child.getX(), paddingLeft);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y", child.getY(), paddingTop);

        animator = ObjectAnimator.ofPropertyValuesHolder(this, pvhX, pvhY);
        animator.setDuration(350L);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                touchedDown = false;
                untilAnimationDone = false;
                isAnimating = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                touchedDown = true;
                isAnimating = true;
            }
        });
        animator.start();
    }

    public void setX(float x) {
//        mChildX = x;
//        float rotation = 15 * 2.f * (x + child.getWidth()/2) / getWidth();
//        child.setRotation(rotation);
        Log.d("test", "center of the view - > " + x);
        child.setX(x);
        procesRotation(child);
    }

    public void setY(float y) {
        child.setY(y);
    }

    public void changeImage() {
        Picasso.with(getContext()).load(imagesLink[new Random().nextInt(imagesLink.length)]).into(imageView);
    }

    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_THRESHOLD_VELOCITY = 150;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float difference = e1.getX() - e2.getX();
            if(difference > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(getContext(), "Right to Left Fling", Toast.LENGTH_SHORT).show();
                return true; // Right to left
            }  else {
                float d = e2.getX() - e1.getX();
                if (d > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        Toast.makeText(getContext(), "Left to Right Fling", Toast.LENGTH_SHORT).show();
                    return true; // Left to right
                }
            }
            return true;
        }
    }
}
