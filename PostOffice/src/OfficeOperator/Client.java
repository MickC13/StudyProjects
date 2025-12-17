package OfficeOperator;

public class Client {
    private int id;
    private String surname;
    private String phone;
    private String address;
    private String newspaper;
    private String district; // Добавляем поле для района
    private Integer postmanId; // Добавляем поле для ID почтальона
    
    public Client() {}
    
    // Конструктор с новыми полями
    public Client(int id, String surname, String phone, String address, 
                 String newspaper, String district, Integer postmanId) {
        this.id = id;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.newspaper = newspaper;
        this.district = district;
        this.postmanId = postmanId;
    }
    
    // Старый конструктор для обратной совместимости
    public Client(int id, String surname, String phone, String address, String newspaper) {
        this(id, surname, phone, address, newspaper, null, null);
    }
    
    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getNewspaper() { return newspaper; }
    public void setNewspaper(String newspaper) { this.newspaper = newspaper; }
    
    // Новые геттеры и сеттеры для связей
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public Integer getPostmanId() { return postmanId; }
    public void setPostmanId(Integer postmanId) { this.postmanId = postmanId; }
    
    @Override
    public String toString() {
        return surname + ", " + phone + ", " + address + ", " + newspaper;
    }
}