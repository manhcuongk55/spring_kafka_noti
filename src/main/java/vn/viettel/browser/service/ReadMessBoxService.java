package vn.viettel.browser.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.viettel.browser.model.SentMessBox;
import vn.viettel.browser.repo.SentMessBoxRepository;

@Service
public class ReadMessBoxService {
	@Autowired
	private SentMessBoxRepository repository;
	public String updateStatus(String jobId, String id){
		SentMessBox sentMessBox =  repository.findByJobId(jobId);
		sentMessBox.addIdFirebase(id);
		repository.save(sentMessBox);
		return repository.findByJobId(jobId).toString();
	}
	public String updateStatusForCMS(String jobId){
		List<String> listId = new ArrayList<>();
		repository.save(new SentMessBox(jobId, listId));
		return repository.findByJobId(jobId).toString();
	}
	
	public String checkSentMessByDevice(String jobId){
		SentMessBox sentMessBox =  repository.findByJobId(jobId);
		return sentMessBox.getCountSentDevice() +"";
	}
}
