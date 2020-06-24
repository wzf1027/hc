package cn.stylefeng.guns.core.interceptor;

import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

public class GunsUserFilter extends AccessControlFilter {



    private boolean kickoutAfter = false; //踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
    private int maxSession = 1; //同一个帐号最大会话数 默认1

    private SessionManager sessionManager;
    private Cache<String, Deque<Serializable>> cache;


    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("shiro-kickout-session");
    }

    /**
     * Returns <code>true</code> if the request is a
     * {@link #isLoginRequest(ServletRequest, ServletResponse) loginRequest} or
     * if the current {@link #getSubject(ServletRequest, ServletResponse) subject}
     * is not <code>null</code>, <code>false</code> otherwise.
     *
     * @return <code>true</code> if the request is a
     * {@link #isLoginRequest(ServletRequest, ServletResponse) loginRequest} or
     * if the current {@link #getSubject(ServletRequest, ServletResponse) subject}
     * is not <code>null</code>, <code>false</code> otherwise.
     */
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginRequest(request, response)) {
            return true;
        } else {
            Subject subject = getSubject(request, response);
            if (subject.getPrincipal() == null){
                return false;
            }
            Session session = subject.getSession();
            ShiroUser user = (ShiroUser) subject.getPrincipal();
            String username = user.getAccount();
            Serializable sessionId = session.getId();
            //读取缓存   没有就存入
            Deque<Serializable> deque = cache.get(username);
            if(deque == null) {
                deque = new LinkedList<Serializable>();
                cache.put(username, deque);
            }
            //如果队列里没有此sessionId，且用户没有被踢出；放入队列
            if(!deque.contains(sessionId) && session.getAttribute("kickout") == null) {
                deque.push(sessionId);
            }

            //如果队列里的sessionId数超出最大会话数，开始踢人
            while(deque.size() > maxSession) {
                Serializable kickoutSessionId = null;
                if(kickoutAfter) { //如果踢出后者
                    kickoutSessionId = deque.removeFirst();
                } else { //否则踢出前者
                    kickoutSessionId = deque.removeLast();
                }
                try {
                    Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                    if(kickoutSession != null) {
                        //设置会话的kickout属性表示踢出了
                        kickoutSession.setAttribute("kickout", true);
                    }
                } catch (Exception e) {//ignore exception
                    e.printStackTrace();
                }
            }

            //如果被踢出了，直接退出，重定向到踢出后的地址
            if (session.getAttribute("kickout") != null) {
                //会话被踢出了
                try {
                    subject.logout();
                } catch (Exception e) { //ignore
                }
                return false;
            }
            return true;
        }
    }

    /**
     * This default implementation simply calls
     * {@link #saveRequestAndRedirectToLogin(ServletRequest, ServletResponse) saveRequestAndRedirectToLogin}
     * and then immediately returns <code>false</code>, thereby preventing the chain from continuing so the redirect may
     * execute.
     */
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);

        /**
         * 如果是ajax请求则不进行跳转
         */
        if (httpServletRequest.getHeader("x-requested-with") != null
                && httpServletRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
            httpServletResponse.setHeader("sessionstatus", "timeout");
            return false;
        } else {

            /**
             * 第一次点击页面
             */
            String referer = httpServletRequest.getHeader("Referer");
            if (referer == null) {
                saveRequestAndRedirectToLogin(request, response);
                return false;
            } else {

                /**
                 * 从别的页面跳转过来的
                 */
                if (ShiroKit.getSession().getAttribute("sessionFlag") == null) {
                    httpServletRequest.setAttribute("tips", "您的账号已经在另一处登录了,您被迫下线或者登录已失效");
                    httpServletRequest.getRequestDispatcher("/login").forward(request, response);
                    return false;
                } else {
                    saveRequestAndRedirectToLogin(request, response);
                    return false;
                }
            }
        }
    }
}
