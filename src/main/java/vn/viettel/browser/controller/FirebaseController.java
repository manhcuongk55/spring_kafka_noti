package vn.viettel.browser.controller;

import org.elasticsearch.action.search.SearchResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.browser.service.FirebaseService;


import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class FirebaseController {

    @Resource
    private FirebaseService firebaseService;

    @CrossOrigin
    @RequestMapping(value = "/send_mess", method = RequestMethod.POST)
    @ResponseBody
    public String createNotifi(@RequestBody String notification) throws Exception {
        System.out.println("@RequestBody : " + notification);
       return firebaseService.sendNotoToListDeviceIdsByCategoryId(notification);
    }
    @CrossOrigin
    @RequestMapping(value = "/notification_clicks", method = RequestMethod.GET)
    public String getTotalnotificationclicks(@RequestParam (value = "from", defaultValue = "") String from,
                                             @RequestParam (value = "to", defaultValue = "") String to,
                                             @RequestParam (value = "device", defaultValue = "") String device)
            throws org.json.simple.parser.ParseException, JSONException {
        return firebaseService.getTotalNotificationClicks(from,to,device);
    }
    @CrossOrigin
    @RequestMapping(value = "/send_mess_all", method = RequestMethod.POST)
    @ResponseBody
    public String createNotifiToAll(@RequestBody String notification) throws Exception {
        System.out.println("@RequestBody : " + notification);
       return firebaseService.sendNotiToAll(notification);
    }
    
    @CrossOrigin
    @RequestMapping(value = "/get_list_devices", method = RequestMethod.GET, produces="application/json")
    public String getTotalnotificationclicks()
            throws JSONException {
        return firebaseService.getListDeviceIdsFromAllCategories().toString();
    }
    @CrossOrigin
    @RequestMapping(value = "/list_deviceId_by_category", method = RequestMethod.GET, produces = "application/json")
    public String getListDeviceIdByCategory(@RequestParam (value = "id", defaultValue = "2") String id,
                                            @RequestParam (value = "from" , defaultValue = "0") String from,
                                            @RequestParam (value = "size" , defaultValue = "20") String size)
            throws org.json.simple.parser.ParseException {
        return firebaseService.getListDeviceIdsByCategoryId(id, from, size).toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/count_notification_clicked_by_day", method = RequestMethod.GET, produces = "application/json")
    public String countNotificationClickedByDay(@RequestParam (value = "from" , defaultValue = "") String from,
                                                @RequestParam (value = "to" , defaultValue = "") String to,
                                                @RequestParam (value = "size" , defaultValue = "1") String size)
            throws org.json.simple.parser.ParseException {
        return firebaseService.getNumberOfNotificationsClickedByTime(from, to, size).toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/get_top_article_by_category", method = RequestMethod.GET, produces = "application/json")
    public String getTopArticleByCategory(@RequestParam (value = "categoryId" , defaultValue = "2") String categoryId)
            throws org.json.simple.parser.ParseException {
        int catId = Integer.parseInt(categoryId);
        return firebaseService.getHotArticleRecentlyByCategory(catId).toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/get_top_article", method = RequestMethod.GET, produces = "application/json")
    public String getTopArticle()
            throws org.json.simple.parser.ParseException {
        return firebaseService.getHotArticleRecently().toString();
    }
    @CrossOrigin
    @RequestMapping(value = "/checkStatus", method = RequestMethod.GET, produces = "application/json")
    public String checkStatusSendCategory()
            throws org.json.simple.parser.ParseException, JSONException {
        return firebaseService.checkStatusSending();
    }

    @CrossOrigin
    @RequestMapping(value = "/get_devices_by_version", method = RequestMethod.GET, produces = "application/json")
    public String sendMessToDevice(@RequestParam (value = "device", defaultValue = "*") String device,
                                        @RequestParam (value = "version" , defaultValue = "1.0.0") String version)
            throws org.json.simple.parser.ParseException, JSONException {
        return firebaseService.getListDeviceByVersion(device,version).toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/count_devices_by_version", method = RequestMethod.GET, produces = "application/json")
    public String countDeviceByVersion(@RequestParam (value = "device", defaultValue = "*") String device,
                                @RequestParam (value = "version" , defaultValue = "1.0.0") String version)
            throws org.json.simple.parser.ParseException, JSONException {
        return firebaseService.countNumberOfDeviceByVersion(device,version).toString();
    }

}
