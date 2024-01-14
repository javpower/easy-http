package com.gc.easy.http.exception;


/**
 * @description:  业务异常
 * @author: gc.x
 * @create: 2019-03-16 17:00:34
 **/
public class EasyHttpException extends RuntimeException {

    /**
     * 异常编码
     */
    private ResultEnum<Integer> code = EasyHttpCode.BUSINESS_ERROR;

    private Object data;


    public ResultEnum<Integer> getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public EasyHttpException(ResultEnum<Integer> code, String message, Throwable cause) {
        this(message, cause);
        this.code = code;
    }

    public EasyHttpException(ResultEnum<Integer> code, String message) {
        super(message);
        this.code = code;
    }


    public EasyHttpException(ResultEnum<Integer> code) {
        this(code, code.getDesc());
    }



    public EasyHttpException(String message) {
        super(message);
    }

    public EasyHttpException(String message, Throwable cause) {
        super(message, cause);
    }


    public EasyHttpException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public EasyHttpException withData(Object data) {
        this.data = data;
        return this;
    }

}
