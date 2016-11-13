package xyz.nulldev.secureaccess;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SecureAccessibility implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        try {
            if(loadPackageParam.packageName.equals("com.android.settings")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //Nougat+
                    XposedHelpers.findAndHookMethod("com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment",
                            loadPackageParam.classLoader,
                            "isFullDiskEncrypted",
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    param.setResult(false);
                                }
                            });
                } else {
                    //Kitkat+
                    XposedHelpers.findAndHookMethod("com.android.internal.widget.LockPatternUtils",
                            loadPackageParam.classLoader,
                            "isDeviceEncrypted",
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    for (StackTraceElement element : new Exception().getStackTrace()) {
                                        //Only from accessibility fragment
                                        if (element
                                                .getClassName()
                                                .equalsIgnoreCase("com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment")) {
                                            param.setResult(false);
                                            return;
                                        }
                                    }
                                }
                            });
                }
            }
        } catch (Exception e) {
            XposedBridge.log("SecureAccessibility failed to initialize!");
            XposedBridge.log(e);
        }
    }
}
