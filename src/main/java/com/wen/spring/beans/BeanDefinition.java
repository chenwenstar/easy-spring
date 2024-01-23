package com.wen.spring.beans;

import com.wen.spring.enums.ScopeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * bean 元数据信息
 */
@Data
@Builder
public class BeanDefinition {
    private String beanName;
    private Class<?> clazz;
    private ScopeEnum scope;

}
