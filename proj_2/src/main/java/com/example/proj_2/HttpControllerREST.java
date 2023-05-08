package com.example.proj_2;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@RestController
public class HttpControllerREST extends HttpServlet {
    private ArrayList<Integer> array_of_id = new ArrayList<>();

    @RequestMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException {
        if (request.getParameter("what_do") != null) {
            switch (request.getParameter("what_do")) {
                case "out_db" -> {
                    return get_all();
                }
                case "delete_str" -> {
                    if (array_of_id.size() == 0) {
                        array_of_id = get_size_str();
                        if (array_of_id.size() == 0) return "Записей нет";
                        else if (request.getParameter("num_str_for_delete") != null && Integer.parseInt(request.getParameter("num_str_for_delete")) <= array_of_id.size() && Integer.parseInt(request.getParameter("num_str_for_delete")) >= 0 && array_of_id.contains(Integer.parseInt(request.getParameter("num_str_for_delete"))))
                        {
                            return delete_str(Integer.parseInt(request.getParameter("num_str_for_delete")));
                        }
                    } else {
                        return delete_str(Integer.parseInt(request.getParameter("num_str_for_delete")));
                    }
                }
                case "add_str"->{
                    if ((request.getParameter("lastname") != null || request.getParameter("name") != null || request.getParameter("num_y") != null)) {
                        if (!request.getParameter("lastname").equals("") && !request.getParameter("name").equals("") && !request.getParameter("num_y").equals("")) {
                            if (Integer.parseInt(request.getParameter("num_y")) > 0 && Integer.parseInt(request.getParameter("num_y")) < 12) {
                                String lastname = request.getParameter("lastname");
                                String firstname = request.getParameter("name");
                                int number_year = Integer.parseInt(request.getParameter("num_y"));
                                return add_str(firstname, lastname, number_year);

                            }
                        }
                        else return "Ошбика";
                    }
                    else return "Ошбика";
                }
                case "edit_str"->{
                    if ((request.getParameter("lastname") != null || request.getParameter("name") != null || request.getParameter("num_y") != null || request.getParameter("id") != null)) {
                        if (!request.getParameter("lastname").equals("") && !request.getParameter("name").equals("") && !request.getParameter("num_y").equals("") && !request.getParameter("id").equals("")) {
                            if (Integer.parseInt(request.getParameter("num_y")) > 0 && Integer.parseInt(request.getParameter("num_y")) < 12 && get_size_str().contains(Integer.parseInt(request.getParameter("id")))) {
                                String lastname = request.getParameter("lastname");
                                String firstname = request.getParameter("name");
                                int number_year = Integer.parseInt(request.getParameter("num_y"));
                                int id=Integer.parseInt(request.getParameter("id"));
                                return edit_str(id, firstname, lastname, number_year);
                            }
                        }
                        else return "Ошбика";
                    }
                    else return "Ошбика";
                }
                default -> {
                    return "Неизвестный запрос";
                }
            }
        } else {
            return "ArrayList<String>()";
        }
        return null;
    }


    private String delete_str(int num) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://YOUR_DB",
                        "user", "pass");

        try {
            //ResultSet rs = stmt.executeQuery("DELETE FROM table_of_user WHERE ID=" + num);
            PreparedStatement pr_stmt = null;
            String sql = "DELETE FROM table_of_user WHERE ID=(?)";
            pr_stmt = c.prepareStatement(sql);
            pr_stmt.setInt(1, num);
            pr_stmt.executeUpdate();
            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return "Ошибка";
        }
        return "ОК";
    }

    private ArrayList<Integer> get_size_str() {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.getCurrentSession();

        ArrayList<Integer> all_id = new ArrayList<>();
        try {
            session.beginTransaction();
            List<User> list_user = session.createQuery("FROM User", User.class).list();
            for (User us : list_user) {
                all_id.add(us.getId());
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
            factory.close();
        }


        return all_id;
    }

    private String get_all() {

        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.getCurrentSession();

        String tt = "";
        try {
            session.beginTransaction();
            List<User> list_user = session.createQuery("FROM User", User.class).list();
            for (User us : list_user) {
                tt += (us.getId()) + "  ";
                tt += ("LN" + (us.getLastname())) + "  ";
                tt += ("NM" + us.getName()) + "  ";
                tt += ("Y_S" + us.getNumYear()) + " ; ";
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
            factory.close();
        }


        return tt;
    }

    private String edit_str(int ID, String name, String lastname, int num) {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.getCurrentSession();
        String sql="UPDATE table_of_user SET name = :uname, lastname = :luname, num_year = :num WHERE id = :id";
        session.beginTransaction();
        NativeQuery query = session.createNativeQuery(sql);
        query.setParameter("uname", name);
        query.setParameter("luname", lastname);
        query.setParameter("num", num);
        query.setParameter("id", ID);
        int rr = query.executeUpdate();
        session.getTransaction().commit();
        if (rr != 0) {
            return "OK";
        } else {
            return "Error";
        }
    }

    private String add_str(String name, String lastname, int num) throws ClassNotFoundException, SQLException {
        array_of_id=get_size_str();

        int number = 0;
        if (array_of_id.size() != 0) {
            for(int i=0;i<=array_of_id.size();i++){
                if(!array_of_id.contains(i)) number=i;
            }
            if(number==0){
                number = array_of_id.get(array_of_id.size() - 1) + 1;
            }
        }


        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://YOUR_DB",
                        "user", "pass");
        try {
            PreparedStatement pr_stmt = null;
            String sql = "INSERT INTO table_of_user (id,lastname,name,num_year) VALUES (?,?,?,?)";
            pr_stmt = c.prepareStatement(sql);
            pr_stmt.setInt(1, number);
            pr_stmt.setString(2, lastname);
            pr_stmt.setString(3, name);
            pr_stmt.setInt(4, num);
            pr_stmt.executeUpdate();
            pr_stmt.close();
            c.close();
            return "OK";
        } catch (Exception e) {
            System.out.println("Ошибка во внесении данных в БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            return "ERROR";
        }
    }
}

