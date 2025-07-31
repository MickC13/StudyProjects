#pragma once

#include <iostream>
#include <list>
#include "Product.h"

class ProductList {
    std::list<Product*> m_Items;
public:
    virtual ~ProductList();
    void addItem(Product* product);
    void removeItem(int index);
    virtual void display();
};


