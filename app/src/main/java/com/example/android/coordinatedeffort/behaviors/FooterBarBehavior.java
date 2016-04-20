package com.example.android.coordinatedeffort.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.example.android.coordinatedeffort.widget.FooterBarLayout;

/**
 * Simple layout behavior that will track the state of the AppBarLayout
 * and match its offset for a corresponding footer.
 */
public class FooterBarBehavior extends CoordinatorLayout.Behavior<FooterBarLayout> {

    //Track offset for determining dependency changes
    private int mDependencyOffset;
    //Track initial layout position to properly offset child
    private int mChildInitialOffset;

    //Required to instantiate as a default behavior
    public FooterBarBehavior() {
    }

    //Required to attach behavior via XML
    public FooterBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //This is called to determine which views this behavior depends on
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   FooterBarLayout child,
                                   View dependency) {
        //We are watching changes in the AppBarLayout
        return dependency instanceof AppBarLayout;
    }

    //This is called for each change to a dependent view
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent,
                                          FooterBarLayout child,
                                          View dependency) {
        //Check if the view position has actually changed
        if (mDependencyOffset != dependency.getTop()) {
            mDependencyOffset = dependency.getTop();

            child.offsetTopAndBottom(
                    mChildInitialOffset - child.getTop() - mDependencyOffset);
            //Notify that we changed our attached child
            return true;
        }

        return false;
    }

    //This is called on each layout request.
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent,
                                 FooterBarLayout child,
                                 int layoutDirection) {
        //Lay out the view ourselves to hang to the bottom
        child.layout(parent.getLeft(),
                parent.getBottom() - child.getMeasuredHeight(),
                parent.getRight(),
                parent.getBottom());

        //Gather initial state
        mChildInitialOffset = child.getTop();

        //Tell the framework not to bother
        return true;
    }
}
