package vn.viettel.browser.controller;

import javax.annotation.Resource;

import org.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.browser.service.CheckStatusService;


@RestController
@RequestMapping("/user")
public class CheckStatusController {
	@Resource
	CheckStatusService checkStatus ;
	@CrossOrigin
    @RequestMapping(value = "/checkStatus", method = RequestMethod.GET, produces = "application/json")
    public String checkStatusSendCategory()
            throws org.json.simple.parser.ParseException, JSONException {
        return checkStatus.checkStatusSending();
    }

}
