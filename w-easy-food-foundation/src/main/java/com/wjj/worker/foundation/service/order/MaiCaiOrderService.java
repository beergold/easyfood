package com.wjj.worker.foundation.service.order;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.RedisCacheUtils;
import com.wjj.worker.foundation.constant.MaiCaiApiConstants;
import com.wjj.worker.foundation.constant.MaiCaiConstants;
import com.wjj.worker.foundation.service.notify.Notify;
import com.wjj.worker.foundation.util.MaiCaiHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author BeerGod
 * 订单服务
 */
@Component
public class MaiCaiOrderService {

    Logger logger = LoggerFactory.getLogger(MaiCaiOrderService.class);

    @Autowired
    MaiCaiHttpUtil maiCaiHttpUtil;
    @Autowired
    RedisCacheUtils cacheUtils;
    @Autowired
    Notify notify;

    /**
     * 构建订单信息
     *
     * @return
     */
    private WjjApiParameter preBuildOrder(WjjApiParameter addressInfo, List<WjjApiParameter> cartProductList, String sign, String totalAmount, WjjApiParameter receiveTime) {
        //商品信息等等
        Map<String, Object> packageOrder = new LinkedHashMap<>();
        packageOrder.put("first_selected_big_time", "1");
        packageOrder.put("products", cartProductList);
        packageOrder.put("package_id", 1);
        packageOrder.put("package_type", 2);
        packageOrder.put("reserved_time_start", receiveTime.getInteger("start_timestamp"));
        packageOrder.put("reserved_time_end", receiveTime.getInteger("end_timestamp"));
        //付款主要方式等等
        Map<String, Object> paymentOrder = new LinkedHashMap<>();
        paymentOrder.put("freight_discount_money", "5.00");
        paymentOrder.put("freight_money", "5.00");
        paymentOrder.put("order_freight", "0.00");
        paymentOrder.put("address_id", addressInfo.getString("addressId"));
//        paymentOrder.put("used_point_num", 0);
        paymentOrder.put("parent_order_sign", sign);
        paymentOrder.put("product_type", 1);
        paymentOrder.put("pay_type", 6);
        paymentOrder.put("receipt_without_sku", 1);
        paymentOrder.put("price", totalAmount);
        paymentOrder.put("reserved_time_start", receiveTime.getInteger("start_timestamp"));
        paymentOrder.put("reserved_time_end", receiveTime.getInteger("end_timestamp"));
        //构建返回结果
        WjjApiParameter result = new WjjApiParameter();
        result.put("packages", Collections.singletonList(packageOrder));
        result.put("payment_order", paymentOrder);
        return result;
    }

    /**
     * 新建订单信息
     *
     * @param addressInfo     地址信息
     * @param cartProductList 购物车产品
     * @param sign            签名
     * @param totalAmount     总金额
     * @param receiveTime     收货时间
     * @return
     */
    public WjjApiParameter createOrder(WjjApiParameter addressInfo, List<WjjApiParameter> cartProductList, String sign, String totalAmount, WjjApiParameter receiveTime) {
        WjjApiParameter packageOrder = preBuildOrder(addressInfo, cartProductList, sign, totalAmount, receiveTime);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("station_id", addressInfo.getString("stationId"));
        params.put("city_number", addressInfo.getString("cityNumber"));
        params.put("api_version", "9.49.0");
        params.put("app_version", "2.81.0");
        params.put("showData", "true");
        params.put("app_client_id", "3");
        params.put("showMsg", "false");
        params.put("ab_config", "{\"key_onion\":\"C\"}");
        String query = URLUtil.buildQuery(params, StandardCharsets.UTF_8);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("ddmc-app-client-id", "3");
        httpHeaders.set("ddmc-api-version", "9.49.0");
        httpHeaders.set("ddmc-station-id", addressInfo.getString("stationId"));
        httpHeaders.set("ddmc-os-version", "undefined");
        httpHeaders.set("ddmc-channel", "undefined");
        httpHeaders.set("ddmc-build-version", "2.81.0");
        httpHeaders.set("ddmc-city-number", addressInfo.getString("cityNumber"));
        httpHeaders.set("Content-Type", "multipart/form-data");
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("package_order", JSON.toJSONString(packageOrder));
        WjjApiParameter orderInfo = maiCaiHttpUtil.execute(UriComponentsBuilder.fromHttpUrl(MaiCaiApiConstants.ADD_NEW_ORDER + query).build(true).toUri(), HttpMethod.POST, param, httpHeaders, true);
        logger.info("{}", orderInfo);
        String tradeTag = orderInfo.getString("tradeTag");
        //商品缺货或者部分商品信息发生变更
        String[] removeCartTag = {"PRODUCT_INFO_HAS_CHANGED", "PRODUCT_OUT_OF_STOCK", "SOLD_OUT", "PRODUCT_OUT_OF_STOCK"};
        if (ArrayUtil.contains(removeCartTag, tradeTag)) {
            cacheUtils.deleteObject(MaiCaiConstants.SHOP_CART_REDIS_KEY + addressInfo.getString("addressId"));
        } else if (tradeTag.equals("TIME_DELIVERY")) {
            cacheUtils.deleteObject(MaiCaiConstants.SHOP_RECEIVE_TIME_REDIS_KEY + addressInfo.getString("addressId"));
        }
        if (tradeTag.equals("FETCH_PAY_PARAM") || tradeTag.equals("success")) {
            notify.success();
        } else {
            notify.error(tradeTag + "-" + orderInfo.getString("msg") + "-" + orderInfo.getString("code"));
        }
        return orderInfo;
    }

}
