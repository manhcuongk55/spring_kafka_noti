package vn.viettel.browser.repo;


import org.springframework.data.mongodb.repository.MongoRepository;
import vn.viettel.browser.model.SentMessBox;

public interface SentMessBoxRepository extends MongoRepository<SentMessBox, String> {
    public SentMessBox findByJobId(String jobId);

}