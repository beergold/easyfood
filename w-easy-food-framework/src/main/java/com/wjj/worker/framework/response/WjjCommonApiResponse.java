package com.wjj.worker.framework.response;

import com.wjj.worker.framework.enums.WjjApiResponseCode;
import com.wjj.worker.framework.exception.WjjApiException;
import com.wjj.worker.framework.exception.WjjApiValidException;
import com.wjj.worker.framework.request.WjjApiPage;
import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

/**
 * @author BeerGod
 * 自定义请求响应
 */
@Data
@ResponseBody
public class WjjCommonApiResponse implements Serializable {

    private static final long serialVersionUID = 5806752971894676240L;

    private boolean success;

    private int errCode;

    private String msg;

    private Object data;

    public WjjCommonApiResponse success() {
        this.success = Boolean.TRUE;
        this.msg = "success";
        return this;
    }

    public WjjCommonApiResponse success(Object data) {
        this.success = Boolean.TRUE;
        this.msg = "success";
        this.data = getApiData(data);
        return this;
    }

    public WjjCommonApiResponse error(WjjApiResponseCode responseCode) {
        this.success = Boolean.FALSE;
        this.msg = responseCode.msg();
        this.errCode = responseCode.code();
        return this;
    }

    public WjjCommonApiResponse error(WjjApiResponseCode responseCode, Object data) {
        this.success = Boolean.FALSE;
        this.msg = responseCode.msg();
        this.errCode = responseCode.code();
        this.data = data;
        return this;
    }

    public WjjCommonApiResponse error(WjjApiException e) {
        this.success = Boolean.FALSE;
        this.msg = e.getResponseCode().msg();
        this.errCode = (int) e.getResponseCode().code();
        this.data = e.getData();
        return this;
    }

    public WjjCommonApiResponse error(WjjApiValidException e) {
        this.success = Boolean.FALSE;
        this.msg = e.getMsg();
        this.errCode = WjjApiResponseCode.METHOD_ARGUMENT_NOT_VALID.code();
        return this;
    }

    private Object getApiData(Object obj) {
        //去除内部参数
        if (obj instanceof WjjApiPage) {
            WjjApiPage wjjApiPage = (WjjApiPage) obj;
            wjjApiPage.setWjjParams(null);
        }
        return obj;
    }
}
