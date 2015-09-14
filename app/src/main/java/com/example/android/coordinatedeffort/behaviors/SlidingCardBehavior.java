package com.example.android.coordinatedeffort.behaviors;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.view.View;

import com.example.android.coordinatedeffort.widget.SlidingCardLayout;

/**
 * Complex custom behavior used to accomplish the following:
 * - Lay out attached views in a staggered offset pattern
 * - Capture scrolling events to drag/slide attached views
 * - Shift sibling views as a result of movement in the captured view
 */
public class SlidingCardBehavior extends CoordinatorLayout.Behavior<SlidingCardLayout> {

    private int mInitialOffset;

    private ScrollerCompat mScroller;
    private FlingRunnable mFlingRunnable;

    public SlidingCardBehavior() {
    }

    /* Sibling View Layout Methods */

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, SlidingCardLayout child,
                                  int parentWidthMeasureSpec, int widthUsed,
                                  int parentHeightMeasureSpec, int heightUsed) {
        final int offset = getChildMeasureOffset(parent, child);
        final int measuredHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec) - heightUsed - offset;
        int childMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY);
        child.measure(parentWidthMeasureSpec, childMeasureSpec);

        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent,
                                 SlidingCardLayout child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);

        SlidingCardLayout previous = getPreviousChild(parent, child);
        if (previous != null) {
            int offset = previous.getTop() + previous.getHeaderHeight();
            child.offsetTopAndBottom(offset);
        }

        mInitialOffset = child.getTop();
        return true;
    }

    /* Scrolling View Handlers */

    //Called before a nested scroll event. Return true to declare interest
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       SlidingCardLayout child, View directTargetChild,
                                       View target, int nestedScrollAxes) {
        //We have to declare interest in the scroll to receive further events
        boolean isVertical = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        //Only capture on the view currently being scrolled
        return isVertical && child == directTargetChild;
    }

    //Called before the scrolling child consumes the event
    // We can steal all/part of the event by filling in the consumed array
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout,
                                  SlidingCardLayout child, View target,
                                  int dx, int dy,
                                  int[] consumed) {
        //When not at the top, consume all scrolling for the card
        if (child.getTop() > mInitialOffset) {
            //Tell the parent what we've consumed
            consumed[1] = scroll(child, dy,
                    mInitialOffset,
                    mInitialOffset + child.getHeight() - child.getHeaderHeight());
            shiftSiblings(coordinatorLayout, child, consumed[1]);
        }
    }

    //Called after the scrolling child consumes the event, with amount consumed
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               SlidingCardLayout child, View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        //Use any unconsumed distance to scroll the card layout
        int shift = scroll(child, dyUnconsumed,
                mInitialOffset,
                mInitialOffset + child.getHeight() - child.getHeaderHeight());
        shiftSiblings(coordinatorLayout, child, shift);
    }

    //Scroll the view and return back the actual distance scrolled
    private int scroll(View child, int dy, int minOffset, int maxOffset) {
        final int initialOffset = child.getTop();
        //Clamped new position - initial position = offset change
        int delta = clamp(initialOffset - dy, minOffset, maxOffset) - initialOffset;
        child.offsetTopAndBottom(delta);

        return -delta;
    }

    private void shiftSiblings(CoordinatorLayout parent, SlidingCardLayout child, int shift) {
        if (shift == 0) return;

        if (shift > 0) {
            //Push siblings up if overlapping
            SlidingCardLayout current = child;
            SlidingCardLayout card = getPreviousChild(parent, current);
            while (card != null) {
                int delta = getHeaderOverlap(card, current);
                if (delta > 0) {
                    card.offsetTopAndBottom(-delta);
                }

                current = card;
                card = getPreviousChild(parent, current);
            }
        } else {
            //Push siblings down if overlapping
            SlidingCardLayout current = child;
            SlidingCardLayout card = getNextChild(parent, current);
            while (card != null) {
                int delta = getHeaderOverlap(current, card);
                if (delta > 0) {
                    card.offsetTopAndBottom(delta);
                }

                current = card;
                card = getNextChild(parent, current);
            }
        }
    }

    private int getHeaderOverlap(SlidingCardLayout above, SlidingCardLayout below) {
        return (above.getTop() + above.getHeaderHeight()) - below.getTop();
    }

    /* Fling Scroll Handlers */

    //Called before the scrolling child consumes the fling.
    // We can steal the event here by returning true.
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout,
                                    SlidingCardLayout child, View target,
                                    float velocityX, float velocityY) {
        if (child.getTop() > mInitialOffset) {
            return fling(coordinatorLayout,
                    child,
                    mInitialOffset,
                    mInitialOffset + child.getHeight() - child.getHeaderHeight(),
                    -velocityY);
        }

        return false;
    }

    //Called after the scrolling child handles the fling
    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout,
                                 SlidingCardLayout child, View target,
                                 float velocityX, float velocityY, boolean consumed) {
        if (!consumed) {
            return fling(coordinatorLayout,
                    child,
                    mInitialOffset,
                    mInitialOffset + child.getHeight() - child.getHeaderHeight(),
                    -velocityY);
        }

        return false;
    }

    //Attempt to fling and return if successfully started
    private boolean fling(CoordinatorLayout parent, SlidingCardLayout layout,
                          int minOffset, int maxOffset, float velocityY) {
        if (mFlingRunnable != null) {
            layout.removeCallbacks(mFlingRunnable);
        }

        if (mScroller == null) {
            mScroller = ScrollerCompat.create(layout.getContext());
        }

        mScroller.fling(
                0, layout.getTop(), // curr
                0, Math.round(velocityY), // velocity.
                0, 0, // x
                minOffset, maxOffset); // y

        if (mScroller.computeScrollOffset()) {
            mFlingRunnable = new FlingRunnable(parent, layout);
            ViewCompat.postOnAnimation(layout, mFlingRunnable);
            return true;
        } else {
            mFlingRunnable = null;
            return false;
        }
    }

    private class FlingRunnable implements Runnable {
        private final CoordinatorLayout mParent;
        private final SlidingCardLayout mLayout;

        FlingRunnable(CoordinatorLayout parent, SlidingCardLayout layout) {
            mParent = parent;
            mLayout = layout;
        }

        @Override
        public void run() {
            if (mLayout != null && mScroller != null && mScroller.computeScrollOffset()) {
                int delta = mScroller.getCurrY() - mLayout.getTop();
                mLayout.offsetTopAndBottom(delta);
                shiftSiblings(mParent, mLayout, -delta);

                // Post ourselves so that we run on the next animation
                ViewCompat.postOnAnimation(mLayout, this);
            }
        }
    }

    /* Helper Methods */

    private int clamp(int value, int min, int max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        } else {
            return value;
        }
    }

    private SlidingCardLayout getPreviousChild(CoordinatorLayout parent, View child) {
        int cardIndex = parent.indexOfChild(child);
        for (int i = cardIndex - 1; i >= 0; i--) {
            View v = parent.getChildAt(i);
            if (v instanceof SlidingCardLayout) {
                return (SlidingCardLayout) v;
            }
        }

        return null;
    }

    private SlidingCardLayout getNextChild(CoordinatorLayout parent, View child) {
        int cardIndex = parent.indexOfChild(child);
        for (int i = cardIndex + 1; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof SlidingCardLayout) {
                return (SlidingCardLayout) v;
            }
        }

        return null;
    }

    private int getChildMeasureOffset(CoordinatorLayout parent, View child) {
        //Offset is the sum of all header heights except the target
        int offset = 0;
        for (int i=0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view != child && view instanceof SlidingCardLayout) {
                offset += ((SlidingCardLayout) view).getHeaderHeight();
            }
        }

        return offset;
    }
}
