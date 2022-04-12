package com.wjj.worker.foundation.service.notify;

import cn.hutool.core.util.StrUtil;
import com.wjj.worker.framework.configuration.WjjWorkerEasyFoodProperty;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.foundation.constant.MaiCaiApiConstants;
import com.wjj.worker.foundation.constant.MaiCaiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 异步通知，用于下单状况
 *
 * @author BeerGod
 */
@Component
public class Notify {

    Logger logger = LoggerFactory.getLogger(Notify.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    WjjWorkerEasyFoodProperty workerEasyFoodProperty;

    @Async
    public void success() {
        logger.info("叮咚买菜抢菜成功～，快去付款吧！！");
        execute("🎉抢菜成功～，快去付款吧～ \uD83E\uDD70", "🎉抢菜成功～");
    }

    @Async
    public void error(String msg) {
        execute("抢菜失败。。快去看下原因！！！！！", msg);
    }

    private void execute(String title, String msg) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("title", title);
        data.put("value", msg);
        restTemplate.getForEntity(StrUtil.format(workerEasyFoodProperty.getNotifyUrl(), data), WjjApiParameter.class);
    }
}
