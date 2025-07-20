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
    std::cout << "Товар: " << m_Name << " | Цена: " << m_Price << " руб.";
}