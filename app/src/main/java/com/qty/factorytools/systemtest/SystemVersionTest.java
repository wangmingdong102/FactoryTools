package com.qty.factorytools.systemtest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.TextView;

import com.qty.factorytools.AbstractTestActivity;
import com.qty.factorytools.Log;
import com.qty.factorytools.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemVersionTest extends AbstractTestActivity {

    private static final int REQUEST_PERMISSION_CODE = 119;

    private TextView mAndroidVersionTv;
    private TextView mKernalVersionTv;
    private TextView mSystemVersionTv;
    private TextView mDeviceSerialNumberTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.system_version_test_title);
        setContentView(R.layout.system_version_test);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String androidVersion = Build.VERSION.RELEASE;
        String kernalVersion = getKernalVersion();
        String systemVersion = Build.DISPLAY;
        String deviceSerialNumber = getDeviceSerialNumber();
        mAndroidVersionTv.setText(TextUtils.isEmpty(androidVersion) ? getString(R.string.unknown) : androidVersion);
        mKernalVersionTv.setText(TextUtils.isEmpty(kernalVersion) ? getString(R.string.unknown) : kernalVersion);
        mSystemVersionTv.setText(TextUtils.isEmpty(systemVersion) ? getString(R.string.unknown) : systemVersion);
        mDeviceSerialNumberTv.setText(TextUtils.isEmpty(deviceSerialNumber) ? getString(R.string.unknown) : deviceSerialNumber);
        if (mIsAutoTest) {
            if (!TextUtils.isEmpty(androidVersion) && !TextUtils.isEmpty(kernalVersion) && !TextUtils.isEmpty(systemVersion)
                    && !TextUtils.isEmpty(deviceSerialNumber)) {
                mHandler.sendEmptyMessageDelayed(MSG_PASS, mAutoTestDelayTime);
            } else {
                mHandler.sendEmptyMessageDelayed(MSG_FAIL, mAutoTestDelayTime);
            }
        }
    }

    private void initViews() {
        mAndroidVersionTv = (TextView) findViewById(R.id.android_version);
        mKernalVersionTv = (TextView) findViewById(R.id.kernal_version);
        mSystemVersionTv = (TextView) findViewById(R.id.system_version);
        mDeviceSerialNumberTv = (TextView) findViewById(R.id.device_serial_number);
    }

    private String getKernalVersion() {
        String version = null;
        BufferedReader reader = null;
        StringBuffer versionBuffer = new StringBuffer();

        try {
            FileInputStream fi = new FileInputStream("/proc/version");
            reader = new BufferedReader(new InputStreamReader(fi));
            String str = reader.readLine();

            while (str != null) {
                versionBuffer.append(str + "\n");
                str = reader.readLine();
            }
        } catch (Exception e) {
            versionBuffer = null;
            Log.d(this, "getKernalVersion=>error: ", e);
        } finally {
            if (versionBuffer != null) {
                version = versionBuffer.toString();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(this, "getKernalVersion=>kernal version: " + version);
        return version;
    }

    private String getDeviceSerialNumber() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String number = tm.getDeviceId();
        Log.d(this, "getDeviceSerialNumber=>number: " + number);
        return number;
    }
}
