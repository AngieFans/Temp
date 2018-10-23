package com.ccmt.library.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Log记录类
 */
public class LogUtil {

    public static boolean sIsDebug;
    private static final String TAG = "MyLog";

    static {
        try {
            sIsDebug = (boolean) ReflectUtils.obtainStaticFieldValue(Class.forName("com.jj.game.boost.BuildConfig"), "DEBUG");
        } catch (ClassNotFoundException e) {
            android.util.Log.e(TAG, getFileLineMethod() + e.getMessage());
        }
    }

    public static void i(String TAG, String msg) {
        if (sIsDebug) {
            android.util.Log.i(TAG, "[" + getFileLineMethod() + "]" + msg);
        }
    }

    public static void i(String msg) {
        if (sIsDebug) {
            android.util.Log.i(TAG, msg);
        }
    }

    @SuppressWarnings("unused")
    public static void d(String TAG, String method, String msg) {
        android.util.Log.d(TAG, "[" + method + "]" + msg);
    }

    @SuppressWarnings("unused")
    public static void d(String TAG, String msg) {
        if (sIsDebug) {
            android.util.Log.d(TAG, "[" + getFileLineMethod() + "]" + msg);
        }
    }

    @SuppressWarnings("unused")
    public static void d(String msg) {
        if (sIsDebug) {
            android.util.Log.d(_FILE_(), "[" + getLineMethod() + "]" + msg);
        }
    }

    @SuppressWarnings("unused")
    public static void e(String msg, Exception e) {
        if (sIsDebug) {
            android.util.Log.e(TAG, getFileLineMethod() + msg, e);
        }
    }

    public static void e(String msg) {
        if (sIsDebug) {
            android.util.Log.e(TAG, getFileLineMethod() + msg);
        }
    }

    @SuppressWarnings("unused")
    public static void e(String TAG, String msg, Exception e) {
        if (sIsDebug) {
            android.util.Log.e(TAG, msg, e);
        }
    }

    @SuppressWarnings("unused")
    public static void e(String TAG, String msg) {
        if (sIsDebug) {
            android.util.Log.e(TAG, getLineMethod() + msg);
        }
    }

    private static String getFileLineMethod() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        return "[" +
                traceElement.getFileName() + " | " +
                traceElement.getLineNumber() + " | " +
                traceElement.getMethodName() + "]";
    }

    private static String getLineMethod() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        return "[" +
                traceElement.getLineNumber() + " | " +
                traceElement.getMethodName() + "]";
    }

    private static String _FILE_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        return traceElement.getFileName();
    }
