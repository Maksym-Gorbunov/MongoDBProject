import java.util.List;

/**
 * Restaurant class is extra class,
 * it creates while importing data from json file,
 * after all instance data kan be load to mongodb collection,
 * one instance kan be inserted as one document in mongo collection
 * @author Maksym
 * @since 2019-01-31
 */
public class Restaurant {

    /**
     * Fields
     */
    private String _id;
    private String name;
    private Integer stars;
    private List<String> categories;
    public static final long serialVersionUID = 11L;

    /**
     * Make Restaurant class comparable, possible use equals like on java object class
     * @param obj is contact instance
     * @return boolean if same instance
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Restaurant obj2 = (Restaurant) obj;
        return get_id().equals(obj2.get_id());
    }

    /**
     * standart java hash method
     * @return number
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(get_id());
    }

    /**
     * Transform all Restaurants data to readable format
     * @return contact info as text
     */
    @Override
    public String toString() {
        String data = "_id: "+this.get_id()+"\n"+
                      "name: "+this.getName()+"\n"+
                      "stars: "+this.getStars()+"\n"+
                      "categories: "+this.getCategories();
        return data;
    }

    /**
     * Getters and setters
     */
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getStars() {
        return stars;
    }
    public void setStars(Integer stars) {
        this.stars = stars;
    }
    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
