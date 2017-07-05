package vn.viettel.browser.worker;

import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import vn.viettel.browser.service.NotificationService;
import vn.viettel.browser.ultils.ElasticsearchUtils;

@Component
public class ScheduledAutoSendNotiTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledAutoSendNotiTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Resource
    private NotificationService firebaseService;
    ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() throws JSONException {
       org.json.JSONObject articlJson = elasticsearchUtils.getHotArticleRecently();
       
       
    }
    
 
}