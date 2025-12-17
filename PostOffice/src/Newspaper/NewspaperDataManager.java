package Newspaper;

import java.util.List;
import OfficeOperator.Client;

public interface NewspaperDataManager {
    List<Newspaper> getNewspapers();
    void addNewspaper(Newspaper newspaper);
    void updateNewspaper(Newspaper newspaper);
    void deleteNewspaper(int id);
    Newspaper getNewspaperById(int id);
    
    // Добавляем методы для работы со связями
    List<Client> getSubscribers(String newspaperTitle);
    int getSubscriberCount(String newspaperTitle);
    List<String> getPopularNewspapers(int limit);
}