package vn.viettel.browser.test;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import vn.viettel.browser.model.DeviceAndroid;
import vn.viettel.browser.ultils.HibernateUtils;

public class QueryObjectDemo {
 
  public static void main(String[] args) {
	  SessionFactory factory = HibernateUtils.getSessionFactory();
      Session session = factory.getCurrentSession();
      DeviceAndroid value = new DeviceAndroid();
      try {
          // Tất cả các lệnh hành động với DB thông qua Hibernate
          // đều phải nằm trong 1 giao dịch (Transaction)
          // Bắt đầu giao dịch
          session.getTransaction().begin();
 
          // Tạo một câu lệnh HQL query object.
          // Tương đương với Native SQL:
          // Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO
 
          String sql = "Select e from " + DeviceAndroid.class.getName() + " e "
                  + " where e.id = " + 2;
 
          // Tạo đối tượng Query.
          Query<DeviceAndroid> query = session.createQuery(sql);
 
          // Thực hiện truy vấn.
           value = query.getSingleResult();
           System.out.println(value.getName());
          // Commit dữ liệu
          session.getTransaction().commit();
      } catch (Exception e) {
          e.printStackTrace();
          // Rollback trong trường hợp có lỗi xẩy ra.
          session.getTransaction().rollback();
      }
  }
  
}