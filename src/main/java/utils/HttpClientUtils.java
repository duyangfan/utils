package utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

public class HttpClientUtils {

    private static final int socketTimeout = 50000;
    private static final int connectTimeout = 50000;

    /**
     * get
     *
     * @throws Exception
     */
    public static String get(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String get(String url,Map<String, String> params) throws Exception {
        return get(url+"?"+buildSignStr(params));
    }

    /**
     * 排序
     * @param params
     * @return
     */
    public static String buildSignStr(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        // 将参数以参数名的字典升序排序
        Map<String, String> sortParams = new TreeMap<String, String>(params);
        // 遍历排序的字典,并拼接"key=value"格式
        for (Entry<String, String> entry : sortParams.entrySet()) {
            if (sb.length() != 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String postJson(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        StringEntity entity = new StringEntity(params, "utf-8");
        entity.setContentType("application/json");
        entity.setContentEncoding("UTF-8");
        httppost.setEntity(entity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String post(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        StringEntity entity = new StringEntity(params, "utf-8");
        entity.setContentType("application/x-www-form-urlencoded");
        entity.setContentEncoding("UTF-8");
        httppost.setEntity(entity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String post(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            formparams.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        httppost.setEntity(entity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String postEntity(String url, Map<String, Object> params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        // 对请求的表单域进行填充
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();

        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            Object obj = params.get(key);
            if (obj instanceof String) {
                reqEntity.addPart(key, new StringBody((String) obj, ContentType.TEXT_PLAIN));
            } else if (obj instanceof File) {
                FileBody fileBody = new FileBody((File) obj);
                reqEntity.addPart(key, fileBody);
            }
        }

        httppost.setEntity(reqEntity.build());

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * 简单的获取一个HTML页面的内容
     *
     * @throws Exception
     */
    public static String getHTML(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity);
        httpclient.close();
        return html;
    }

    /**
     * 下载图片
     *
     * @param url          图片的网址
     * @param destfilename 要保存的路径
     * @throws IOException
     * @throws IllegalStateException
     */
    public static void downloadFile(String url, String destfilename) throws Exception {
        // 生成一个httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        File file = new File(destfilename);
        FileOutputStream fout = new FileOutputStream(file);
        int l = -1;
        byte[] tmp = new byte[1024];
        while ((l = in.read(tmp)) != -1) {
            fout.write(tmp, 0, l);
            // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
        }
        fout.flush();
        fout.close();
        // 关闭低层流。
        in.close();
        httpclient.close();
    }

    /**
     * 取restfull
     *
     * @param url
     * @param json
     * @param method GET POST
     * @return
     * @throws Exception
     */
    public static String postJson(String url, String json, String method) throws Exception {
        if (url.indexOf("?") != -1) {
            url = url + "&_method=" + method;
        } else {
            url = url + "?_method=" + method;
        }
        return postJson(url, json);
    }









    /**
     * ********************************https******************************************
     * @param args
     * @throws Exception
     */

    /**
     * 用来解析https
     *
     * @return
     */
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * 设置信任自签名证书
     *
     * @param keyStorePath 密钥库路径
     * @param keyStorepass 密钥库密码
     * @return
     */
    public static CloseableHttpClient custom(String keyStorePath, String keyStorepass) {
        SSLContext sc = null;
        CloseableHttpClient client =null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            // tomcat是我自己的密钥库的密码，你可以替换成自己的
            // 如果密码为空，则用"nopassword"代替
//	        SSLContext sslcontext = custom("D:\\keys\\wsriakey", "tomcat");

            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sc)).build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            // 创建自定义的httpclient对象
            client = HttpClients.custom().setConnectionManager(connManager).build();

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return client;
    }

    /**
     * 模拟请求
     *
     * @param url      资源地址
     * @param map      参数列表
     * @param encoding 编码
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws ClientProtocolException
     */
    public static String postHttps(String keyStorePath, String keyStorepass, String url, Map<String, String> map, String encoding) throws ClientProtocolException, IOException {
        String body = "";


//        CloseableHttpClient client = HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        // 装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        // 设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));


        // 设置header信息
        // 指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = custom(keyStorePath, keyStorepass).execute(httpPost);
        // 获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // 按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        // 释放链接
        response.close();
        return body;
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String postHttps(String keyStorePath, String keyStorepass, String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpclient =custom(keyStorePath, keyStorepass);
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            formparams.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        httppost.setEntity(entity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String postJsonHttps(String keyStorePath, String keyStorepass,String url, String params) throws Exception {
        CloseableHttpClient httpclient = custom(keyStorePath, keyStorepass);
        HttpPost httppost = new HttpPost(url);
        StringEntity entity = new StringEntity(params, "utf-8");
        entity.setContentType("application/json");
        entity.setContentEncoding("UTF-8");
        httppost.setEntity(entity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String postJsonHttps(String url, String params) throws Exception {
        CloseableHttpClient httpclient = null;
        if (StringUtils.startsWith(url, "https")) {
            httpclient = createSSLClientDefault();
        } else {
            httpclient = HttpClients.createDefault();
        }
        HttpPost httppost = new HttpPost(url);
        StringEntity entity = new StringEntity(params, "utf-8");
        entity.setContentType("application/json");
        entity.setContentEncoding("UTF-8");
        httppost.setEntity(entity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * post
     *
     * @throws Exception
     */
    public static String postHttps(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpclient = null;
        if (StringUtils.startsWith(url, "https")) {
            httpclient = createSSLClientDefault();
        } else {
            httpclient = HttpClients.createDefault();
        }
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            formparams.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        httppost.setEntity(entity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * https请求 get
     *
     * @throws Exception
     */
    public static String getHttps(String url) throws Exception {
        CloseableHttpClient httpclient = null;
        if (StringUtils.startsWith(url, "https")) {
            httpclient = createSSLClientDefault();
        } else {
            httpclient = HttpClients.createDefault();
        }
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity, "UTF-8");
        httpclient.close();
        return html;
    }

    /**
     * 取restfull
     *
     * @param url
     * @param json
     * @param method GET POST
     * @return
     * @throws Exception
     */
    public static String postJsonHttps(String keyStorePath, String keyStorepass,String url, String json, String method) throws Exception {
        if (url.indexOf("?") != -1) {
            url = url + "&_method=" + method;
        } else {
            url = url + "?_method=" + method;
        }
        return postJsonHttps(keyStorePath,keyStorepass,url, json);
    }

    /**
     * 取restfull
     *
     * @param url
     * @param json
     * @param method GET POST
     * @return
     * @throws Exception
     */
    public static String postJsonHttps(String url, String json, String method) throws Exception {
        if (url.indexOf("?") != -1) {
            url = url + "&_method=" + method;
        } else {
            url = url + "?_method=" + method;
        }
        return postJsonHttps(url, json);
    }


    /********************************http请求webservice*****************************************************************************/

    /**
     * 访问服务
     *
     * @param wsdl   wsdl地址
     * @param ns     命名空间
     * @param method 方法名
     * @param params   参数
     * @return
     * @throws Exception
     */
    public static String webService(String wsdl, String ns, String soapAction, String method, Map<String, String> params) throws Exception {
        // 拼接SOAP
        StringBuffer soapRequestData = new StringBuffer("");
        StringBuffer bu = new StringBuffer("");
        if (params != null) {
            for (Entry<String, String> m : params.entrySet()) {
                String name = m.getKey();
                String content = m.getValue();
                bu.append("<tem:" + name + ">" + content + "</tem:" + name + ">");
            }
        }
        // 拼接SOAP
        soapRequestData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"" + ns + "\">");
        soapRequestData.append("<soapenv:Header/>");
        soapRequestData.append("<soapenv:Body>");
        soapRequestData.append("<tem:" + method + ">");
        soapRequestData.append(bu.toString());
        soapRequestData.append("</tem:" + method + ">");
        soapRequestData.append("</soapenv:Body>");
        soapRequestData.append("</soapenv:Envelope>");
        return doPostSoap(wsdl, soapRequestData.toString(), soapAction);
    }

    public static String webService12(String wsdl, String ns, String method, Map<String, String> params) throws Exception {
        // 拼接SOAP
        StringBuffer soapRequestData = new StringBuffer("");
        StringBuffer bu = new StringBuffer("");
        if (params != null) {
            for (Entry<String, String> m : params.entrySet()) {
                String name = m.getKey();
                String content = m.getValue();
                bu.append("<tem:" + name + ">" + content + "</tem:" + name + ">");
            }
        }
        // 拼接SOAP
        soapRequestData.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">");
        soapRequestData.append("<soap:Header/>");
        soapRequestData.append("<soap:Body>");
        soapRequestData.append("<tem:" + method + ">");
        soapRequestData.append(bu.toString());
        soapRequestData.append("</tem:" + method + ">");
        soapRequestData.append("</soap:Body>");
        soapRequestData.append("</soap:Envelope>");
        return doPostSoap12(wsdl, soapRequestData.toString());
    }

    public static String doPostSoap(String postUrl, String soapXml,String soapAction) throws ClientProtocolException, IOException {
        return doPostSoap(postUrl, soapXml, "text/xml;charset=UTF-8", soapAction);
    }

    public static String doPostSoap(String postUrl, String soapXml, String contentType,String soapAction) throws ClientProtocolException, IOException {
        // HttpClient
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(postUrl);
        StringEntity entity = new StringEntity(soapXml, "utf-8");
        entity.setContentType(contentType);
        entity.setContentEncoding("UTF-8");
        httppost.setEntity(entity);
        httppost.setHeader("SOAPAction", soapAction);
        httppost.setConfig(requestConfig);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity1 = response.getEntity();
        String html = EntityUtils.toString(entity1, "UTF-8");
        httpclient.close();

        return html;
    }

    public static String doPostSoap12(String postUrl, String soapXml) throws ClientProtocolException, IOException {
        return doPostSoap(postUrl, soapXml, "application/soap+xml;charset=UTF-8", "");
    }


}

/**
maven 依赖：
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5</version>
        </dependency>

        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpmime</artifactId>
            <version>4.5</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.3.2</version>
        </dependency>

**/


