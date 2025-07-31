#pragma once

#include "Product_list.h" 

class Order : public ProductList { 
    bool isPaid = false;

public:
    void pay();
    void display() const override; 
};