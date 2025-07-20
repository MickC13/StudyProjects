#pragma once
#include <iostream>
#include <list>
#include <string>
// list of strings, display strings, add, getChoice( private check input)
//��������� ������� ����(list ��� vector)

// process �������,������� ������� ���� � ��������� ���� ��� �����
// ���������� �� �������� � ������� �� vector � list

    class Menu {
    private:
        std::list<std::string> m_Chran;  
        std::string m_Title;             
                           

  
        bool isValid(unsigned int choice) {
            return (choice > 0 && choice <= m_Chran.size());
        }

    public:
        
        Menu(const std::string& title, const std::list<std::string>& items)
            : m_Title(title), m_Chran(items) {}


        void add(const std::string& item) {
            m_Chran.push_back(item);
        }


        void display() {
            std::cout << "\n--- " << m_Title << " ---\n";
            int counter = 1;
           
            for (auto it = m_Chran.begin(); it != m_Chran.end(); ++it) {
                std::cout << counter << ". " << *it << '\n';
                ++counter;
            }
            
        }


        int getChoice() {
            int choice;
            while (true) {
                std::cout << "> ������� ����� ������: ";
                std::cin >> choice;

                if (std::cin.fail()) {
                    std::cin.clear();
                    std::cin.ignore(10000, '\n');
                    std::cout << "������! ������� �����.\n";
                    continue;
                }

                if (isValid(choice)) {
                    return choice;
                }

                std::cout << "�������� �����! ������� ����� �� 1 �� " << m_Chran.size() << ".\n";
            }
        }

        int process(){
            display();       
            return getChoice();
        }
    };