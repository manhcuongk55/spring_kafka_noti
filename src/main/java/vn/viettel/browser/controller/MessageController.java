package vn.viettel.browser.controller;

import javax.annotation.Resource;

import org.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.browser.service.MessageService;
import vn.viettel.browser.ultils.ElasticsearchUtils;

@RestController
@RequestMapping("/user")
public class MessageController {
	
	@Resource
    private MessageService messService;
	
	@CrossOrigin
    @RequestMapping(value = "/get_devices_by_version", method = RequestMethod.GET, produces = "application/json")
    public String sendMessToDevice(@RequestParam (value = "device", defaultValue = "*") String device,
                                        @RequestParam (value = "version" , defaultValue = "1.0.0") String version)
            throws org.json.simple.parser.ParseException, JSONException {
        return ElasticsearchUtils.getListDeviceByVersion(device,version).toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/count_devices_by_version", method = RequestMethod.GET, produces = "application/json")
    public String countDeviceByVersion(@RequestParam (value = "device", defaultValue = "*") String device,
                                @RequestParam (value = "version" , defaultValue = "1.0.0") String version)
            throws org.json.simple.parser.ParseException, JSONException {
        return ElasticsearchUtils.countNumberOfDeviceByVersion(device,version).toString();
    }

}
