package vn.viettel.browser.test;

import vn.viettel.browser.ultils.HibernateUtils;

public class QueryObjectDemo {

	public static void main(String[] args) {

		System.out.println(HibernateUtils.getVersionAppFromKeyInDB("[1,2,4]"));

	}

}