package com.forbroteam.xposedparentalcontrol;

import android.app.AndroidAppHelper;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;

/**
 * Created by bogatenkov on 28/10/17.
 */

public class LoadHook implements IXposedHookLoadPackage {

    // TODO: add a specific application that should be tracked
    String targetPackage = "com.android.chrome";
    String packageName = null;
    boolean forceClose = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        packageName = lpparam.packageName;

        XposedBridge.log("Loaded app: " + packageName);
        if (!targetPackage.isEmpty() && !targetPackage.equals(packageName))
            return;

        findAndHookConstructor("java.net.URL", lpparam.classLoader, String.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("ParentalControl: " + packageName + " URL param 0: " + param.args[0] +
                                " URL param 1: " + param.args[1]);
                        if (param.args[0].toString().contains("youtube")) {
                            forceClose = true;
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (forceClose) {
                            AndroidAppHelper.currentApplication().onTerminate();
                            Toast.makeText(AndroidAppHelper.currentApplication(),
                                    "Sorry, you are restricted to be here!", Toast.LENGTH_SHORT)
                                    .show();
                            forceClose = false;
                        }
                    }
                });
    }
}