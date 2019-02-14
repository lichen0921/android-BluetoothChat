package com.example.android.bluetoothchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private String[] mPermissions;
    private OnPermissionCallback mPermissionCallback;
    private static final int PERMISSION_REQUEST_CODE = 921;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void requestPermissions(@NonNull String[] permissions,
                                   @NonNull OnPermissionCallback callback) {
        if (permissions.length < 1 || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        mPermissions = permissions;
        mPermissionCallback = callback;
        if (checkAllPermissions(permissions)) {
            // 有权限
            if (mPermissionCallback != null) {
                mPermissionCallback.onGranted();
            }
        } else {
            // 无权限
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkAllPermissionsResult(grantResults)) {
                // 全部授权
                if (mPermissionCallback != null) {
                    mPermissionCallback.onGranted();
                }
            } else {
                if (shouldShowRequestPermissionsRationale(permissions)) {
                    // 用户选择了拒绝；
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                } else {
                    // 用户选择了不再提示；
                    showPermissionRationale(null, null);
                }
                // 没有全部授权；
                if (mPermissionCallback != null) {
                    mPermissionCallback.onDenied();
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * 弹出提示，提醒用户需要授权——去设置页
     * @param title
     * @param content
     */
    private void showPermissionRationale(@Nullable String title, @NonNull String content) {
//        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
//        dialog.setCancelable(false);
//        dialog.setTitleText(TextUtils.isEmpty(title) ? getString(R.string.permission_title) : title)
//                .setContentText(TextUtils.isEmpty(content) ? getString(R.string
//                        .permission_content) : content)
//                .setConfirmButton(R.string.dialog_ok, new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        sweetAlertDialog.dismissWithAnimation();
//                        gotoSetting();
//                    }
//                })
//                .setCancelButton(R.string.permission_cancel, new SweetAlertDialog
//                        .OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        sweetAlertDialog.dismissWithAnimation();
//                        if(mPermissionCallback!=null){
//                            mPermissionCallback.onDenied();
//                        }
//                    }
//                })
//                .show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setTitle("请授权").setIcon(R.drawable.ic_launcher)
                .setMessage("需要权限")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {// 积极

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {// 消极

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
        });
        builder.create().show();
    }

    /**
     * 跳转到设置界面
     */
    private void gotoSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (mPermissions != null && checkAllPermissions(mPermissions)) {
                if (mPermissionCallback != null) {
                    mPermissionCallback.onGranted();
                }
            } else {
                if (mPermissionCallback != null) {
                    mPermissionCallback.onDenied();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 检查权限，只有全部授权才return true
     * @param permissions
     * @return
     */
    public boolean checkAllPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : permissions) {
                if (checkSelfPermission(p) == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 检查授权结果，只有全部授权才return true
     * @param grantResults
     * @return
     */
    public boolean checkAllPermissionsResult(int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int g : grantResults) {
                if (g == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 是否显示权限申请原因
     * @param permissions
     * @return
     */
    public boolean shouldShowRequestPermissionsRationale(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : permissions) {
                if (shouldShowRequestPermissionRationale(p)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }


    /**
     * 动态授权回调接口
     * @author Lichen
     */
    public interface OnPermissionCallback {
        /**
         * 授权
         */
        void onGranted();

        /**
         * 拒绝
         */
        void onDenied();

    }
}
