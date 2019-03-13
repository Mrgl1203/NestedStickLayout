package com.gulei.nestedview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by gl152 on 2019/3/11.
 */

public class NestedStickLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "NestedStickLayout";


    //    NestedScrollingChildHelper mNestedChildHelper;
    NestedScrollingParentHelper mNestedParentHelper;
    int mTopViewHeight;
    OverScroller mScroller;
    private ValueAnimator mOffsetAnimator;
    private GestureDetector mGestureDetector;

    public NestedStickLayout(Context context) {
        this(context, null);
    }

    public NestedStickLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedStickLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mNestedChildHelper = new NestedScrollingChildHelper(this);
        mNestedParentHelper = new NestedScrollingParentHelper(this);
        mScroller = new OverScroller(context);
        setOrientation(LinearLayout.VERTICAL);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight;
                boolean showTop = dy < 0 && getScrollY() > 0;
                if (hiddenTop || showTop) {
                    scrollBy(0, (int) dy);
                    Log.i(TAG, "onNestedPreScroll: " + (hiddenTop) + "   " + (showTop) + "   " + "scrollBy：" + dy + "getScrollY:" + getScrollY());
                }
                return hiddenTop || showTop;
            }

        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure: -------------------------");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTopViewHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            NestedStickLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.scrollType == LayoutParams.scroll) {
                mTopViewHeight += child.getMeasuredHeight();
            }

            if (i == childCount - 1) {
                lp.height = getMeasuredHeight();
            }
        }


    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.i(TAG, "onLayout: -------------------------");
//        super.onLayout(changed, l, t, r, b);
//    }
//
//    @Override
//    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//        Log.i(TAG, "drawChild: -------------------------");
//        return super.drawChild(canvas, child, drawingTime);
//    }

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
        //竖直方向拦截
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
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
        mNestedParentHelper.onNestedScrollAccepted(child, target, axes);
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
        //上滑并且滑动距离小于顶部高度
        boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight;
        //下滑并且滑动的目标View无法在滑动
        boolean showTop = dy < 0 && getScrollY() > 0 && !target.canScrollVertically(-1);
        if (hiddenTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;
            Log.i(TAG, "onNestedPreScroll: " + (hiddenTop) + "   " + (showTop) + "   " + "scrollBy：" + dy + "getScrollY:" + getScrollY());
        }
    }

    /**
     * 子View消耗剩余的距离再次返回给父布局onNestedScroll
     *
     * @param target
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(TAG, "onNestedScroll: " + target.getId() + "    " + dxConsumed + "    " + dyConsumed + "   " + dxUnconsumed + "   " + dyUnconsumed);
        scrollBy(0, dyUnconsumed);
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
//        return super.onNestedPreFling(target, velocityX, velocityY);
//        if (getScrollY() >= mTopViewHeight) return false;
//        fling((int) velocityY);
        return false;
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    private int TOP_CHILD_FLING_THRESHOLD = 3;

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
//        return super.onNestedFling(target, velocityX, velocityY, consumed);
        //如果是recyclerView 根据判断第一个元素是哪个位置可以判断是否消耗
        //这里判断如果第一个元素的位置是大于TOP_CHILD_FLING_THRESHOLD的
        //认为已经被消耗，在animateScroll里不会对velocityY<0时做处理
        if (target instanceof RecyclerView && velocityY < 0) {
            final RecyclerView recyclerView = (RecyclerView) target;
            final View firstChild = recyclerView.getChildAt(0);
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(firstChild);
            consumed = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD;
        }
        if (!consumed) {
            animateScroll(velocityY, computeDuration(0), consumed);
        } else {
            animateScroll(velocityY, computeDuration(velocityY), consumed);
        }
        return true;
    }

    /**
     * 根据速度计算滚动动画持续时间
     *
     * @param velocityY
     * @return
     */
    private int computeDuration(float velocityY) {
        final int distance;
        if (velocityY > 0) {
            distance = Math.abs(mTopViewHeight - getScrollY());
        } else {
            distance = Math.abs(mTopViewHeight - (mTopViewHeight - getScrollY()));
        }

        final int duration;
        velocityY = Math.abs(velocityY);
        if (velocityY > 0) {
            duration = 3 * Math.round(1000 * (distance / velocityY));
        } else {
            final float distanceRatio = (float) distance / getHeight();
            duration = (int) ((distanceRatio + 1) * 150);
        }

        return duration;

    }

    private void animateScroll(float velocityY, final int duration, boolean consumed) {
        final int currentOffset = getScrollY();
        final int topHeight = mTopViewHeight;
        if (mOffsetAnimator == null) {
            mOffsetAnimator = new ValueAnimator();
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedValue() instanceof Integer) {
                        scrollTo(0, (Integer) animation.getAnimatedValue());
                    }
                }
            });
        } else {
            mOffsetAnimator.cancel();
        }
        mOffsetAnimator.setDuration(Math.min(duration, 600));

        if (velocityY >= 0) {
            mOffsetAnimator.setIntValues(currentOffset, topHeight);
            mOffsetAnimator.start();
        } else {
            //如果子View没有消耗down事件 那么就让自身滑倒0位置
            if (!consumed) {
                mOffsetAnimator.setIntValues(currentOffset, 0);
                mOffsetAnimator.start();
            }

        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
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
//        super.onStopNestedScroll(child);
        mNestedParentHelper.onStopNestedScroll(child);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    @Override
    public NestedStickLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new NestedStickLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected NestedStickLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new NestedStickLayout.LayoutParams(lp);
    }

    @Override
    protected NestedStickLayout.LayoutParams generateDefaultLayoutParams() {
        return new NestedStickLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof NestedStickLayout.LayoutParams;
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public static final int scroll = 1;
        public static final int noscroll = 2;

        public int scrollType = noscroll;


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.NestedStickLayout);
            scrollType = ta.getInt(R.styleable.NestedStickLayout_scrollType, noscroll);
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }
    }


}
