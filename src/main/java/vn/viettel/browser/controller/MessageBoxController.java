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

import vn.viettel.browser.Application;
import vn.viettel.browser.service.ProducerPutMessKafkaNotiService;

@RestController
@RequestMapping("/user")
public class MessageBoxController {
	@Resource
	private ProducerPutMessKafkaNotiService producerPutMessKafkaNotiService;

	@CrossOrigin
	@RequestMapping(value = "/get_devices_by_version", method = RequestMethod.GET, produces = "application/json")
	public String sendMessToDevice(@RequestParam(value = "device", defaultValue = "*") String device,
			@RequestParam(value = "version", defaultValue = "1.0.0") String version)
			throws org.json.simple.parser.ParseException, JSONException {
		return Application.elasticsearchUtils.getListDeviceByVersion(device, version).toString();
	}

	@CrossOrigin
	@RequestMapping(value = "/count_devices_by_version", method = RequestMethod.GET, produces = "application/json")
	public String countDeviceByVersion(@RequestParam(value = "device", defaultValue = "*") String device,
			@RequestParam(value = "version", defaultValue = "1.0.0") String version)
			throws org.json.simple.parser.ParseException, JSONException {
		return Application.elasticsearchUtils.countNumberOfDeviceByVersion(device, version).toString();
	}

	@CrossOrigin
	@RequestMapping(value = "/send_mess_box", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String sendMess(@RequestBody String message) throws Exception {
		//System.out.println("@RequestBody : " + message);
		return producerPutMessKafkaNotiService.SendMess(message, 2);

	}

	@CrossOrigin
	@RequestMapping(value = "/send_mess_box_all", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String sendMessToAll(@RequestBody String message) throws Exception {
		return producerPutMessKafkaNotiService.SendMess(message, 3);
	}

	@CrossOrigin
	@RequestMapping(value = "/send_mess_box_test", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String sendMessToTestDevice(@RequestBody String message) throws Exception {
		return producerPutMessKafkaNotiService.SendMess(message, 5);
	}

}
