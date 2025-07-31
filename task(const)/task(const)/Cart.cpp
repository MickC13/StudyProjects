#include "Cart.h"
#include <vector>

void Cart:: addItem(Product* product) {
    m_Items.push_back(product);
}

void Cart::display() {
    std::cout << "\nСодержимое корзины:";
    for (size_t i = 0; i < m_Items.size(); ++i) {
        std::cout << "\n" << i + 1 << ". ";
        m_Items[i]->display();
    }
}