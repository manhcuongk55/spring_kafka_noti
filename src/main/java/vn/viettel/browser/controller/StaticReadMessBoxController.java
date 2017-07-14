package vn.viettel.browser.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.browser.service.ReadMessBoxService;

@RestController
@RequestMapping("/user")
public class StaticReadMessBoxController {
	
	@Resource
    private ReadMessBoxService readMessBoxService ;

	
	@CrossOrigin
    @RequestMapping(value = "/log_read_mess_box", method = RequestMethod.GET)
    public String updateDeviceToDB(@RequestParam (value = "jobId", defaultValue = "2") String jobId, @RequestParam (value = "id", defaultValue = "2") String id)
            throws org.json.simple.parser.ParseException {
        return readMessBoxService.updateStatus(jobId, id);
    }
	
	@CrossOrigin
    @RequestMapping(value = "/log_read_mess_box_cms", method = RequestMethod.GET)
    public String initJobID(@RequestParam (value = "jobId", defaultValue = "2") String jobId)
            throws org.json.simple.parser.ParseException {
        return readMessBoxService.updateStatusForCMS(jobId);
    }
	@CrossOrigin
    @RequestMapping(value = "/check_status_sending_mess_box", method = RequestMethod.GET)
    public String checkStatus(@RequestParam (value = "jobId", defaultValue = "2") String jobId)
            throws org.json.simple.parser.ParseException {
        return readMessBoxService.checkSentMessByDevice(jobId);
    }
}
