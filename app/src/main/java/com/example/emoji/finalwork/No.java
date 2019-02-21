package com.example.emoji.finalwork;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

class NoScrollViewPager extends ViewPager {
        private boolean noScroll = false;

        public NoScrollViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);

        }

        public NoScrollViewPager(Context context) {
            super(context);
        }

        public void setNoScroll(boolean noScroll) {
            this.noScroll = noScroll;
        }

        @Override
        public void scrollTo(int x, int y) {
            super.scrollTo(x, y);
        }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return noScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return noScroll && super.onTouchEvent(ev);

    }


        @Override
        public void setCurrentItem(int item, boolean smoothScroll) {
            super.setCurrentItem(item, smoothScroll);
        }

        @Override
        public void setCurrentItem(int item) {
            super.setCurrentItem(item);
        }



    }


