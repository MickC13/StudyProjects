#pragma once

#include <iostream>
#include <list>
#include "Product_list.h"



ProductList::~ProductList() {
    for (auto* item : m_Items) {
        delete item;
    }
}

void ProductList::addItem(Product* product) {
    m_Items.push_back(product);
}

void ProductList::removeItem() {
    if (index >= 0 && static_cast<size_t>(index) < m_Items.size()) {
        delete m_Items[index];     
        m_Items.erase(m_Items.begin() + index); 
    }
}

void ProductList::addItem(Product* product) {
    m_Items.push_back(product);
}

void ProductList::display() {
    std::cout << "\nСодержимое списка продуктов:";
    for (size_t i = 0; i < m_Items.size(); ++i) {
        std::cout << "\n" << i + 1 << ". ";
        m_Items[i]->display();
    }
}






