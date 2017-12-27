package com.dawn.impetus.automove.utils;

import android.util.Log;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

//通过URL访问服务器 含get方法 以及POS方法

public class HttpUtil {
    // 创建HttpClient对象
    public static HttpClient httpClient = new DefaultHttpClient();

    //发布服务器的基本地址
    public static final String BASE_URL = "http://vpn.zixuncr.com:5080/report/";



    public static String ENCODING = "UTF8";

    /**
     * @param url 发送请求的URL
     * @return 服务器响应字符串
     * @throws Exception
     */

    public static synchronized String getRequest(final String url) throws Exception {
        FutureTask<String> task = new FutureTask<>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        // 创建HttpGet对象
                        HttpGet get = new HttpGet(url);
                        // 发送GET请求
                        HttpResponse httpResponse = httpClient.execute(get);
                        // 如果服务器成功地返回响应 如果服务器返回响应不成功  报网络连接不成功错误
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            // 获取服务器响应字符串
                            String result = EntityUtils
                                    .toString(httpResponse.getEntity());
                            //					Log.e("--getRequest--",result);
                            return result;
                        }
                        return null;
                    }
                });
        new Thread(task).start();
        return task.get();
    }

    /**
     * @param url 发送请求的URL
     * @return 服务器响应字符串
     * @throws Exception
     */

    public static synchronized String postRequest(final String url, final Map<String, String> rawParams) throws Exception {
        FutureTask<String> task = new FutureTask<>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        // 创建HttpPost对象
                        //httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 300);//socket 链接时间
                        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);//链接超时
                        HttpPost post = new HttpPost(url);
                        // 如果传递参数个数比较多的话可以对传递的参数进行封装
                        List<NameValuePair> params =  new ArrayList<>();
                        for (String key : rawParams.keySet()) {
                            //封装请求参数
                            params.add(new BasicNameValuePair(key  , rawParams.get(key)));
                        }
                        // 设置请求参数
                        //post.set
                        post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
                        // 发送POST请求
                        HttpResponse httpResponse = httpClient.execute(post);
                        // 如果服务器成功地返回响应
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            // 获取服务器响应字符串
                            String result = EntityUtils.toString(httpResponse.getEntity());
                            //					Log.e("--postRequest--",result);
                          //  post.abort();
                            return result;
                        }
                        return null;
                    }
                });

        new Thread(task).start();
        return task.get();
    }

    public static synchronized String doPost(final String url, final JSONObject json)throws Exception{
        FutureTask<String> task = new FutureTask<>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        DefaultHttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        String result = null;
                        StringEntity s = new StringEntity(json.toString(), HTTP.UTF_8);
                        s.setContentType("application/json;charset=utf-8");//发送json数据需要设置contentType
                        post.setEntity(s);
                        HttpResponse res = client.execute(post);
                        if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                            HttpEntity entity = res.getEntity();
                            result = EntityUtils.toString(res.getEntity());// 返回json格式：
                        }

                        return result;
                    }

                });

        new Thread(task).start();
        return task.get();
    }

    public static  String postSmsRequest(final String url, final Map<String, String> rawParams) throws Exception {


        FutureTask<String> task = new FutureTask<>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        // 创建HttpPost对象
                        //httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 300);//socket 链接时间
                        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);//链接超时
                        HttpPost post = new HttpPost(url);

                        // 如果传递参数个数比较多的话可以对传递的参数进行封装
                        List<NameValuePair> params =  new ArrayList<>();
                        for (String key : rawParams.keySet()) {
                            //封装请求参数
                            params.add(new BasicNameValuePair(key  , rawParams.get(key)));
                        }
                        // 设置请求参数
                        //post.set
                        post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
                        // 发送POST请求
                        HttpResponse httpResponse = httpClient.execute(post);
                        // 如果服务器成功地返回响应
                        /*if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            // 获取服务器响应字符串
                            String result = EntityUtils.toString(httpResponse.getEntity());
                            //					Log.e("--postRequest--",result);
                            //  post.abort();
                            return result;
                        }*/
                        try{
                            //执行请求
                            HttpEntity httpEntity = httpResponse.getEntity();

                            //获取请求结果
                            if(httpEntity!= null){
                                //利用缓冲区,获取返回结果输入流并读取
                                InputStream inputStream = httpEntity.getContent();
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                try{
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while((length = inputStream.read(buffer))!= -1){
                                        bos.write(buffer,0,length);
                                    }
                                    byte[] result = bos.toByteArray();

                                    //将获取到的字节数据结果转换为字符串
                                   String resultStr = new String(result,ENCODING);
                                   // System.out.println(resultStr);
                                    Log.e("SMS_CODE",resultStr);
                                    return resultStr;
                                }catch (Exception e){
                                    System.out.println(e.getMessage());
                                }finally {
                                    //关闭输入流
                                    inputStream.close();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }



                        return null;
                    }
                });

        new Thread(task).start();
        return task.get();
    }



}
