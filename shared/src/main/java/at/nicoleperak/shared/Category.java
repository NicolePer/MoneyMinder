package at.nicoleperak.shared;

import java.util.Objects;

@SuppressWarnings("unused")
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

    public enum CategoryType {
        INCOME("Income"), EXPENSE("Expense");
        private final String label;

        CategoryType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

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

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
