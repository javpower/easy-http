package com.gc.easy.http.entity;

import lombok.Data;

@Data
public class EasyFileStream {
    private String fileName;

    private byte[] inputStreamBytes;
}
