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
package com.imooc.service.house;

import java.io.File;
import java.io.InputStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * @ClassName: QiNiuServiceImpl
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年5月7日 上午11:12:43
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class QiNiuServiceImpl implements IQiNiuService, InitializingBean {

	@Autowired
	private UploadManager uploadManager;
	@Autowired
	private BucketManager bucketManager;
	@Autowired
	private Auth auth;
	@Value("${qiniu.Bucket}")
	private String bucket;
	private StringMap putPolicy;

	/**
	 * <p>
	 * Title: uploadFile
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param file
	 * @return
	 * @throws QiniuException 
	 * @see com.imooc.service.house.IQiNiuService#uploadFile(java.io.File)
	 */
	@Override
	public Response uploadFile(File file) throws QiniuException {
		Response response = uploadManager.put(file, null, getUploadToken());
		int retry = 0;
		while (response.needRetry() && retry < 3) {
			 response = uploadManager.put(file, null, getUploadToken());
			 retry++;
		}
		return response;
	}

	/**
	 * <p>
	 * Title: uploadFile
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param inputStream
	 * @return
	 * @see com.imooc.service.house.IQiNiuService#uploadFile(java.io.InputStream)
	 */
	@Override
	public Response uploadFile(InputStream inputStream) throws QiniuException {
		Response response = uploadManager.put(inputStream, null, getUploadToken(),null,null);
		int retry=0;
		while (response.needRetry()&&retry<3) {
			response = uploadManager.put(inputStream, null, getUploadToken(),null,null);
			retry++;
		}
		return response;
	}

	/**
	 * <p>
	 * Title: delete
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param key
	 * @return
	 * @see com.imooc.service.house.IQiNiuService#delete(java.lang.String)
	 */
	@Override
	public Response delete(String key) throws QiniuException {
		Response response = bucketManager.delete(bucket, key);
		int retry=0;
		while (response.needRetry()&&retry<3) {
			response=bucketManager.delete(bucket, key);
			retry++;
		}
		
		return response;
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
		putPolicy=new StringMap();
		putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
	}

	/**
	 * @Title: getUploadToken @Description: 获取上传凭证 @param: @return @return:
	 * String @throws
	 */
	private String getUploadToken() {
		return auth.uploadToken(bucket, null, 3600, putPolicy);
	}
}
