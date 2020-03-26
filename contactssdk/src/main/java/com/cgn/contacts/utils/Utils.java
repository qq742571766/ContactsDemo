package com.cgn.contacts.utils;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.cgn.contacts.bean.ContactsBean;
import com.cgn.contacts.bean.InformationBean;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

/**
 * @author 雷嘉
 */
public class Utils {

    private static final String TAG = "inossem_cgn";

    private static final String LOG_ROOT_PATH = "Inossem_Log"; // 日志根目录名称

    private static final String LOG_CONTACTS = "Contacts"; // 通讯录目录名称

    private static final String LOG_EMERGENCY_TECHNOLOGY = "EmergencyTechnology"; // 应急技术目录名称

    private static final String LOG_SCHEDULE = "Schedule"; // 运行排班目录名称

    private static final String LOG_EXCEPTION = "Exception"; // 通讯录目录名称

    private static final String SD_FILE_NAME = "SD"; // SD卡日志

    private static final String FILE_DATE_TEMPLATE = "yyyyMMddHHmmss"; // 文件名日期格式

    private static final String LOG_DATE_TEMPLATE = "yyyy-MM-dd HH:mm:ss"; // 日志日期格式

    private static final String FILE_SUFFIX = ".txt"; // 日志文件后缀名

    private static final String TOP_MESSAGE = "\n*************";

    private static final String BOTTOM_MESSAGE = "\n*************\n\n";

    private static final String VERSION_NAME = "inossem_version_contacts";

    private static final String VERSION_KEY_CONTACTS = "inossem_version_key_contacts";

    private static final String VERSION_KEY_EMERGENCY_TECHNOLOGY = "inossem_version_key_emergency_technology";

    private static final String VERSION_KEY_SCHEDULE = "inossem_version_key_schedule";

    private static final int VERSION_DEFAULT = -1;

    private static final int BUFFER = 1024;

    private static final int ADD_UPDATE = 1;

