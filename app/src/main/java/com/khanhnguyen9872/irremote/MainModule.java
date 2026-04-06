package com.khanhnguyen9872.irremote;

import android.content.Context;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.robv.android.xposed.IXposedHookZygoteInit;

public class MainModule implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final String TARGET_CLASS = "com.oplus.content.OplusFeatureConfigManager";
    private static String modulePath;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        modulePath = startupParam.modulePath;
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        // Chỉ áp dụng với những App có vẻ là IR Remote hoặc không hệ thống
        if (lpparam.packageName.equals("android")) return;

        // 1. Chỉ áp dụng plugin Hook cho app đích (Remote Hồng Ngoại)
        // Dựa theo đề xuất từ bạn, package cụ thể là `com.oplus.consumerIRApp`.
        String targetPkg = lpparam.packageName;
        if (!targetPkg.equals("com.oplus.consumerIRApp") && !targetPkg.contains("irremote")) {
            return;
        }
            
        // 1. Inject module Dex vào ClassLoader của App để xử lý tận gốc NoClassDefFoundError (Natively by ART ClassLinker)
        try {
            Object pathList = XposedHelpers.getObjectField(lpparam.classLoader, "pathList");
            Object[] dexElements = (Object[]) XposedHelpers.getObjectField(pathList, "dexElements");

            ClassLoader tempLoader = new dalvik.system.PathClassLoader(modulePath, ClassLoader.getSystemClassLoader());
            Object tempPathList = XposedHelpers.getObjectField(tempLoader, "pathList");
            Object[] tempDexElements = (Object[]) XposedHelpers.getObjectField(tempPathList, "dexElements");

            // Nối 2 array dexElements lại
            Object[] newElements = (Object[]) java.lang.reflect.Array.newInstance(dexElements.getClass().getComponentType(), dexElements.length + tempDexElements.length);
            System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
            System.arraycopy(tempDexElements, 0, newElements, dexElements.length, tempDexElements.length);

            // Ghi đè vào ClassLoader
            XposedHelpers.setObjectField(pathList, "dexElements", newElements);
            XposedBridge.log("IRRemoteXposed: Successfully injected module dex into app classloader.");
        } catch (Throwable t) {
            XposedBridge.log("IRRemoteXposed: Failed to inject dex elements - " + t.getMessage());
            // Fallback
            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (TARGET_CLASS.equals(param.args[0])) param.setResult(com.oplus.content.OplusFeatureConfigManager.class);
                }
            });
        }

            // 2. Vá lỗi "No Network Connection" cục bộ (Local Checks)
            Class<?> j0Class = XposedHelpers.findClassIfExists("b7.j0", lpparam.classLoader);
            if (j0Class != null) {
                XposedBridge.log("IRRemoteXposed: Found b7.j0. Hooking a() and b() to return true.");
                XposedHelpers.findAndHookMethod(j0Class, "a", Context.class, XC_MethodReplacement.returnConstant(true));
                XposedHelpers.findAndHookMethod(j0Class, "b", Context.class, XC_MethodReplacement.returnConstant(true));
            }

            Class<?> c1Class = XposedHelpers.findClassIfExists("com.kookong.sdk.ir.c1", lpparam.classLoader);
            if (c1Class != null) {
                XposedBridge.log("IRRemoteXposed: Found com.kookong.sdk.ir.c1. Hooking b() to return true and set a=true.");
                XposedHelpers.findAndHookMethod(c1Class, "b", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.setStaticBooleanField(c1Class, "a", true);
                        param.setResult(true);
                    }
                });
            }

            // 3. Vá lỗi Kookong SDK chặn hãng thiết bị (Hardware API Filter)
            Class<?> m3Class = XposedHelpers.findClassIfExists("com.kookong.sdk.ir.m3", lpparam.classLoader);
            if (m3Class != null) {
                XposedBridge.log("IRRemoteXposed: Found com.kookong.sdk.ir.m3. Setting static fields to OPPO.");
                XposedHelpers.setStaticObjectField(m3Class, "f", "OPPO");
                XposedHelpers.setStaticObjectField(m3Class, "g", "OPPO");

                XposedHelpers.findAndHookMethod(m3Class, "a", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.setStaticObjectField(m3Class, "f", "OPPO");
                        XposedHelpers.setStaticObjectField(m3Class, "g", "OPPO");
                    }
                });
            }
    }
}
