
#include "Electronics.h"



void Electronics:: display() const {
    std::cout << "�����������: " << getName()
        << " | ����: " << getPrice()
        << " | ��������: " << warranty() << " ���." << std::endl;

}