    private static void apply(final Context context, final ArrayList<ContentProviderOperation> contentProviderOperationList) throws NullPointerException, OperationApplicationException, RemoteException {
        if (context == null || contentProviderOperationList == null || contentProviderOperationList.isEmpty()) {
            throw new NullPointerException();
        }
        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperationList);
    }

    private static void deleteAllConstacts(Context context, String s) throws Exception {
        if (context == null) {
            throw new Exception();
        }
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        context.getContentResolver().delete(uri, "display_name like '" + s + "%'", null);
    }

    private static void deleteConstacts(Context context, List<String> nameList) throws NullPointerException, OperationApplicationException, RemoteException {
        if (context == null || nameList == null || nameList.isEmpty()) {
            throw new NullPointerException();
        }
        ArrayList<ContentProviderOperation> contentProviderOperationList = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++) {
            String name = nameList.get(i);
            contentProviderOperationList.add(deleteContactsByName(name));
            if (contentProviderOperationList.size() > BUFFER) {
                Utils.apply(context, contentProviderOperationList);
                contentProviderOperationList.clear();
            }
        }
        if (!contentProviderOperationList.isEmpty()) {
            Utils.apply(context, contentProviderOperationList);
        }
    }

    private static void addConstacts(Context context, List<ContactsBean> contactsBeanList) throws NullPointerException, OperationApplicationException, RemoteException {
        if (context == null || contactsBeanList == null || contactsBeanList.isEmpty()) {
            throw new NullPointerException();
        }
        ArrayList<ContentProviderOperation> contentProviderOperationList = new ArrayList<>();
        int previousResult;
        for (int i = 0; i < contactsBeanList.size(); i++) {
            previousResult = contentProviderOperationList.size();
            ContactsBean contactsBean = contactsBeanList.get(i);
            contentProviderOperationList.add(createInit());
            if (!TextUtils.isEmpty(contactsBean.getName())) {
                contentProviderOperationList.add(createName(previousResult, contactsBean.getName()));
            }
            for (int j = 0; j < contactsBean.getPhoneList().size(); j++) {
                ContactsBean.Bean bean = contactsBean.getPhoneList().get(j);
                if (!TextUtils.isEmpty(bean.getLabel()) && !TextUtils.isEmpty(bean.getData())) {
                    contentProviderOperationList.add(createPhone(previousResult, bean.getLabel(), bean.getData()));
                }
            }
            for (int j = 0; j < contactsBean.getEmailList().size(); j++) {
                ContactsBean.Bean bean = contactsBean.getEmailList().get(j);
                if (!TextUtils.isEmpty(bean.getLabel()) && !TextUtils.isEmpty(bean.getData())) {
                    contentProviderOperationList.add(createEmail(previousResult, bean.getLabel(), bean.getData()));
                }
            }
            for (int j = 0; j < contactsBean.getAddressList().size(); j++) {
                ContactsBean.Bean bean = contactsBean.getAddressList().get(j);
                if (!TextUtils.isEmpty(bean.getLabel()) && !TextUtils.isEmpty(bean.getData())) {
                    contentProviderOperationList.add(createAddress(previousResult, bean.getLabel(), bean.getData()));
                }
            }
            if (!TextUtils.isEmpty(contactsBean.getCompany()) || !TextUtils.isEmpty(contactsBean.getPosition()))
                contentProviderOperationList.add(createCompanyAndPosition(previousResult, !TextUtils.isEmpty(contactsBean.getCompany()) ? contactsBean.getCompany() : "", !TextUtils.isEmpty(contactsBean.getPosition()) ? contactsBean.getPosition() : ""));
            if (contentProviderOperationList.size() > BUFFER) {
                Utils.apply(context, contentProviderOperationList);
                contentProviderOperationList.clear();
            }
        }
        if (!contentProviderOperationList.isEmpty()) {
            Utils.apply(context, contentProviderOperationList);
        }
    }

    private static ContentProviderOperation deleteContactsByName(String name) {
        return ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.Contacts.DISPLAY_NAME + "=?", new String[]{name})
                .withYieldAllowed(true)
                .build();
    }

    private static ContentProviderOperation createInit() {
        return ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .withYieldAllowed(true)
                .build();
    }

    private static ContentProviderOperation createName(int previousResult, String name) {
        return ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, previousResult)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .withYieldAllowed(true)
                .build();
    }

    private static ContentProviderOperation createPhone(int previousResult, String phoneName, String phone) {
        return ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, previousResult)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, phoneName)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withYieldAllowed(true)
                .build();
    }

    private static ContentProviderOperation createEmail(int previousResult, String emailName, String email) {
        return ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, previousResult)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Email.LABEL, emailName)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                .withYieldAllowed(true)
                .build();
    }

    private static ContentProviderOperation createAddress(int previousResult, String organizationName, String organization) {
        return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, previousResult)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, organizationName)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, organization)
                .withYieldAllowed(true)
                .build();
    }

    private static ContentProviderOperation createCompanyAndPosition(int previousResult, String company, String position) {
        return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, previousResult)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, position)
                .withYieldAllowed(true)
                .build();
    }

    private static String unCompress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
        ByteArrayInputStream in = new ByteArrayInputStream(str
                .getBytes(StandardCharsets.ISO_8859_1));
        // 使用默认缓冲区大小创建新的输入流
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
            // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
            out.write(buffer, 0, n);
        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("UTF-8");
    }

    private static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.VERSION_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static int getInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.VERSION_NAME,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, Utils.VERSION_DEFAULT);
    }

    private static int getContactsVersion(Context context) {
        return getInt(context, VERSION_KEY_CONTACTS);
    }

    private static void updateContactsVersion(Context context, int version) {
        putInt(context, VERSION_KEY_CONTACTS, version);
    }

    private static int getEmergencyTechnologyVersion(Context context) {
        return getInt(context, VERSION_KEY_EMERGENCY_TECHNOLOGY);
    }

    private static void updateEmergencyTechnologyVersion(Context context, int version) {
        putInt(context, VERSION_KEY_EMERGENCY_TECHNOLOGY, version);
    }

    private static int getScheduleVersion(Context context) {
        return getInt(context, VERSION_KEY_SCHEDULE);
    }

    private static void updateScheduleVersion(Context context, int version) {
        putInt(context, VERSION_KEY_SCHEDULE, version);
    }

    private static String getCurrentStringDate(String format) {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat(format, Locale.getDefault()).format(calendar.getTime());
    }

    private static void initLog(Context context, String path, String fileName) throws IOException {
        String information = collectDeviceInfo(context);
        saveInitLog(path, fileName, information);
    }

    private static String collectDeviceInfo(Context context) {
        StringBuilder sb = new StringBuilder();// 保存异常信息
        Map<String, String> infos = new HashMap<>();
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode;
                if (Build.VERSION.SDK_INT >= 28 && context.getApplicationInfo().targetSdkVersion >= 28) {
                    versionCode = String.valueOf(pi.getLongVersionCode());
                } else {
                    versionCode = String.valueOf(pi.versionCode);
                }
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields(); // 获取当前类所有静态属性
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), Objects.requireNonNull(field.get(null)).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }
        return sb.toString();
    }

    private static void saveInitLog(String path, String fileName, String information)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(path + fileName, true);
        String top = TOP_MESSAGE + "CreateTime:" + Utils.getCurrentStringDate(LOG_DATE_TEMPLATE)
                + "\n***********************************************\n";
        fos.write(top.getBytes());
        fos.write(information.getBytes());
        fos.write(BOTTOM_MESSAGE.getBytes());
        fos.flush();
        fos.close();
    }

    private static void saveContactsLog(Context context, String information) {
        saveLog(context, LOG_CONTACTS, information);
    }

    private static void saveEmergencyTechnologyLog(Context context, String information) {
        saveLog(context, LOG_EMERGENCY_TECHNOLOGY, information);
    }

    private static void saveScheduleLog(Context context, String information) {
        saveLog(context, LOG_SCHEDULE, information);
    }

    private static void saveExceptionLog(Context context, String information) {
        saveLog(context, LOG_EXCEPTION, information);
    }

    private static void saveLog(Context context, String type, String information) {
        FileOutputStream fos = null;
        String formatType = type.replace("/", "-");
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 向SD卡中存日志
                String rootPath = context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS)// 沙盒 - notifications 通知
                        + File.separator + LOG_ROOT_PATH;
                String path = rootPath + File.separator + type + File.separator;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = SD_FILE_NAME + "-" + formatType + "-"
                        + Utils.getCurrentStringDate(FILE_DATE_TEMPLATE) + FILE_SUFFIX;
                initLog(context, path, fileName);
                fos = new FileOutputStream(path + fileName, true);
                String top = TOP_MESSAGE + Utils.getCurrentStringDate(LOG_DATE_TEMPLATE)
                        + "\n**********************************\n";
                fos.write(top.getBytes());
                fos.write(information.getBytes());
                fos.write(BOTTOM_MESSAGE.getBytes());
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String exceptionToString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        String exceptionMessage = "";
        try {
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw, true));
            exceptionMessage = sw.toString();
            sw.flush();
            sw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return exceptionMessage;
    }

    /**
     * <p>更新运行排班，当服务器版本大于本地版本时进行更新，并记录日志，当服务器版本小于等于本地版本时不操作，报错时记录错误信息
     *
     * @param context    上下文对象
     * @param ciphertext 更新运行倒班所需内容的密文
     * @return 更新运行倒班结果
     * @throws NullPointerException 当context为null时抛出异常
     */
    public static boolean updateSchedule(Context context, String ciphertext) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException();
        }
        try {
            Gson gson = new Gson();
            InformationBean informationBean = gson.fromJson(unCompress(ciphertext), InformationBean.class);
            int localVersion = getScheduleVersion(context);
            int remoteVersion = informationBean.getVersion();
            if (remoteVersion > localVersion) {
                Utils.saveScheduleLog(context, "密文数据：" + ciphertext);
                List<ContactsBean> list = informationBean.getContactsBeanList();
                List<ContactsBean> addList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    ContactsBean contactsBean = list.get(i);
                    if (contactsBean.getFlag() == ADD_UPDATE) {
                        addList.add(contactsBean);
                    }
                }
                deleteAllConstacts(context, "[运行]");
                addConstacts(context, addList);
                updateScheduleVersion(context, remoteVersion);
                return true;
            } else {
                i("运行排班已经是最新版本");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.saveExceptionLog(context, "异常信息：" + exceptionToString(e) + "\n\n\n密文数据：" + ciphertext);
            return false;
        }
    }

    /**
     * <p>更应急值班，技术值班，当服务器版本大于本地版本时进行更新，并记录日志，当服务器版本小于等于本地版本时不操作，报错时记录错误信息
     *
     * @param context    上下文对象
     * @param ciphertext 更新应急值班或技术值班所需内容的密文
     * @return 更新应急值班或技术值班结果
     * @throws NullPointerException 当context为null时抛出异常
     */
    public static boolean updateEmergencyTechnology(Context context, String ciphertext) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException();
        }
        try {
            Gson gson = new Gson();
            InformationBean informationBean = gson.fromJson(unCompress(ciphertext), InformationBean.class);
            int localVersion = getEmergencyTechnologyVersion(context);
            int remoteVersion = informationBean.getVersion();
            if (remoteVersion > localVersion) {
                Utils.saveEmergencyTechnologyLog(context, "密文数据：" + ciphertext);
                List<ContactsBean> list = informationBean.getContactsBeanList();
                List<ContactsBean> addList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    ContactsBean contactsBean = list.get(i);
                    if (contactsBean.getFlag() == ADD_UPDATE) {
                        addList.add(contactsBean);
                    }
                }
                deleteAllConstacts(context, "[应急]");
                deleteAllConstacts(context, "[技术]");
                addConstacts(context, addList);
                updateEmergencyTechnologyVersion(context, remoteVersion);
                return true;
            } else {
                i("应急，技术已经是最新版本");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.saveExceptionLog(context, "异常信息：" + exceptionToString(e) + "\n\n\n密文数据：" + ciphertext);
            return false;
        }
    }

    /**
     * <p>更新企业通讯录，当服务器版本大于本地版本时进行更新，并记录日志，当服务器版本小于等于本地版本时不操作，报错时记录错误信息
     *
     * @param context    上下文对象
     * @param ciphertext 更新企业通讯录所需内容的密文
     * @return 更新企业通讯录结果
     * @throws NullPointerException          当context为null时抛出异常
     * @throws IOException                   解压缩时内容不符合抛出异常
     * @throws OperationApplicationException 通讯录处理时可能抛出异常
     * @throws RemoteException               通讯录处理时可能抛出异常
     */
    public static boolean updateContacts(Context context, String ciphertext) throws NullPointerException, IOException, OperationApplicationException, RemoteException {
        if (context == null) {
            throw new NullPointerException();
        }
        try {
            Gson gson = new Gson();
            InformationBean informationBean = gson.fromJson(unCompress(ciphertext), InformationBean.class);
            int localVersion = getContactsVersion(context);
            int remoteVersion = informationBean.getVersion();
            if (remoteVersion > localVersion) {
                Utils.saveContactsLog(context, "密文数据：" + ciphertext);
                List<ContactsBean> list = informationBean.getContactsBeanList();
                List<ContactsBean> addList = new ArrayList<>();
                List<String> nameList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    ContactsBean contactsBean = list.get(i);
                    nameList.add(contactsBean.getName());
                    if (contactsBean.getFlag() == ADD_UPDATE) {
                        addList.add(contactsBean);
                    }
                }
                deleteConstacts(context, nameList);
                addConstacts(context, addList);
                updateContactsVersion(context, remoteVersion);
                return true;
            } else {
                i("通讯录已经是最新版本");
                return true;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Utils.saveExceptionLog(context, "异常信息：" + exceptionToString(e) + "\n\n\n密文数据：" + ciphertext);
            return false;
        }
    }

    /**
     * <p>info级别日志信息，TAG为inossem_cgn
     */
    private static void i(String message) {
        Log.i(TAG, message);
    }
}