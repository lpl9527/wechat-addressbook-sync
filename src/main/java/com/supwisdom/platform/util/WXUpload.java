package com.supwisdom.platform.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.supwisdom.platform.framework.util.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 微信临时文件上传工具类
 * TYPE
 * 所有文件size必须大于5个字节
 * 图片（image）:2MB，支持JPG,PNG格式
 * 语音（voice）：2MB，播放长度不超过60s，支持AMR格式
 * 视频（video）：10MB，支持MP4格式
 * 普通文件（file）：20MB
 * @author Administrator
 *
 */
public class WXUpload {  
	
    private static final String upload_wechat_url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";  
    
    public static JSONObject upload(String accessToken, String type, String fileUrl) {  
        JSONObject jsonObject = null;  
        String last_wechat_url = upload_wechat_url.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);  
        // 定义数据分割符  
        String boundary = "----------sunlight";  
        try {  
            URL uploadUrl = new URL(last_wechat_url);  
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();  
            uploadConn.setDoOutput(true);  
            uploadConn.setDoInput(true);  
            uploadConn.setRequestMethod("POST");  
            // 设置请求头Content-Type  
            uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);  
            // 获取媒体文件上传的输出流（往微信服务器写数据）  
            OutputStream outputStream = uploadConn.getOutputStream();  
  
            URL mediaUrl = new URL(fileUrl);  
            HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();  
            meidaConn.setDoOutput(true);  
            meidaConn.setRequestMethod("GET");  
  
            // 从请求头中获取内容类型  
            String contentType = meidaConn.getHeaderField("Content-Type");  
            String filename=getFileName(fileUrl,contentType);  
            // 请求体开始  
            outputStream.write(("--" + boundary + "\r\n").getBytes());  
            outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", filename).getBytes());  
            outputStream.write(String.format("Content-Type: %s\r\n\r\n", contentType).getBytes());  
  
