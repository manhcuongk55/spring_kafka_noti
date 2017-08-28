package vn.viettel.browser.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "version_app")
public class AppVersion {
	
	
	private Integer id;
	private String name;
	
	
	
	public AppVersion() {
		super();
	}
	public AppVersion(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	@Id
	@Column(name = "id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}