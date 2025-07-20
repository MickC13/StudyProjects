
#include "Electronics.h"



void Electronics:: display() const {
    std::cout << "Электроника: " << getName()
        << " | Цена: " << getPrice()
        << " | Гарантия: " << warranty() << " мес." << std::endl;

}