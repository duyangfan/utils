package utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 *
 *
 *
 */
public class WebUtils {

    /**
     * Safari 浏览器
     */
    private static final String SAFARI = "Safari";
    /**
     * 谷歌浏览器
     */
    private static final String CHROME = "Chrome";
    /**
     * 火狐浏览器
     */
    private static final String FIREFOX = "Firefox";
    /**
     * IE浏览器
     */
    private static final String RV = "rv:";
    /**
     * IE浏览器
     */
    private static final String MSIE = "MSIE";

    /**
     * Edge浏览器
     */
    private static final String EDGE = "Edge";
    /**
     * 64位
     */
    private static final String X642 = "x64";
    private static final String BIT_64 = "_64";
    private static final String X64 = "X64";
    private static final String WOW64 = "WOW64";
    /**
     * 苹果系统
     */
    private static final String MAC = "Mac";
    /**
     * Linux系统
     */
    private static final String LINUX = "Linux";
    /**
     * Windows XP系统
     */
    private static final String WINDOWS_XP = "Windows XP";
    /**
     * Windows NT系统
     */
    private static final String WINDOWS_NT_6_1 = "Windows NT 6.1";
    private static final String ARM = "ARM;";
    private static final String WINDOWS_NT_6_2 = "Windows NT 6.2";

    private static final String X_REQUESTED_WITH2 = "X_REQUESTED_WITH";
    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    private static final String X_REQUESTED_WITH = "X-Requested-With";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCEPT = "accept";
    public static final String DEFAULT_COOKIE_PATH = "/";

