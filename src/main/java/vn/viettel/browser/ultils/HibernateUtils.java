package vn.viettel.browser.ultils;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import vn.viettel.browser.model.AppVersion;
import vn.viettel.browser.model.DeviceVersion;

public class HibernateUtils {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	// Hibernate 5:
	private static SessionFactory buildSessionFactory() {
		try {
			// Tạo đối tượng ServiceRegistry từ hibernate.cfg.xml
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()//
					.configure("hibernate.cfg.xml").build();

			// Tạo nguồn siêu dữ liệu (metadata) từ ServiceRegistry
			Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

			return metadata.getSessionFactoryBuilder().build();
		} catch (Throwable ex) {

			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		// Giải phóng cache và Connection Pools.
		getSessionFactory().close();
	}

	public static String getVersionDeviceFromKeyInDB(String key) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.getCurrentSession();
		DeviceVersion value = new DeviceVersion();
		String result = "[";
		try {
			// Tất cả các lệnh hành động với DB thông qua Hibernate
			// đều phải nằm trong 1 giao dịch (Transaction)
			// Bắt đầu giao dịch
			session.getTransaction().begin();

			// Tạo một câu lệnh HQL query object.
			// Tương đương với Native SQL:
			// Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO

			String sql = "Select e from " + DeviceVersion.class.getName() + " e " + " where e.id in "
					+ key.replace("[", "(").replace("]", ")");

			Query<DeviceVersion> query = session.createQuery(sql);
			// Tạo đối tượng Query.
			List<DeviceVersion> values = query.getResultList();
			for (DeviceVersion emp : values) {
				result = result + emp.getName() + ",";

			}
			result = result.substring(0, result.length() - 1) + "]";
			// Commit dữ liệu
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			// Rollback trong trường hợp có lỗi xẩy ra.
			session.getTransaction().rollback();
		}
		return result;
	}

	public static String getVersionAppFromKeyInDB(String key) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.getCurrentSession();
		DeviceVersion value = new DeviceVersion();
		String result = "[";
		try {
			// Tất cả các lệnh hành động với DB thông qua Hibernate
			// đều phải nằm trong 1 giao dịch (Transaction)
			// Bắt đầu giao dịch
			session.getTransaction().begin();

			// Tạo một câu lệnh HQL query object.
			// Tương đương với Native SQL:
			// Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO

			String sql = "Select e from " + AppVersion.class.getName() + " e " + " where e.id in "
					+ key.replace("[", "(").replace("]", ")");

			Query<AppVersion> query = session.createQuery(sql);
			// Tạo đối tượng Query.
			List<AppVersion> values = query.getResultList();
			for (AppVersion emp : values) {
				result = result + emp.getName() + ",";

			}
			result = result.substring(0, result.length() - 1) + "]";
			// Commit dữ liệu
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			// Rollback trong trường hợp có lỗi xẩy ra.
			session.getTransaction().rollback();
		}
		return result;
	}

}