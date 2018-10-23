package com.ccmt.library.global;

import com.ccmt.library.service.AbstractForeignService;
import com.ccmt.library.service.AbstractService;

import java.util.HashMap;
import java.util.Map;

public class Global {

    //    public static List<Class<? extends Service>> allRunningServices = new ArrayList<>();
    public static Map<Class<? extends AbstractService>, AbstractService> allRunningServices = new HashMap<>();
    //    public static String serializableFileDir;
//    public static String serializableFileDirNotDelete;
    public static Map<Class<? extends AbstractForeignService>, Boolean> allInitForeignServices = new HashMap<>();

}
