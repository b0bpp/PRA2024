package hibernate;

import hibernate.model.Address;
import hibernate.model.Employee;
import hibernate.queries.Queries;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


class Manager {

    public static void main(String[] args) {

        System.out.println("Start");

        EntityManager entityManager = null;

        EntityManagerFactory entityManagerFactory = null;

        try {

            // FACTORY NAME HAS TO MATCHED THE ONE FROM PERSISTED.XML !!!
            entityManagerFactory = Persistence.createEntityManagerFactory("hibernate-dynamic");

            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);

            //New transaction
            session.beginTransaction();

            // Create Employee
            Employee emp = createEmployee();

            // Save in First order Cache (not database yet)
            session.save(emp);

            Employee employee = session.get(Employee.class, emp.getId());
            if (employee == null) {
                System.out.println(emp.getId() + " not found! ");
            } else {
                System.out.println("Found " + employee);
            }

            System.out.println("Employee " + employee.getId() + " " + employee.getFirstName() + employee.getLastName());
            changeFirstGuyToNowak(session);

            session.flush();
            session.refresh(employee);
            //changeFirstGuyToNowak(session);
            employee.setLastName("NowakPRE" + new Random().nextInt()); // No SQL needed
            session.flush();
            employee.setLastName("NowakPRE" + new Random().nextInt()); // No SQL needed

            //emp.getAddress().setStreet(null);
            //Commit transaction to database
            session.getTransaction().commit();

            //CASCADE SESSION ERROR

            //session.refresh(employee);

            employee.getAddress().setStreet(null);
            session.save(emp.getAddress());

            System.out.println(new Queries(session).getThemAll().stream().map(a -> a.getFirstName() + " " + a.getLastName()).collect(Collectors.joining()));

            System.out.println("Done");

            session.beginTransaction();
            Address add = session.get(Address.class, 1);

            // Need to be not null before commmit
            employee.getAddress().setStreet("noname");
            session.save(employee.getAddress());
            session.getTransaction().commit();

            // FETCH
            System.out.println("Done");

            for (int i = 1; i < 10; i++) {
                session.save(Employee.copyEmployee(emp));
            }

            session.getTransaction().begin();
            Employee employee1 = session.get(Employee.class, 1);

            session.getTransaction().commit();

            session.clear();

            session.getTransaction().begin();

            employee1 = session.get(Employee.class, 1);
            add = employee1.getAddress();
            System.out.println(add.getCity());

            System.out.println(new Queries(session).getAllEmployeeByPage(1, session).stream().map(em -> em.getFirstName() + em.getLastName()).collect(Collectors.joining(" ")));

            session.getTransaction().commit();

            session.close();

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
        } finally {

        }

    }

    private static Employee createEmployee() {
        Employee emp = new Employee();
        Address address = new Address();
        address.setCity("Poznan");
        address.setStreet("Poznanska");
        address.setNr("5");
        address.setPostcode("44444");
        address.setHousenr("5");
        emp.setAddress(address);
        emp.setFirstName("Jan");
        emp.setLastName("Polak" + new Random().nextInt());
        emp.setSalary(100);
        emp.setPesel(new Random().nextInt());
        return emp;
    }

    static void changeFirstGuyToNowak(Session session) {

        List<Employee> employees = new Queries(session).getEmployeeByName("Polak");

        employees.get(0).setLastName("NowakPRE" + new Random().nextInt());

    }

}