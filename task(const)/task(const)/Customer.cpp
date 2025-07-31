#include "Customer.h"
#include "Cart.h"


void Customer::addToCart(Product* product) {
    m_Cart.addItem(product);
}

void Customer:: viewCart() {
    m_Cart.display();
}

