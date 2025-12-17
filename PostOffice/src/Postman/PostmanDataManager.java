package Postman;

import java.util.List;
import OfficeOperator.Client;

public interface PostmanDataManager {
    List<Postman> getPostmen();
    void addPostman(Postman postman);
    void updatePostman(Postman postman);
    void deletePostman(int id);
    Postman getPostmanById(int id);
    
    // Добавляем методы для работы со связями
    List<Postman> getPostmenByDistrict(String district);
    List<Client> getAssignedClients(int postmanId);
    int getClientCount(int postmanId);
}