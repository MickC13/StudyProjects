package Districts;

import java.util.ArrayList;
import java.util.List;

public class District {
    private int id;
    private String name;
    private String description;
    private String postalCode;
    private int population;
    private int area; // площадь в км²
    private List<Integer> postmanIds; // ID почтальонов, работающих в районе
    
    public District() {
        this.postmanIds = new ArrayList<>();
    }
    
    public District(int id, String name, String description, String postalCode, int population, int area) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.postalCode = postalCode;
        this.population = population;
        this.area = area;
    }
    
    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }
    
    public int getArea() { return area; }
    public void setArea(int area) { this.area = area; }
    
    public List<Integer> getPostmanIds() { return postmanIds; }
    public void setPostmanIds(List<Integer> postmanIds) { this.postmanIds = postmanIds; }
    
    public void addPostmanId(int postmanId) {
        if (!postmanIds.contains(postmanId)) {
            postmanIds.add(postmanId);
        }
    }
    
    public void removePostmanId(int postmanId) {
        postmanIds.remove((Integer) postmanId);
    }
    
    public int getPostmanCount() {
        return postmanIds.size();
    }
    
    public double getPopulationDensity() {
        return area > 0 ? (double) population / area : 0;
    }
    
    @Override
    public String toString() {
        return name + " (" + postalCode + ") - " + description;
    }
}