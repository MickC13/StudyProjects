#pragma once
#include <iostream>

class SetArray {
private:
    static int N, cnt;
    int n;
    char S;
    char* A;
    static bool debug; // Флаг для отладки

public:
    // Конструкторы
    SetArray() : n(0), S('A' + (cnt++ % 26)), A(new char[N + 1]) {
        A[0] = 0;
        if (debug) std::cout << "SetArray создан: " << S << " (конструктор по умолчанию)" << std::endl;
    }

    SetArray(const SetArray& other) : n(other.n), S('A' + (cnt++ % 26)), A(new char[N + 1]) {
        if (debug) std::cout << "SetArray создан: " << S << " (конструктор копирования из " << other.S << ")" << std::endl;
        for (int i = 0; i <= other.n; i++) {
            A[i] = other.A[i];
        }
    }

    SetArray(const char* str) : n(0), S('A' + (cnt++ % 26)), A(new char[N + 1]) {
        if (debug) std::cout << "SetArray создан: " << S << " (из строки \"" << (str ? str : "null") << "\")" << std::endl;
        A[0] = 0;
        if (str) {
            for (int i = 0; str[i]; i++) {
                if (str[i] >= 'A' && str[i] <= 'Z') {
                    bool found = false;
                    for (int j = 0; j < n; j++) {
                        if (A[j] == str[i]) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) A[n++] = str[i];
                }
            }
            A[n] = 0;
        }
    }

    // Деструктор
    ~SetArray() {
        if (debug) std::cout << "SetArray уничтожается: " << S << std::endl;
        delete[] A;
    }

    // Оператор присваивания
    SetArray& operator=(const SetArray& other) {
        if (debug) std::cout << "SetArray оператор присваивания: " << S << " = " << other.S << std::endl;
        if (this != &other) {
            n = other.n;
            for (int i = 0; i <= other.n; i++) {
                A[i] = other.A[i];
            }
        }
        return *this;
    }

    // Операции с множествами
    SetArray operator&(const SetArray& other) const {
        if (debug) std::cout << "SetArray оператор &: " << S << " & " << other.S << std::endl;
        SetArray result;
        for (int i = 0; i < n; i++) {
            bool found = false;
            for (int j = 0; j < other.n; j++) {
                if (A[i] == other.A[j]) {
                    found = true;
                    break;
                }
            }
            if (found) result.A[result.n++] = A[i];
        }
        result.A[result.n] = 0;
        return result;
    }

    SetArray operator|(const SetArray& other) const {
        if (debug) std::cout << "SetArray оператор |: " << S << " | " << other.S << std::endl;
        SetArray result = *this;
        for (int i = 0; i < other.n; i++) {
            bool found = false;
            for (int j = 0; j < result.n; j++) {
                if (result.A[j] == other.A[i]) {
                    found = true;
                    break;
                }
            }
            if (!found) result.A[result.n++] = other.A[i];
        }
        result.A[result.n] = 0;
        return result;
    }

    SetArray operator~() const {
        if (debug) std::cout << "SetArray оператор ~: ~" << S << std::endl;
        SetArray result;
        for (char c = 'A'; c <= 'Z'; c++) {
            bool found = false;
            for (int i = 0; i < n; i++) {
                if (A[i] == c) {
                    found = true;
                    break;
                }
            }
            if (!found) result.A[result.n++] = c;
        }
        result.A[result.n] = 0;
        return result;
    }

    // Методы
    void Show() const {
        std::cout << S << " = {";
        for (int i = 0; i < n; i++) {
            std::cout << A[i];
            if (i < n - 1) std::cout << ", ";
        }
        std::cout << "}" << std::endl;
    }

    int power() const {
        if (debug) std::cout << "SetArray power(): " << S << " = " << n << std::endl;
        return n;
    }

    // Статический метод для сброса счетчика
    static void ResetCounter() {
        if (debug) std::cout << "SetArray: счетчик сброшен" << std::endl;
        cnt = 0;
    }

    // Метод для включения/выключения отладки
    static void SetDebug(bool state) {
        debug = state;
    }
};

int SetArray::N = 26;
int SetArray::cnt = 0;
bool SetArray::debug = true;