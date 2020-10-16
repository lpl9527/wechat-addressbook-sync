package com.supwisdom.platform.util;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * HTTP请求封装
 *
 */
public class HttpHelper {
	
    /**
     * 1.GET请求
     */
    public static JSONObject doGet(String url) throws Exception {

        //1.生成一个请求
        HttpGet httpGet = new HttpGet(url);
        //2.配置请求的属性
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();//2000
        httpGet.setConfig(requestConfig);

        //3.发起请求，获取响应信息    
        //3.1 创建httpClient 
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //3.2 发起请求，获取响应信息    
            response = httpClient.execute(httpGet, new BasicHttpContext());

            //如果返回结果的code不等于200，说明出错了  
            if (response.getStatusLine().getStatusCode() != 200) {

                System.err.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + url);
                return null;
            }
            //4.解析请求结果
            HttpEntity entity = response.getEntity();      //reponse返回的数据在entity中 
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");  //将数据转化为string格式  
                System.err.println("GET请求结果：" + resultStr);
                JSONObject result = JSON.parseObject(resultStr);    //将String转换为 JSONObject

                if(result.getInteger("errcode")==null) {
                    return result;
                }else if (0 == result.getInteger("errcode")) {
                    return result;
                }else {
                    /*int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new Exception(result.toJSONString()); */
                    return result;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();                     //释放资源
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 2.POST请求
     */
    public static JSONObject doPost(String url, Object data) throws Exception {
        //1.生成一个请求
        HttpPost httpPost = new HttpPost(url);

        //2.配置请求属性
        //2.1 设置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(100000).setConnectTimeout(100000).build();
        httpPost.setConfig(requestConfig);
        //2.2 设置数据传输格式-json
        httpPost.addHeader("Content-Type", "application/json");
        //2.3 设置请求实体，封装了请求参数
        StringEntity requestEntity = new StringEntity(JSON.toJSONString(data), "utf-8");
        httpPost.setEntity(requestEntity);

        //3.发起请求，获取响应信息    
        //3.1 创建httpClient 
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //3.3 发起请求，获取响应
            response = httpClient.execute(httpPost, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                System.err.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + url);
                return null;
            }
            //获取响应内容
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                //解析响应内容
                JSONObject result = JSON.parseObject(resultStr);
                if(result.getInteger("errcode")==null) {
                    return result;
                }else if (0 == result.getInteger("errcode")) {
                    return result;
                }else {
                    /*int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new Exception(result.toJSONString()); */
                    return result;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();              //释放资源
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}