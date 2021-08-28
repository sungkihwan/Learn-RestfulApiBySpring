package com.example.restapistudy.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE) // 소스까지만 유효, 컴파일 이후에는 X
public @interface TestDescription {
    String value() default "";
}
