#include "Product.h"

std::string Product:: getName() const 
{
    return m_Name; 
}
double Product:: getPrice() const 
{ return m_Price; 
}

void Product::display()
{
    std::cout << "�����: " << m_Name << " | ����: " << m_Price << " ���.";
}