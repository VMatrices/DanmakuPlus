package cn.vmatrices.danmakuplus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import cn.vmatrices.danmakuplus.R;
import cn.vmatrices.danmakuplus.tool.Common;
import cn.vmatrices.danmakuplus.tool.PermissionChecker;

public class PermissionActivity extends AppCompatActivity {
    private Intent intent1;
    private Intent intent2;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        init();
    }

    private void init() {

        intent1 = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        button= (Button) findViewById(R.id.button);

        if (PermissionChecker.canReadNotifications(this)) {
            checkBox1.setChecked(true);
        }

        if (PermissionChecker.canShowFloatWind(this)) {
            checkBox2.setChecked(true);
        }
    }

    public void check1(View view) {
        if (!PermissionChecker.canReadNotifications(this)) {
            Toast.makeText(this, R.string.toast_permission_readnotification, Toast.LENGTH_SHORT).show();
            startActivityForResult(intent1, 0);
        }
    }

    public void check2(View view) {
        if (!PermissionChecker.canShowFloatWind(this)) {
            Toast.makeText(this, R.string.toast_permission_showfloatwind, Toast.LENGTH_LONG).show();
            startActivityForResult(intent2, 1);
        }
    }


    public void done(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent(Common.ACTION_UPDATE));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (PermissionChecker.canReadNotifications(this)) {
                    checkBox1.setChecked(true);
                    if(checkBox2.isChecked()){
                        button.setVisibility(View.VISIBLE);
                    }
                } else {
                    checkBox1.setChecked(false);
                    button.setVisibility(View.GONE);
                }
                break;
            case 1:
                if (PermissionChecker.canShowFloatWind(this)) {
                    checkBox2.setChecked(true);
                    if(checkBox1.isChecked()){
                        button.setVisibility(View.VISIBLE);
                    }
                } else {
                    checkBox2.setChecked(false);
                    button.setVisibility(View.GONE);
                }
                break;
        }
    }
}
