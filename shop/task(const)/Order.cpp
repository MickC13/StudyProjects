#include "Order.h"


bool isPaid = false;
void Order:: pay() {
    isPaid = true;
    std::cout << "\n����� ������� �������!";
}

void Order:: display() {
    std::cout << "\n\n���������� � ������:";
    for (size_t i = 0; i < items.size(); ++i) {
        std::cout << "\n" << i + 1 << ". ";
        items[i].display();
    }
}