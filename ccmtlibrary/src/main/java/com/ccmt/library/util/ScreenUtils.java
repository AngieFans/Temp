package com.ccmt.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Arrays;

public class ScreenUtils {

    private ScreenUtils() {
        // cannot be instantiated
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == //
                Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取包括状态栏和不包括导航栏的屏幕高度.
     * 状态栏: 不管界面有没有完全显示,也不管有没有沉浸式状态栏,该方法获取的值都会包括状态栏的静态高度.
     * 导航栏: 导航栏显示的时候,该方法获取的值不会包括导航栏静态高度.导航栏隐藏的时候,该方法获取的值会包括导航栏静态高度.
     * 不需要界面完全显示才可以拿到,属于静态行为.
     *
     * @param context
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取不包括状态栏和导航栏的屏幕高度.
     * 状态栏: 就算状态栏的实际高度是0,比如沉浸式状态栏,该方法获取的值也不会包括状态栏的静态高度.
     * 导航栏: 导航栏显示的时候,该方法获取的值不会包括导航栏静态高度.导航栏隐藏的时候,该方法获取的值会包括导航栏静态高度.
     * 该方法获取的值 = 普通窗体高度 - 状态栏的静态高度.
     * 必须界面完全显示才可以拿到,属于动态行为.
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int getScreenHeightReal(Activity activity) {
        return getContentView(activity).getHeight();
    }

    /**
     * 获取包括状态栏和导航栏的整个屏幕的完整高度
     *
     * @param activity
     * @return
     */
    @SuppressWarnings({"JavaDoc", "WeakerAccess"})
    public static int getScreenHeightFull(Activity activity) {
        int result;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealMetrics(outMetrics);
            result = outMetrics.heightPixels;
        } else {
            display.getMetrics(outMetrics);
//            int navigationHeight = getNavigationHeight(activity);
            int navigationHeight = getNavigationHeightNew(activity);
            if (navigationHeight == 0) {
                result = outMetrics.heightPixels;
            } else {
                if (isScreenOriatationPortrait(activity)) {
                    result = outMetrics.heightPixels + navigationHeight;
                } else {
                    result = outMetrics.heightPixels;
                }
            }
        }
        return result;
    }

    /**
     * 获取包括状态栏和导航栏的整个屏幕的完整宽度和高度,也可以获取屏幕的普通宽度和高度.
     *
     * @param activity
     * @param display
     * @return 第1个元素为普通宽度, 第2个元素为普通高度, 第3个元素为完整宽度, 第4个元素为完整高度.
     */
    @SuppressWarnings("JavaDoc")
    public static int[] getScreenWeightAndHeightFull(Activity activity, Display display) {
        int[] result = new int[4];
        if (display == null) {
            display = activity.getWindowManager().getDefaultDisplay();
        }
        DisplayMetrics size = new DisplayMetrics();
        display.getMetrics(size);
        result[0] = size.widthPixels;
        result[1] = size.heightPixels;
        if (Build.VERSION.SDK_INT >= 17) {
            DisplayMetrics realSize = new DisplayMetrics();
            display.getRealMetrics(realSize);
//            LogUtil.i("size -> " + size);
//            LogUtil.i("realSize -> " + realSize);
            result[2] = realSize.widthPixels;
            result[3] = realSize.heightPixels;
        } else {
            DisplayMetrics realSize = new DisplayMetrics();
            final boolean[] flag = {true};
            ReflectUtils.invokeNonStaticMethod(display, "getRealMetrics", new Class[]{DisplayMetrics.class},
                    () -> flag[0] = false, realSize);
//            LogUtil.i("size -> " + size);
//            LogUtil.i("realSize -> " + realSize);

            if (flag[0]) {
                // 反射getRealMetrics()方法成功
                if (realSize.widthPixels >= size.widthPixels) {
                    result[2] = realSize.widthPixels;
                } else {
                    LogUtil.e("反射getRealMetrics()方法返回的值不正常");
                }
                if (realSize.heightPixels >= size.heightPixels) {
                    result[3] = realSize.heightPixels;
                } else {
                    LogUtil.e("反射getRealMetrics()方法返回的值不正常");
                }
            } else {
                // 反射getRealMetrics()方法失败
                final boolean[] flag2 = {true};
                Point realSize2 = new Point();
                ReflectUtils.invokeNonStaticMethod(display, "getRealSize",
                        new Class[]{Point.class},
                        () -> flag2[0] = false, realSize2);
                if (flag2[0]) {
                    // 反射getRealSize()方法成功
                    if (realSize2.x >= size.widthPixels) {
                        result[2] = realSize2.x;
                    } else {
                        LogUtil.e("反射getRealSize()方法返回的值不正常");
                    }
                    if (realSize2.y >= size.heightPixels) {
                        result[3] = realSize2.y;
                    } else {
                        LogUtil.e("反射getRealSize()方法返回的值不正常");
                    }
                } else {
                    // 反射getRealSize()方法失败
                    final boolean[] flag3 = {true};
                    Object rawWidth = ReflectUtils.invokeNonStaticMethod(display, "getRawWidth", null,
                            () -> flag3[0] = false);
                    if (flag3[0]) {
                        // 反射getRawWidth()方法成功
                        if (rawWidth != null) {
                            int rawWidthValue = (int) rawWidth;
                            if (rawWidthValue >= size.widthPixels) {
                                result[2] = rawWidthValue;
                            } else {
                                LogUtil.e("反射getRawWidth()方法返回的值不正常");
                            }
                        }
                    } else {
                        // 反射getRawWidth()方法失败
                        flag3[0] = true;
                    }
                    Object rawHeight = ReflectUtils.invokeNonStaticMethod(display, "getRawHeight", null,
                            () -> flag3[0] = false);
                    if (flag3[0]) {
                        // 反射getRawHeight()方法成功
                        if (rawHeight != null) {
                            int rawHeightValue = (int) rawHeight;
                            if (rawHeightValue >= size.heightPixels) {
                                result[3] = rawHeightValue;
                            } else {
                                LogUtil.e("反射getRawHeight()方法返回的值不正常");
                            }
                        }
                    }
                }
            }
        }
        LogUtil.i("当前屏幕的宽度和高度 -> " + Arrays.toString(result));
        return result;
    }

