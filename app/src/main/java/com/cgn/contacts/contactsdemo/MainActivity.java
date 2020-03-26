package com.cgn.contacts.contactsdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cgn.contacts.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class MainActivity extends Activity {

    Button button1, button2, button3;

    private boolean isNeedCheck = true;

    private static final int PERMISSON_REQUESTCODE = 0;

    private String[] needPermissions = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            }
        }

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String testInfo = "{\"contactsBeanList\":[{\"addressList\":[{\"data\":\"广东省深圳市科技大厦888888\",\"label\":\"办公地址\"},{\"data\":\"南-888888-888888\",\"label\":\"宿舍地址\"}],\"company\":\"南京英诺森软件科技有限公司\",\"emailList\":[{\"data\":\"888888@cgnpc.com.cn\",\"label\":\"外部邮箱\"}],\"flag\":1,\"name\":\"姓名[P0]\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"},{\"data\":\"888888\",\"label\":\"宿舍电话\"},{\"data\":\"888888\",\"label\":\"办公电话\"},{\"data\":\"888888\",\"label\":\"BP机号码\"},{\"data\":\"\",\"label\":\"其他手机\"}],\"position\":\"APP开发\"},{\"addressList\":[{\"data\":\"广东省深圳市科技大厦888888\",\"label\":\"办公地址\"},{\"data\":\"南-888888-888888\",\"label\":\"宿舍地址\"}],\"company\":\"南京英诺森软件科技有限公司\",\"emailList\":[{\"data\":\"888888@cgnpc.com.cn\",\"label\":\"外部邮箱\"}],\"flag\":1,\"name\":\"姓名[P1]\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"},{\"data\":\"888888\",\"label\":\"宿舍电话\"},{\"data\":\"888888\",\"label\":\"办公电话\"},{\"data\":\"888888\",\"label\":\"BP机号码\"},{\"data\":\"\",\"label\":\"其他手机\"}],\"position\":\"APP开发\"},{\"addressList\":[{\"data\":\"广东省深圳市科技大厦888888\",\"label\":\"办公地址\"},{\"data\":\"南-888888-888888\",\"label\":\"宿舍地址\"}],\"company\":\"南京英诺森软件科技有限公司\",\"emailList\":[{\"data\":\"888888@cgnpc.com.cn\",\"label\":\"外部邮箱\"}],\"flag\":1,\"name\":\"姓名[P2]\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"},{\"data\":\"888888\",\"label\":\"宿舍电话\"},{\"data\":\"888888\",\"label\":\"办公电话\"},{\"data\":\"888888\",\"label\":\"BP机号码\"},{\"data\":\"\",\"label\":\"其他手机\"}],\"position\":\"APP开发\"},{\"addressList\":[{\"data\":\"广东省深圳市科技大厦888888\",\"label\":\"办公地址\"},{\"data\":\"南-888888-888888\",\"label\":\"宿舍地址\"}],\"company\":\"南京英诺森软件科技有限公司\",\"emailList\":[{\"data\":\"888888@cgnpc.com.cn\",\"label\":\"外部邮箱\"}],\"flag\":1,\"name\":\"姓名[P3]\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"},{\"data\":\"888888\",\"label\":\"宿舍电话\"},{\"data\":\"888888\",\"label\":\"办公电话\"},{\"data\":\"888888\",\"label\":\"BP机号码\"},{\"data\":\"\",\"label\":\"其他手机\"}],\"position\":\"APP开发\"}],\"version\":1}";
                    //由于测试数据需要调用compress方法，正式调用时不需要调用compress方法
                    boolean result = Utils.updateContacts(MainActivity.this, compress(testInfo));
                    Toast.makeText(MainActivity.this, String.valueOf(result), Toast.LENGTH_LONG).show();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String testInfo = "{\"contactsBeanList\":[{\"company\":\"公司0-岗位0\",\"flag\":1,\"name\":\"[应急]岗位0-姓名0\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"}],\"position\":\"姓名0\"},{\"company\":\"公司1-岗位1\",\"flag\":1,\"name\":\"[技术]岗位1-姓名1\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"}],\"position\":\"姓名1\"},{\"company\":\"公司2-岗位2\",\"flag\":1,\"name\":\"[应急]岗位2-姓名2\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"}],\"position\":\"姓名2\"},{\"company\":\"公司3-岗位3\",\"flag\":1,\"name\":\"[技术]岗位3-姓名3\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"}],\"position\":\"姓名3\"}],\"version\":1}";
                    //由于测试数据需要调用compress方法，正式调用时不需要调用compress方法
                    boolean result = Utils.updateEmergencyTechnology(MainActivity.this, compress(testInfo));
                    Toast.makeText(MainActivity.this, String.valueOf(result), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String testInfo = "{\"contactsBeanList\":[{\"company\":\"公司0-岗位0\",\"flag\":1,\"name\":\"[运行]岗位0-姓名0\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"}],\"position\":\"姓名0\"},{\"company\":\"公司1-岗位1\",\"flag\":1,\"name\":\"[运行]岗位1-姓名1\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"}],\"position\":\"姓名1\"},{\"company\":\"公司2-岗位2\",\"flag\":1,\"name\":\"[运行]岗位2-姓名2\",\"phoneList\":[{\"data\":\"86-88888888\",\"label\":\"办公手机\"},{\"data\":\"888888\",\"label\":\"4G号码\"}],\"position\":\"姓名2\"}],\"version\":1}\n";
                    //由于测试数据需要调用compress方法，正式调用时不需要调用compress方法
                    boolean result = Utils.updateSchedule(MainActivity.this, compress(testInfo));
                    Toast.makeText(MainActivity.this, String.valueOf(result), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });
    }

    //检查权限
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class,
                            int.class});
                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    //获取未授权权限列表
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
                    Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                    Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                            String.class);
                    if ((Integer) checkSelfMethod.invoke(this, perm) != PackageManager.PERMISSION_GRANTED
                            || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {

            }
        }
        return needRequestPermissonList;
    }

    //验证权限是否已申请
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //开启权限回掉方法
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            } else {

            }
        }

    }

    //提示权限未开通，并跳转系统设置页
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\"-\"权限\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    //跳转到应用权限设置页
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private static String compress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        // 将 b.length 个字节写入此输出流
        gzip.write(str.getBytes());
        gzip.close();
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("ISO-8859-1");
    }
}
