package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES(?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id=?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE ID=?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers where firstname=?";
    private static final String findAllUserSQL = "SELECT * FROM myusers;";

    public Long createUser(User user) {
        Long result = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getLong(1);
            }
        } catch (Exception s) {
            s.printStackTrace();
        }
        return result;
    }

    public User findUserById(Long userId) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByIdSQL)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(userId);
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setAge(rs.getInt("age"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                int age = rs.getInt("age");
                Long id = Long.parseLong(rs.getString("id"));
                user = new User(id, firstname, lastname, age);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findAllUserSQL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                int age = rs.getInt("age");
                users.add(new User(id, firstname, lastname, age));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            if (ps.executeUpdate() == 0)
                throw new SQLException("No such user exists");
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteUser)) {
            ps.setLong(1, userId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}