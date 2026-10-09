package ru.leti.lab8.client;

import java.util.List;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.leti.lab8.shared.Parcel;

/**
 * Синхронный RPC-интерфейс получения клиентов и принадлежащих им посылок.
 */
@RemoteServiceRelativePath("parcelService")
public interface ParcelService extends RemoteService {

    /** @return список клиентов почтового отделения */
    List<String> getClientList();

    /**
     * Возвращает посылки выбранного клиента.
     *
     * @param clientName ФИО клиента
     * @return список его посылок
     * @throws IllegalArgumentException если клиент не найден
     */
    List<Parcel> getParcels(String clientName) throws IllegalArgumentException;
}
