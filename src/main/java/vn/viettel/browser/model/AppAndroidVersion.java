package vn.viettel.browser.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "version_android_app")
public class AppAndroidVersion {
	
	
	private Integer id;
	private String name;
	
	
	
	public AppAndroidVersion() {
		super();
	}
	public AppAndroidVersion(Integer id, String name) {
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