package com.wjj.worker.framework.exception;

import com.wjj.worker.framework.enums.WjjApiEnumsInterFace;
import com.wjj.worker.framework.enums.WjjApiResponseCode;
import lombok.Data;

/**
 * 自定义公共返回异常
 *
 * @author BeerGod
 */
@Data
public class WjjApiException extends RuntimeException {
    private final WjjApiEnumsInterFace responseCode;

    private Object data;

    public WjjApiException(WjjApiEnumsInterFace responseCode) {
        super(responseCode.msg());
        this.responseCode = responseCode;
    }

    public WjjApiException(WjjApiEnumsInterFace responseCode, Object data) {
        super(responseCode.msg());
        this.responseCode = responseCode;
        this.data = data;
    }
}