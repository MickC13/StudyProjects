
#include "Books.h"
void Books::display() {
    std::cout << "�����: " << getName()
        << " | �����: " << author()
        << " | ����: " << getPrice() << " ���." << std::endl;
}