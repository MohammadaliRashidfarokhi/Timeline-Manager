package Utils;

import Models.Event;
import Models.Timeline;
import Models.User;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Restrictions;

import ch.vorburger.exec.ManagedProcessException;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.Query;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;


public class DatabaseController {

    //START OF DB CONFIGURATION
    //If you want to use and external DB just change persistence property to Persistance.EXTERNAL_DB
    //and add the program needs to connect to that DB
    final static Persistence persistence = Persistence.DB;
    final static String dbDriver = "org.mariadb.jdbc.Driver";
    final static String dbUrl = "jdbc:mysql://localhost:26439/testDB";
    final static String dbUser = "root";
    final static String dbPassword = "root";
    final static String dbDialect = "org.hibernate.dialect.MariaDBDialect";
    //END OF DATABASE CONFIGURATION

    public static SessionFactory getSessionFactory() {
        SessionFactory sessionFactory = null;
        try {
            Configuration configuration = new Configuration();
            // Hibernate settings equivalent to hibernate.cfg.xml's properties
            Properties settings = new Properties();
            if(persistence==Persistence.DB) {
                settings.put(Environment.DRIVER, "org.mariadb.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://localhost:26439/testDB");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MariaDBDialect");
            } else {
                settings.put(Environment.DRIVER, dbDriver);
                settings.put(Environment.URL, dbUrl);
                if(!dbUser.isEmpty()) {
                    settings.put(Environment.USER, dbUser);
                }
                if(!dbPassword.isEmpty()) {
                    settings.put(Environment.PASS, dbPassword);
                }
                settings.put(Environment.DIALECT, dbDialect);
            }

            settings.put(Environment.SHOW_SQL, "true");
            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
            //settings.put(Environment.HBM2DDL_AUTO, "create-drop");
            settings.put(Environment.HBM2DDL_AUTO, "update");

            configuration.setProperties(settings);
            configuration.addAnnotatedClass(Event.class);
            configuration.addAnnotatedClass(Timeline.class);
            configuration.addAnnotatedClass(User.class);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }

    public static void startDB() throws ManagedProcessException {
        if(persistence == Persistence.DB) {
            DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
            config.setPort(26439); // 0 => autom. detect free port
            config.setDataDir("DB2");
            DB db = DB.newEmbeddedDB(config.build());
            db.start();
            String dBname = "testDB";
            //if (!dBname.equals("test")) {
            db.createDB(dBname);
            //}
            System.out.println("Database running on: " + config.getURL(dBname));
        }
    }

    public static Object getRecord(int id, Class entityClass) {
        Session s = getSessionFactory().openSession();
        s.beginTransaction();
        Object result = s.load(entityClass, id);
        Hibernate.initialize(result);
        //s.flush();
        //s.close();
        return result;
    }

    public static void createRecord(Object e) {
        Session s = getSessionFactory().openSession();
        s.beginTransaction();
        s.save(e);
        s.getTransaction().commit();
        //s.flush();
        //s.close();
    }

    public static void deleteRecord(int id, Class entityClass) {
        Session s = getSessionFactory().openSession();
        s.beginTransaction();
        Object object = s.load(entityClass, id);
        s.delete(object);
        s.getTransaction().commit();
        //s.flush();
       //s.close();
    }

    public static User getUserByUsernamePassword(String username, String password) {
        Session s = getSessionFactory().openSession();
        Criteria criteria = s.createCriteria(User.class);
        criteria.add(Restrictions.eq("username", username));
        criteria.add(Restrictions.eq("password", password));
        User user =(User) criteria.uniqueResult();
        //s.flush();
        //s.close();
        return user;
    }
    
   public static boolean usernameTaken(String username) {
    	Session s = getSessionFactory().openSession();
    	Criteria criteria = s.createCriteria(User.class).add(Restrictions.eq("username", username));
    	List<User> allUsernames = (List<User>)(List<?>) criteria.list();
    	
    	for (int i = 0; i < allUsernames.size(); i++) {
    		if (allUsernames.get(i).getUsername().equals(username)) {
    			return true;
    		}
    	}
    		return false;
    }

    public static List<Object> getAll(Class objectClass) {
        Session s = getSessionFactory().openSession();
        System.out.println("DB: getting list of "+objectClass.getName());
        List<Object> list = s.createCriteria(objectClass).list();
        System.out.println("DB: got list of "+objectClass.getName());
        return list;
    }

    public static List<Event> getEventsBetween(int timelineID, Date startDate, Date endDate) {
        Session session = getSessionFactory().openSession();

        String hql = "from Event where startDate >= :beginDate and startDate <= :endDate and timeline_id = :timelineID";
        Query query = session.createQuery(hql);
        //SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date beginDate = dateFormatter.format(startDate);
        query.setParameter("beginDate", startDate);
        //Date endDate = dateFormatter.parse("2014-11-22");
        query.setParameter("endDate", endDate);
        query.setParameter("timelineID", timelineID);
        List<Event> listEvents = query.getResultList();

        /*for (Event anEvent : listEvents) {
            System.out.println(anEvent.getName());
        }*/
        return listEvents;
    }
}
