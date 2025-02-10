package com.lazylite.play.playpage.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lazylite.media.Media;
import com.lazylite.mod.utils.ScreenUtility;

public class LeftRightPaddingDecoration extends RecyclerView.ItemDecoration {
    private int padding;
    public LeftRightPaddingDecoration(float dp) {
        this.padding = ScreenUtility.dip2px(Media.getContext(), dp);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        if (childAdapterPosition == 0 ) {
            outRect.left = padding;
        } else if (parent.getAdapter() != null && childAdapterPosition == parent.getAdapter().getItemCount() - 1) {
            outRect.right = padding;
        }
    }
}
