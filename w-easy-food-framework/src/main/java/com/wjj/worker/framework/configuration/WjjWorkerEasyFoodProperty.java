package com.wjj.worker.framework.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 属性获取
 *
 * @author BeerGod
 */
@ConfigurationProperties(prefix = "wjj.worker.easyfood")
@Component
@Data
public class WjjWorkerEasyFoodProperty {
    /**
     * 通知url
     * <pre>
     *     抢购结果通知，苹果推荐使用(Bark) / 安卓可使用server酱或其他方式可自行选择
     *     默认发送时按照 https://xxx.xxx/标题/内容  方式发送
     * </pre>
     */
    private String notifyUrl;

    /**
     * 用户Id
     * <pre>
     *     自行抓包获取 对应ddmc-uid
     *     2022-04-10 叮咚买菜加入uid校验，增加封号风险
     * </pre>
     */
    private String userId;

    /**
     * 查看请求中
     * 携带cookie DDXQSESSID=xxxx填入
     */
    private String userToken;


    /**
     * 地址关键词
     */
    private String addressKeyWord;
}
