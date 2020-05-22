package com.ray.hc_spring2.shrio;

import com.ray.hc_spring2.core.service.UserInfoService;
import com.ray.hc_spring2.model.PrivilegeInfo;
import com.ray.hc_spring2.model.RoleInfo;
import com.ray.hc_spring2.model.UserInfo;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 身份校验核心类  
 *
 * @author Administrator
 *
 */
public class MyShiroRealm extends AuthorizingRealm {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserInfoService userInfoService;

    /**
     * 认证信息(身份验证) Authentication 是用来验证用户身份  
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        LOGGER.info("MyShiroRealm.doGetAuthenticationInfo()");
        // 获取用户的输入帐号  
        String username = (String) token.getPrincipal();
        // 通过username从数据库中查找 User对象，如果找到，没找到.
        // 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法  
        UserInfo userInfo = userInfoService.findByUsername(username);
        LOGGER.info("----->>userInfo=" + userInfo);
        if (userInfo == null) {
            return null;
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, // 用户名
                userInfo.getPassword(), // 密码  
                ByteSource.Util.bytes(userInfo.getCredentialsSalt()), // salt=username+salt  
                getName() // realm name  
        );
        return authenticationInfo;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // TODO Auto-generated method stub  
       // LOGGER.info("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();

        RoleInfo role = userInfo.getRoleInfo();
        authorizationInfo.addRole(role.getRoleName());
        System.out.println(role.getPermissions());
        for(PrivilegeInfo p:role.getPermissions()){
            authorizationInfo.addStringPermission(p.getPrivilegeName());
        }

        return authorizationInfo;
    }


}  