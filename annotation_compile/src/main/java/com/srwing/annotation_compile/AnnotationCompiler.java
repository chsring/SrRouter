package com.srwing.annotation_compile;

import com.google.auto.service.AutoService;
import com.srwing.annotation.BindPath;
import com.srwing.annotation.Variable;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

// 生成代码Util类
// 通过注解获取到注解标记的Activity，然后动态写到Util
@AutoService(Processor.class) //注册当前类为注解处理器类
public class AnnotationCompiler extends AbstractProcessor {
    Filer filer;

    /**
     * 核心功能 真正处理注解
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 获取到当前模块中 被BindPath标记的类 通过roundEnv拿，得到的是类对象。
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindPath.class);
        Map<String, String> map = new HashMap<>();
        // Element 结点基类
        // 类结点 TypeElement
        // 方法节点 ExecutableElement
        // 成员变量结点 VariableElement
        // 遍历类节点
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            //获取到带包名的类名，就是作为value
            String activityName = typeElement.getQualifiedName().toString();
            //拿到注解
            BindPath annotation = typeElement.getAnnotation(BindPath.class);
            //拿到注解之后 再去拿到value，这个value就 "test/MainActivity"
            String key = annotation.value();
            map.put(key, activityName + ".class");
        }
        //  写文件，如果当前模块 没有标记注解，那么就不执行写文件
        if (map.size() > 0) {
            Writer writer = null;
            // 生成一个文件的名字,每次生成的时候 都加上时间戳。因为可能有多个模块依赖，所以会生成多个，每个生成的时候加不同时间戳
            String className = "ActivityUtil" + System.currentTimeMillis();
            try {
                JavaFileObject sourceFile = filer.createSourceFile(Variable.PACK_NAME +"."+ className);
                writer = sourceFile.openWriter();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("package "+Variable.PACK_NAME + ";\n" +
                        "import com.srwing.router.SrRouter;\n" +
                        "import com.srwing.router.IRouter;\n" +
                        "\n" +
                        "public class " + className + " implements IRouter {\n" +
                        "      @Override\n" +
                        "public void putActivity(){\n");
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = map.get(key);
                    stringBuffer.append("SrRouter.getInstance().addActivity(\"" + key + "\"," +
                            value + ");\n");
                }
                stringBuffer.append("\n}\n}");
                writer.write(stringBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    /**
     * 声明当前注解处理器支持java的最新版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    /**
     * 注解处理器 要识别 处理的注解 要识别BindPath
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindPath.class.getCanonicalName());
        return types;
    }
}