package com.clubank.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *同一界面 ViewPager 嵌套ViewPager事件处理
 */
public class MyViewPager extends ViewPager {
    private int currentX, currentY;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {

      /*  //下面这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);*/

        getParent().requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = (int) ev.getX();
                currentY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getX();
                //做是向上下移动还是左右移动操作处理
                int mx = moveX - currentX;
                int my = moveY - currentY;
                if (Math.abs(mx) > Math.abs(my)) {  //如果大于表示是左右移动  否则的话是上下移动
                    int currentItem = getCurrentItem();
                    if (mx > 0) {
                        if (currentItem == 0) {   //如果是第一个让父view处理
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        int itemCount = getAdapter().getCount();
                        if (currentItem == itemCount - 1) {  //如果是最后一个让父view处理
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);//让父view处理
                }
                break;
            default:
                break;

        }
        return super.dispatchTouchEvent(ev);
    }
}
