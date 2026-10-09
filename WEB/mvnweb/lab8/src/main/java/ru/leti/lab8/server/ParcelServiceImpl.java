package ru.leti.lab8.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import ru.leti.lab8.client.ParcelService;
import ru.leti.lab8.shared.Parcel;

/**
 * Серверная реализация RPC-сервиса почтового отделения.
 */
public class ParcelServiceImpl extends RemoteServiceServlet implements ParcelService {
    private static final long serialVersionUID = 1L;

    private final Map<String, List<Parcel>> parcelsByClient = createData();

    /** @return список клиентов в порядке добавления */
    @Override
    public List<String> getClientList() {
        return new ArrayList<String>(parcelsByClient.keySet());
    }

    /**
     * Ищет посылки клиента в учебном наборе данных.
     *
     * @param clientName ФИО клиента
     * @return копия списка посылок клиента
     * @throws IllegalArgumentException если такого клиента нет
     */
    @Override
    public List<Parcel> getParcels(String clientName) throws IllegalArgumentException {
        List<Parcel> parcels = parcelsByClient.get(clientName);
        if (parcels == null) {
            throw new IllegalArgumentException("Клиент не найден: " + clientName);
        }
        return new ArrayList<Parcel>(parcels);
    }

    /** Создаёт данные, аналогичные списку посылок лабораторной работы № 3. */
    private Map<String, List<Parcel>> createData() {
        Map<String, List<Parcel>> data = new LinkedHashMap<String, List<Parcel>>();

        List<Parcel> ivanov = new ArrayList<Parcel>();
        ivanov.add(new Parcel("RA123456789RU", "ООО «Техносклад»",
                "Ожидает выдачи", "10.09.2026"));
        ivanov.add(new Parcel("RA987654321RU", "Петров П.С.",
                "Выдана", "05.09.2026"));
        data.put("Иванов И. И.", ivanov);

        List<Parcel> petrova = new ArrayList<Parcel>();
        petrova.add(new Parcel("RB111222333RU", "Интернет-магазин «Книга»",
                "В пути", "12.09.2026"));
        petrova.add(new Parcel("RB444555666RU", "Сидоров А.А.",
                "Ожидает выдачи", "14.09.2026"));
        data.put("Петрова А. С.", petrova);

        List<Parcel> sidorov = new ArrayList<Parcel>();
        sidorov.add(new Parcel("RC777888999RU", "АО «Электроника»",
                "Выдана", "01.09.2026"));
        data.put("Сидоров С. С.", sidorov);

        return data;
    }
}
