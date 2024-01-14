package com.gc.easy.http.handler;

public interface FallbackFactory {

  public Object create(Throwable throwable);

}
