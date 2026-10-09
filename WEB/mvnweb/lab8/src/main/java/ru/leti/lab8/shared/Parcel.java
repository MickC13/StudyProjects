package ru.leti.lab8.shared;

import java.io.Serializable;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Данные одной почтовой посылки, передаваемые между сервером и GWT-клиентом.
 */
public class Parcel implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    private String trackingNumber;
    private String sender;
    private String status;
    private String arrivalDate;

    /** Обязательный конструктор без параметров для GWT-RPC. */
    public Parcel() {
    }

    /**
     * Создаёт запись о посылке.
     *
     * @param trackingNumber трек-номер
     * @param sender отправитель
     * @param status текущий статус
     * @param arrivalDate дата поступления
     */
    public Parcel(String trackingNumber, String sender, String status, String arrivalDate) {
        this.trackingNumber = trackingNumber;
        this.sender = sender;
        this.status = status;
        this.arrivalDate = arrivalDate;
    }

    /** @return трек-номер посылки */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /** @param trackingNumber новый трек-номер */
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    /** @return наименование отправителя */
    public String getSender() {
        return sender;
    }

    /** @param sender новый отправитель */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /** @return текущий статус посылки */
    public String getStatus() {
        return status;
    }

    /** @param status новый статус */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return дата поступления посылки */
    public String getArrivalDate() {
        return arrivalDate;
    }

    /** @param arrivalDate новая дата поступления */
    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
}
