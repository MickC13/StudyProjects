#pragma once
#include <iostream>
#include <vector>
#include "Product.h"
class Order {
    std::vector<Product> items;
    bool isPaid = false;

    Order(const std::vector<Product>& products) : items(products) {}
    void pay();
    void display();
};