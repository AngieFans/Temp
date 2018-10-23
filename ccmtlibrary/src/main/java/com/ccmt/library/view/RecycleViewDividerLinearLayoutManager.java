package com.ccmt.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by huzd on 2017/5/16.
 */

public class RecycleViewDividerLinearLayoutManager extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerWeight = -1; // 分割线宽度,默认为1px.
    private int mDividerHeight = -1;//分割线高度，默认为1px
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     * @param orientation 列表方向
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    private RecycleViewDividerLinearLayoutManager(Context context, int orientation) {
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
    public RecycleViewDividerLinearLayoutManager(Context context, int orientation, Drawable divider) {
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
    public RecycleViewDividerLinearLayoutManager(Context context, int orientation, int dividerWeight, int dividerHeight, int dividerColor) {
        mDividerWeight = dividerWeight;
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        init(context, orientation);
    }

    public RecycleViewDividerLinearLayoutManager(Context context, int orientation, int dividerColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        init(context, orientation);
    }

    @SuppressWarnings("unused")
    public RecycleViewDividerLinearLayoutManager(Context context, int orientation, int drawableId, int dividerColor) {
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


    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 以下代码不用调用了.
//        super.getItemOffsets(outRect, view, parent, state);

        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, mDividerWeight, 0);
        }
//        if (mOrientation == LinearLayoutManager.VERTICAL) {
//            outRect.set(0, 0, mDividerWeight, mDividerHeight);
//        } else {
//            outRect.set(0, 0, mDividerWeight, mDividerHeight);
//        }
//        if (mOrientation == LinearLayoutManager.VERTICAL) {
//            outRect.set(0, 0, mDividerWeight, 0);
//        } else {
//            outRect.set(0, 0, 0, mDividerHeight);
//        }
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    //绘制横向 item 分割线
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //绘制纵向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
