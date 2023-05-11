package recipes.entity;

public class Category {
	private Integer categoryId;
	private String categoryName;

	//Getters and Setters created below
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	//toString() method created below
	@Override
	public String toString() {
		return "Id=" + categoryId + ", categoryName=" + categoryName;
	}
}
