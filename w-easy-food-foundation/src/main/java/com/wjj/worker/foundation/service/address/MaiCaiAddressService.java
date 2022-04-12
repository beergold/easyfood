package com.wjj.worker.foundation.service.address;

import cn.hutool.crypto.digest.MD5;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.ObjectUtils;
import com.wjj.worker.framework.utils.RedisCacheUtils;
import com.wjj.worker.foundation.constant.MaiCaiApiConstants;
import com.wjj.worker.foundation.constant.MaiCaiConstants;
import com.wjj.worker.foundation.util.MaiCaiHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 根据账户获取地址信息
 *
 * @author BeerGod
 */
@Component
public class MaiCaiAddressService {

    Logger logger = LoggerFactory.getLogger(MaiCaiAddressService.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    RedisCacheUtils cacheUtils;
    @Autowired
    MaiCaiHttpUtil maiCaiHttpUtil;

    /**
     * 搜索关键词，返回第一个匹配的地址
     *
     * @param searchValue 搜索关键词
     * @return
     */
    public WjjApiParameter queryAddress(String searchValue) {
        String cacheId = MaiCaiConstants.ADDRESS_REDIS_KEY + MD5.create().digestHex(searchValue, StandardCharsets.UTF_8);
        //查询指定关键词缓存
        WjjApiParameter cacheAddress = cacheUtils.getCacheObject(cacheId);
        if (ObjectUtils.isNotEmpty(cacheAddress)) {
            return cacheAddress;
        }
        List<WjjApiParameter> addressList = queryAddress();
        //指定地址不存在
        if (ObjectUtils.isEmpty(addressList)) {
            return null;
        }
        for (WjjApiParameter e : addressList) {
            //存在则存入缓存并返回
            if (e.getString("addr_detail", "").contains(searchValue)) {
                WjjApiParameter data = new WjjApiParameter();
                data.put("addressId", e.getString("id"));
                data.put("stationId", e.getJsonObject("station_info").getString("id"));
                data.put("cityNumber", e.getString("city_number"));
                cacheUtils.setCacheObject(cacheId, data, 72, TimeUnit.HOURS);
                return data;
            }
        }
        return null;
    }


    public List<WjjApiParameter> queryAddress() {
        WjjApiParameter responseData = maiCaiHttpUtil.execute(MaiCaiApiConstants.GET_ADDRESS_URL, HttpMethod.GET, null, new HttpHeaders());
        if (ObjectUtils.isEmpty(responseData)) {
            return null;
        }
        List<WjjApiParameter> data = responseData.getListWithJson("valid_address");
        if (ObjectUtils.isEmpty(data)) {
            logger.info("获取叮咚买菜地址失败,指定账户无任何可用地址,返回主体信息:{}@", responseData);
            return null;
        }
        return data;
    }

}
