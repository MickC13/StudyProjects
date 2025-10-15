#pragma once

#include <iostream>

class SetWord {
private:
    static int N, cnt;
    char S;
    unsigned int word;
    static bool debug; // ���� ��� �������

public:
    SetWord() : S('A' + (cnt++ % 26)), word(0) {
        if (debug) std::cout << "SetWord ������: " << S << " (����������� �� ���������)" << std::endl;
    }

    SetWord(const SetWord& other) : S('A' + (cnt++ % 26)), word(other.word) {
        if (debug) std::cout << "SetWord ������: " << S << " (����������� ����������� �� " << other.S << ")" << std::endl;
    }

    SetWord(const char* str) : S('A' + (cnt++ % 26)), word(0) {
        if (debug) std::cout << "SetWord ������: " << S << " (�� ������ \"" << (str ? str : "null") << "\")" << std::endl;
        if (str) {
            for (int i = 0; str[i]; i++) {
                if (str[i] >= 'A' && str[i] <= 'Z') {
                    word |= (1 << (str[i] - 'A'));
                }
            }
        }
    }

    ~SetWord() {
        if (debug) std::cout << "SetWord ������������: " << S << std::endl;
    }

    SetWord& operator=(const SetWord& other) {
        if (debug) std::cout << "SetWord �������� ������������: " << S << " = " << other.S << std::endl;
        if (this != &other) {
            word = other.word;
        }
        return *this;
    }

    SetWord operator&(const SetWord& other) const {
        if (debug) std::cout << "SetWord �������� &: " << S << " & " << other.S << std::endl;
        SetWord result;
        result.word = word & other.word;
        return result;
    }

    SetWord operator|(const SetWord& other) const {
        if (debug) std::cout << "SetWord �������� |: " << S << " | " << other.S << std::endl;
        SetWord result;
        result.word = word | other.word;
        return result;
    }

    SetWord operator~() const {
        if (debug) std::cout << "SetWord �������� ~: ~" << S << std::endl;
        SetWord result;
        result.word = ~word & 0x03FFFFFF;
        return result;
    }

    void Show() const {
        std::cout << S << " = {";
        bool first = true;
        for (int i = 0; i < 26; i++) {
            if (word & (1 << i)) {
                if (!first) std::cout << ", ";
                std::cout << char('A' + i);
                first = false;
            }
        }
        std::cout << "}" << std::endl;
    }

    int power() const {
        if (debug) std::cout << "SetWord power(): " << S << std::endl;
        int count = 0;
        for (int i = 0; i < 26; i++) {
            if (word & (1 << i)) count++;
        }
        return count;
    }

    // ����������� ����� ��� ������ ��������
    static void ResetCounter() {
        if (debug) std::cout << "SetWord: ������� �������" << std::endl;
        cnt = 0;
    }

    // ����� ��� ���������/���������� �������
    static void SetDebug(bool state) {
        debug = state;
    }
};

int SetWord::N = 26;
int SetWord::cnt = 0;
bool SetWord::debug = true;