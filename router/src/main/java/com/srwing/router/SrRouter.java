package com.srwing.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.srwing.annotation.Variable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * Description:
 * Created by srwing
 * Date: 2022/5/19
 * Email: 694177407@qq.com
 */
public class SrRouter {
    private Map<String, Class<? extends Activity>> map;
    //这个容器会跟着App的周期走，所以可以声明成单例模式
    private static SrRouter instance = new SrRouter();
    private Context context;

    private SrRouter() {
        map = new HashMap<>();
    }

    public static SrRouter getInstance() {
        return instance;
    }

    public void addActivity(String key, Class<? extends Activity> value) {
        if (null != key && null != value && !map.containsKey(key)) {
            map.put(key, value);
        }

    }

    // 在程序一开始运行的时候就要运行 因此要在application中执行
    public void init(Context context) {
        //我们只是生成了ActivityUtilxxx代码，并未执行里面的put方法
        // 因此运行时我们要执行put方法
        this.context = context;
        // 执行生成的工具类中的putActivity方法
        //首先找到生成的类，通过包名获取到这个包下面的所有的类名
        List<String> className = getClassName(Variable.PACK_NAME);
        //得到类名之后 通过反射 变成一个class对象
        for (String s : className) {
            try {
                Class<?> aClass = Class.forName(s);
                //判断这个类是否是ARouter的子类
                if (IRouter.class.isAssignableFrom(aClass)) {
                    // 通过接口的引用 指向子类
                    IRouter iRouter = (IRouter) aClass.newInstance();
                    iRouter.putActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //收集这个packageName包下面所有类名的方法
    private List<String> getClassName(String packageName) {
        List<String> classList = new ArrayList<>();
        String path = null;
        try {
            //  通过包管理 获取到应用信息，然后获取到APK的完整路径
            path = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            //根据APK的完整路径获取到编译后的dex文件目录
            DexFile dexFile = new DexFile(path);
            //获取编译后的dex文件中所有的class
            Enumeration entries = dexFile.entries();
            //拿到迭代器后，进行遍历，遍历了当前应用所有的文件
            while (entries.hasMoreElements()) {
                //通过遍历所有的class的包名
                String name = (String) entries.nextElement();
                //判断类的包名是否符合com.xxx.xxx
                if (name.contains(packageName)) {
                    //这里曾出现问题，只能拿到一个ActivityUtilxxx
                    classList.add(name);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

    public void jumpActivity(String key, Bundle bundle) {
        Class<? extends Activity> activity = map.get(key);
        if (activity != null) {
            Intent intent = new Intent(context, activity);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        }
    }
}
