#include <iostream>
#include "Order.h"

void Order::pay() {
    isPaid = true;
    std::cout << "\n����� �������!";
}


void Order::display() const {
    if (isPaid) {
        std::cout << "\n\n���������� �����:";
    }
    else {
        std::cout << "\n\n������������ �����:";
    }


    ProductList::display();
}