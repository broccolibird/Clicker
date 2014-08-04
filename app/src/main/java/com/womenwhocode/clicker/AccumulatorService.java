package com.womenwhocode.clicker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

/**
 * Created by Kat on 7/30/14.
 */
public class AccumulatorService extends Service {

    private static final String TAG = "AccumulatorService";

    private NotificationManager notificationManager;
    private final IBinder mBinder = new LocalBinder();

    private Handler handler;
    private Runnable accumulatorRunnable;

    private static final int MILLISECONDS_BETWEEN_UPDATES_FOREGROUND = 1000;
    private static final int MILLISECONDS_BETWEEN_UPDATES_BACKGROUND = 10000;

    public class LocalBinder extends Binder {
        AccumulatorService getService() {
            return AccumulatorService.this;
        }
    }

    private Count count = new Count();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.
        showNotification(count);

        BusProvider.getInstance().register(this);

        handler = new Handler();
        accumulatorRunnable = new AccumulatorRunnable();
        new Thread(accumulatorRunnable).start();

        ClickerGroup clickerGroup = new ClickerGroup("clicker", "simple clicker", 1);
        clickerGroup.increaseNumberOfClickers(1);
        count.addClicker(clickerGroup);

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

    private void showNotification(Count count) {
        CharSequence text = (CharSequence) ("" + count.count);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(" " + count.count)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .build();

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_label),
                text, contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        notificationManager.notify(R.string.service_started, notification);
    }

    @Subscribe
    public void onClickEvent(ClickEvent clickEvent) {
        count.count++;

        sendOutUpdate();
    }

    @Produce
    public Count produceCount() {
        // Assuming 'lastAnswer' exists.
        return count;
    }

    public void sendOutUpdate() {

        BusProvider.getInstance().post(count);

        showNotification(count);
    }

    private class AccumulatorRunnable implements Runnable {
        private static final int MILLISECONDS_PER_SECOND = 1000;
        private int millisecondsBetweenUpdates = 1000;

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(millisecondsBetweenUpdates);

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            count.addClicksFromClickers(millisecondsBetweenUpdates / MILLISECONDS_PER_SECOND);
                            sendOutUpdate();
                        }
                    });
                } catch (InterruptedException e) {
                    Log.e(TAG, "Runnable failed to sleep.", e);
                }
            }
        }

        public void setMillisecondsBetweenUpdates(int millisecondsBetweenUpdates) {
            this.millisecondsBetweenUpdates = millisecondsBetweenUpdates;
        }
    }

}
