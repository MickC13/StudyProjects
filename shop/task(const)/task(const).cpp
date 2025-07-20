#include "Customer.h"
#include "Menu.h"
#include "Books.h"
#include "Clothing.h"
#include "Electronics.h"
#include <vector>
#include "Customer.h"
int main()
{
	Customer customer("Adam Baranov ");
	int choice;
	std::vector<Books> books = {
	Books("1984", 350.0, "George Orwell"),
	Books("Преступление и наказание", 280.0, "Ф.М. Достоевский")
	};

	std::vector<Clothing> clothes = {
	  Clothing("Футболка", 1200.0, "L"),
	  Clothing("Джинсы", 3500.0, "M")
	};

	std::vector<Electronics> electronics = {
	  Electronics("Смартфон", 29999.0, 24),
	  Electronics("Наушники", 5990.0, 12)
	};

	std::vector<Product*> allProducts;

	for (size_t i = 0; i < books.size(); i++) {
		allProducts.push_back(&books[i]);
	}


	for (size_t i = 0; i < clothes.size(); i++) {
		allProducts.push_back(&clothes[i]);
	}

	
	for (size_t i = 0; i < electronics.size(); i++) {
		allProducts.push_back(&electronics[i]);
	}

	Menu mainMenu("Главное меню", {	
		"Просмотреть товары",
		"Добавить товар в корзину",
		"Просмотреть корзину",
		"Оформить заказ",
		"Выход"
		});

	while (true) {
		choice = mainMenu.process();
		switch (choice) {
		case 1: 
		{
			std::list<std::string> categoryItems;
			categoryItems.push_back("Книги");
			categoryItems.push_back("Одежда");
			categoryItems.push_back("Электроника");
			categoryItems.push_back("Назад");

			Menu categoryMenu("Категории товаров", {
				"Книги",
				"Одежда",
				"Электроника",
				"Назад"
				});
			int categoryChoice = categoryMenu.process();
			switch (categoryChoice) {

			}

			switch (categoryChoice) {
			case 1: 
				for (size_t i = 0; i < books.size(); i++) {
					books[i].display();
				}
				break;
			case 2: 
				for (size_t i = 0; i < clothes.size(); i++) {
					clothes[i].display();
				}
				break;
			case 3:
				for (size_t i = 0; i < electronics.size(); i++) {
					electronics[i].display();
				}
				break;
			}
			break;
		}
		case 2: 
		{
			
			std::list<std::string> productNames;
			for (size_t i = 0; i < allProducts.size(); i++) {
				productNames.push_back(allProducts[i]->getName());
			}

			Menu productMenu("Выберите товар", productNames);
			int productChoice = productMenu.process();
			customer.addToCart(allProducts[productChoice - 1]);
			std::cout << "\nТовар добавлен в корзину!\n";
			break;
		}
		case 3: 
			customer.viewCart();
			break;
		case 4: 
			std::cout << "\nЗаказ оформлен! Спасибо за покупку!\n";
			break;
		case 5: 
			return 0;
		}
	}

}