//
//    public static String _FUNC_() {
//        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
//        return traceElement.getMethodName();
//    }
//
//    public static int _LINE_() {
//        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
//        return traceElement.getLineNumber();
//    }
//
//    public static String _TIME_() {
//        Date now = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
//                Locale.getDefault());
//        return sdf.format(now);
//    }

    /**
     * 这是测试代码,做屏幕适配有时想看看当前手机的分辨率,调用该方法就可以.
     *
     * @param activity
     */
    @SuppressWarnings({"JavaDoc", "unused", "WeakerAccess"})
    public static void showScreenInfo(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(outMetrics);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
//        int widthPixels = outMetrics.widthPixels;
        int widthPixels = point.x;
        int screenWidth = ScreenUtils.getScreenWidth(activity);
//        int heightPixels = outMetrics.heightPixels;
        int heightPixels = point.y;
        int screenHeight = ScreenUtils.getScreenHeight(activity);
        int screenHeightReal = ScreenUtils.getScreenHeightReal(activity);
        int statusHeight = ScreenUtils.getStatusHeight(activity);
        int statusHeight2 = ScreenUtils.getStatusHeight2(activity);
        int statusHeightDip = DensityUtil.px2dip(activity, statusHeight2);
        int titleBarHeight = ScreenUtils.getTitleBarHeight(activity);
        int navigationHeight = ScreenUtils.getNavigationHeight(activity);
        int navigationHeightDip = DensityUtil.px2dip(activity, navigationHeight);
        int navigationHeightNew = ScreenUtils.getNavigationHeightNew(activity);
        int navigationHeightNewDip = DensityUtil.px2dip(activity, navigationHeightNew);
        i("widthPixels -> " + widthPixels);
        i("screenWidth -> " + screenWidth);
        i("heightPixels -> " + heightPixels);
        i("screenHeight -> " + screenHeight);
        i("screenHeightReal -> " + screenHeightReal);
        i("statusHeight -> " + statusHeight);
        i("statusHeight2 -> " + statusHeight2);
        i("statusHeightDip -> " + statusHeightDip);
        i("titleBarHeight -> " + titleBarHeight);
        i("navigationHeight -> " + navigationHeight);
        i("navigationHeightDip -> " + navigationHeightDip);
        i("navigationHeight -> " + navigationHeightNew);
        i("navigationHeightDip -> " + navigationHeightNewDip);
    }

    /**
     * 这是测试代码,显示相关文件路径.
     *
     * @param context
     */
    @SuppressWarnings({"JavaDoc", "unused", "WeakerAccess"})
    public static void showFilePath(Context context) {
        i("context.getApplicationInfo().nativeLibraryDir -> " + context.getApplicationInfo().nativeLibraryDir);
        i("context.getApplicationInfo().sourceDir -> " + context.getApplicationInfo().sourceDir);
        i("context.getApplicationInfo().publicSourceDir -> " + context.getApplicationInfo().publicSourceDir);
        i("context.getPackageCodePath() -> " + context.getPackageCodePath());
        i("context.getPackageResourcePath() -> " + context.getPackageResourcePath());
//        String appPathAtSystem = RootUtil.obtainAppPathAtSystem(context.getPackageName(), true);
//        String appPath = RootUtil.obtainAppPathAtSystem(context.getPackageName(), false);
//        i("appPathAtSystem -> " + appPathAtSystem);
//        i("appPath -> " + appPath);
//        if (appPathAtSystem != null) {
//            i("new File(appPathAtSystem).length() -> " + new File(appPathAtSystem).length());
//            i("new File(appPathAtSystem).exists() -> " + new File(appPathAtSystem).exists());
//        }
//        if (appPath != null) {
//            i("new File(appPath).length() -> " + new File(appPath).length());
//            i("new File(appPath).exists() -> " + new File(appPath).exists());
//        }
    }

    @SuppressWarnings("unused")
    public static void showMethodInfo(Object obj) {
        Class<?> cla = obj.getClass();
        Method[] ms = cla.getDeclaredMethods();
        for (Method m : ms) {
            i("ms[i].toString() -> " + m.toString());
        }
    }

    @SuppressWarnings("unused")
    public static void showFieldInfo(Object obj) {
        Class<?> cla = obj.getClass();
        Field[] fs = cla.getDeclaredFields();
        for (Field f : fs) {
            try {
                f.setAccessible(true);
                i(f + " -> " + f.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    public static void showFieldInfo(Class<?> cla) {
        Field[] fs = cla.getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            i(f + " -> " + f);
        }
    }

    @SuppressWarnings("unused")
    public static void showCollectionInfo(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            for (Object aCollection : collection) {
                i("aCollection -> " + aCollection);
            }
        } else if (obj instanceof Map) {
            Map map = (Map) obj;
            Map.Entry next;
            for (Object o : map.entrySet()) {
                next = (Map.Entry) o;
                i(next.getKey() + " -> " + next.getValue());
            }
        } else if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            Object value;
            for (int i = 0; i < length; i++) {
                value = Array.get(obj, i);
                i("value -> " + value);
            }
        }
    }

    /**
     * 根据指定包名显示app的相关信息
     *
     * @param context
     * @param packageName
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    public static void showApplicationInfo(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                i("Arrays.toString(applicationInfo.splitSourceDirs) -> " + Arrays.toString(applicationInfo.splitSourceDirs));
            }
            i("applicationInfo.loadLabel(packageManager).toString() -> " + applicationInfo.loadLabel(packageManager).toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                i("Arrays.toString(applicationInfo.splitPublicSourceDirs) -> " + Arrays.toString(applicationInfo.splitPublicSourceDirs));
            }
            i("applicationInfo.name -> " + applicationInfo.name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                LogUtil.i("Arrays.toString(applicationInfo.splitSourceDirs)) -> " + Arrays.toString(applicationInfo.splitSourceDirs));
            }
            i("applicationInfo.publicSourceDir -> " + applicationInfo.publicSourceDir);
            i("applicationInfo.sourceDir -> " + applicationInfo.sourceDir);
            i("applicationInfo.backupAgentName -> " + applicationInfo.backupAgentName);
            i("applicationInfo.className -> " + applicationInfo.className);
            i("applicationInfo.dataDir -> " + applicationInfo.dataDir);
            i("applicationInfo.manageSpaceActivityName -> " + applicationInfo.manageSpaceActivityName);
            i("applicationInfo.nativeLibraryDir -> " + applicationInfo.nativeLibraryDir);
            i("applicationInfo.permission -> " + applicationInfo.permission);
            i("applicationInfo.processName -> " + applicationInfo.processName);
            i("applicationInfo.taskAffinity -> " + applicationInfo.taskAffinity);
            i("applicationInfo.compatibleWidthLimitDp -> " + applicationInfo.compatibleWidthLimitDp);
            i("applicationInfo.enabled -> " + applicationInfo.enabled);
            i("applicationInfo.loadDescription(packageManager) -> " + applicationInfo.loadDescription(packageManager));
            i("Arrays.toString(applicationInfo.sharedLibraryFiles) -> " + Arrays.toString(applicationInfo.sharedLibraryFiles));
            i("applicationInfo.targetSdkVersion -> " + applicationInfo.targetSdkVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HardwareIds")
    @SuppressWarnings({"unused", "deprecation"})
    public static void showPhoneInfo() {
        i("Build.MANUFACTURER -> " + Build.MANUFACTURER);
        i("Build.MODEL -> " + Build.MODEL);
        i("Build.BOARD -> " + Build.BOARD);
        i("Build.BOOTLOADER -> " + Build.BOOTLOADER);
        i("Build.DEVICE -> " + Build.DEVICE);
        i("Build.DISPLAY -> " + Build.DISPLAY);
        i("Build.FINGERPRINT -> " + Build.FINGERPRINT);
        i("Build.getRadioVersion() -> " + Build.getRadioVersion());
        i("Build.HARDWARE -> " + Build.HARDWARE);
        i("Build.HOST -> " + Build.HOST);
        i("Build.ID -> " + Build.ID);
        i("Build.PRODUCT -> " + Build.PRODUCT);
        i("Build.SERIAL -> " + Build.SERIAL);
        i("Build.TAGS -> " + Build.TAGS);
        i("Build.TYPE -> " + Build.TYPE);
        i("Build.UNKNOWN -> " + Build.UNKNOWN);
        i("Build.USER -> " + Build.USER);
        i("Build.CPU_ABI -> " + Build.CPU_ABI);
        i("Build.CPU_ABI2 -> " + Build.CPU_ABI2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i("Arrays.toString(Build.SUPPORTED_32_BIT_ABIS) -> " + Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i("Arrays.toString(Build.SUPPORTED_64_BIT_ABIS) -> " + Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i("Arrays.toString(Build.SUPPORTED_ABIS) -> " + Arrays.toString(Build.SUPPORTED_ABIS));
        }
        i("Build.TIME -> " + Build.TIME);
    }

    @SuppressWarnings("unused")
    public static void showExternalStorageInfo() {
        File downloadDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_DOWNLOADS);
        File downloadDir2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        i("downloadDir.getAbsolutePath() -> " + downloadDir.getAbsolutePath());
        i("downloadDir2.getAbsolutePath() -> " + downloadDir2.getAbsolutePath());
        i("downloadDir.exists() -> " + downloadDir.exists());
        i("downloadDir2.exists() -> " + downloadDir2.exists());
        i("Environment.getExternalStorageState() -> " + Environment.getExternalStorageState());
        String externalStorageState = null;
        String externalStorageState2 = null;
        if (Build.VERSION.SDK_INT >= 21) {
            externalStorageState = Environment.getExternalStorageState(downloadDir);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            externalStorageState2 = Environment.getExternalStorageState(downloadDir2);
        }
        i("externalStorageState -> " + externalStorageState);
        i("externalStorageState2 -> " + externalStorageState2);
    }

    @SuppressWarnings("unused")
    public static void showMemeryInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        LogUtil.i("mi.availMem -> " + mi.availMem);
        if (Build.VERSION.SDK_INT >= 16) {
            LogUtil.i("mi.totalMem -> " + mi.totalMem);
        }
        LogUtil.i("am.getMemoryClass() -> " + am.getMemoryClass());
        LogUtil.i("am.getLargeMemoryClass() -> " + am.getLargeMemoryClass());
        Runtime runtime = Runtime.getRuntime();
        LogUtil.i("runtime.maxMemory() -> " + runtime.maxMemory());
        LogUtil.i("runtime.totalMemory() -> " + runtime.totalMemory());
        LogUtil.i("runtime.freeMemory() -> " + runtime.freeMemory());
    }

//    public static void showStatusBarNotificationInfo(IStatusBarNotification sbn) {
//        Bundle extras;
//        String notificationTitle = null;
//        Bitmap notificationLargeIcon = null;
//        Bitmap notificationSmallIcon = null;
//        CharSequence notificationText = null;
//        CharSequence notificationSubText = null;
//        if (Build.VERSION.SDK_INT >= 19) {
//            extras = sbn.getNotification().extras;
//            if (extras != null) {
//                notificationTitle = extras.getString(Notification.EXTRA_TITLE);
//                notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
//                notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
//                notificationLargeIcon = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
//                notificationSmallIcon = extras.getParcelable(Notification.EXTRA_SMALL_ICON);
//            }
//        }
//        LogUtil.i("notificationTitle -> " + notificationTitle);
//        LogUtil.i("notificationText -> " + notificationText);
//        LogUtil.i("notificationSubText -> " + notificationSubText);
//        LogUtil.i("(notificationLargeIcon != null) -> " + (notificationLargeIcon != null));
//        LogUtil.i("(notificationSmallIcon != null) -> " + (notificationSmallIcon != null));
//    }

    @SuppressWarnings("unused")
    public static void showNodeInfo(AccessibilityNodeInfo node) {
        String text;
        AccessibilityNodeInfo accessibilityNodeInfo;
        CharSequence temp;
        if (node != null) {
            LogUtil.i("node.getClassName().toString() -> " + node.getClassName().toString());
            LogUtil.i("node.getText() -> " + node.getText());
            LogUtil.i("node.getContentDescription() -> " + node.getContentDescription());
            int childCount = node.getChildCount();
            LogUtil.i("node子节点的数量 -> " + childCount);
            for (int i = 0; i < childCount; i++) {
                accessibilityNodeInfo = node.getChild(i);
                LogUtil.i("------");
                LogUtil.i("node子节点的className -> " + accessibilityNodeInfo.getClassName().toString());
                temp = accessibilityNodeInfo.getText();
                if (temp != null) {
                    text = temp.toString();
                    LogUtil.i("node子节点的text -> " + text);
                }
                temp = accessibilityNodeInfo.getContentDescription();
                if (temp != null) {
                    text = temp.toString();
                    LogUtil.i("node子节点的contentDescription -> " + text);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static void showActiveNetworkInfo(Context context) {
        NetworkInfo networkInfo = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                networkInfo = connectivityManager.getNetworkInfo(network);
            }
        } else {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null) {
            i("networkInfo.getExtraInfo() -> " + networkInfo.getExtraInfo());
            i("networkInfo.getType() -> " + networkInfo.getType());
            i("networkInfo.getTypeName() -> " + networkInfo.getTypeName());
            i("networkInfo.getSubtype() -> " + networkInfo.getSubtype());
            i("networkInfo.getSubtypeName() -> " + networkInfo.getSubtypeName());
            i("networkInfo.getReason() -> " + networkInfo.getReason());
            i("networkInfo.getState() -> " + networkInfo.getState());
            i("networkInfo.getDetailedState().name() -> " + networkInfo.getDetailedState().name());
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    public static void showAllNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            Network[] ns = connectivityManager.getAllNetworks();
            for (Network network : ns) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                i("network -> " + network);
                i("networkCapabilities -> " + networkCapabilities);
                if (network != null) {
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                    if (networkInfo != null) {
                        i("networkInfo.getExtraInfo() -> " + networkInfo.getExtraInfo());
                        i("networkInfo.getType() -> " + networkInfo.getType());
                        i("networkInfo.getTypeName() -> " + networkInfo.getTypeName());
                        i("networkInfo.getSubtype() -> " + networkInfo.getSubtype());
                        i("networkInfo.getSubtypeName() -> " + networkInfo.getSubtypeName());
                        i("networkInfo.getReason() -> " + networkInfo.getReason());
                        i("networkInfo.getState() -> " + networkInfo.getState());
                        i("networkInfo.getDetailedState().name() -> " + networkInfo.getDetailedState().name());
                    }
                }
            }
        } else {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo networkInfo : networkInfos) {
                if (networkInfo != null) {
                    i("networkInfo.getExtraInfo() -> " + networkInfo.getExtraInfo());
                    i("networkInfo.getType() -> " + networkInfo.getType());
                    i("networkInfo.getTypeName() -> " + networkInfo.getTypeName());
                    i("networkInfo.getSubtype() -> " + networkInfo.getSubtype());
                    i("networkInfo.getSubtypeName() -> " + networkInfo.getSubtypeName());
                    i("networkInfo.getReason() -> " + networkInfo.getReason());
                    i("networkInfo.getState() -> " + networkInfo.getState());
                    i("networkInfo.getDetailedState().name() -> " + networkInfo.getDetailedState().name());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static void showBoundNetworkInfo(Context context) {
        NetworkInfo networkInfo = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            Network network = connectivityManager.getBoundNetworkForProcess();
            if (network != null) {
                networkInfo = connectivityManager.getNetworkInfo(network);
            }
        } else {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null) {
            i("networkInfo.getExtraInfo() -> " + networkInfo.getExtraInfo());
            i("networkInfo.getType() -> " + networkInfo.getType());
            i("networkInfo.getTypeName() -> " + networkInfo.getTypeName());
            i("networkInfo.getSubtype() -> " + networkInfo.getSubtype());
            i("networkInfo.getSubtypeName() -> " + networkInfo.getSubtypeName());
            i("networkInfo.getReason() -> " + networkInfo.getReason());
            i("networkInfo.getState() -> " + networkInfo.getState());
            i("networkInfo.getDetailedState().name() -> " + networkInfo.getDetailedState().name());
        }
    }

    @SuppressWarnings({"unused", "DanglingJavadoc"})
    public static void showCellInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfos;
        if (Build.VERSION.SDK_INT >= 17) {
            cellInfos = telephonyManager.getAllCellInfo();
            if (cellInfos != null) {
                CellInfo cellInfo;

                CellInfoGsm cellInfoGsm;
                CellIdentityGsm cellIdentityGsm;
                CellSignalStrengthGsm cellSignalStrengthGsm;

                CellInfoCdma cellInfoCdma;
                CellIdentityCdma cellIdentityCdma;
                CellSignalStrengthCdma cellSignalStrengthCdma;

                CellInfoWcdma cellInfoWcdma;
                CellIdentityWcdma cellIdentityWcdma;
                CellSignalStrengthWcdma cellSignalStrengthWcdma;

                CellInfoLte cellInfoLte;
                CellIdentityLte cellIdentityLte;
                CellSignalStrengthLte cellSignalStrengthLte;

                int size = cellInfos.size();
                if (size == 0) {
                    return;
                }
                for (int i = 0; i < size; i++) {
                    i("------");
                    cellInfo = cellInfos.get(i);
                    i("cellInfo -> " + cellInfo);
                    if (cellInfo instanceof CellInfoGsm) {
                        // gsm
                        cellInfoGsm = (CellInfoGsm) cellInfo;

                        /**
                         * 手机sim卡相信信息
                         */
                        cellIdentityGsm = cellInfoGsm.getCellIdentity();
                        if (Build.VERSION.SDK_INT >= 24) {
                            // gsm射频通道编号
                            i("cellIdentityGsm.getArfcn() -> " + cellIdentityGsm.getArfcn());
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            // 基站码
                            i("cellIdentityGsm.getBsic() -> " + cellIdentityGsm.getBsic());
                        }

                        // 0到65535的gsm手机身份描述
                        i("cellIdentityGsm.getCid() -> " + cellIdentityGsm.getCid());

                        // 0到65535的位置区域码
                        i("cellIdentityGsm.getLac() -> " + cellIdentityGsm.getLac());

                        // 0到999的移动国家码
                        i("cellIdentityGsm.getMcc() -> " + cellIdentityGsm.getMcc());

                        // 0到999的移动网络码
                        i("cellIdentityGsm.getMnc() -> " + cellIdentityGsm.getMnc());

                        /**
                         * 手机流量信号强度相关信息
                         */
                        cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();

                        // 0到31的asu值,99为未知.
                        i("cellSignalStrengthGsm.getAsuLevel() -> " + cellSignalStrengthGsm.getAsuLevel());

                        // Integer.MAX_VALUE为未知.
                        i("cellSignalStrengthGsm.getDbm() -> " + cellSignalStrengthGsm.getDbm());

                        // 0到4的level值,没有未知值.
                        i("cellSignalStrengthGsm.getLevel() -> " + cellSignalStrengthGsm.getLevel());
                    } else if (cellInfo instanceof CellInfoCdma) {
                        // cdma
                        cellInfoCdma = (CellInfoCdma) cellInfo;

                        /**
                         * 手机sim卡相信信息
                         */
                        cellIdentityCdma = cellInfoCdma.getCellIdentity();

                        // 0到65535的基站id
                        i("cellIdentityCdma.getBasestationId() -> " + cellIdentityCdma.getBasestationId());

                        // 基站纬度
                        i("cellIdentityCdma.getLatitude() -> " + cellIdentityCdma.getLatitude());

                        // 基站经度
                        i("cellIdentityCdma.getLongitude() -> " + cellIdentityCdma.getLongitude());

                        // 0到65535的网络id
                        i("cellIdentityCdma.getNetworkId() -> " + cellIdentityCdma.getNetworkId());

                        // 0到32767的系统id
                        i("cellIdentityCdma.getSystemId() -> " + cellIdentityCdma.getSystemId());

                        /**
                         * 手机流量信号强度相关信息
                         */
                        cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();

                        // 0到97的asu值,99为未知.
                        i("cellSignalStrengthCdma.getAsuLevel() -> " + cellSignalStrengthCdma.getAsuLevel());

                        i("cellSignalStrengthCdma.getDbm() -> " + cellSignalStrengthCdma.getDbm());

                        // 0到4的level值,没有未知值.
                        i("cellSignalStrengthCdma.getLevel() -> " + cellSignalStrengthCdma.getLevel());

                        // 0到4的cdma level值,没有未知值.
                        i("cellSignalStrengthCdma.getCdmaLevel() -> " + cellSignalStrengthCdma.getCdmaLevel());

                        // cdma RSSI dbm值
                        i("cellSignalStrengthCdma.getCdmaDbm() -> " + cellSignalStrengthCdma.getCdmaDbm());

                        // cdma Ec/Io值
                        i("cellSignalStrengthCdma.getCdmaEcio() -> " + cellSignalStrengthCdma.getCdmaEcio());

                        // 0到4的evdo level值,没有未知值.
                        i("cellSignalStrengthCdma.getEvdoLevel() -> " + cellSignalStrengthCdma.getEvdoLevel());

                        // evdo RSSI dbm值
                        i("cellSignalStrengthCdma.getEvdoDbm() -> " + cellSignalStrengthCdma.getEvdoDbm());

                        // evdo Ec/Io值
                        i("cellSignalStrengthCdma.getEvdoEcio() -> " + cellSignalStrengthCdma.getEvdoEcio());

                        // 0到8的evdo噪声比值
                        i("cellSignalStrengthCdma.getEvdoSnr() -> " + cellSignalStrengthCdma.getEvdoSnr());
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        // wcdma
                        cellInfoWcdma = (CellInfoWcdma) cellInfo;

                        /**
                         * 手机sim卡相信信息
                         */
                        if (Build.VERSION.SDK_INT >= 18) {
                            cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                            if (Build.VERSION.SDK_INT >= 24) {
                                // UMTS射频通道编号
                                i("cellIdentityWcdma.getUarfcn() -> " + cellIdentityWcdma.getUarfcn());
                            }

                            // 0到268435455的UMTS手机身份描述
                            i("cellIdentityWcdma.getCid() -> " + cellIdentityWcdma.getCid());

                            // 0到65535的位置区域码
                            i("cellIdentityWcdma.getLac() -> " + cellIdentityWcdma.getLac());

                            // 0到999的移动国家码
                            i("cellIdentityWcdma.getMcc() -> " + cellIdentityWcdma.getMcc());

                            // 0到999的移动网络码
                            i("cellIdentityWcdma.getMnc() -> " + cellIdentityWcdma.getMnc());

                            // 0到511的UMTS主要的不规则的代码描述
                            i("cellIdentityWcdma.getPsc() -> " + cellIdentityWcdma.getPsc());
                        }

                        /**
                         * 手机流量信号强度相关信息
                         */
                        if (Build.VERSION.SDK_INT >= 18) {
                            cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

                            // 0到31的asu值,99为未知.
                            i("cellSignalStrengthWcdma.getAsuLevel() -> " + cellSignalStrengthWcdma.getAsuLevel());

                            // Integer.MAX_VALUE为未知
                            i("cellSignalStrengthWcdma.getDbm() -> " + cellSignalStrengthWcdma.getDbm());

                            // 0到4的level值,没有未知值.
                            i("cellSignalStrengthWcdma.getLevel() -> " + cellSignalStrengthWcdma.getLevel());
                        }
                    } else if (cellInfo instanceof CellInfoLte) {
                        // lte
                        cellInfoLte = (CellInfoLte) cellInfo;

                        /**
                         * 手机sim卡相信信息
                         */
                        cellIdentityLte = cellInfoLte.getCellIdentity();
                        if (Build.VERSION.SDK_INT >= 24) {
                            // lte射频通道编号
                            i("cellIdentityLte.getEarfcn() -> " + cellIdentityLte.getEarfcn());
                        }

                        // lte手机身份描述
                        i("cellIdentityLte.getCi() -> " + cellIdentityLte.getCi());

                        // 跟踪区域码
                        i("cellIdentityLte.getTac() -> " + cellIdentityLte.getTac());

                        // 0到999的移动国家码
                        i("cellIdentityLte.getMcc() -> " + cellIdentityLte.getMcc());

                        // 0到999的移动网络码
                        i("cellIdentityLte.getMnc() -> " + cellIdentityLte.getMnc());

                        // 0到503的物理手机id
                        i("cellIdentityLte.getPci() -> " + cellIdentityLte.getPci());

                        /**
                         * 手机流量信号强度相关信息
                         */
                        cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();

                        // 0到97的asu值,99为未知.
                        i("cellSignalStrengthLte.getAsuLevel() -> " + cellSignalStrengthLte.getAsuLevel());

                        i("cellSignalStrengthLte.getDbm() -> " + cellSignalStrengthLte.getDbm());

                        // 0到4的level值,没有未知值.
                        i("cellSignalStrengthLte.getLevel() -> " + cellSignalStrengthLte.getLevel());
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static void showSignalStrengthInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @SuppressWarnings("DanglingJavadoc")
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                i("onSignalStrengthsChanged()");

                /**
                 * gsm
                 */
                // 0到31,99为未知.
                Integer gsmAsuLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getGsmAsuLevel", null);
                i("gsmAsuLevel -> " + gsmAsuLevel);

                // 0到4
                Integer gsmLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getGsmLevel", null);
                i("gsmLevel -> " + gsmLevel);

                Integer gsmDbm = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getGsmDbm", null);
                i("gsmDbm -> " + gsmDbm);

                // 0到31的gsm信号强度
                i("signalStrength.getGsmSignalStrength() -> " + signalStrength.getGsmSignalStrength());

                // 0到7的gsm比特误码率
                i("signalStrength.getGsmBitErrorRate() -> " + signalStrength.getGsmBitErrorRate());

                /**
                 * cdma
                 */
                // 0到31,99为未知.
                Integer cdmaAsuLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getCdmaAsuLevel", null);
                i("cdmaAsuLevel -> " + cdmaAsuLevel);

                // 0到4
                Integer cdmaLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getCdmaLevel", null);
                i("cdmaLevel -> " + cdmaLevel);

                // cdma RSSI dbm值
                i("signalStrength.getCdmaDbm() -> " + signalStrength.getCdmaDbm());

                // cdma Ec/Io值
                i("signalStrength.getCdmaEcio() -> " + signalStrength.getCdmaEcio());

                /**
                 * evdo
                 */
                // 0到31,99为未知.
                Integer evdoAsuLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getEvdoAsuLevel", null);
                i("evdoAsuLevel -> " + evdoAsuLevel);

                // 0到4
                Integer evdoLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getEvdoLevel", null);
                i("evdoLevel -> " + evdoLevel);

                // evdo RSSI dbm值
                i("signalStrength.getEvdoDbm() -> " + signalStrength.getEvdoDbm());

                // evdo Ec/Io值
                i("signalStrength.getEvdoEcio() -> " + signalStrength.getEvdoEcio());

                // 0到8的evdo噪声比值
                i("signalStrength.getEvdoSnr() -> " + signalStrength.getEvdoSnr());

                /**
                 * tdscdma
                 */
                // 255为未知
                Integer tdScdmaAsuLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getTdScdmaAsuLevel", null);
                i("tdScdmaAsuLevel -> " + tdScdmaAsuLevel);

                // 0到4
                Integer tdScdmaLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getTdScdmaLevel", null);
                i("tdScdmaLevel -> " + tdScdmaLevel);

                Integer tdScdmaDbm = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getTdScdmaDbm", null);
                i("tdScdmaDbm -> " + tdScdmaDbm);

                /**
                 * lte
                 */
                // 255为未知
                Integer lteAsuLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteAsuLevel", null);
                i("lteAsuLevel -> " + lteAsuLevel);

                // 0到4
                Integer lteLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteLevel", null);
                i("lteLevel -> " + lteLevel);

                Integer lteDbm = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteDbm", null);
                i("lteDbm -> " + lteDbm);

                Integer lteSignalStrength = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteSignalStrength", null);
                i("lteSignalStrength -> " + lteSignalStrength);

                Integer lteCqi = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteCqi", null);
                i("lteCqi -> " + lteCqi);

                Integer lteRsrp = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteRsrp", null);
                i("lteRsrp -> " + lteRsrp);

                Integer lteRsrq = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteRsrq", null);
                i("lteRsrq -> " + lteRsrq);

                Integer lteRssnr = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLteRssnr", null);
                i("lteRssnr -> " + lteRssnr);

                /**
                 * 其他
                 */
                // 0到31,99或255为未知.
                Integer asuLevel = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getAsuLevel", null);
                i("asuLevel -> " + asuLevel);

                // 0到4,小米手机出现过最高为5的情况.
                Integer level;
                if (Build.VERSION.SDK_INT >= 23) {
                    level = signalStrength.getLevel();
                } else {
                    level = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getLevel", null);
                }
                i("level -> " + level);

                Integer dbm = (Integer) ReflectUtils.invokeNonStaticMethod(signalStrength, "getDbm", null);
                i("dbm -> " + dbm);

                i("signalStrength.isGsm() -> " + signalStrength.isGsm());
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @SuppressWarnings("unused")
    public static void showNetworkInterfaceInfo() {
        Enumeration<NetworkInterface> niList;
        try {
            niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                ArrayList<NetworkInterface> list = Collections.list(niList);
                for (NetworkInterface intf : list) {
                    i("------");
                    i("intf.getName() -> " + intf.getName());
                    i("intf.getDisplayName() -> " + intf.getDisplayName());
                    if (Build.VERSION.SDK_INT >= 19) {
                        i("intf.getIndex() -> " + intf.getIndex());
                    }
                    Enumeration<InetAddress> inetAddresses = intf.getInetAddresses();
                    InetAddress inetAddress;
                    while (inetAddresses.hasMoreElements()) {
                        inetAddress = inetAddresses.nextElement();
                        i("inetAddress -> " + inetAddress);
                    }
                    i("intf.getMTU() -> " + intf.getMTU());
                    i("intf.getParent() -> " + intf.getParent());
                    Enumeration<NetworkInterface> subInterfaces = intf.getSubInterfaces();
                    NetworkInterface subInterface;
                    while (subInterfaces.hasMoreElements()) {
                        subInterface = subInterfaces.nextElement();
                        i("subInterface -> " + subInterface);
                    }
                    i("intf.isLoopback() -> " + intf.isLoopback());
                    i("intf.isPointToPoint() -> " + intf.isPointToPoint());
                    i("intf.isUp() -> " + intf.isUp());
                    i("intf.isVirtual() -> " + intf.isVirtual());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void showInetAddressInfo(InetAddress inetAddress) {
        i("inetAddress.getCanonicalHostName() -> " + inetAddress.getCanonicalHostName());
        i("inetAddress.getHostAddress() -> " + inetAddress.getHostAddress());
        i("inetAddress.getHostName() -> " + inetAddress.getHostName());
        i("Arrays.toString(inetAddress.getAddress()) -> " + Arrays.toString(inetAddress.getAddress()));
    }

    @SuppressWarnings("unused")
    public static void showInetSocketAddressInfo(InetSocketAddress inetSocketAddress) {
        i("inetSocketAddress.getHostName() -> " + inetSocketAddress.getHostName());
        if (Build.VERSION.SDK_INT >= 19) {
            i("inetSocketAddress.getHostString() -> " + inetSocketAddress.getHostString());
        }
        i("inetSocketAddress.getPort() -> " + inetSocketAddress.getPort());
        InetAddress inetAddress = inetSocketAddress.getAddress();
        i("inetAddress.getHostName() -> " + inetAddress.getHostName());
        i("inetAddress.getHostAddress() -> " + inetAddress.getHostAddress());
        i("inetAddress.getCanonicalHostName() -> " + inetAddress.getCanonicalHostName());
        i("Arrays.toString(inetAddress.getAddress()) -> " + Arrays.toString(inetAddress.getAddress()));
    }

}
