package com.wen.spring;

import com.wen.spring.annotations.Autowired;
import com.wen.spring.annotations.Component;
import com.wen.spring.annotations.ComponentScan;
import com.wen.spring.annotations.Scope;
import com.wen.spring.beans.BeanDefinition;
import com.wen.spring.enums.ScopeEnum;
import com.wen.spring.utils.StringUtils;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 抽象工厂类
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private static final Map<String, Object> singletons = new ConcurrentHashMap<>();
    private static final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();
    private static final Map<Class<?>, String> clazzBeanNames = new ConcurrentHashMap<>();


    // 对象锁
    private static final Object objectMonitor = new Object();
    // 容器状态 0：未创建 1：创建中 2：完成
    private static int state = 0;


    protected abstract void test();


    public AbstractBeanFactory(Class<?> clazz) {
        // double check
        if (state == 0) {
            synchronized (objectMonitor) {
                if (state == 0) {
                    try {
                        state = 1;
                        scan(clazz);
                        initBeans();
                        state = 2;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


    }

    private void scan(Class<?> clazz) throws Exception {
        doScan(clazz);
    }

    private void doScan(Class<?> clazz) throws Exception {
        ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
        if (!Objects.isNull(componentScan)) {
            String path = componentScan.value();
            if (!StringUtils.isEmpty(path)) {
                String newPath = path.replace(".", "/");
                ClassLoader classLoader = clazz.getClassLoader();
                URL resource = classLoader.getResource(newPath);
                if (!Objects.isNull(resource)) {
                    File folder = new File(resource.getFile());
                    if (folder.isDirectory()) {
                        File[] files = folder.listFiles();
                        if (!Objects.isNull(files)) {
                            for (File file : files) {
                                String fileName = file.getName();

                                if (fileName.endsWith(".class")) {

                                    // 获取componentScan的路径 拼接成 com.demo.aService.class
                                    String filePathName = path + "." + fileName.substring(0, fileName.indexOf(".class"));

                                    Class<?> beanClazz = classLoader.loadClass(filePathName);
                                    Component component = beanClazz.getAnnotation(Component.class);
                                    if (!Objects.isNull(component)) {
                                        String beanName = component.value();
                                        if (StringUtils.isEmpty(beanName)) {
                                            beanName = Introspector.decapitalize(beanClazz.getSimpleName());
                                        }
                                        BeanDefinition build = BeanDefinition.builder().beanName(beanName).clazz(beanClazz).build();
                                        // 加入 map集合
                                        beanDefinitions.put(beanName, build);
                                        clazzBeanNames.put(beanClazz, beanName);

                                        // bean scope  单例 or 原型 等
                                        Scope scope = beanClazz.getAnnotation(Scope.class);
                                        if (!Objects.isNull(scope)) {
                                            build.setScope(scope.value());
                                        } else {
                                            build.setScope(ScopeEnum.singleton);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        throw new Exception("error params not folder");
                    }
                }
            }
        }
    }


    private void initBeans() throws Exception {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            if (ScopeEnum.singleton.equals(beanDefinition.getScope())) {
                createBean(beanDefinition.getBeanName());
            }
        }
    }


    @Override
    public Object getBean(String beanName) throws Exception {
        Object bean = singletons.get(beanName);
        if (Objects.isNull(bean)) {
            createBean(beanName);
        }
        return singletons.get(beanName);
    }

    @Override
    public Class<?> getClassByName(String beanName) {
        return null;
    }

    @Override
    public Object createBean(String beanName) throws Exception {
        Object bean = singletons.get(beanName);
        if (Objects.isNull(bean)) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);
            if (ScopeEnum.singleton.equals(beanDefinition.getScope())) {
                Class<?> clazz = beanDefinition.getClazz();
                Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
                try {
                    bean = declaredConstructor.newInstance();
                    // 暂时
                    singletons.put(beanName, bean);

                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        if (!Objects.isNull(autowired)) {

                            // by type
                            String fieldBeanName = clazzBeanNames.get(field.getType());
                            if (Objects.isNull(fieldBeanName)) {
                                throw new Exception("no this bean:" + field.getType());
                            }
                            Object fieldBean = singletons.get(fieldBeanName);
                            if (Objects.isNull(fieldBean)) {
                                fieldBean = createBean(fieldBeanName);
                            }

                            // 属性赋值
                            field.setAccessible(true);
                            field.set(bean, fieldBean);
                        }
                    }

                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }


}
