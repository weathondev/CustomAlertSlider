package com.dev.weathon.customalertslider.notUsed;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;

/**
 * Created by Joshua on 06.08.2016.
 */
public class docu {


    //handleActionTurnOnFlashLight(AndroidAppHelper.currentApplication());

    private static void handleActionTurnOnFlashLight(Context context){
        try{

            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] list = manager.getCameraIdList();
            manager.setTorchMode(list[0], true);
        }
        catch (CameraAccessException cae){
            Log.e("CustomAlertSlider", cae.getMessage());
            cae.printStackTrace();
        }
    }

    private static void handleActionTurnOffFlashLight(Context context){
        try{
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            manager.setTorchMode(manager.getCameraIdList()[0], false);
        }
        catch (CameraAccessException cae){
            Log.e("CustomAlertSlider", cae.getMessage());
            cae.printStackTrace();
        }
    }

    /* to get all methods of a class!!!!!!!!!!!!
                   try {
                    Class c = XposedHelpers.findClass("com.android.server.OemExService", lpparam.classLoader);
                    Method[] m = c.getDeclaredMethods();
                    for (int i = 0; i < m.length; i++)
                        XposedBridge.log(m[i].toString());
                } catch (Throwable e) {
                    System.err.println(e);
                }
     */

    /*
    TODO:
    -remove DND icon
    -check if possible to update the status bar thing
    -check if volume restriction is possible for vibrate
        -ZenmodeControllerImpl
            @Override
            public boolean isVolumeRestricted() {
                return mUserManager.hasUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME,
                        new UserHandle(mUserId));
}



                            UserManager mUserManager = (UserManager) AndroidAppHelper.currentApplication().getSystemService(Context.USER_SERVICE);
                            final String DISALLOW_ADJUST_VOLUME = "no_adjust_volume";
                            Method setUserRestriction = mUserManager.getClass().getDeclaredMethod("setUserRestriction", String.class, boolean.class);
                            setUserRestriction.invoke(mUserManager, DISALLOW_ADJUST_VOLUME, false);


     */
}
