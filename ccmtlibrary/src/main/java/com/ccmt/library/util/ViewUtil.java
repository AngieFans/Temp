package com.ccmt.library.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.ccmt.library.view.RecycleViewDividerLinearLayoutManager;

import java.util.concurrent.atomic.AtomicInteger;

public class ViewUtil {

    @SuppressWarnings("unused")
    public static float obtainViewPx(Activity context, float viewPx,
                                     boolean width) {
        if (width) {
            return viewPx * ScreenUtils.getScreenWidth(context) / 720;
        } else {
            return viewPx * ScreenUtils.getScreenHeightFull(context) / 1280;
        }
    }

    @SuppressWarnings("unused")
    public static int obtainViewPx(Activity context, int viewPx, boolean width) {
        if (width) {
            return viewPx * ScreenUtils.getScreenWidth(context) / 720;
        } else {
            return viewPx * ScreenUtils.getScreenHeightFull(context) / 1280;
        }
    }

    @SuppressWarnings("unused")
    public static int obtainViewPx(Context context, int viewPx, boolean width) {
        if (width) {
            return viewPx * ScreenUtils.getScreenWidth(context) / 720;
        } else {
            return viewPx * ScreenUtils.getScreenHeight(context) / 1280;
        }
    }

    public static void setVisibility(View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    @SuppressWarnings("unused")
    public static void setText(TextView view, int resid) {
        String str = view.getResources().getString(resid);
        setText(view, str);
    }

    @SuppressWarnings("WeakerAccess")
    public static void setText(TextView view, String str) {
        if (!str.equals(view.getText().toString())) {
            view.setText(str);
        }
    }

    @SuppressWarnings("unused")
    public static void setSelected(View view, boolean select) {
        if (select) {
            if (!view.isSelected()) {
                view.setSelected(true);
            }
        } else {
            if (view.isSelected()) {
                view.setSelected(false);
            }
        }
    }

    @SuppressWarnings("unused")
    public static void setEnable(View view, boolean enabled) {
        if (view != null) {
            if (view.isEnabled() != enabled) {
                view.setEnabled(enabled);
            }
        }
    }

    @SuppressWarnings("unused")
    public static void viewPostInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 16)
            view.postInvalidateOnAnimation();
        else
            view.invalidate();
    }

    @SuppressWarnings("deprecation")
    public static void removeOnGlobalLayoutListener(
            View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressWarnings("unused")
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void initRecyclerViewLinearLayoutManager(RecyclerView recyclerView, int orientation,
                                                           int dividerColor, boolean isUseDefaultItemAnimator) {
        Context context = recyclerView.getContext();
        LinearLayoutManager layoutManager = null;
        recyclerView.setHasFixedSize(true);
        if (orientation == LinearLayoutManager.VERTICAL) {
            layoutManager = new LinearLayoutManager(context);
//            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(new RecycleViewDividerLinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, dividerColor));
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.addItemDecoration(new RecycleViewDividerLinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, dividerColor));
        }
        recyclerView.setLayoutManager(layoutManager);
        if (isUseDefaultItemAnimator) {
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    @SuppressWarnings("unused")
    public static void initRecyclerViewLinearLayoutManager(RecyclerView recyclerView, int orientation,
                                                           int dividerColor) {
        initRecyclerViewLinearLayoutManager(recyclerView, orientation, dividerColor, true);
    }

}
