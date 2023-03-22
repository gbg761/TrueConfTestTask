package com.example.trueconftesttask;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION_MILLIS = 10_000;

    private FrameLayout mainLayout;
    private TextView helloView;

    private boolean isAnimationClear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        helloView = findViewById(R.id.helloView);

        helloView.setOnClickListener(view -> {
            helloView.clearAnimation();
            isAnimationClear = true;
        });

        mainLayout.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            if (view.getId() != R.id.mainLayout)
                return false;

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                isAnimationClear = false;
                int fingerPointX = (int) motionEvent.getX();
                int fingerPointY = (int) motionEvent.getY();
                changePositionHelloView(fingerPointX, fingerPointY);
            }
            return true;
        });
    }

    private void changePositionHelloView(int fingerPointX, int fingerPointY) {
        int mainLayoutWidth = mainLayout.getMeasuredWidth();
        int mainLayoutHeight = mainLayout.getMeasuredHeight();
        int helloViewWidth = helloView.getMeasuredWidth();
        int helloViewHeight = helloView.getMeasuredHeight();
        int x = (mainLayoutWidth - fingerPointX) < helloViewWidth ? (mainLayoutWidth - helloViewWidth) : fingerPointX;
        int y = (mainLayoutHeight - fingerPointY) < helloViewHeight ? mainLayoutHeight - helloViewHeight : fingerPointY;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) helloView.getLayoutParams();
        params.setMargins(x, y, 0, 0);
        helloView.setLayoutParams(params);

        changeTextColorHelloView();
        startAnimation(y);
    }

    private void changeTextColorHelloView() {
        String language = Locale.getDefault().getLanguage();
        if (language.equals("en")) {
            helloView.setTextColor(Color.RED);
        } else {
            helloView.setTextColor(Color.BLUE);
        }
    }

    private void startAnimation(int y) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, 0f,
                Animation.ABSOLUTE, 0f,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, mainLayout.getMeasuredHeight() - y - helloView.getMeasuredHeight()
        );

        long animationDuration = getAnimationDuration(y);
        translateAnimation.setDuration(animationDuration);
        translateAnimation.setFillAfter(true);
        translateAnimation.setStartOffset(5000);
        helloView.startAnimation(translateAnimation);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isAnimationClear) {
                    TranslateAnimation toTopAnimation = new TranslateAnimation(
                            Animation.ABSOLUTE, 0f,
                            Animation.ABSOLUTE, 0f,
                            Animation.ABSOLUTE, mainLayout.getMeasuredHeight() - y - helloView.getMeasuredHeight(),
                            Animation.ABSOLUTE, -y
                    );
                    toTopAnimation.setDuration(ANIMATION_DURATION_MILLIS);
                    toTopAnimation.setFillAfter(true);
                    toTopAnimation.setRepeatCount(Animation.INFINITE);
                    toTopAnimation.setRepeatMode(Animation.REVERSE);
                    helloView.startAnimation(toTopAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private long getAnimationDuration(int y) {
        int height = mainLayout.getMeasuredHeight();
        int unit = ANIMATION_DURATION_MILLIS / height;
        return (long) (height - y) * unit;
    }
}