package com.lazylite.play.playpage.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by DongJr on 2017/9/13.
 *
 * 解决嵌套RecyclerView设置wrap_content高度不正常问题
 */

public class KwRecyclerGridLayoutManager extends GridLayoutManager {

    public KwRecyclerGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public KwRecyclerGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public KwRecyclerGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        try{
            int height = 0;
            int childCount = getItemCount();
            if (childCount <= 0){
                super.onMeasure(recycler,state,widthSpec,heightSpec);
                return;
            }
            for (int i = 0; i < childCount; i++) {
                if (i % getSpanCount() == 0) {
                    View child = recycler.getViewForPosition(i);
                    measureChild(child, widthSpec, heightSpec);
                    int measuredHeight = child.getMeasuredHeight() + getBottomDecorationHeight(child) +
                            child.getPaddingBottom() + child.getPaddingTop();
                    height += measuredHeight;
                }
            }
            height += getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), height);
        } catch (Exception e){
            super.onMeasure(recycler,state,widthSpec,heightSpec);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            return super.scrollVerticallyBy(dy, recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
