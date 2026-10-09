package ru.leti.lab8.client;

import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.leti.lab8.shared.Parcel;

/** Асинхронная версия RPC-интерфейса, используемая браузерным клиентом. */
public interface ParcelServiceAsync {

    /** @param callback обработчик списка клиентов или ошибки */
    void getClientList(AsyncCallback<List<String>> callback);

    /**
     * Запрашивает посылки без блокировки пользовательского интерфейса.
     *
     * @param clientName ФИО выбранного клиента
     * @param callback обработчик списка посылок или ошибки
     */
    void getParcels(String clientName, AsyncCallback<List<Parcel>> callback);
}
