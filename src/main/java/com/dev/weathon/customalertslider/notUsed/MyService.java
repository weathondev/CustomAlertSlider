package com.dev.weathon.customalertslider.notUsed;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Joshua on 19.08.2016.
 */
public class MyService extends Service
{
    private MyReceiverVolume receiver;

    @Override
    public void onCreate()
    {
        super.onCreate();
        // see if there is a saved sticky intent.
        Intent previousIntent = registerReceiver(null, new IntentFilter(
                Intent.ACTION_HEADSET_PLUG));

        // set up the receiver.
        registerReceiver(MyReceiverVolume.getInstance(), new IntentFilter(
                Intent.ACTION_HEADSET_PLUG));
        registerReceiver(MyReceiverVolume.getInstance(), new IntentFilter(
                BluetoothDevice.ACTION_ACL_CONNECTED));
        registerReceiver(MyReceiverVolume.getInstance(), new IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED));

        Log.e("CustomAlertSlider", "register receivers");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Be sure and unregister the receiver when the service is destroyed. This usually
        // means the service is being killed by the system, and it will be restarted again
        // momentarily. if we don't unregister, sometimes multiple instances of the
        // receiver getregistered.
        try
        {
            if(receiver != null)
            {
                unregisterReceiver(receiver);
            }
        }
        catch(IllegalArgumentException e)
        {
            // eat this exception.
        }
    }
}
