package com.example.networktest;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by J。 on 2016/4/23.
 * OkHttp工具类
 */
public class OkHttpUtil {
    /**
     * 实例化静态okHttpClient对象
     */
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();
    /**
     * Host
     */
    private static final String HOST = "jw.gdgm.cn";
    /**
     * Referer
     */
    private static final String REFERER = "http://jw.gdgm.cn/";
    /**
     * 验证码请求地址
     */
    private static final String URL_CODE = "http://kmustjwcxk4.kmust.edu.cn/JWWEB/sys/ValidateCode.aspx";
    /**
     * 登录地址
     */
    private static final String URL_LOGIN = "http://kmustjwcxk4.kmust.edu.cn/JWWEB/_data/index_LOGIN.aspx";

    /**
     * 图书查询url
     */
    private static final String LIBRARY_SEARCH = "http://121.32.26.99:9000/chaxun/chaxun.jsp";

    /**
     * 图书查询host
     */
    private static final String LIBRAY_SEARCH_HOST = "121.32.26.99:9000";

    /**
     * 图书查询Referer
     */
    private static final String LIBRARY_SEARCH_REFERER = "http://121.32.26.99:9000/index.jsp";

    /**
     * 图书查询url所属文件夹
     */
    private static final String LIBRARY_SEARCH_PARENT_URL = "http://121.32.26.99:9000/chaxun/";

    public static OkHttpClient getOkHttpClient() {

        return okHttpClient;
    }

    public static String getUrlLogin() {
        return URL_LOGIN;
    }

    public static String getUrlCode() {
        return URL_CODE;
    }

    public static String getREFERER() {
        return REFERER;
    }

    public static String getLibrarySearchParentUrl() {
        return LIBRARY_SEARCH_PARENT_URL;
    }

    /**
     * 获得一个Request对象
     *
     * @param url 请求的Url
     * @return 返回一个Request对象
     */
    public static Request getRequest(String url,String session) {
        Request request = new Request.Builder().url(url)
                .addHeader("cookie",session)
                .addHeader("Accept","image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding","gzip, deflate")
                .addHeader("Accept-Language","zh-CN,zh;q=0.8")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0")
                .addHeader("Host","kmustjwcxk4.kmust.edu.cn")
                .addHeader("Referer","http://kmustjwcxk4.kmust.edu.cn/JWWEB/_data/index_LOGIN.aspx")
                .build();
        return request;
    }

    /**
     * 获得一个Request对象
     *
     * @param url         请求的Url
     * @param requestBody 请求的参数
     * @return 返回一个Request对象
     */
    public static Request getRequest(String url, RequestBody requestBody) {
        Request request = new Request.Builder().url(url).addHeader("Host", HOST).addHeader("Referer", REFERER).post(requestBody).build();
        return request;
    }

    /**
     * 获得一个Request对象
     *
     * @param url         请求的Url
     * @param Referer     请求头Referer
     * @param requestBody 请求的参数
     * @return 返回一个Request对象
     */
    public static Request getRequest(String url, String Referer, RequestBody requestBody) {
        Request request = new Request.Builder().url(url).addHeader("Host", HOST).addHeader("Referer", Referer).post(requestBody).build();
        return request;
    }

    /**
     * 获得一个Requese对象
     *
     * @param url     请求的Url
     * @param Host    请求头Host
     * @param Referer 请求头Referer
     * @return 返回一个Request对象
     */
    public static Request getRequest(String url, String Host, String Referer) {
        Request request = new Request.Builder().url(url).addHeader("Host", Host).addHeader("Referer", Referer).build();
        return request;
    }

    public static String getLibrarySearch() {
        return LIBRARY_SEARCH;
    }

    public static String getLibrarySearchReferer() {
        return LIBRARY_SEARCH_REFERER;
    }

    public static String getLibraySearchHost() {
        return LIBRAY_SEARCH_HOST;
    }

    /**
     * 将Url中的中文进行编码
     *
     * @param url 要进行编码的Url
     * @return 编码后的url
     */
    public static String encodeUrl(String url) {
        String new_url = url;
        Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(new_url);
        while (matcher.find()) {
            try {
                new_url = new_url.replaceAll(matcher.group(), URLEncoder.encode(matcher.group(), "gb2312"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new_url;
    }
}