    /**
     * 获取标题栏高度
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int getTitleBarHeight(Activity activity) {
        int contentTop = getContentView(activity).getTop();
        if (contentTop == 0) {
            return 0;
        }
        return contentTop - getStatusHeight(activity);
    }

    /**
     * 获取状态栏的高度,getWindowVisibleDisplayFrame()方法拿到包括标题栏但不包括状态栏的区域,
     * 必须界面完全显示才可以拿到,属于动态行为.
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int getStatusHeight(Activity activity) {
//        int contentTop = getContentView(activity).getTop();
//        if (contentTop == 0) {
//            return 0;
//        }
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取状态栏的高度,不管界面是不是有状态栏,还是隐藏了状态栏,都能拿到状态栏的高度,
     * <p>
     * 因为是反射系统dimen资源的内部类的1个静态常量,属于静态行为.
     *
     * @param context
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int getStatusHeight2(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            // Object object = clazz.newInstance();
            // Field f = clazz.getField("status_bar_height");
            // Log.i("MyLog", "f.getName() -> " + f.getName());
            // Log.i("MyLog", "f.toString() -> " + f.toString());
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(null).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    @SuppressWarnings("WeakerAccess")
    public static FrameLayout getContentView(Activity activity) {
        return (FrameLayout) activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 属于静态行为
     *
     * @param context
     * @return
     */
    @SuppressWarnings({"JavaDoc", "WeakerAccess"})
    public static int getNavigationHeight(Context context) {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
        int navigationBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar",
                "bool", "android");
        if (resourceId > 0) {
            boolean hasNav = resources.getBoolean(resourceId);
            if (hasNav) {
                resourceId = resources.getIdentifier("navigation_bar_height",
                        "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = resources
                            .getDimensionPixelSize(resourceId);
                }
            }
        }

//        if (navigationBarHeight <= 0) {
//            DisplayMetrics dMetrics = new DisplayMetrics();
//            display.getMetrics(dMetrics);
//            int screenHeight = Math.max(dMetrics.widthPixels, dMetrics.heightPixels);
//            int realHeight = 0;
//            try {
//                Method mt = display.getClass().getMethod("getRealSize", Point.class);
//                Point size = new Point();
//                mt.invoke(display, size);
//                realHeight = Math.max(size.x, size.y);
//            } catch (NoSuchMethodException e) {
//                Method mt = null;
//                try {
//                    mt = display.getClass().getMethod("getRawHeight");
//                } catch (NoSuchMethodException e2) {
//                    try {
//                        mt = display.getClass().getMethod("getRealHeight");
//                    } catch (NoSuchMethodException ignored) {
//
//                    }
//                }
//                if (mt != null) {
//                    try {
//                        realHeight = (int) mt.invoke(display);
//                    } catch (Exception ignored) {
//
//                    }
//                }
//            } catch (Exception ignored) {
//
//            }
//            // 如果是橫屏,这种计算方式是不是会有问题.
//            navigationBarHeight = realHeight - screenHeight;
//        }

        LogUtil.i("导航栏静态高度 -> " + navigationBarHeight);

//        sNavigationHeight = navigationBarHeight;

        return navigationBarHeight;
    }

    /**
     * 属于动态行为
     *
     * @param activity
     * @return
     */
    @SuppressWarnings({"JavaDoc", "WeakerAccess"})
    public static int getNavigationHeightNew(Activity activity) {
        int[] arr = ScreenUtils.getScreenWeightAndHeightFull(activity, null);
        int navigationBarHeight;
        if (ScreenUtils.isScreenOriatationPortrait(activity)) {
            navigationBarHeight = arr[3] - arr[1];
            LogUtil.i("导航栏动态高度 -> " + navigationBarHeight);
            return navigationBarHeight;
        }
        navigationBarHeight = arr[2] - arr[0];
        LogUtil.i("导航栏动态高度 -> " + navigationBarHeight);
        return navigationBarHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

}
