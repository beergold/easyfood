package com.wjj.worker.foundation.constant;

/**
 * 叮咚买菜api请求url常量
 *
 * @author BeerGod
 */
public class MaiCaiApiConstants {

    /**
     * 获取叮咚买菜地址url
     */
    public final static String GET_ADDRESS_URL = "https://sunquan.api.ddxq.mobi/api/v1/user/address/?api_version=9.49.0&app_version=2.81.0&app_client_id=3&source_type=5&applet_source&openid";

    /**
     * 叮咚买菜获取购物商品信息url
     */
    public final static String SHOP_CART_URL = "https://maicai.api.ddxq.mobi/cart/index/?station_id={stationId}&city_number={cityNumber}&api_version=9.49.0&app_version=2.81.0&app_client_id=3&is_load=1&ab_config=%7B%22key_onion%22%3A%22D%22%2C%22key_cart_discount_price%22%3A%22C%22%7D";

    /**
     * 获取可用配送时间
     */
    public final static String RECEIVE_TIME_URL = "https://maicai.api.ddxq.mobi/order/getMultiReserveTime?station_id={stationId}&city_number=0101&api_version=9.49.0&app_version=2.81.0&isBridge=false&app_client_id=3";

    /**
     * 创建订单
     */
    public final static String ADD_NEW_ORDER = "https://maicai.api.ddxq.mobi/order/addNewOrder?";
}
