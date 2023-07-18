package at.nicoleperak.shared;

public class Category {

    private Long id;
    private String title;
    private CategoryType type;

    public Category(Long id, String title, CategoryType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public Category() {
    }

    public enum CategoryType {Income, Expense}
}
