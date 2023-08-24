package at.nicoleperak.shared;

import java.util.List;

@SuppressWarnings("unused")
public class CategoryList {

    private List<Category> categories;

    public CategoryList() {

    }

    public CategoryList(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
