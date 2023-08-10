package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.CategoryList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static at.nicoleperak.server.database.DatabaseUtils.*;
import static java.sql.DriverManager.getConnection;

public class CategoryOperations {
    protected static final String CATEGORY_TABLE = "categories";
    protected static final String CATEGORY_ID = "id";
    protected static final String CATEGORY_TITLE = "title";
    protected static final String CATEGORY_TYPE = "category_type";


    public static CategoryList selectCategoryList(Category.CategoryType categoryType) throws ServerException {
        ArrayList<Category> categories = new ArrayList<>();
        String select = "SELECT * FROM " + CATEGORY_TABLE
                + " WHERE " + CATEGORY_TYPE + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setInt(1, categoryType.ordinal());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(
                            new Category(rs.getLong(CATEGORY_ID),
                                    rs.getString(CATEGORY_TITLE),
                                    Category.CategoryType.values()[rs.getShort(CATEGORY_TYPE)]));
                }
                return new CategoryList(categories);
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select category list", e);
        }
    }

    public static CategoryList selectCategoryList() throws ServerException {
        ArrayList<Category> categories = new ArrayList<>();
        String select = "SELECT * FROM " + CATEGORY_TABLE;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(
                            new Category(rs.getLong(CATEGORY_ID),
                                    rs.getString(CATEGORY_TITLE),
                                    Category.CategoryType.values()[rs.getShort(CATEGORY_TYPE)]));
                }
                return new CategoryList(categories);
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select category list", e);
        }
    }
}
