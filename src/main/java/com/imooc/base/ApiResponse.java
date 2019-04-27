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
package com.imooc.base;

/**   
 * @ClassName:  ApiResponse   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年4月25日 下午3:46:07   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class ApiResponse {

	public ApiResponse(int code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	public ApiResponse() {
		 this.code=Status.SUCCESS.getCode();
		 this.message=Status.SUCCESS.getStandardMessage();
	}
	public static ApiResponse ofMessage(int code,String message){
		return new ApiResponse(code,message,null);
	}
	
	public static ApiResponse ofSucess(Object data){
		return new ApiResponse(Status.SUCCESS.getCode(),Status.SUCCESS.getStandardMessage(), data);
	}
	
	public static ApiResponse ofStatus(Status status){
		return new ApiResponse(status.getCode(),status.getStandardMessage(), null);
	}
	private int code;
	private String message;
	private Object data;
	private boolean more;
	public enum Status{
		SUCCESS(200,"OK"),
		BAD_REQUEST(400,"Bad Request"),
		NOT_FOUND(404,"Not Found"),
		INTERNAL_SERVER_ERROR(500,"Unknown Internal Error"),
		NOT_VALID_PARAM(40005,"Not valid Params"),
		NOT_SUPPORTED_OPERATION(40006,"Operation not supported"),
		NOT_LOGIN(50000,"Not Login");
		
		private int code;
		private String standardMessage;
		 Status(int code, String standardMessage) {
			this.code = code;
			this.standardMessage = standardMessage;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getStandardMessage() {
			return standardMessage;
		}
		public void setStandardMessage(String standardMessage) {
			this.standardMessage = standardMessage;
		}
	}
/*	public static void main(String[] args) {
		System.out.println(Status.values());
		System.out.println(Status.values().length);
		System.out.println(Status.SUCCESS.getCode());
		System.out.println(Status.SUCCESS.getStandardMessage());
	}*/
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public boolean isMore() {
		return more;
	}
	public void setMore(boolean more) {
		this.more = more;
	}
}
