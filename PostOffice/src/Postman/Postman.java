package Postman;

public class Postman {
    private int id;
    private String surname;
    private String firstName;
    private String patronymic;
    private String phone;
    private String district; // Район
    private int experience; // Стаж в годах
    private String workSchedule; // График работы
    
    public Postman() {}
    
    public Postman(int id, String surname, String firstName, String patronymic, 
                   String phone, String district, int experience, String workSchedule) {
        this.id = id;
        this.surname = surname;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.phone = phone;
        this.district = district;
        this.experience = experience;
        this.workSchedule = workSchedule;
    }
    
    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }
    
    public String getFullName() {
        return surname + " " + firstName + " " + (patronymic != null ? patronymic : "");
    }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    
    public String getWorkSchedule() { return workSchedule; }
    public void setWorkSchedule(String workSchedule) { this.workSchedule = workSchedule; }
}