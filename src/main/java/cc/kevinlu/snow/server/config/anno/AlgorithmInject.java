package cc.kevinlu.snow.server.config.anno;

import java.lang.annotation.*;

/**
 * @author chuan
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface AlgorithmInject {

    /**
     * 需要注入的类
     * 
     * @return
     */
    Class<?> clazz();

    /**
     * 是否手动创建，默认为false:从spring容器中获取
     * 
     * @return
     */
    boolean manual() default false;

}
