#pragma once

#include <iostream>

class SetBool {
private:
    static int N, cnt;
    char S;
    bool elements[26];
    static bool debug; // Флаг для отладки

public:
    SetBool() : S('A' + (cnt++ % 26)) {
        if (debug) std::cout << "SetBool создан: " << S << " (конструктор по умолчанию)" << std::endl;
        for (int i = 0; i < 26; i++) {
            elements[i] = false;
        }
    }

    SetBool(const SetBool& other) : S('A' + (cnt++ % 26)) {
        if (debug) std::cout << "SetBool создан: " << S << " (конструктор копирования из " << other.S << ")" << std::endl;
        for (int i = 0; i < 26; i++) {
            elements[i] = other.elements[i];
        }
    }

    SetBool(const char* str) : S('A' + (cnt++ % 26)) {
        if (debug) std::cout << "SetBool создан: " << S << " (из строки \"" << (str ? str : "null") << "\")" << std::endl;
        for (int i = 0; i < 26; i++) {
            elements[i] = false;
        }
        if (str) {
            for (int i = 0; str[i]; i++) {
                if (str[i] >= 'A' && str[i] <= 'Z') {
                    elements[str[i] - 'A'] = true;
                }
            }
        }
    }

    ~SetBool() {
        if (debug) std::cout << "SetBool уничтожается: " << S << std::endl;
    }

    SetBool& operator=(const SetBool& other) {
        if (debug) std::cout << "SetBool оператор присваивания: " << S << " = " << other.S << std::endl;
        if (this != &other) {
            for (int i = 0; i < 26; i++) {
                elements[i] = other.elements[i];
            }
        }
        return *this;
    }

    SetBool operator&(const SetBool& other) const {
        if (debug) std::cout << "SetBool оператор &: " << S << " & " << other.S << std::endl;
        SetBool result;
        for (int i = 0; i < 26; i++) {
            result.elements[i] = elements[i] && other.elements[i];
        }
        return result;
    }

    SetBool operator|(const SetBool& other) const {
        if (debug) std::cout << "SetBool оператор |: " << S << " | " << other.S << std::endl;
        SetBool result;
        for (int i = 0; i < 26; i++) {
            result.elements[i] = elements[i] || other.elements[i];
        }
        return result;
    }

    SetBool operator~() const {
        if (debug) std::cout << "SetBool оператор ~: ~" << S << std::endl;
        SetBool result;
        for (int i = 0; i < 26; i++) {
            result.elements[i] = !elements[i];
        }
        return result;
    }

    void Show() const {
        std::cout << S << " = {";
        bool first = true;
        for (int i = 0; i < 26; i++) {
            if (elements[i]) {
                if (!first) std::cout << ", ";
                std::cout << char('A' + i);
                first = false;
            }
        }
        std::cout << "}" << std::endl;
    }

    int power() const {
        if (debug) std::cout << "SetBool power(): " << S << std::endl;
        int count = 0;
        for (int i = 0; i < 26; i++) {
            if (elements[i]) count++;
        }
        return count;
    }

    // Статический метод для сброса счетчика
    static void ResetCounter() {
        if (debug) std::cout << "SetBool: счетчик сброшен" << std::endl;
        cnt = 0;
    }

    // Метод для включения/выключения отладки
    static void SetDebug(bool state) {
        debug = state;
    }
};

int SetBool::N = 26;
int SetBool::cnt = 0;
bool SetBool::debug = true;