            // 获取媒体文件的输入流（读取文件）  
            BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());  
            byte[] buf = new byte[1024 * 8];  
            int size = 0;  
            while ((size = bis.read(buf)) != -1) {  
                // 将媒体文件写到输出流（往微信服务器写数据）  
                outputStream.write(buf, 0, size);  
            }  
            // 请求体结束  
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());  
            outputStream.close();  
            bis.close();  
            meidaConn.disconnect();  
  
            // 获取媒体文件上传的输入流（从微信服务器读数据）  
            InputStream inputStream = uploadConn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
            StringBuffer buffer = new StringBuffer();  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            bufferedReader.close();  
            inputStreamReader.close();  
            // 释放资源  
            inputStream.close();  
            inputStream = null;  
            uploadConn.disconnect();  
            // 使用json解析  
            jsonObject = JSONObject.fromObject(buffer.toString());  
            System.out.println(jsonObject);  
        } catch (Exception e) {  
            System.out.println("上传文件失败！");  
            e.printStackTrace();  
        }  
        return jsonObject;  
    }  
    
    /** 
     * 文件上传到微信服务器 
     * @param fileType 文件类型 媒体文件类型，分别有图片（image）、语音（voice）、视频（video），普通文件(file) 
     * @param filePath 文件路径 
     * @return JSONObject 
     * @throws Exception 
     */  
    public static JSONObject uploadFile(String token,String fileType, String filePath) throws Exception {    
        String result = null;    
        File file = new File(filePath);    
        if (!file.exists() || !file.isFile()) {    
            throw new IOException("文件不存在");    
        }    
        /**  
        * 第一部分  
        */    
        URL urlObj = new URL("https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token="+ token + "&type="+fileType+"");   
                               
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();    
        con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式    
        con.setDoInput(true);    
        con.setDoOutput(true);    
        con.setUseCaches(false); // post方式不能使用缓存    
        // 设置请求头信息    
        con.setRequestProperty("Connection", "Keep-Alive");    
        con.setRequestProperty("Charset", "UTF-8");    
        // 设置边界    
        String BOUNDARY = "----------" + System.currentTimeMillis();    
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ BOUNDARY);    
        // 请求正文信息    
        // 第一部分：    
        StringBuilder sb = new StringBuilder();    
        sb.append("--"); // 必须多两道线    
        sb.append(BOUNDARY);    
        sb.append("\r\n");    
        sb.append("Content-Disposition: form-data;name=\"media\";filename=\""+ file.getName() + "\"\r\n");    
        sb.append("Content-Type:application/octet-stream\r\n\r\n");    
        byte[] head = sb.toString().getBytes("utf-8");    
        // 获得输出流    
        OutputStream out = new DataOutputStream(con.getOutputStream());    
        // 输出表头    
        out.write(head);    
        // 文件正文部分    
        // 把文件已流文件的方式 推入到url中    
        DataInputStream in = new DataInputStream(new FileInputStream(file));    
        int bytes = 0;    
        byte[] bufferOut = new byte[1024];    
        while ((bytes = in.read(bufferOut)) != -1) {    
        	out.write(bufferOut, 0, bytes);    
        }    
        in.close();    
        // 结尾部分    
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线    
        out.write(foot);    
        out.flush();    
        out.close();    
        StringBuffer buffer = new StringBuffer();    
        BufferedReader reader = null;    
        try {    
	        // 定义BufferedReader输入流来读取URL的响应    
	        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));    
	        String line = null;    
	        while ((line = reader.readLine()) != null) {    
	        	//System.out.println(line);    
	        	buffer.append(line);    
	        }    
	        if(result==null){    
	        	result = buffer.toString();    
	        }    
        } catch (IOException e) {    
	        System.out.println("发送POST请求出现异常！" + e);    
	        e.printStackTrace();    
	        throw new IOException("数据读取异常");    
        } finally {    
	        if(reader!=null){    
	        	reader.close();    
	        }    
        }    
        JSONObject jsonObj = JSONObject.fromObject(result);    
        return jsonObj;    
    }  
    
    public static String getFileName(String fileUrl,String contentType) {  
        String filename="";  
        if (fileUrl != null && !"".equals(fileUrl)) {  
            if(fileUrl.contains(".")){  
                filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);  
            }else{  
                if(contentType==null || "".equals(contentType)){  
                    return "";  
                }  
                String fileExt="";  
                if ("image/jpeg".equals(contentType)) {  
                    fileExt = ".jpg";  
                } else if ("audio/mpeg".equals(contentType)) {  
                    fileExt = ".mp3";  
                } else if ("audio/amr".equals(contentType)) {  
                    fileExt = ".amr";  
                } else if ("video/mp4".equals(contentType)) {  
                    fileExt = ".mp4";  
                } else if ("video/mpeg4".equals(contentType)) {  
                    fileExt = ".mp4";  
                } else if ("text/plain".equals(contentType)) {  
                    fileExt = ".txt";  
                } else if ("text/xml".equals(contentType)) {  
                    fileExt = ".xml";  
                } else if ("application/pdf".equals(contentType)) {  
                    fileExt = ".pdf";  
                } else if ("application/msword".equals(contentType)) {  
                    fileExt = ".doc";  
                } else if ("application/vnd.ms-powerpoint".equals(contentType)) {  
                    fileExt = ".ppt";  
                } else if ("application/vnd.ms-excel".equals(contentType)) {  
                    fileExt = ".xls";  
                }  
                filename="Media文件"+fileExt;  
            }  
        }  
        return filename;  
    } 
    
    /**
	 * 执行异步任务
	 * @param mediaId  上传的CVS文件
	 * @param url  不同的URL代表不同的操作
	 * @return
	 */
	public static String sendCVSData(String mediaId,String url){
		String jsonContext="{"+
				"\"media_id\":\""+mediaId+"\","+
				"\"to_invite\":false"+		//关闭邀请通知
				/*"\"callback\":"+
				"{"+
				 	"\"url\": \""+"\","+
				 	"\"token\": \""+"\","+
				 	"\"encodingaeskey\": \""+"\""+
				"}"+*/
			"}";
		//发送消息
		//消息json格式
		String flag= "error";
		try {
			 CloseableHttpClient httpclient = HttpClients.createDefault();
			 HttpPost httpPost= new HttpPost(url);
			 //发送json格式的数据
			 StringEntity myEntity = new StringEntity(jsonContext, ContentType.create("text/plain", "UTF-8"));
			 httpPost.setEntity(myEntity);
			 // Create a custom response handler
			 ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {
			    public JSONObject handleResponse(
			            final HttpResponse response) throws ClientProtocolException, IOException {
			        int status = response.getStatusLine().getStatusCode();
			        if (status >= 200 && status < 300) {
			            HttpEntity entity = response.getEntity();
			            if(null!=entity){
			            	String result= EntityUtils.toString(entity);
			                //根据字符串生成JSON对象
			       		 	JSONObject resultObj = JSONObject.fromObject(result);
			       		 	return resultObj;
			            }else{
			            	return null;
			            }
			        } else {
			            throw new ClientProtocolException("Unexpected response status: " + status);
			        }
			    }
			};
			//返回的json对象
			JSONObject responseBody = httpclient.execute(httpPost, responseHandler);
			int result= (Integer) responseBody.get("errcode");
			System.err.println(responseBody.getString("errmsg"));
			if(0==result){
				flag=responseBody.getString("jobid");
			}else{
				flag="error";
			}
			httpclient.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * 执行异步任务
	 * @param mediaId  上传的CVS文件
	 * @param url  不同的URL代表不同的操作
	 * @return
	 */
	public static String sendCVSDataNew(String mediaId,String url){
		String jsonContext="{"+
				"\"media_id\":\""+mediaId+"\""+","+
				"\"to_invite\":false"+
				/*"\"callback\":"+
				"{"+
				 	"\"url\": \""+"\","+
				 	"\"token\": \""+"\","+
				 	"\"encodingaeskey\": \""+"\""+
				"}"+*/
			"}";
		//发送消息
		//消息json格式
		String flag= "error";
		try {
			 CloseableHttpClient httpclient = HttpClients.createDefault();
			 HttpPost httpPost= new HttpPost(url);
			 //发送json格式的数据
			 StringEntity myEntity = new StringEntity(jsonContext, ContentType.create("text/plain", "UTF-8"));
			 httpPost.setEntity(myEntity);
			 // Create a custom response handler
			 ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {
			    public JSONObject handleResponse(
			            final HttpResponse response) throws ClientProtocolException, IOException {
			        int status = response.getStatusLine().getStatusCode();
			        if (status >= 200 && status < 300) {
			            HttpEntity entity = response.getEntity();
			            if(null!=entity){
			            	String result= EntityUtils.toString(entity);
			                //根据字符串生成JSON对象
			       		 	JSONObject resultObj = JSONObject.fromObject(result);
			       		 	return resultObj;
			            }else{
			            	return null;
			            }
			        } else {
			            throw new ClientProtocolException("Unexpected response status: " + status);
			        }
			    }
			};
			//返回的json对象
			JSONObject responseBody = httpclient.execute(httpPost, responseHandler);
			System.out.println("responseBody==========="+responseBody); 
			int result= (Integer) responseBody.get("errcode");
			if(0==result){
				flag=responseBody.getString("jobid");
			}else{
				flag="error";
			}
			httpclient.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/**
  	 * 获得异步任务结果
	 * @throws Exception 
  	 */
    /*public static Object getResultByjobid(String token,String jobid){
  	  try {
	  	  String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/getresult?access_token="+token+"&jobid="+jobid;
	      JSONObject data = new JSONObject();
	      Object resp = HttpClientUtil.post1(url, data);
	  	  return resp;
  	  }catch(Exception e) {
  	      e.printStackTrace();
  	  }
  	  return null;
  	    
  	}*/
    
  	/**
  	 * 获得异步任务结果
  	 */
  	public static JSONObject getResult(String url){
		 try {
			 CloseableHttpClient httpclient = HttpClients.createDefault();
			 HttpPost httpPost= new HttpPost(url);
             ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {
                public JSONObject handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        if(null!=entity){
                        	String result= EntityUtils.toString(entity);
                            //根据字符串生成JSON对象
                   		 	JSONObject resultObj = JSONObject.fromObject(result);
                   		 	return resultObj;
                        }else{
                        	return null;
                        }
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
          //返回的json对象
            JSONObject responseBody = httpclient.execute(httpPost, responseHandler);
            System.out.println(responseBody.toString());
            return responseBody;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
  	}
  	
  	/**
  	 * 批量删除用户
  	 * @param userInfos
  	 * @param url
  	 * @return
  	 */
  	public static String deleteBatchUser(String userInfos,String url){
		String jsonContext="{\"useridlist\":["+userInfos+"]}";
		//发送消息
		//消息json格式
		String flag= "error";
		try {
			 CloseableHttpClient httpclient = HttpClients.createDefault();
			 HttpPost httpPost= new HttpPost(url);
			 //发送json格式的数据
			 StringEntity myEntity = new StringEntity(jsonContext, ContentType.create("text/plain", "UTF-8"));
			 httpPost.setEntity(myEntity);
			 // Create a custom response handler
			 ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {
			    public JSONObject handleResponse(
			            final HttpResponse response) throws ClientProtocolException, IOException {
			        int status = response.getStatusLine().getStatusCode();
			        if (status >= 200 && status < 300) {
			            HttpEntity entity = response.getEntity();
			            if(null!=entity){
			            	String result= EntityUtils.toString(entity);
			                //根据字符串生成JSON对象
			       		 	JSONObject resultObj = JSONObject.fromObject(result);
							System.out.println("resultObj======"+resultObj.toString());
			       		 	return resultObj;
			            }else{
			            	return null;
			            }
			        } else {
			            throw new ClientProtocolException("Unexpected response status: " + status);
			        }
			    }
			};
			//返回的json对象
			JSONObject responseBody = httpclient.execute(httpPost, responseHandler);
			int result= (Integer) responseBody.get("errcode");
			if(0==result){
				flag=responseBody.getString("errmsg");
			}else{
				System.out.println(responseBody.getString("errmsg"));
				flag="error";
			}
			httpclient.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
    /*public static void main(String[] args) throws IOException {
        String appid = "ww851b72535c927f24";
        String appsecret = "Y3MaeIzfk3UXe6Dv4jEpj4lmVIOiDnvriwXMwgIxK2Q";
        String jobid = "3_1562572066_47845";
        String token = "V7aA8czHEjx84SWs2UAAqT9A3dP_TxVPs6AkQB9m9LcQtNGrEU5kJoDWaYyEyVHtBVQWSRu8ZeTroZpnN2qu4ALCYcKmBxzshEO6DyOnlFw1TieujLd51Jl08VwlVGRjD1hCJRhUYgK9PZX_hybUM4S6LmO9zI7tqWoqaV7FABLkDwKoITOIeU1BK4RDElLnjvdkF9lPvol1q_M_ElMpFw";
        String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/getresult?access_token="+token+"&jobid="+jobid;
        JSONObject data = new JSONObject();
        String resp = HttpClientUtil.post1(url, data);
        JSONObject json = JSONObject.fromObject(resp);
        JSONArray jsonArr = json.getJSONArray("result");
        List<String> success = new ArrayList<String>();
        List<Map> failed = new ArrayList<Map>();
        for(int i = 0;i < jsonArr.size();i++) {
            if("0".equals(jsonArr.getJSONObject(i).get("errcode").toString())) {
                success.add(jsonArr.getJSONObject(i).get("userid").toString());
            }else {
                Map map = new HashMap();
                map.put("userid", jsonArr.getJSONObject(i).get("userid").toString());
                map.put("errmsg", jsonArr.getJSONObject(i).get("errmsg").toString());
                failed.add(map);
            }
        }
        json.put("success_userid",success);
        json.put("failed_userid",failed);
        System.out.println();
    }*/
}  