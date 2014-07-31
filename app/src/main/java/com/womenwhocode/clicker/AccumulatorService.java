package com.womenwhocode.clicker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

/**
 * Created by Kat on 7/30/14.
 */
public class AccumulatorService extends Service {

    private static final String TAG = "AccumulatorService";

    private NotificationManager notificationManager;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        AccumulatorService getService() {
            return AccumulatorService.this;
        }
    }

    private Count currencyCount = new Count();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.
        showNotification();

        BusProvider.getInstance().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        notificationManager.cancel(R.string.service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show();

        BusProvider.getInstance().unregister(this);
    }

    private void showNotification() {
        CharSequence text = getText(R.string.service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_label),
                text, contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        notificationManager.notify(R.string.service_started, notification);
    }

    @Subscribe
    public void onClickEvent(ClickEvent clickEvent) {
        currencyCount.count++;

        BusProvider.getInstance().post(currencyCount);
    }
}
