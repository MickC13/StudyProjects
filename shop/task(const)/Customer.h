#pragma once
#include <iostream>
#include "Product.h"
#include "Cart.h"

class Customer {
    std::string m_Name;
    Cart m_Cart;
public:
    Customer(const std::string& name) : m_Name(name) {}
    void addToCart(Product* product);
    void viewCart();

    
};