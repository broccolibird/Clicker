package com.womenwhocode.clicker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

public class MainActivity extends Activity implements View.OnClickListener{

    private TextView countTextView;
    private boolean upVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countTextView = (TextView) findViewById(R.id.count);
        findViewById(R.id.click).setOnClickListener(this);

        doBindService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        BusProvider.getInstance().post(new ForegroundEvent(true));
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getInstance().post(new ForegroundEvent(false));
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onCountUpdate(Count count) {

        countTextView.setText("" + count.count);
    }

    private AccumulatorService mBoundService;
    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            mBoundService = ((AccumulatorService.LocalBinder)service).getService();
            Toast.makeText(MainActivity.this, R.string.service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(MainActivity.this, R.string.service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        bindService(new Intent(MainActivity.this,
                AccumulatorService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.click:
                click();
                break;
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (event.getKeyCode())
            {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    // Volume up key detected
                    // Do something
                    if (upVolume) {
                        upVolume = !upVolume;
                        click();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    // Volume down key detected
                    // Do something
                    if (!upVolume) {
                        upVolume = !upVolume;
                        click();
                    }
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public void click() {
        BusProvider.getInstance().post(new ClickEvent());
    }

    public class ForegroundEvent {
        boolean foreground;

        public ForegroundEvent(boolean foreground) {
            this.foreground = foreground;
        }
    }
}
