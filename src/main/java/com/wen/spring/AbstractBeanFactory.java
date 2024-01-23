package com.wen.spring;

import com.wen.spring.annotations.Component;
import com.wen.spring.annotations.ComponentScan;
import com.wen.spring.annotations.Scope;
import com.wen.spring.beans.BeanDefinition;
import com.wen.spring.utils.StringUtils;

import java.beans.Introspector;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象工厂类
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private static final Map<String, Object> singletons = new ConcurrentHashMap<>();
    private static final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();


    protected abstract void test();


    public AbstractBeanFactory(Class<?> clazz) {
        try {
            scan(clazz);
            System.out.println();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void scan(Class<?> clazz) throws Exception {
        doScan(clazz);
    }

    private void doScan(Class<?> clazz) throws Exception {
        ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
        if (null != componentScan) {
            String path = componentScan.value();
            if (!StringUtils.isEmpty(path)) {
                path = path.replace(".", "/");
                URL resource = clazz.getClassLoader().getResource(path);
                if (null != resource) {
                    File folder = new File(resource.getFile());
                    if (folder.isDirectory()) {
                        File[] files = folder.listFiles();
                        if (null != files) {
                            for (File file : files) {
                                if (file.getName().endsWith(".class")) {

                                    Component component = file.getClass().getAnnotation(Component.class);
                                    if (null != component) {
                                        String beanName = component.value();
                                        if (!StringUtils.isEmpty(beanName)) {
                                            beanName = Introspector.decapitalize(file.getClass().getName());
                                        }
                                        BeanDefinition build = BeanDefinition.builder().beanName(beanName).clazz(file.getClass()).build();
                                        // 加入 map集合
                                        beanDefinitions.put(beanName,build);

                                        // bean scope  单例 or 原型 等
                                        Scope scope = file.getClass().getAnnotation(Scope.class);
                                        if (null != scope) {
                                            build.setScope(scope.value());
                                        }
                                    }
                                }
                            }
                        }
                    }else {
                        throw new Exception("error params not folder");
                    }
                }
            }

        }

    }


    @Override
    public Object getBean(String beanName) {

        return null;
    }

    @Override
    public Class<?> getClassByName(String beanName) {
        return null;
    }

    @Override
    public void createBean(String beanName) {

    }


}
