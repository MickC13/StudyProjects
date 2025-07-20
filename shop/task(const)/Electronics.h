#pragma once
#include "Product.h"

class Electronics : public Product {
    unsigned m_Warranty;

    Electronics(const std::string& name, double price, unsigned warranty)
        : Product(name, price), m_Warranty(warranty) {}

    unsigned warranty() const { return m_Warranty; }

    virtual void display() const;
};



