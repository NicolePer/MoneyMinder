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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }
}
