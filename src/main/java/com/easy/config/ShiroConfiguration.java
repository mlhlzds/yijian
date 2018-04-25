package com.easy.config;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class ShiroConfiguration implements TransactionManagementConfigurer {

    private static Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
    @Resource(name="txManager1")
    private PlatformTransactionManager txManager1;

    // 创建事务管理器1
    @Bean(name = "txManager1")
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 实现接口 TransactionManagementConfigurer 方法，其返回值代表在拥有多个事务管理器的情况下默认使用的事务管理器
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager1;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    @Bean(name = "ShiroRealmImpl")
    public ShiroRealmImpl getShiroRealm() {
        ShiroRealmImpl x = new ShiroRealmImpl();
        x.setCredentialsMatcher(hashedCredentialsMatcher());
        return x;
    }

    @Bean(name = "shiroEhcacheManager")
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return em;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean(name = "securityManager")
    public SecurityManager getDefaultWebSecurityManager() {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(getShiroRealm());
//        dwsm.setCacheManager(getEhCacheManager());
        return dwsm;
    }

    /**
     * 密码匹配凭证管理器
     *
     * @return
     */
    @Bean(name = "hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        RetryLimitHashedCredentialsMatcher r = new RetryLimitHashedCredentialsMatcher(getEhCacheManager());
        r.setHashAlgorithmName("MD5");// 散列算法:这里使用MD5算法;
        r.setHashIterations(3);// 散列的次数，比如散列两次，相当于

        return r;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(getDefaultWebSecurityManager());
        return aasa;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean
                .setSecurityManager(getDefaultWebSecurityManager());
        shiroFilterFactoryBean.setLoginUrl("/templates/index.html");
        filterChainDefinitionMap.put("/templates/daily/**", "anon");
        filterChainDefinitionMap.put("/templates/login.html", "anon");
        filterChainDefinitionMap.put("/templates/loginpc.html", "anon");
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/user/**", "authc");
        filterChainDefinitionMap.put("/templates/**", "authc");
        filterChainDefinitionMap.put("/**", "anon");
        shiroFilterFactoryBean
                .setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


}




