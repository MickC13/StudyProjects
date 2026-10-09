package ru.leti.lab8.client;

import java.util.List;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import ru.leti.lab8.shared.Parcel;

/**
 * Точка входа клиентской части GWT-приложения со списком посылок.
 */
public class ParcelModule implements EntryPoint {
    private static final String SERVER_ERROR = "Ошибка обращения к серверу.";

    private final ParcelServiceAsync parcelService = GWT.create(ParcelService.class);

    /**
     * Создаёт элементы интерфейса и загружает список клиентов через GWT-RPC.
     */
    @Override
    public void onModuleLoad() {
        final ListBox clientList = new ListBox(false);
        final Label errorLabel = new Label();
        final Button showButton = new Button("Показать посылки");

        RootPanel.get("clientListContainer").add(clientList);
        RootPanel.get("showButtonContainer").add(showButton);
        RootPanel.get("errorLabelContainer").add(errorLabel);

        final CellTable<Parcel> table = createParcelTable();
        final DialogBox dialog = createResultDialog(table, showButton);

        showButton.setEnabled(false);
        parcelService.getClientList(new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                errorLabel.setText(SERVER_ERROR + " Невозможно получить список клиентов.");
            }

            @Override
            public void onSuccess(List<String> clients) {
                for (String client : clients) {
                    clientList.addItem(client);
                }
                showButton.setEnabled(!clients.isEmpty());
                clientList.setFocus(true);
            }
        });

        showButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                errorLabel.setText("");
                showButton.setEnabled(false);
                final String clientName = clientList.getSelectedValue();

                parcelService.getParcels(clientName, new AsyncCallback<List<Parcel>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        errorLabel.setText(SERVER_ERROR + " Невозможно получить список посылок.");
                        showButton.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(List<Parcel> parcels) {
                        table.setRowCount(parcels.size(), true);
                        table.setRowData(0, parcels);
                        dialog.setText("Посылки клиента " + clientName);
                        dialog.center();
                    }
                });
            }
        });
    }

    /**
     * Создаёт диалоговое окно с таблицей результата.
     *
     * @param table таблица посылок
     * @param showButton кнопка основного окна
     * @return настроенное диалоговое окно
     */
    private DialogBox createResultDialog(CellTable<Parcel> table, final Button showButton) {
        final DialogBox dialog = new DialogBox();
        final Button closeButton = new Button("Закрыть");
        VerticalPanel panel = new VerticalPanel();

        panel.setSpacing(10);
        panel.add(table);
        panel.add(closeButton);
        dialog.setWidget(panel);
        dialog.setAnimationEnabled(true);
        dialog.setGlassEnabled(true);

        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialog.hide();
                showButton.setEnabled(true);
                showButton.setFocus(true);
            }
        });
        return dialog;
    }

    /** @return таблица с колонками, соответствующими лабораторной работе № 3 */
    private CellTable<Parcel> createParcelTable() {
        CellTable<Parcel> table = new CellTable<Parcel>();

        table.addColumn(new TextColumn<Parcel>() {
            @Override
            public String getValue(Parcel parcel) {
                return parcel.getTrackingNumber();
            }
        }, "Трек-номер");

        table.addColumn(new TextColumn<Parcel>() {
            @Override
            public String getValue(Parcel parcel) {
                return parcel.getSender();
            }
        }, "Отправитель");

        table.addColumn(new TextColumn<Parcel>() {
            @Override
            public String getValue(Parcel parcel) {
                return parcel.getStatus();
            }
        }, "Статус");

        table.addColumn(new TextColumn<Parcel>() {
            @Override
            public String getValue(Parcel parcel) {
                return parcel.getArrivalDate();
            }
        }, "Дата поступления");

        return table;
    }
}
