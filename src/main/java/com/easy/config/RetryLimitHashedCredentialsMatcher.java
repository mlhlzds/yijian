package com.easy.config;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
    // 声明一个缓存接口，这个接口是Shiro缓存管理的一部分，它的具体实现可以通过外部容器注入
    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }



    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        // 自定义一个验证过程：当用户连续输入密码错误5次以上禁止用户登录一段时间
        if (retryCount.incrementAndGet() > 5) {
            throw new ExcessiveAttemptsException();
        }
        boolean match = super.doCredentialsMatch(token, info);
        if (match) {
            passwordRetryCache.remove(username);
        }
        return match;
    }
    
    public static void main(String[] args) {  
        String algorithmName = "md5";  
        String username = "wang";  
        String password = "111111";  
        String salt1 = username;  
        String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();  
        int hashIterations = 3;  
        SimpleHash hash = new SimpleHash(algorithmName, password,  
                salt1 + salt2, hashIterations);  
        String encodedPassword = hash.toHex();  
        System.out.println(encodedPassword);  
        System.out.println(salt2);  
    }
}