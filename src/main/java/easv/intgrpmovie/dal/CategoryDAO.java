package easv.intgrpmovie.dal;

import easv.intgrpmovie.be.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


    public class CategoryDAO {

        private DBConnection conn = new DBConnection();

        // Method to collect category from the database
        public List<Category> getCategory() {
            List<Category> category = new ArrayList<>();
            try {
                Connection c = conn.getConnection();
                String sql = "SELECT c.id, c.name " +
                        "FROM Category c";
                PreparedStatement stmnt = c.prepareStatement(sql);
                ResultSet rs = stmnt.executeQuery();
                while (rs.next()) { //While there are rows
                    int id = rs.getInt("id");
                    String name = rs.getString("name");

                    Category catMovie = new Category(id, name); // Correct constructor usage
                    category.add(catMovie);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return category;
        }
    }




