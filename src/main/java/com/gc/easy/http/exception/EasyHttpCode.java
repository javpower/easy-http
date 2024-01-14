package com.gc.easy.http.exception;

import java.util.Objects;

/**
 * @description: 返回状态
 * @author: gc.x
 * @create: 2019-03-16 16:30:34
 **/
public enum EasyHttpCode implements ResultEnum<Integer> {
	/**
	 * 0:执行成功
	 */
	SUCCESS(200, "操作成功"),
	BUSINESS_ERROR(3000,"业务异常")
	 ;

	private Integer code;
	private String desc;

	EasyHttpCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}


	/**
	 * 根据code值换取中文提示信息
	 * @param code 错误码
	 * @return
	 */
	public static String getDescByCode(Integer code){
		for (EasyHttpCode resultCode : EasyHttpCode.values()){
			if (resultCode.getCode().equals(code)){
				return resultCode.getDesc();
			}
		}
		return null;
	}

	public static EasyHttpCode getEnum(Integer code){
		for(EasyHttpCode resultCode : EasyHttpCode.values()){
			if(Objects.equals(code,resultCode.getCode())){
				return resultCode;
			}
		}
		return null;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
