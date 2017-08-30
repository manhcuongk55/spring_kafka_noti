package vn.viettel.browser.ultils;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import vn.viettel.browser.model.AppAndroidVersion;
import vn.viettel.browser.model.AppIosVersion;
import vn.viettel.browser.model.DeviceAndroidVersion;
import vn.viettel.browser.model.DeviceIosVersion;

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

	public static String getVersionAndroidDeviceFromKeyInDB(String key) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.getCurrentSession();
		DeviceIosVersion value = new DeviceIosVersion();
		String result = "";
		try {
			// Tất cả các lệnh hành động với DB thông qua Hibernate
			// đều phải nằm trong 1 giao dịch (Transaction)
			// Bắt đầu giao dịch
			session.getTransaction().begin();

			// Tạo một câu lệnh HQL query object.
			// Tương đương với Native SQL:
			// Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO

			String sql = "Select e from " + DeviceAndroidVersion.class.getName() + " e " + " where e.id in "
					+ key.replace("[", "(").replace("]", ")");

			Query<DeviceIosVersion> query = session.createQuery(sql);
			// Tạo đối tượng Query.
			List<DeviceIosVersion> values = query.getResultList();
			for (DeviceIosVersion emp : values) {
				result = result + emp.getName() + ",";

			}
			result = result.substring(0, result.length() - 1);
			// Commit dữ liệu
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			// Rollback trong trường hợp có lỗi xẩy ra.
			session.getTransaction().rollback();
		}
		return result;
	}

	public static String getVersionAndroidAppFromKeyInDB(String key) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.getCurrentSession();
		DeviceIosVersion value = new DeviceIosVersion();
		String result = "";
		try {
			// Tất cả các lệnh hành động với DB thông qua Hibernate
			// đều phải nằm trong 1 giao dịch (Transaction)
			// Bắt đầu giao dịch
			session.getTransaction().begin();

			// Tạo một câu lệnh HQL query object.
			// Tương đương với Native SQL:
			// Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO

			String sql = "Select e from " + AppAndroidVersion.class.getName() + " e " + " where e.id in "
					+ key.replace("[", "(").replace("]", ")");

			Query<AppIosVersion> query = session.createQuery(sql);
			// Tạo đối tượng Query.
			List<AppIosVersion> values = query.getResultList();
			for (AppIosVersion emp : values) {
				result = result + emp.getName() + ",";

			}
			result = result.substring(0, result.length() - 1);
			// Commit dữ liệu
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			// Rollback trong trường hợp có lỗi xẩy ra.
			session.getTransaction().rollback();
		}
		return result;
	}
	public static String getVersionIosDeviceFromKeyInDB(String key) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.getCurrentSession();
		DeviceIosVersion value = new DeviceIosVersion();
		String result = "";
		try {
			// Tất cả các lệnh hành động với DB thông qua Hibernate
			// đều phải nằm trong 1 giao dịch (Transaction)
			// Bắt đầu giao dịch
			session.getTransaction().begin();

			// Tạo một câu lệnh HQL query object.
			// Tương đương với Native SQL:
			// Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO

			String sql = "Select e from " + DeviceIosVersion.class.getName() + " e " + " where e.id in "
					+ key.replace("[", "(").replace("]", ")");

			Query<DeviceIosVersion> query = session.createQuery(sql);
			// Tạo đối tượng Query.
			List<DeviceIosVersion> values = query.getResultList();
			for (DeviceIosVersion emp : values) {
				result = result + emp.getName() + ",";

			}
			result = result.substring(0, result.length() - 1);
			// Commit dữ liệu
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			// Rollback trong trường hợp có lỗi xẩy ra.
			session.getTransaction().rollback();
		}
		return result;
	}

	public static String getVersionIosAppFromKeyInDB(String key) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.getCurrentSession();
		DeviceIosVersion value = new DeviceIosVersion();
		String result = "";
		try {
			// Tất cả các lệnh hành động với DB thông qua Hibernate
			// đều phải nằm trong 1 giao dịch (Transaction)
			// Bắt đầu giao dịch
			session.getTransaction().begin();

			// Tạo một câu lệnh HQL query object.
			// Tương đương với Native SQL:
			// Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO

			String sql = "Select e from " + AppIosVersion.class.getName() + " e " + " where e.id in "
					+ key.replace("[", "(").replace("]", ")");

			Query<AppIosVersion> query = session.createQuery(sql);
			// Tạo đối tượng Query.
			List<AppIosVersion> values = query.getResultList();
			for (AppIosVersion emp : values) {
				result = result + emp.getName() + ",";

			}
			result = result.substring(0, result.length() - 1);
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