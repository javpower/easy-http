package com.gc.easy.http;


import lombok.Data;

import java.util.Objects;

@Data
public class EasyHttpVo<T> {
    private String code;
    private String msg;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public boolean isSuccess(){
        if(Objects.equals(this.code,"0")){
            return true;
        }
        return false;
    }

}
