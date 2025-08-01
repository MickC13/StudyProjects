#include <iostream>
#include "Order.h"

void Order::pay() {
    isPaid = true;
    std::cout << "\nЗаказ оплачен!";
}


void Order::display() const {
    if (isPaid) {
        std::cout << "\n\nОплаченный заказ:";
    }
    else {
        std::cout << "\n\nНеоплаченный заказ:";
    }


    ProductList::display();
}