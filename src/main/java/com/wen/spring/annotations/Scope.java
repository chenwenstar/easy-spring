package com.wen.spring.annotations;

import com.wen.spring.enums.ScopeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
     ScopeEnum value() default ScopeEnum.singleton;
}
