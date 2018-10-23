package com.ccmt.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;

public class RecycleViewDividerGridLayoutManager extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerWeight = -1; // 分割线宽度,默认为1px.
    private int mDividerHeight = -1;//分割线高度，默认为1px
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    @SuppressWarnings("unused")
    public RecycleViewDividerGridLayoutManager(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     * @param orientation 列表方向
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    private RecycleViewDividerGridLayoutManager(Context context, int orientation) {
        init(context, orientation);
    }

//    /**
//     * 自定义分割线
//     *
//     * @param context
//     * @param orientation 列表方向
//     * @param drawableId  分割线图片
//     */
//    @SuppressWarnings("JavaDoc")
//    public RecycleViewDividerLinearLayoutManager(Context context, int orientation, int drawableId) {
//        mDivider = ContextCompat.getDrawable(context, drawableId);
//        mDividerWeight = mDivider.getIntrinsicWidth();
//        mDividerHeight = mDivider.getIntrinsicHeight();
//        init(context, orientation);
//    }

    @SuppressWarnings("unused")
    public RecycleViewDividerGridLayoutManager(Context context, int orientation, Drawable divider) {
        this.mDivider = divider;
        this.mDividerWeight = mDivider.getIntrinsicWidth();
        this.mDividerHeight = mDivider.getIntrinsicHeight();
        init(context, orientation);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    public RecycleViewDividerGridLayoutManager(Context context, int orientation, int dividerWeight,
                                               int dividerHeight, int dividerColor) {
        mDividerWeight = dividerWeight;
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        init(context, orientation);
    }

    @SuppressWarnings("unused")
    public RecycleViewDividerGridLayoutManager(Context context, int orientation, int dividerColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        init(context, orientation);
    }

    @SuppressWarnings("unused")
    public RecycleViewDividerGridLayoutManager(Context context, int orientation, int drawableId, int dividerColor) {
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        init(context, orientation);
    }

    private void init(Context context, int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mOrientation = orientation;

        if (mDividerWeight != -1 && mDividerHeight != -1) {
//            LogUtil.i("mDividerWeight -> " + mDividerWeight);
//            LogUtil.i("mDividerHeight -> " + mDividerHeight);
            return;
        }

        if (mDivider == null) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            if (mDivider != null) {
                mDividerWeight = mDivider.getIntrinsicWidth();
                mDividerHeight = mDivider.getIntrinsicHeight();
            } else {
                mDividerWeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1,
                        context.getResources().getDisplayMetrics());
                mDividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1,
                        context.getResources().getDisplayMetrics());
            }
        } else {
            mDividerWeight = mDivider.getIntrinsicWidth();
            mDividerHeight = mDivider.getIntrinsicHeight();
        }
//        rectShape.resize(ScreenUtils.getScreenWidth(mContext),DensityUtil.px2dip(mContext, 1));
//        LogUtil.i("mDividerWeight -> " + mDividerWeight);
//        LogUtil.i("mDividerHeight -> " + mDividerHeight);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else {
                // StaggeredGridLayoutManager 且横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
                               RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
        {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        } else if (isLastColum(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
        {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(),
                    mDivider.getIntrinsicHeight());
        }
    }

}
