#pragma once
#include <iostream>
#include <vector>
#include <memory>
#include <string>

class Product {

private:
    std::string m_Name;
    double m_Price;

public:
    Product(const std::string& name, double price)
        : m_Name(name), m_Price(price) {}

    virtual ~Product() {}

    std::string getName() const;
    double getPrice() const;

    virtual void display();
   
};
