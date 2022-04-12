package com.wjj.worker.foundation.service.cart;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.ObjectUtils;
import com.wjj.worker.framework.utils.RedisCacheUtils;
import com.wjj.worker.foundation.constant.MaiCaiApiConstants;
import com.wjj.worker.foundation.constant.MaiCaiConstants;
import com.wjj.worker.foundation.util.MaiCaiHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 购物车管理
 *
 * @author BeerGod
 */
@Service
public class MaiCaiCartService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    RedisCacheUtils cacheUtils;
    @Autowired
    MaiCaiHttpUtil maiCaiHttpUtil;

    /**
     * 查询叮咚买菜购物车内有效商品信息
     *
     * @return
     */
    public WjjApiParameter queryCartValidProductList(WjjApiParameter addressInfo) {
        String cacheId = MaiCaiConstants.SHOP_CART_REDIS_KEY + addressInfo.getString("addressId");
        WjjApiParameter result = cacheUtils.getCacheObject(cacheId);
        if (ObjectUtil.isNotEmpty(result)) {
            return result;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("ddmc-build-version", "2.81.0");
        httpHeaders.set("x-requested-with", "com.yaya.zone");
        httpHeaders.set("ddmc-station-id", addressInfo.getString("stationId"));
        httpHeaders.set("ddmc-city-number", addressInfo.getString("cityNumber"));
        httpHeaders.set("ddmc-app-client-id", "3");
        httpHeaders.set("ddmc-api-version", "9.49.0");
        httpHeaders.set("ddmc-channel", "undefined");
        httpHeaders.set("ddmc-os-version", "undefined");
        WjjApiParameter data = maiCaiHttpUtil.execute(StrUtil.format(MaiCaiApiConstants.SHOP_CART_URL, addressInfo), HttpMethod.GET, null, httpHeaders);
        if (ObjectUtil.isEmpty(data)) {
            return null;
        }
        try {
            result = new WjjApiParameter();
            List<WjjApiParameter> productList = new ArrayList<>();
            List<WjjApiParameter> productListSource = data.getJsonObject("product").getListWithJson("effective");
            for (WjjApiParameter source : productListSource) {
                List<WjjApiParameter> products = source.getListWithJson("products");
                if (ObjectUtil.isNotEmpty(products)) {
                    productList.addAll(products);
                }
            }
            if (ObjectUtils.isEmpty(productList)) {
                return null;
            }
            result.put("products", productList);
            result.put("totalAmount", data.getString("total_money"));
            result.put("sign", data.getJsonObject("parent_order_info").getString("parent_order_sign"));
            if (ObjectUtils.isNotEmpty(result)) {
                cacheUtils.setCacheObject(cacheId, result, 2, TimeUnit.MINUTES);
            }
            return result;
        } catch (Exception ignored) {
        }
        return null;
    }
}
