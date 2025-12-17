package PostalSystem;

import java.util.*;
import Newspaper.Newspaper;
import Newspaper.NewspaperDataManager;
import Postman.Postman;
import Postman.PostmanDataManager;
import OfficeOperator.Client;
import OfficeOperator.ClientDataManager;
import Districts.District;
import Districts.DistrictDataManager;

public class IntegratedDataManager implements NewspaperDataManager, PostmanDataManager, 
                                             ClientDataManager, DistrictDataManager {
    // Коллекции данных
    private List<Newspaper> newspapers = new ArrayList<>();
    private List<Postman> postmen = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private List<District> districts = new ArrayList<>();
    
    // Счетчики ID
    private int nextNewspaperId = 1;
    private int nextPostmanId = 1;
    private int nextClientId = 1;
    private int nextDistrictId = 1;
    
    // Конструктор
    public IntegratedDataManager() {
        loadTestData();
    }
    
    // === Реализация NewspaperDataManager ===
    @Override
    public List<Newspaper> getNewspapers() {
        return new ArrayList<>(newspapers);
    }
    
    @Override
    public void addNewspaper(Newspaper newspaper) {
        newspaper.setId(nextNewspaperId++);
        newspapers.add(newspaper);
    }
    
    @Override
    public void updateNewspaper(Newspaper newspaper) {
        for (int i = 0; i < newspapers.size(); i++) {
            if (newspapers.get(i).getId() == newspaper.getId()) {
                newspapers.set(i, newspaper);
                break;
            }
        }
    }
    
    @Override
    public void deleteNewspaper(int id) {
        newspapers.removeIf(n -> n.getId() == id);
    }
    
    @Override
    public Newspaper getNewspaperById(int id) {
        return newspapers.stream()
                .filter(n -> n.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Client> getSubscribers(String newspaperTitle) {
        List<Client> result = new ArrayList<>();
        for (Client client : clients) {
            if (client.getNewspaper() != null && client.getNewspaper().equals(newspaperTitle)) {
                result.add(client);
            }
        }
        return result;
    }
    
    @Override
    public int getSubscriberCount(String newspaperTitle) {
        int count = 0;
        for (Client client : clients) {
            if (client.getNewspaper() != null && client.getNewspaper().equals(newspaperTitle)) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public List<String> getPopularNewspapers(int limit) {
        Map<String, Integer> newspaperCounts = new HashMap<>();
        for (Client client : clients) {
            String newspaper = client.getNewspaper();
            if (newspaper != null && !newspaper.isEmpty()) {
                newspaperCounts.put(newspaper, 
                    newspaperCounts.getOrDefault(newspaper, 0) + 1);
            }
        }
        
        return newspaperCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
    }
    
    // === Реализация PostmanDataManager ===
    @Override
    public List<Postman> getPostmen() {
        return new ArrayList<>(postmen);
    }
    
    @Override
    public void addPostman(Postman postman) {
        postman.setId(nextPostmanId++);
        postmen.add(postman);
    }
    
    @Override
    public void updatePostman(Postman postman) {
        for (int i = 0; i < postmen.size(); i++) {
            if (postmen.get(i).getId() == postman.getId()) {
                postmen.set(i, postman);
                break;
            }
        }
    }
    
    @Override
    public void deletePostman(int id) {
        postmen.removeIf(p -> p.getId() == id);
    }
    
    @Override
    public Postman getPostmanById(int id) {
        return postmen.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Postman> getPostmenByDistrict(String district) {
        List<Postman> result = new ArrayList<>();
        for (Postman postman : postmen) {
            if (district != null && district.equals(postman.getDistrict())) {
                result.add(postman);
            }
        }
        return result;
    }
    
    @Override
    public List<Client> getAssignedClients(int postmanId) {
        return getClientsByPostman(postmanId);
    }
    
    @Override
    public int getClientCount(int postmanId) {
        return getClientsByPostman(postmanId).size();
    }
    
    // === Реализация ClientDataManager ===
    @Override
    public List<Client> getClients() {
        return new ArrayList<>(clients);
    }
    
    @Override
    public void addClient(Client client) {
        client.setId(nextClientId++);
        clients.add(client);
    }
    
    @Override
    public void updateClient(Client client) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == client.getId()) {
                clients.set(i, client);
                break;
            }
        }
    }
    
    @Override
    public void deleteClient(int id) {
        clients.removeIf(c -> c.getId() == id);
    }
    
    @Override
    public Client getClientById(int id) {
        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Client> getClientsByDistrict(String district) {
        List<Client> result = new ArrayList<>();
        for (Client client : clients) {
            if (district != null && district.equals(client.getDistrict())) {
                result.add(client);
            }
        }
        return result;
    }
    
    @Override
    public List<Client> getClientsByNewspaper(String newspaper) {
        List<Client> result = new ArrayList<>();
        for (Client client : clients) {
            if (newspaper != null && newspaper.equals(client.getNewspaper())) {
                result.add(client);
            }
        }
        return result;
    }
    
    @Override
    public List<Client> getClientsByPostman(int postmanId) {
        List<Client> result = new ArrayList<>();
        for (Client client : clients) {
            if (client.getPostmanId() != null && client.getPostmanId() == postmanId) {
                result.add(client);
            }
        }
        return result;
    }
    
    // === Реализация DistrictDataManager ===
    @Override
    public List<District> getDistricts() {
        return new ArrayList<>(districts);
    }
    
    @Override
    public void addDistrict(District district) {
        district.setId(nextDistrictId++);
        districts.add(district);
    }
    
    @Override
    public void updateDistrict(District district) {
        for (int i = 0; i < districts.size(); i++) {
            if (districts.get(i).getId() == district.getId()) {
                districts.set(i, district);
                break;
            }
        }
    }
    
    @Override
    public void deleteDistrict(int id) {
        districts.removeIf(d -> d.getId() == id);
    }
    
    @Override
    public District getDistrictById(int id) {
        return districts.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public District getDistrictByName(String name) {
        return districts.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<District> getDistrictsByPostmanCount(int minPostmen) {
        List<District> result = new ArrayList<>();
        for (District district : districts) {
            if (district.getPostmanCount() >= minPostmen) {
                result.add(district);
            }
        }
        return result;
    }
    
    // === Дополнительные методы ===
    public Map<String, Object> getPostmanChain(int postmanId) {
        Map<String, Object> chain = new HashMap<>();
        
        Postman postman = getPostmanById(postmanId);
        if (postman == null) return chain;
        
        chain.put("postman", postman);
        
        List<Client> assignedClients = getClientsByPostman(postmanId);
        chain.put("clients", assignedClients);
        
        Map<String, Integer> newspaperStats = new HashMap<>();
        for (Client client : assignedClients) {
            String newspaper = client.getNewspaper();
            if (newspaper != null && !newspaper.isEmpty()) {
                newspaperStats.put(newspaper, newspaperStats.getOrDefault(newspaper, 0) + 1);
            }
        }
        chain.put("newspaperDistribution", newspaperStats);
        
        return chain;
    }
    
    public Map<String, Object> getNewspaperChain(String newspaperTitle) {
        Map<String, Object> chain = new HashMap<>();
        
        List<Client> subscribers = getClientsByNewspaper(newspaperTitle);
        chain.put("subscribers", subscribers);
        
        return chain;
    }
    
    public int getClientCount() {
        return clients.size();
    }
    
    public int getNewspaperCount() {
        return newspapers.size();
    }
    
    public int getPostmanCount() {
        return postmen.size();
    }
    
    public int getDistrictCount() {
        return districts.size();
    }
    
    public void autoAssignClientsToPostmen() {
        // Простая логика автораспределения
        for (Client client : clients) {
            if (client.getPostmanId() == null && client.getDistrict() != null) {
                List<Postman> postmenInDistrict = getPostmenByDistrict(client.getDistrict());
                if (!postmenInDistrict.isEmpty()) {
                    // Назначаем первого попавшегося почтальона из района
                    client.setPostmanId(postmenInDistrict.get(0).getId());
                }
            }
        }
    }
    
    // === Загрузка тестовых данных ===
    public void loadTestData() {
        // Очистка старых данных
        newspapers.clear();
        postmen.clear();
        clients.clear();
        districts.clear();
        
        nextNewspaperId = 1;
        nextPostmanId = 1;
        nextClientId = 1;
        nextDistrictId = 1;
        
        // Тестовые данные районов
        District district1 = new District(0, "Центральный", "Центр города", "100001", 25000, 5);
        District district2 = new District(0, "Северный", "Спальный район", "100002", 45000, 12);
        District district3 = new District(0, "Южный", "Промышленная зона", "100003", 32000, 18);
        
        addDistrict(district1);
        addDistrict(district2);
        addDistrict(district3);
        
        // Тестовые данные почтальонов
        Postman postman1 = new Postman(0, "Иванов", "Петр", "Сергеевич", 
            "111-22-33", "Центральный", 5, "5/2 (пн-пт 8:00-17:00)");
        Postman postman2 = new Postman(0, "Петрова", "Мария", "Ивановна", 
            "444-55-66", "Северный", 3, "Сменный график");
        Postman postman3 = new Postman(0, "Сидоров", "Алексей", "Петрович", 
            "777-88-99", "Центральный", 7, "6/1 (вс - выходной)");
        Postman postman4 = new Postman(0, "Козлов", "Дмитрий", "Александрович", 
            "123-45-67", "Южный", 2, "5/2 (пн-пт 8:00-17:00)");
        
        addPostman(postman1);
        addPostman(postman2);
        addPostman(postman3);
        addPostman(postman4);
        
        // Тестовые данные газет
        addNewspaper(new Newspaper(0, "Почтовый вестник", "Ежедневно"));
        addNewspaper(new Newspaper(0, "Новости города", "Понедельник"));
        addNewspaper(new Newspaper(0, "Спортивные известия", "Вторник"));
        addNewspaper(new Newspaper(0, "Техника молодежи", "Раз в месяц"));
        addNewspaper(new Newspaper(0, "Вечерняя почта", "Ежедневно"));
        
        // Тестовые данные клиентов
        Client client1 = new Client(0, "Иванов", "111-11-11", "ул. Ленина 10", 
            "Почтовый вестник", "Центральный", postman1.getId());
        Client client2 = new Client(0, "Петров", "222-22-22", "ул. Советская 25", 
            "Новости города", "Центральный", postman1.getId());
        Client client3 = new Client(0, "Сидорова", "333-33-33", "ул. Мира 15", 
            "Спортивные известия", "Центральный", postman3.getId());
        Client client4 = new Client(0, "Кузнецов", "444-44-44", "ул. Северная 5", 
            "Техника молодежи", "Северный", postman2.getId());
        Client client5 = new Client(0, "Николаев", "555-55-55", "ул. Южная 20", 
            "Почтовый вестник", "Южный", postman4.getId());
        
        addClient(client1);
        addClient(client2);
        addClient(client3);
        addClient(client4);
        addClient(client5);
        
        // Устанавливаем связи в районах
        district1.addPostmanId(postman1.getId());
        district1.addPostmanId(postman3.getId());
        district2.addPostmanId(postman2.getId());
        district3.addPostmanId(postman4.getId());
    }
}