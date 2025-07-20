#pragma once
#include <iostream>
#include <vector>
#include "Product.h"
#include <vector>

class Cart {
    std::vector<Product*> m_Items;
public:  
    void addItem(Product* product);
    void display();
};

