package com.wjj.worker.foundation.service.order;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.ObjectUtils;
import com.wjj.worker.framework.utils.RedisCacheUtils;
import com.wjj.worker.foundation.constant.MaiCaiApiConstants;
import com.wjj.worker.foundation.constant.MaiCaiConstants;
import com.wjj.worker.foundation.util.MaiCaiHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 收货时间服务
 *
 * @author BeerGod
 */
@Component
public class MaiCaiReceiveTimeService {

    @Autowired
    MaiCaiHttpUtil maiCaiHttpUtil;
    @Autowired
    RedisCacheUtils cacheUtils;

    /**
     * 查询收货时间
     * 默认获取今天
     *
     * @return
     */
    public List<WjjApiParameter> queryReceiveTimeList(WjjApiParameter addressInfo, List<WjjApiParameter> productList) {
        String cacheId = MaiCaiConstants.SHOP_RECEIVE_TIME_REDIS_KEY + addressInfo.getString("addressId");
        List<WjjApiParameter> result = cacheUtils.getCacheObject(cacheId);
        if (ObjectUtils.isNotEmpty(result)) {
            return result;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("ddmc-api-version", "9.49.0");
        httpHeaders.set("ddmc-build-version", "2.81.0");
        httpHeaders.set("ddmc-app-client-id", "3");
        httpHeaders.set("Content-Type", "multipart/form-data");
        httpHeaders.set("ddmc-city-number", addressInfo.getString("cityNumber"));
        httpHeaders.set("ddmc-station-id", addressInfo.getString("stationId"));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("products", JSON.toJSONString(Collections.singletonList(productList)));
        WjjApiParameter data = maiCaiHttpUtil.execute(StrUtil.format(MaiCaiApiConstants.RECEIVE_TIME_URL, addressInfo), HttpMethod.POST, param, httpHeaders);
        if (ObjectUtils.isEmpty(data)) {
            return null;
        }
        List<WjjApiParameter> dataList = data.getList("data");
        if (ObjectUtils.isEmpty(dataList)) {
            return null;
        }
        try {
            List<WjjApiParameter> times = dataList.get(0).getListWithJson("time").get(0).getListWithJson("times");
            times = times.stream().filter(e -> e.getInteger("disableType") == 0).collect(Collectors.toList());
            if (ObjectUtils.isNotEmpty(times)) {
                cacheUtils.setCacheObject(cacheId, times, 120, TimeUnit.SECONDS);
            }
            return times;
        } catch (Exception ignored) {
        }
        return null;
    }
}
