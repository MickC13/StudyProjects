#pragma once
#include "Product.h"
class Books: public Product {

    std::string m_Author;

    Books(const std::string& name, double price, const std::string& author)
        : Product(name, price), m_Author(author) {}

   
    std::string const& author() const { return m_Author; }

    void display();
};
