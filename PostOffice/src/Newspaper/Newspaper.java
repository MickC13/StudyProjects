package Newspaper;

public class Newspaper {
    private int id;
    private String title;
    private String releaseDay; // День выпуска
    
    public Newspaper() {}
    
    public Newspaper(int id, String title, String releaseDay) {
        this.id = id;
        this.title = title;
        this.releaseDay = releaseDay;
    }
    
    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getReleaseDay() { return releaseDay; }
    public void setReleaseDay(String releaseDay) { this.releaseDay = releaseDay; }
}