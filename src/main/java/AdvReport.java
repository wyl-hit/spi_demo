
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;

/*
    推送数据消息结构
 */
@Data
class NotifyInfo{
    public String message_id;
    public List<Long> advertiser_ids;
    public String service_label;
    public String data;
    public long publish_time;
    public long timestamp;
    public int nonce;
    public long subscribe_task_id;
}

/**
 * 客户端灰度策略
 * level 表示灰度百分比  1～100
 * advertiser_ids 表示灰度的广告主账号
 * turn true 开启灰度 false 关闭灰度
 */
@Data
class GrayRule{
    public boolean turn;
    public Integer level;
    public List<Long> advertiser_ids;
}
public class AdvReport {

    //订阅服务对应的secret_key
    public final static String SecretKey = "1234567890";
    private static final Logger logger = LogManager.getLogger("spi-demo");
    @Getter
    @Setter
    private GrayRule grayRule = new GrayRule();
    public Random random = new Random();
    /**
     * spi推送数据接口(消息)实现样例
     * @param request
     * @param response
     * @throws Exception
     * @author rewong
     */
    @PostMapping(value = "/spi-callback")
    public void handleSpi(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //对header头部字段验证签名
        boolean tokenFlag = AuthTokenUtil.isValidToken(SecretKey, request);
        if(!tokenFlag) {
            throw new Exception("isValidToken failed");
        }
        //提取data信息并反序列化
        String qs = FastJsonUtil.paramToJsonStr(request.getQueryString());
        NotifyInfo notifyInfo = FastJsonUtil.parseToBean(qs, NotifyInfo.class);
        //spi接入api接口
        notifyInfo.advertiser_ids.forEach(advertiserId->{
            //判断是否灰度处理该广告主消息
            if(grayConvertSpi(advertiserId)){
                requsetBizApi(advertiserId);
                if(logger.isDebugEnabled()){
                    logger.info("handle data by spi advertiserId:{},message_id:{},publish_time:{},timestamp:{},service_label:{},task_id:{}"
                            ,advertiserId,notifyInfo.message_id,notifyInfo.publish_time,notifyInfo.timestamp,notifyInfo.service_label,notifyInfo.subscribe_task_id);
                }
            }
        });
    }

    /**
     * 灰度方案
     * @param advertiserId
     * @return true 数据可以灰度spi处理，false 数据不能灰度spi处理
     * @author rewong
     */
    public boolean grayConvertSpi(long advertiserId){
        //未开启灰度方案
        if (!grayRule.turn){
            return false;
        }
        //灰度方案开启，但是没有配置level 和 advertiser_ids
        if(CollectionUtils.isEmpty(grayRule.advertiser_ids) || grayRule.level==null){
            return false;
        }
        if(!CollectionUtils.isEmpty(grayRule.advertiser_ids) && grayRule.advertiser_ids.contains(advertiserId)){
            if(grayRule.level==null ||grayRule.level==0){
                return false;
            }
            int num = random.nextInt(100);
            if(num <= grayRule.level){
                return true;
            }
        }
        return false;
    }

    /**
     *  客户端业务逻辑，获取报表数据&稳定报表数据处理
     */
    public void requsetBizApi(long advertiserId){
        //todo
    }


}
