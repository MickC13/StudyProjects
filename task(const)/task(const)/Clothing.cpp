
#include "Clothing.h"
void Clothing:: display() {
    std::cout << "������: " << getName()
        << " | ������: " << size()
        << " | ����: " << getPrice() << " ���." << std::endl;
}