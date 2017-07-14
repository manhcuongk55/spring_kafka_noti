package vn.viettel.browser.model;

import java.util.List;

import org.springframework.data.annotation.Id;


public class SentMessBox {
	@Id
    public String jobId;
    public List<String> listFireBaseID;

    public SentMessBox() {}

    public SentMessBox(String jobId, List<String> listFireBaseID) {
        this.jobId = jobId;
        this.listFireBaseID = listFireBaseID;
    }
    public void addIdFirebase(String id){
    	listFireBaseID.add(id);
    }
  
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public List<String> getListFireBaseID() {
		return listFireBaseID;
	}

	public void setListFireBaseID(List<String> listFireBaseID) {
		this.listFireBaseID = listFireBaseID;
	}

	@Override
    public String toString() {
        return String.format(
                "SentMessBox[jobId='%s', listFireBaseID='%s']",
                jobId, listFireBaseID);
    }
    public int getCountSentDevice(){
    	return listFireBaseID.size();
    }
}