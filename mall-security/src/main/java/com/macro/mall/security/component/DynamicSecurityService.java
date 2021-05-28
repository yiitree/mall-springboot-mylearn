package com.macro.mall.security.component;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * 自定义的动态权限加载验证，就是使用路径匹配，是一个接口，其他模块需要使用安全验证的时候，只需要实现该接口即可
 * DynamicSecurityService
 * 动态权限相关业务类
 * Created by macro on 2020/2/7.
 */
public interface DynamicSecurityService {
    /**
     * 加载资源ANT通配符和资源对应MAP
     */
    Map<String, ConfigAttribute> loadDataSource();
}
