package com.chang.oobe.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import static android.os.UserHandle.USER_CURRENT;
import static android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_2BUTTON;
import static android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_2BUTTON_OVERLAY;
import static android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_3BUTTON_OVERLAY;
import static android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL;
import static android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL_OVERLAY;

// 开机向导导航切换
public class SystemNavActivity extends AppCompatActivity {

    private static final String TAG = "SystemNavActivity";

    static final String KEY_SYSTEM_NAV_3BUTTONS = "system_nav_3buttons";
    static final String KEY_SYSTEM_NAV_2BUTTONS = "system_nav_2buttons";
    static final String KEY_SYSTEM_NAV_GESTURAL = "system_nav_gestural";

    // 从framework.jar中引入
    private IOverlayManager mOverlayManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_nav);

        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));

        ((RadioGroup) findViewById(R.id.rg_navigation)).setOnCheckedChangeListener((group, checkId) -> {
            if (checkId == R.id.rb_gesture) {
                setCurrentSystemNavigationMode(KEY_SYSTEM_NAV_GESTURAL);
            } else if (checkId == R.id.rb_two) {
                setCurrentSystemNavigationMode(KEY_SYSTEM_NAV_2BUTTONS);
            } else if (checkId == R.id.rb_three) {
                setCurrentSystemNavigationMode(KEY_SYSTEM_NAV_3BUTTONS);
            }
        });

        findViewById(R.id.btn_complete_oobe).setOnClickListener(v -> {
            finishSetup();
        });
    }

    String getCurrentSystemNavigationMode(Context context) {
        if (isGestureNavigationEnabled(context)) {
            return KEY_SYSTEM_NAV_GESTURAL;
        } else if (is2ButtonNavigationEnabled(context)) {
            return KEY_SYSTEM_NAV_2BUTTONS;
        } else {
            return KEY_SYSTEM_NAV_3BUTTONS;
        }
    }

    void setCurrentSystemNavigationMode(String key) {
        if (key.equals(getCurrentSystemNavigationMode(this))) {
            return;
        }
        String overlayPackage = NAV_BAR_MODE_GESTURAL_OVERLAY;
        switch (key) {
            case KEY_SYSTEM_NAV_GESTURAL:
                overlayPackage = NAV_BAR_MODE_GESTURAL_OVERLAY;
                break;
            case KEY_SYSTEM_NAV_2BUTTONS:
                overlayPackage = NAV_BAR_MODE_2BUTTON_OVERLAY;
                break;
            case KEY_SYSTEM_NAV_3BUTTONS:
                overlayPackage = NAV_BAR_MODE_3BUTTON_OVERLAY;
                break;
        }

        try {
            mOverlayManager.setEnabledExclusiveInCategory(overlayPackage, USER_CURRENT);
        } catch (RemoteException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    private void finishSetup() {
        setProvisioningState();
        disableSelfAndFinish();
    }

    private void setProvisioningState() {
        Log.i(TAG, "Setting provisioning state");
        // Add a persistent setting to allow other apps to know the device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
    }

    private void disableSelfAndFinish() {
        // remove this activity from the package manager.
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, WelcomeActivity.class);
        Log.i(TAG, "Disabling itself (" + name + ")");
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        // terminate the activity.
        finish();
    }

    boolean is2ButtonNavigationEnabled(Context context) {
        return NAV_BAR_MODE_2BUTTON == context.getResources().getInteger(
                com.android.internal.R.integer.config_navBarInteractionMode);
    }

    boolean isGestureNavigationEnabled(Context context) {
        return NAV_BAR_MODE_GESTURAL == context.getResources().getInteger(
                com.android.internal.R.integer.config_navBarInteractionMode);
    }
}