#pragma once
#include "Product.h"
class Clothing: public Product {
  
    std::string m_Size;

    Clothing(const std::string& name, double price, const std::string& size)
        : Product(name, price), m_Size(size) {}


    std::string const& size() const { return m_Size; }

    void display();
};