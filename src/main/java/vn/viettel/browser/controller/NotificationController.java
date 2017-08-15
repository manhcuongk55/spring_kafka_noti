package vn.viettel.browser.controller;

import javax.annotation.Resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.browser.service.NotificationService;
import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.FireBaseUtils;

@RestController
@RequestMapping("/user")
public class NotificationController {
	ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	@Resource
	private NotificationService firebaseService;

	@CrossOrigin
	@RequestMapping(value = "/send_mess", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String sendNotifificationByCate(@RequestBody String notification) throws Exception {
		System.out.println("@RequestBody : " + notification);
		return firebaseService.sendNotoToListDeviceByCategoryId(notification);
	}

	@CrossOrigin
	@RequestMapping(value = "/send_mess_test", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String sendNotifiForTest(@RequestBody String notification) throws Exception {
		System.out.println("@RequestBody : " + notification);
		return FireBaseUtils.pushNotificationToSingleDevice(new JSONObject(notification));
	}

	@CrossOrigin
	@RequestMapping(value = "/notification_clicks", method = RequestMethod.GET, produces = "application/json")
	public String getTotalnotificationclicks(@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "device", defaultValue = "") String device)
			throws org.json.simple.parser.ParseException, JSONException {
		return elasticsearchUtils.getTotalNotificationClicks(from, to, device);
	}

	@CrossOrigin
	@RequestMapping(value = "/send_mess_all", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String sendNotifiToAll(@RequestBody String notification) throws Exception {
		System.out.println("@RequestBody : " + notification);
		return firebaseService.sendNotiToAll(notification);
	}

	@CrossOrigin
	@RequestMapping(value = "/get_list_devices", method = RequestMethod.GET, produces = "application/json")
	public String getTotalnotificationclicks() throws JSONException {
		JSONObject inputSearch = new JSONObject();
		return elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch).toString();
	}

	@CrossOrigin
	@RequestMapping(value = "/list_deviceId_by_category", method = RequestMethod.GET, produces = "application/json")
	public String getListDeviceIdByCategory(@RequestParam(value = "id", defaultValue = "2") String id,
			@RequestParam(value = "from", defaultValue = "0") String from,
			@RequestParam(value = "size", defaultValue = "20") String size,
			@RequestParam(value = "device", defaultValue = "*") String device)
			throws org.json.simple.parser.ParseException, JSONException {
		return elasticsearchUtils.getListDeviceIdsByCategoryId(id, from, size, device).toString();
	}

	@CrossOrigin
	@RequestMapping(value = "/count_notification_clicked_by_day", method = RequestMethod.GET, produces = "application/json")
	public String countNotificationClickedByDay(@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "size", defaultValue = "1") String size)
			throws org.json.simple.parser.ParseException {
		return elasticsearchUtils.getNumberOfNotificationsClickedByTime(from, to, size).toString();
	}

	@CrossOrigin
	@RequestMapping(value = "/get_top_article_by_category", method = RequestMethod.GET, produces = "application/json")
	public String getTopArticleByCategory(@RequestParam(value = "categoryId", defaultValue = "2") String categoryId)
			throws org.json.simple.parser.ParseException {
		int catId = Integer.parseInt(categoryId);
		return elasticsearchUtils.getHotArticleRecentlyByCategory(catId).toString();
	}

	@CrossOrigin
	@RequestMapping(value = "/get_top_article", method = RequestMethod.GET, produces = "application/json")
	public String getTopArticle() throws org.json.simple.parser.ParseException, JSONException {
		return elasticsearchUtils.getHotArticleRecently().toString();
	}

	@CrossOrigin
	@RequestMapping(value = "/get_total_devices_by_cate", method = RequestMethod.GET, produces = "application/json")
	public int getTotalDeviesByCate(@RequestParam(value = "categoryId", defaultValue = "2") String categoryId,
			@RequestParam(value = "device", defaultValue = "*") String device)
			throws org.json.simple.parser.ParseException, JSONException {
		return elasticsearchUtils.getTotalDeviceByCategoryId(categoryId, device);
	}

	@CrossOrigin
	@RequestMapping(value = "/count_total_devices", method = RequestMethod.GET, produces = "application/json")
	public int countTotalDevices() throws org.json.simple.parser.ParseException {
		return elasticsearchUtils.getTotalDevice();
	}

	@CrossOrigin
	@RequestMapping(value = "/get_list_device_by_version", method = RequestMethod.GET, produces = "application/json")
	public String getListDeviceByVersion(@RequestParam(value = "deviceType", defaultValue = "*") String deviceType,
			@RequestParam(value = "version", defaultValue = "*") String version)
			throws org.json.simple.parser.ParseException, JSONException {
		return elasticsearchUtils.getDevicesByDeviceVersion(deviceType, version).toString();
	}
}
