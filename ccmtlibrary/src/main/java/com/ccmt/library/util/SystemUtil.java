package com.ccmt.library.util;

import android.text.TextUtils;

import java.lang.reflect.Method;

@SuppressWarnings("WeakerAccess")
public class SystemUtil {

    static String getDeviceModel() {
        String deviceModel = null;
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            deviceModel = (String) getMethod.invoke(classType, "ro.product.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceModel;
    }

    /**
     * 判断手机格式是否正确
     *
     * @param mobiles
     * @return 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
    @SuppressWarnings({"JavaDoc", "unused"})
    public static boolean isMobileNO(String mobiles) {
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][34578]\\d{9}";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

}
