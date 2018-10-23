package com.ccmt.library.lru;

import com.ccmt.library.util.LogUtil;
import com.ccmt.library.util.ThreadManager;

import java.io.Serializable;

public class Test {

    private static class ProcessInfo implements Serializable {
        private static final long serialVersionUID = -1828226494024909934L;
        private Integer uid;

        public Integer getUid() {
            return uid;
        }

        public void setUid(Integer uid) {
            this.uid = uid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ProcessInfo that = (ProcessInfo) o;

            return uid.equals(that.uid);

        }

        @Override
        public int hashCode() {
            return uid.hashCode();
        }
    }

    @SuppressWarnings("unused")
    public static void testLruMap() {
        ThreadManager.executeAsyncTask(() -> {
            long start = System.currentTimeMillis();
            LogUtil.i("测试开始");
            LruMap lruMap = LruMap.getInstance();
            Thread.currentThread().setPriority(10);
            ProcessInfo processInfo;
            for (int i = 1; i <= 3000000; i++) {
                processInfo = new ProcessInfo();
                processInfo.setUid(i);
                lruMap.put(String.valueOf(i), processInfo);
            }
            LogUtil.i("lruMap.linkedHashMap.size() -> " + lruMap.linkedHashMap.size());
            LogUtil.i("lruMap.softMap.size() -> " + lruMap.softMap.size());
            LogUtil.i("lruMap.getSoftCount() -> " + lruMap.getSoftCount());

//            for (int i = 1; i <= 3000009; i++) {
//                if (i == 100000) {
//                    LogUtil.i("到100000了");
//                } else if (i == 200000) {
//                    LogUtil.i("到200000了");
//                } else if (i == 300000) {
//                    LogUtil.i("到300000了");
//                } else if (i == 400000) {
//                    LogUtil.i("到400000了");
//                } else if (i == 500000) {
//                    LogUtil.i("到500000了");
//                } else if (i == 600000) {
//                    LogUtil.i("到600000了");
//                } else if (i == 700000) {
//                    LogUtil.i("到700000了");
//                } else if (i == 800000) {
//                    LogUtil.i("到800000了");
//                } else if (i == 900000) {
//                    LogUtil.i("到900000了");
//                } else if (i == 1000000) {
//                    LogUtil.i("到1000000了");
//                } else if (i == 3000000) {
//                    LogUtil.i("到1000000了");
//                }
//                processInfo = (ProcessInfo) lruMap.get(String.valueOf(i));
//                // noinspection StatementWithEmptyBody
//                if (processInfo != null) {
////                    LogUtil.i("processInfo.getUid() -> " + processInfo.getUid());
//                } else {
////                    LogUtil.i(i + " -> null");
//                }
//            }
            LogUtil.i("测试结束");
            LogUtil.i("耗时" + (System.currentTimeMillis() - start) + "毫秒");
        });
    }

}
