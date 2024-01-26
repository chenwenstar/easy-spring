package com.wen.spring;

import java.lang.reflect.InvocationTargetException;

/**
 * bean工厂接口
 */
public interface BeanFactory {
     /**
      * 获取bean
      * @param beanName 名称
      * @return bean对象
      */
     Object getBean(String beanName) throws Exception;

     /**
      *
      * @param beanName 名称
      * @return bean的class
      */
     Class<?> getClassByName(String beanName);

     /**
      *
      * @param beanName 名称
      */
     Object createBean(String beanName) throws Exception;
}
