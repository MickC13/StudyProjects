
#include "Clothing.h"
void Clothing:: display() {
    std::cout << "Одежда: " << getName()
        << " | Размер: " << size()
        << " | Цена: " << getPrice() << " руб." << std::endl;
}