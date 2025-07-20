#include "Order.h"


bool isPaid = false;
void Order:: pay() {
    isPaid = true;
    std::cout << "\nЗаказ оплачен успешно!";
}

void Order:: display() {
    std::cout << "\n\nИнформация о заказе:";
    for (size_t i = 0; i < items.size(); ++i) {
        std::cout << "\n" << i + 1 << ". ";
        items[i].display();
    }
}