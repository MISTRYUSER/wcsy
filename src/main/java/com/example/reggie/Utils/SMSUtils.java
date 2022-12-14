package com.example.reggie.Utils;

import com.aliyun.dysmsapi20170525.Client;

import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
		//DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
		//IAcsClient client = new DefaultAcsClient(profile);
		//
		//SendSmsRequest request = new SendSmsRequest();
		//request.setSysRegionId("cn-hangzhou");
		//request.setPhoneNumbers(phoneNumbers);
		//request.setSignName(signName);
		//request.setTemplateCode(templateCode);
		//request.setTemplateParam("{\"code\":\""+param+"\"}");
		//try {
		//	SendSmsResponse response = client.getAcsResponse(request);
		//	System.out.println("短信发送成功");
		//}catch (ClientException e) {
		//	e.printStackTrace();
		//}
	}



	/**
	 * 使用AK&SK初始化账号Client
	 * @param accessKeyId
	 * @param accessKeySecret
	 * @return Client
	 * @throws Exception
	 */
	public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
		Config config = new Config()
				// 您的AccessKey ID
				.setAccessKeyId(accessKeyId)
				// 您的AccessKey Secret
				.setAccessKeySecret(accessKeySecret);
		// 访问的域名
		config.endpoint = "dysmsapi.aliyuncs.com";
		return new Client(config);
	}


}
