
#include "Books.h"
void Books::display() {
    std::cout << "Книга: " << getName()
        << " | Автор: " << author()
        << " | Цена: " << getPrice() << " руб." << std::endl;
}