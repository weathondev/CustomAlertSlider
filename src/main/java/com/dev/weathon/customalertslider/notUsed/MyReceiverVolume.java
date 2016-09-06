package com.dev.weathon.customalertslider.notUsed;

import android.Manifest;
import android.app.AndroidAppHelper;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by Joshua on 05.08.2016.
 */
public class MyReceiverVolume extends BroadcastReceiver
{
    private static MyReceiverVolume instance;
    private int previousVol;


    private MyReceiverVolume()
    {
    }

    /**
     * Get the MyReceiverVolume singleton.
     */
    public static MyReceiverVolume getInstance()
    {
        if(instance == null)
        {
            instance = new MyReceiverVolume();
        }
        return instance;
    }

    /**
     * Tell the receiver to ignore its next broadcast.

    public void setIgnoreNext()
    {
        ignoreNext = true;
    }
*/
    private int processIntent(Context context, Intent intent, boolean btEnabled)
    {
        int state = -1;
        if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG))
        {
            state = intent.getIntExtra("state", -1);
        }
        else
        {
            // it's a bluetooth broadcast.
            if(btEnabled)
            {
                BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(
                        BluetoothDevice.EXTRA_DEVICE);
                BluetoothClass btClass = device.getBluetoothClass();
                int classId = btClass.getMajorDeviceClass();
                if(classId == BluetoothClass.Device.Major.AUDIO_VIDEO)
                {
                    if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED))
                    {
                        state = 1;
                    }
                    else if(intent.getAction().equals(
                            BluetoothDevice.ACTION_ACL_DISCONNECTED))
                    {
                        state = 0;
                    }
                }
            }
        }
        return state;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        /*int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){*/
            boolean bluetoothEnabled = false;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    bluetoothEnabled = true;
                }
            }

            int state = processIntent(context, intent, bluetoothEnabled);
            Log.e("CustomAlertSlider", "received intent " + intent.toString() + " state:" + state);

            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (state == 1){
                previousVol = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                if (previousVol != 0)
                    mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
            }
            else{
                if (previousVol != 0)
                    mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, previousVol, 0);
            }
        //}
    }
}
