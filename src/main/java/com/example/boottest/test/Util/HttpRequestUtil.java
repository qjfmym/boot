package com.example.boottest.test.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.List;
import java.util.Map;

public class HttpRequestUtil {


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param requestUrl 发送请求的URL
     * @param param      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String sendGet(String requestUrl, String param) throws Exception  {

        URL u = new URL(requestUrl);
        //注意：重点知识，此句判断用于信任所有的https请求
       /*暂时没有SSL证书认证，去掉证书认证
        if("https".equalsIgnoreCase(u.getProtocol())){
            //测试SslUtils
            SslUtils.ignoreSsl();
        }
        */
        String result = "";
        BufferedReader in = null;
        try {
            String urlString = requestUrl + "?" + param;
            URL url = new URL(urlString);
            // 打开和URL之间的连接
            URLConnection connection = url.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {

        return sendPost(url, param, false, "", 0);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param requestUrl 发送请求的 URL
     * @param param      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param isproxy    是否使用代理模式
     * @param proxyHost  代理服务器地址
     * @param proxyPort  代理服务器端口号
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String requestUrl, String param, boolean isproxy, String proxyHost, int proxyPort) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = null;
            if (isproxy) {//使用代理模式
                @SuppressWarnings("static-access")
                Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            // 打开和URL之间的连接

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);//设置输出流采用字节流
            conn.setDoInput(true); //设置输入流采用字节流
            conn.setRequestMethod("POST");    // POST方法

            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.connect();

            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}

