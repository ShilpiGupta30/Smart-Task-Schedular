package service;

import java.sql.*;

import database.DBConnection;
import model.User;

public class UserDAO {

    // LOGIN USER
    public User login(
            String username,
            String password
    ) {

        User user = null;

        try {

            Connection con =
                    DBConnection.getConnection();

            String query =
                    "SELECT * FROM users WHERE username=? AND password=?";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                user =
                        new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("role")
                        );
            }

        }

        catch (Exception e) {

            e.printStackTrace();

        }

        return user;
    }

    // REGISTER USER
    public void registerUser(User user) {

        try {

            Connection con =
                    DBConnection.getConnection();

            String query =
                    "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setString(
                    1,
                    user.getUsername()
            );

            ps.setString(
                    2,
                    user.getPassword()
            );

            ps.setString(
                    3,
                    user.getRole()
            );

            ps.executeUpdate();

            System.out.println(
                    "User Registered Successfully!"
            );

        }

        catch (Exception e) {

            e.printStackTrace();

        }
    }
}