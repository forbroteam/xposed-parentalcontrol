package com.forbroteam.xposedparentalcontrol;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Handler;
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

    String targetPackage = "com.android.chrome";
    String urlToProtectFrom = "youtube";
    String packageName = null;
    boolean forceClose = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        packageName = lpparam.packageName;

        XposedBridge.log("ParentalControl: Loaded " + packageName);
        if (!targetPackage.isEmpty() && !targetPackage.equals(packageName))
            return;

        findAndHookConstructor("java.net.URL", lpparam.classLoader, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String url = (param != null && param.args.length > 0) ? ((String) param.args[0]) : null;
                        XposedBridge.log("ParentalControl: " + packageName + " URL: " + url);
                        if (url.toString().contains(urlToProtectFrom)) {
                            forceClose = true;
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (forceClose) {
                            Toast.makeText(AndroidAppHelper.currentApplication(),
                                    "Sorry, you are restricted to be here!", Toast.LENGTH_SHORT)
                                    .show();
                            forceClose = false;

                            terminateApplication(5000);
                        }
                    }
                });
    }

    private void terminateApplication(int milliseconds) {
        if (milliseconds < 0) {
            milliseconds = 0;
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager) AndroidAppHelper
                        .currentApplication().getSystemService(Context.ACTIVITY_SERVICE);
                am.killBackgroundProcesses(targetPackage);
            }
        }, milliseconds);
    }
}
}