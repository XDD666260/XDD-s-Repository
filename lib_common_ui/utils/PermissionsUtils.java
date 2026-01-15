package com.baidu.lib_common_ui.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class PermissionsUtils {
    //提供全局的单例对象
    public static final PermissionsUtils sharedInstance = new PermissionsUtils();
    //保存activity对象
    private WeakReference<AppCompatActivity> mWeakActivity;
    //权限启动器对象
    private ActivityResultLauncher<String> launcher;
    //记录结果回调的监听者
    private RequestPermissionCallback mCallback;

    //私有化构造方法
    private PermissionsUtils() {
    }

    //初始化
    public void init(AppCompatActivity activity, String message) {
        //将activity弱引用
        mWeakActivity = new WeakReference<>(activity);

        //创建启动器对象
        launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (!granted) { //如果拒绝了 再次弹出一个引导配置界面
                        showDialog(message);
                    }

                    if (mCallback != null) {
                        mCallback.callback(granted);
                    }
                }
        );
    }

    //引导用户在设置中修改权限
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mWeakActivity.get());
        builder.setTitle("友情提示！");
        builder.setMessage(message);
        builder.setPositiveButton("立即设置", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", null, null));
            mWeakActivity.get().startActivity(intent);
        });
        builder.setNegativeButton("取消", (dialog, which) -> {

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //请求权限
    public void requestPermission(String permission, RequestPermissionCallback callback) {
        //记录回调的监听者对象
        this.mCallback = callback;
        //启动权限请求启动器
        launcher.launch(permission);
    }

    //检测是否有权限
    public boolean checkPermission(String permission) {
        int result = mWeakActivity.get().checkSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //通过这个接口回调给调用者最终的结果
    public interface RequestPermissionCallback {
        void callback(boolean granted);
    }
}
