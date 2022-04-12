package com.wjj.worker.foundation.util;

import com.alibaba.fastjson.JSON;
import com.wjj.worker.framework.configuration.WjjWorkerEasyFoodProperty;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.ObjectUtils;
import com.wjj.worker.foundation.constant.MaiCaiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * 请求叮咚买菜工具类
 *
 * @author BeerGod
 */
@Component
public class MaiCaiHttpUtil {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    WjjWorkerEasyFoodProperty workerEasyFoodProperty;

    Logger logger = LoggerFactory.getLogger(MaiCaiHttpUtil.class);

    public WjjApiParameter execute(String url, HttpMethod httpMethod, Map<String, ?> param, HttpHeaders headers) {
        defaultHeaders(headers);
        ResponseEntity<WjjApiParameter> response = restTemplate.exchange(url, httpMethod, new HttpEntity(param, headers), WjjApiParameter.class);
        return afterExecute(response, headers, false);
    }

    public WjjApiParameter execute(URI uri, HttpMethod httpMethod, Map<String, ?> param, HttpHeaders headers, boolean errorBack) {
        defaultHeaders(headers);
        ResponseEntity<WjjApiParameter> response = restTemplate.exchange(uri, httpMethod, new HttpEntity(param, headers), WjjApiParameter.class);
        return afterExecute(response, headers, errorBack);
    }

    private void defaultHeaders(HttpHeaders headers) {
        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 15_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.20(0x18001423) NetType/WIFI Language/zh_CN");
        headers.setOrigin(MaiCaiConstants.REQUEST_ORIGIN);
        headers.set("Cookie", "DDXQSESSID=" + workerEasyFoodProperty.getUserToken());
        headers.set("ddmc-uid", workerEasyFoodProperty.getUserId());
    }

    private WjjApiParameter afterExecute(ResponseEntity<WjjApiParameter> response, HttpHeaders headers, boolean errorBack) {
        WjjApiParameter body = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || body == null) {
            logger.info("请求叮咚买菜失败，请求发生异常，请求header:{}@,返回body:{}@", headers, body);
            return null;
        }
        Boolean success = body.getBoolean("success", false);
        if (!success) {
            String message = body.getString("msg", body.getString("message", null));
            logger.info("请求叮咚买菜失败，请求返回错误,请求header:{}@,返回错误信息:{}@", headers, message);
            if (!errorBack) {
                return null;
            }
        }
        Object responseDataSource = body.get("data");
        if (success && ObjectUtils.isEmpty(responseDataSource)) {
            logger.info("请求叮咚买菜失败，成功请求但未返回指定数据，请求header:{}@,返回数据信息:{}@", headers, body);
            if (!errorBack) {
                return null;
            }
        }
        if (responseDataSource instanceof List) {
            WjjApiParameter data = new WjjApiParameter();
            data.put("data", JSON.parseArray(JSON.toJSONString(responseDataSource), WjjApiParameter.class));
            return data;
        }
        WjjApiParameter result = JSON.parseObject(JSON.toJSONString(responseDataSource), WjjApiParameter.class);
        result.put("msg", body.getString("msg"));
        result.put("code", body.getString("code"));
        return result;
    }
}