    /**
     * \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔), 字符串在编译时会被转码一次,所以是 "\\b" \B
     * 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
     */
    private static final String PHONEREG = "\\b(ip(hone|od)|android|opera m(ob|in)i" + "|windows (phone|ce)|blackberry"
            + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp" + "|laystation portable)|nokia|fennec|htc[-_]"
            + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    private static final String TABLEREG = "\\b(ipad|tablet|(Nexus 7)|up.browser" + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

    /** 移动设备正则匹配：手机端、平板 */
    private static final Pattern PHONEPAT = Pattern.compile(PHONEREG, Pattern.CASE_INSENSITIVE);
    private static final Pattern TABLEPAT = Pattern.compile(TABLEREG, Pattern.CASE_INSENSITIVE);



    public final static String ORGAN_CODE="organ_Code";//选中的机构编号
    public final static String LAST_LOGIN_IP="last_login_ip";//上一次登录的ip
    public static final String LOGIN_USER_INFOR = "login_user_infor";// 登陆的用户信息



    /**
     * 得到访问的ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 服务器的IP
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getHostIp() {

        String ipv4 = "";
        try {
            Enumeration<NetworkInterface> netIf = NetworkInterface.getNetworkInterfaces();
            while (netIf.hasMoreElements()) {
                NetworkInterface nwi = netIf.nextElement();
                Enumeration address = nwi.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress in = (InetAddress) address.nextElement();
                    if (in instanceof Inet4Address) {
                        ipv4 = in.getHostAddress();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            ipv4 = "";
        }
        return ipv4;
    }

    /**
     * 返回国际化国家标识串
     *
     * @return
     * @throws Exception
     */
    public static String getI18nCountry(HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        String language = requestContext.getLocale().getLanguage();
        String country = requestContext.getLocale().getCountry();
        if (country == null || "".equals(country)) {
            if ("zh".equals(language)) {
                country = "CN";
            } else if ("en".equals(language)) {
                country = "US";
            }
        }
        // logger.debug("本地国际化语言：" + language + "本地国际化国家：" + country);
        String lc = "_" + language + (StringUtils.isEmpty(country) ? "" : "_" + country);
        return lc;
    }

    /**
     * 获取国际化信息
     *
     * @param key
     * @param request
     * @return
     */
    public static String getI18nMsg(HttpServletRequest request,String key) {
        // 从后台代码获取国际化信息
        String value = new RequestContext(request).getMessage(key);
        return value != null ? value : "";
    }

    /**
     * 获取cookie值
     *
     * @param request
     *            http请求
     * @param cookieName
     *            cookie名称
     * @return
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    String value= cookie.getValue();
                    return value==null?"":value;
                }
            }
        }
        return "";
    }

    /**
     * 从指定cookie数组中获取cookieName对应的cookie信息
     *
     * @param cookies
     *            cookie数组
     * @param cookieName
     *            cookie名称
     * @return
     */
    public static Cookie getCookie(Cookie[] cookies, String cookieName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 设置 cookie
     *
     * @param cookie
     *            cookie名称(key)
     * @param value
     *            cookie值
     * @param age
     *            生命周期
     */
    public static void setCookie(HttpServletResponse response, String cookieName, String value, int age) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(age);
        cookie.setPath(DEFAULT_COOKIE_PATH);
        cookie.setHttpOnly(true);//如果您在cookie中设置了HttpOnly属性，那么通过js脚本将无法读取到cookie信息，这样能有效的防止XSS攻击
        response.addCookie(cookie);
    }

    /**
     * 更新cookie信息
     *
     * @param response
     * @param cookie
     * @param value
     * @param age
     */
    public static void updateCookie(HttpServletResponse response, final Cookie cookie, String value, int age) {
        if (cookie != null) {
            cookie.setValue(value);
            cookie.setMaxAge(age);
            cookie.setPath(DEFAULT_COOKIE_PATH);
            cookie.setHttpOnly(true);//如果您在cookie中设置了HttpOnly属性，那么通过js脚本将无法读取到cookie信息，这样能有效的防止XSS攻击
            response.addCookie(cookie);
        }
    }



    /**
     * 取header中的所有信息
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
     * @param request
     * @return ip
     */
    public static String getLocalIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded.split(",")[0];
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                if(forwarded != null){
                    forwarded = forwarded.split(",")[0];
                }
                ip = realIp + "/" + forwarded;
            }
        }
        return ip;
    }

    public static String getIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded;
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                ip = realIp + "/" + forwarded.replaceAll(", " + realIp, "");
            }
        }
        return ip;
    }

    public static String getIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }



    /**
     * 请求方式判断
     *
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        boolean json = request.getHeader(ACCEPT)!=null&&request.getHeader(ACCEPT).indexOf(APPLICATION_JSON) > -1;
        boolean xmlHttpRequest = (request.getHeader(X_REQUESTED_WITH) != null
                && request.getHeader(X_REQUESTED_WITH).indexOf(XML_HTTP_REQUEST) > -1);
        boolean xmlHttpRequestParam = XML_HTTP_REQUEST.equalsIgnoreCase(request.getParameter(X_REQUESTED_WITH2));

        if (!(json || xmlHttpRequest || xmlHttpRequestParam)) {
            return false;
        }
        return true;
    }

    /**
     * 从Request中获取客户端操作系统名称、操作系统位数、浏览器版本。例如：win7_64位_IE8.0_userAgent详细
     *
     * @param userAgent
     * @return
     */
    public static String parser(String userAgent) {
        int start1 = userAgent.indexOf("(");
        userAgent = userAgent.substring(start1, userAgent.length());
        String os = "Windows";
        String bit = "32位";
        String brower = "";
        if (userAgent.indexOf(WINDOWS_NT_6_2) != -1) {
            os = "Win8";
            if (userAgent.indexOf(ARM) != -1) {
                os = "WinRT";
            }
        } else if (userAgent.indexOf(WINDOWS_NT_6_1) != -1) {
            os = "Win7";
        } else if (userAgent.indexOf(WINDOWS_XP) != -1) {
            os = "WinXP";
        } else if (userAgent.indexOf(LINUX) != -1) {
            os = LINUX;
        } else if (userAgent.indexOf(MAC) != -1) {
            os = MAC;
        }
        if (userAgent.indexOf(WOW64) != -1 || userAgent.indexOf(BIT_64) != -1 || userAgent.indexOf(X64) != -1
                || userAgent.indexOf(X642) != -1) {
            bit = "64位";
        }

        if (userAgent.indexOf(MSIE) != -1) {
            brower = "IE";
            int start = userAgent.indexOf(MSIE);
            int end = userAgent.indexOf(";", start);
            brower = brower + userAgent.substring(start + 5, end);
        } else if (userAgent.indexOf(RV) != -1) {
            brower = "IE";
            int start = userAgent.indexOf(RV);
            int end = userAgent.indexOf(")", start);
            brower = brower + userAgent.substring(start + 3, end);
        } else if (userAgent.indexOf(FIREFOX) != -1) {
            brower = FIREFOX;
            int start = userAgent.indexOf("Firefox/");
            brower = brower + userAgent.substring(start + FIREFOX.length() + 1, userAgent.length());
        } else if (userAgent.indexOf(CHROME) != -1) {
            brower = CHROME;
            int start = userAgent.indexOf("Chrome/");
            int end = userAgent.indexOf(".", start);
            end = userAgent.indexOf(".", end + 1);
            brower = brower + userAgent.substring(start + CHROME.length() + 1, end);
        } else if (userAgent.indexOf(SAFARI) != -1) {
            // Version/5.1.2
            brower = SAFARI;
            int start = userAgent.indexOf("Version/");
            int end = userAgent.indexOf(" ", start);
            brower = brower + userAgent.substring(start + "Version".length() + 1, end);
        }

        return os + "_" + bit + "_" + brower + "_" + userAgent;
    }

    /**
     * 检测是否是移动设备访问
     *
     * @param userAgent
     *            浏览器标识
     * @return true:移动设备接入，false:pc端接入
     */
    public static boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (null == userAgent) {
            userAgent = "";
        }
        // 匹配
        Matcher matcherPhone = PHONEPAT.matcher(userAgent);
        Matcher matcherTable = TABLEPAT.matcher(userAgent);
        if (matcherPhone.find() || matcherTable.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取浏览器编码
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getBrowserCode(HttpServletRequest request,String fileName) throws UnsupportedEncodingException {
        String userAgent=request.getHeader("user-agent");
        if(StringUtils.isNotEmpty(userAgent)) {
            if (userAgent.indexOf(EDGE) != -1) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else if (userAgent.indexOf(FIREFOX) != -1) {
                fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1" );
            } else if (userAgent.indexOf(CHROME) != -1) {
                fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1" );
            } else if (userAgent.indexOf(SAFARI) != -1) {
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }else {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            }
        }
        return fileName;
    }
}
