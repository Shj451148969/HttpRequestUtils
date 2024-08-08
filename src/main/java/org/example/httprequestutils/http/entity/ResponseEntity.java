package com.shj.steam_gui.utils.http.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.ToString;
import org.apache.http.Header;

@Data
@ToString
public class ResponseEntity {

    private String httpCode;
    @JSONField(serialize = false)
    private Header[] headers;

    private String message;

    private String result;

    private byte[] resultBytes;

    public ResponseEntity(String httpCode, String message, String result) {
        this.httpCode = httpCode;
        this.message = message;
        this.result = result;
    }

    public ResponseEntity(String httpCode, String message, String result, Header[] headers) {
        this.httpCode = httpCode;
        this.message = message;
        this.result = result;
        this.headers = headers;
    }

    public ResponseEntity(String httpCode, String message, byte[] resultBytes) {
        this.httpCode = httpCode;
        this.message = message;
        this.resultBytes = resultBytes;
    }

    public ResponseEntity(String httpCode, String message, byte[] resultBytes, Header[] headers) {
        this.httpCode = httpCode;
        this.message = message;
        this.resultBytes = resultBytes;
        this.headers = headers;
    }

    public String findResponseHeader(String headerName) {
        for (Header header : headers) {
            if (headerName.equalsIgnoreCase(header.getName())) {
                return header.getValue();
            }
        }
        return null;
    }

    public JSONObject getResultWithJson() {
        return JSONObject.parseObject(result);
    }
}
