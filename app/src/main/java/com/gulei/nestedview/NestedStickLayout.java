package com.gulei.nestedview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by gl152 on 2019/3/11.
 * https://blog.csdn.net/lmj623565791/article/details/52204039
 */

public class NestedStickLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "NestedStickLayout";


    NestedScrollingChildHelper mNestedChildHelper;
    NestedScrollingParentHelper mNestedParentHelper;

    public NestedStickLayout(Context context) {
        this(context, null);
    }

    public NestedStickLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedStickLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNestedChildHelper = new NestedScrollingChildHelper(this);
        mNestedParentHelper = new NestedScrollingParentHelper(this);
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * NestedScrollingChild调用startNestedScroll向parent传递滑动信息，parent可以通过onStartNestedScroll判断是否拦截处理
     *
     * @param child
     * @param target
     * @param nestedScrollAxes
     * @return
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.i(TAG, "onStartNestedScroll: " + child.getId() + "   " + target.getId() + "    " + nestedScrollAxes);
        return true;
    }

    /**
     * onStartNestedScroll返回false，那么while循环就会继续寻找更上一级的父View让其接手
     * 只有在onStartNestedScroll返回true的时候才会接着调用onNestedScrollAccepted
     *
     * @param child
     * @param target
     * @param axes
     */

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.i(TAG, "onNestedScrollAccepted: " + child.getId() + "   " + target.getId() + "    " + axes);
        super.onNestedScrollAccepted(child, target, axes);
    }

    /**
     * 每次子View在滑动前都需要将滑动细节传递给父View，一般情况下是在ACTION_MOVE中调用dispatchNestedPreScroll,然后父View就会被回调onNestedPreScroll
     *
     * @param target
     * @param dx       滑动x方向的距离
     * @param dy       滑动y方向的距离
     * @param consumed 如果父View选择要消耗掉滑动的值就需要通过此数组传递给子View。比如以下伪代码表示父View要在x方向消耗10px，y方向消耗5px。consumed[0] = 10;consumed[1] = -5;
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.i(TAG, "onNestedPreScroll: " + target.getId() + "    " + dx + "    " + dy + "   " + consumed[0] + "   " + consumed[1]);
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(TAG, "onNestedScroll: " + target.getId() + "    " + dxConsumed + "    " + dyConsumed + "   " + dxUnconsumed + "   " + dyUnconsumed);
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }


    /**
     * 就是当子View ACTION_UP时可能伴随着fling的产生，如果产生了fling，就需要子View在stopNestedScroll前调用dispatchNestedPreFling和dispatchNestedFling，
     * 父View对应的会被回调onNestedPreFling和onNestedFling
     *
     * @param target
     * @param velocityX
     * @param velocityY
     * @return 父View是否消耗掉了fling
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.i(TAG, "onNestedPreFling: " + target.getId() + "    " + velocityX + "    " + velocityY);
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    /**
     * 可以捕获对内部View的fling事件，如果return true则表示拦截掉内部View的事件。
     *
     * @param target
     * @param velocityX
     * @param velocityY
     * @param consumed
     * @return
     */
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.i(TAG, "onNestedFling: " + target.getId() + "    " + velocityX + "    " + velocityY + "   " + consumed);
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }


    /**
     * 随着ACTION_UP或者ACTION_CANCEL的到来，子View需要调用public void stopNestedScroll()来告知父View本次NestedScrollig结束，
     * 父View对应的会被回调public void onStopNestedScroll(View target)，可以在此方法中做一些对应停止的逻辑操作比如资源释放等
     *
     * @param child
     */
    @Override
    public void onStopNestedScroll(View child) {
        Log.i(TAG, "onStopNestedScroll: " + child.getId());
        super.onStopNestedScroll(child);
    }
}
