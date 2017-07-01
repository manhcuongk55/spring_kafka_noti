package vn.viettel.browser.controller;

import javax.annotation.Resource;

import org.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.browser.service.MessageBoxService;
import vn.viettel.browser.ultils.ElasticsearchUtils;

@RestController
@RequestMapping("/user")
public class MessageBoxController {
	ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	@Resource
    private MessageBoxService messService;
	
	@CrossOrigin
    @RequestMapping(value = "/get_devices_by_version", method = RequestMethod.GET, produces = "application/json")
    public String sendMessToDevice(@RequestParam (value = "device", defaultValue = "*") String device,
                                        @RequestParam (value = "version" , defaultValue = "1.0.0") String version)
            throws org.json.simple.parser.ParseException, JSONException {
        return elasticsearchUtils.getListDeviceByVersion(device,version).toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/count_devices_by_version", method = RequestMethod.GET, produces = "application/json")
    public String countDeviceByVersion(@RequestParam (value = "device", defaultValue = "*") String device,
                                @RequestParam (value = "version" , defaultValue = "1.0.0") String version)
            throws org.json.simple.parser.ParseException, JSONException {
        return elasticsearchUtils.countNumberOfDeviceByVersion(device,version).toString();
    }
    @CrossOrigin
    @RequestMapping(value = "/send_mess_box", method = RequestMethod.POST)
    @ResponseBody
    public String sendMess(@RequestBody String message) throws Exception {
        System.out.println("@RequestBody : " + message);
       return messService.sendMessBoxToListDeviceIdsByVersion1(message);
    }
    @CrossOrigin
    @RequestMapping(value = "/send_mess_box_all", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessToAll(@RequestBody String message) throws Exception {
        System.out.println("@RequestBody : " + message);
       return messService.sendMessBoxToAll(message);
    }
    
}
