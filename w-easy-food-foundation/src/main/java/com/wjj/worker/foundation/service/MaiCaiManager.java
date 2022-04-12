package com.wjj.worker.foundation.service;

import com.wjj.worker.framework.configuration.WjjWorkerEasyFoodProperty;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.ObjectUtils;
import com.wjj.worker.foundation.service.address.MaiCaiAddressService;
import com.wjj.worker.foundation.service.cart.MaiCaiCartService;
import com.wjj.worker.foundation.constant.MaiCaiConstants;
import com.wjj.worker.foundation.service.order.MaiCaiOrderService;
import com.wjj.worker.foundation.service.order.MaiCaiReceiveTimeService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 买菜主处理程序
 *
 * @author BeerGod
 */
@Component
public class MaiCaiManager {

    Logger logger = LoggerFactory.getLogger(MaiCaiManager.class);

    @Autowired
    MaiCaiAddressService maiCaiAddressService;
    @Autowired
    MaiCaiCartService maiCaiCartService;
    @Autowired
    MaiCaiReceiveTimeService maiCaiReceiveTimeService;
    @Autowired
    MaiCaiOrderService maiCaiOrderService;
    @Autowired
    WjjWorkerEasyFoodProperty workerEasyFoodProperty;

    @SneakyThrows
    public void doWork() {
        //检测指定关键词的地址信息（详情地址）
        WjjApiParameter addressInfo = maiCaiAddressService.queryAddress(workerEasyFoodProperty.getAddressKeyWord());
        if (ObjectUtils.isEmpty(addressInfo)) {
            logger.info("获取叮咚买菜地址为空，提前推出！");
            return;
        }
        //检测购物车是否存在有效商品信息
        WjjApiParameter cartProduct = maiCaiCartService.queryCartValidProductList(addressInfo);
        if (ObjectUtils.isEmpty(cartProduct)) {
            logger.info("不存在有效的商品，提前推出！");
            return;
        }
        List<WjjApiParameter> cartProductList = cartProduct.getList("products");
        //查询可用配送时间
        List<WjjApiParameter> receiveTimeList = maiCaiReceiveTimeService.queryReceiveTimeList(addressInfo, cartProductList);
        if (ObjectUtils.isEmpty(receiveTimeList)) {
            logger.info("不存在有效的运送时间，当前运力不足稍后重试");
            return;
        }
        String sign = cartProduct.getString("sign");
        String totalAmount = cartProduct.getString("totalAmount");
        //构建订单(多线程抢菜，哪个时间段抢到算哪个自行选择)
        for (WjjApiParameter e : receiveTimeList) {
            CompletableFuture.runAsync(() -> maiCaiOrderService.createOrder(addressInfo, cartProductList, sign, totalAmount, e));
            Thread.sleep(500);
        }
    }

}
