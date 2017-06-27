package vn.viettel.browser.worker;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import vn.viettel.browser.service.NotificationService;
import vn.viettel.browser.ultils.DateTimeUtils;
import vn.viettel.browser.ultils.ElasticsearchUtils;

@Component
public class ScheduledAutoSendNotiTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledAutoSendNotiTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Resource
    private NotificationService firebaseService;
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()) + "======> " + firebaseService.getHotArticleRecently().toString());
    }
    
 // Function lấy bài hot trong vòng 1h gần đây
 	public SearchResponse getHotArticleRecentlyByCategory(int categoryID) {
 		SearchResponse response = new SearchResponse();
 		DateTime dateFrom = DateTimeUtils.getPreviousTime("hour", 1);
 		if (dateFrom != null) {
 			long from = DateTimeUtils.convertDateTimeToUnixTimestamp(dateFrom);

 			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("display", "1"))
 					.must(QueryBuilders.termQuery("category.id", categoryID))
 					.must(QueryBuilders.rangeQuery("time_post").from(from / 1000));
 			SearchRequestBuilder query = ElasticsearchUtils.esClient.prepareSearch("br_article_v4").setTypes("article")
 					.setQuery(boolQuery).addAggregation(AggregationBuilders.terms("hot_tags").field("tags").size(1)
 							.subAggregation(AggregationBuilders.topHits("top_article_of_tags").size(1)));
 			response = query.setSize(0).execute().actionGet();
 		}
 		return response;
 	}
}