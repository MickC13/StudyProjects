package OfficeOperator;

import java.util.List;

public interface ClientDataManager {
    List<Client> getClients();
    void addClient(Client client);
    void updateClient(Client client);
    void deleteClient(int id);
    Client getClientById(int id);
    
    // Добавляем методы для работы со связями
    List<Client> getClientsByDistrict(String district);
    List<Client> getClientsByNewspaper(String newspaper);
    List<Client> getClientsByPostman(int postmanId);
}