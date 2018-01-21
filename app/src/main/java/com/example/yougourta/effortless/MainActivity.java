package com.example.yougourta.effortless;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.yougourta.effortless.utils.AppSharedPreferences;
import com.example.yougourta.effortless.utils.ServiceUtils;

public class MainActivity extends Activity implements OnClickListener {

    static final int RESULT_ENABLE = 1;
    protected static final String TAG = MainActivity.class.getCanonicalName();

    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showIfServiceIsRunning();

        findViewById(R.id.startService).setOnClickListener(this);
        findViewById(R.id.main_disableAdmin_b).setOnClickListener(this);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminReceiver = new ComponentName(MainActivity.this, ScreenLockerDeviceAdminReceiver.class);
        enableAdmin();
    }

    @Override
    protected void onResume() {
        enableAdmin();
        super.onResume();
    }

    private boolean isMyServiceRunning() {
        return ServiceUtils.isMyServiceRunning(this, ScreenLockerService.class.getCanonicalName());
    }

    private void showIfServiceIsRunning() {
        //((TextView) findViewById(R.id.main_serviceRunningStatus_tv))
        // .setText(getString(isMyServiceRunning() ? R.string.main_service_is_running : R.string.main_service_is_not_running));
        ((Button) findViewById(R.id.startService)).setText(getString(isMyServiceRunning() ? R.string.main_stop_service
                : R.string.main_start_service));
    }

    private void stopScreenLockerService() {
        Intent serviceIntent = new Intent(this, ScreenLockerService.class);
        stopService(serviceIntent);
        showIfServiceIsRunning();
        AppSharedPreferences.setAutoStart(this, false);
        this.finish();
    }

    private void startScreenLockerService() {
        Intent serviceIntent = new Intent(this, ScreenLockerService.class);
        startService(serviceIntent);
        showIfServiceIsRunning();
        AppSharedPreferences.setAutoStart(this, true);
    }

    public void enableAdmin() {
        Log.d(TAG, "mEnableListener onclick");

        boolean active = mDPM.isAdminActive(mDeviceAdminReceiver);
        if (!active) {
            // Launch the activity to have the user enable our admin.
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminReceiver);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This app needs admin rights to be able to lock the screen.");
            startActivityForResult(intent, RESULT_ENABLE);
        }
    }

    public void disableAdmin() {
        Log.d(TAG, "disableAdmin");
        mDPM.removeActiveAdmin(mDeviceAdminReceiver);
        // mDPM.removeActiveAdmin(new ComponentName(MainActivity.this,
        // ScreenLockerDeviceAdminReceiver.class));
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick");
        System.out.println("Clicked start");
       // WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        switch (v.getId()) {
            case R.id.startService:
                if (isMyServiceRunning()) {
                    stopScreenLockerService();
                } else {
                    enableAdmin();
                    startScreenLockerService();
                }
                break;
            case R.id.main_disableAdmin_b:
                disableAdmin();
                stopScreenLockerService();
                break;
            default:
                break;
        }
    }
}

