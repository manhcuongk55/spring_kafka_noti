package vn.viettel.browser.test;

import org.hibernate.Hibernate;

import vn.viettel.browser.ultils.HibernateUtils;

public class TestHibernate {
	
	public static void main(String[] args){
		String test = "[40]";
		String value = HibernateUtils.getVersionAndroidAppFromKeyInDB(test);
		System.out.println("test.......... : "+ value);
	}

}
