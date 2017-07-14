package vn.viettel.browser.controller;

import javax.annotation.Resource;

import org.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.browser.service.CheckStatusService;


@RestController
@RequestMapping("/user")
public class CheckStatusController {
	@Resource
	CheckStatusService checkStatus ;
	
	
	@CrossOrigin
    @RequestMapping(value = "/checkStatus", method = RequestMethod.POST, produces = "application/json")
    public String checkStatus(@RequestBody String id)
            throws org.json.simple.parser.ParseException, JSONException {
        return checkStatus.checkStatusSending(id);
    }

	
	@CrossOrigin
    @RequestMapping(value = "/checkStatusBox", method = RequestMethod.POST, produces = "application/json")
    public String checkStatusBox(@RequestBody String id)
            throws org.json.simple.parser.ParseException, JSONException {
        return checkStatus.checkStatusSendingBox(id);
    }
}
