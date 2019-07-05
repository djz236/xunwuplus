/*********************************************************
 *********************************************************
 ********************                  *******************
 *************                                ************
 *******                  _oo0oo_                  *******
 ***                     o8888888o                     ***
 *                       88" . "88                       *
 *                       (| -_- |)                       *
 *                       0\  =  /0                       *
 *                     ___/`---'\___                     *
 *                   .' \\|     |// '.                   *
 *                  / \\|||  :  |||// \                  *
 *                 / _||||| -:- |||||- \                 *
 *                |   | \\\  -  /// |   |                *
 *                | \_|  ''\---/''  |_/ |                *
 *                \  .-\__  '-'  ___/-. /                *
 *              ___'. .'  /--.--\  `. .'___              *
 *           ."" '<  `.___\_<|>_/___.' >' "".            *
 *          | | :  `- \`.;`\ _ /`;.`/ - ` : | |          *
 *          \  \ `_.   \_ __\ /__ _/   .-` /  /          *
 *      =====`-.____`.___ \_____/___.-`___.-'=====       *
 *                        `=---='                        *
 *      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~      *
 *********__佛祖保佑__永无BUG__验收通过__钞票多多__*********
 *********************************************************/
package com.imooc.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * @ClassName: SmsServiceImpl
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年7月1日 下午8:48:03
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class SmsServiceImpl implements ISmsService, InitializingBean {

	@Value("${aliyun.sms.accessKey}")
	private String accessKey;

	@Value("${aliyun.sms.accessKeySecret}")
	private String secertKey;

	@Value("${aliyun.sms.templete.code}")
	private String templateCode;
	private final static String SMS_CODE_CONTENT_PREFIX = "SMS::CODE::CONTENT";
	private IAcsClient acsClient;
	private static final String[] NUMS = { "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9" };
	private static final Random random = new Random();

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * <p>
	 * Title: sendSms
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param telephone
	 * @return
	 * @see com.imooc.service.ISmsService#sendSms(java.lang.String)
	 */
	@Override
	public ServiceResult<String> sendSms(String telephone) {
		String gapKey = "SMS::CODE::INTERVAL::" + telephone;
		String result = redisTemplate.opsForValue().get(gapKey);
		if (result != null) {
			return new ServiceResult<String>(false, "请求次数太频繁");
		}
		String code = generateRandomSmsCode();
		String templateParam = String.format("{\"code\": \"%s\"}", code);

		// 组装请求对象
		SendSmsRequest request = new SendSmsRequest();

		// 使用post提交
		request.setMethod(MethodType.POST);
		request.setPhoneNumbers(telephone);
		request.setTemplateParam(templateParam);
		request.setTemplateCode(templateCode);
		request.setSignName("寻屋");

		boolean success = false;
		try {
			SendSmsResponse response = acsClient.getAcsResponse(request);
			if ("OK".equals(response.getCode())) {
				success = true;
			} else {
				// TODO log this question
			}
		} catch (ClientException e) {
			e.printStackTrace();
		}
		if (success) {
			redisTemplate.opsForValue().set(gapKey, code, 60, TimeUnit.SECONDS);
			redisTemplate.opsForValue().set(SMS_CODE_CONTENT_PREFIX + telephone,
					code, 10, TimeUnit.MINUTES);
			return ServiceResult.of(code);
		} else {
			return new ServiceResult<String>(false, "服务忙，请稍后重试");
		}

//		return ServiceResult.of("123456");
	}

	/**
	 * <p>
	 * Title: getSmsCode
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param telephone
	 * @return
	 * @see com.imooc.service.ISmsService#getSmsCode(java.lang.String)
	 */
	@Override
	public String getSmsCode(String telephone) {
		return redisTemplate.opsForValue().get(SMS_CODE_CONTENT_PREFIX+telephone);
	}

	/**
	 * <p>
	 * Title: remove
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param telephone
	 * @see com.imooc.service.ISmsService#remove(java.lang.String)
	 */
	@Override
	public void remove(String telephone) {
		redisTemplate.delete(SMS_CODE_CONTENT_PREFIX+telephone);
	}

	/**
	 * <p>
	 * Title: afterPropertiesSet
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @throws Exception
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// 设置超时时间
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
				accessKey, secertKey);

		String product = "Dysmsapi";
		String domain = "dysmsapi.aliyuncs.com";

		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,
				domain);
		acsClient = new DefaultAcsClient(profile);
	}

	/**
	 * 6位验证码生成器
	 * 
	 * @return
	 */
	private static String generateRandomSmsCode() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			int index = random.nextInt(10);
			sb.append(NUMS[index]);
		}
		return sb.toString();
	}
}
