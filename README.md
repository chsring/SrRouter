# SrRouter：手动实现ARouter
### ARouter原理：
- ARouter类里面维护了一个Map，key是我们在@ARouter填写的路径，value就是对应跳转的activity。@ARouter是新建了一个编译时类注解，ARouter就是使用编译时注解动态生成的代码
- 在项目编译时，编译器会检测每个module的gradle依赖，如果有annotationProcessor，便会找到对应的注解解释器（@AutoService(Processor.class) //注册当前类为注解处理器类），并执行它的process方法进行解析
- 在这个方法里面，会获取到所有带有@ARouter注解的类，会在固定包名下面动态生成ActivityUtil类，里面的put方法会获取到ARouter单例，并把通过注解获取到的路径作为key，把对应的类名作为value放入map中
- 在ARouter初始化的时候，会通过pms获取到对应的应用信息，然后从APK的完整路径获取到编译后的dex文件目录，获取编译后的dex文件中所有的class，拿到迭代器后，进行遍历，遍历了当前应用所有的文件，通过遍历所有的class的包名，判断类的包名是否符合com.xxx.xxx，如果符合，拿到类名，然后通过反射创建出类，然后调用这个类的putActivity方法，把key和value保存到Map中
- 使用时，直接传入key（ARouter注解中的路径），从Map中获取到对应的value（类名），通过startActivity跳转。
