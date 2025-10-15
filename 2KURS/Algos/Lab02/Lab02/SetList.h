#pragma once

#include <iostream>

struct ListNode {
    char data;
    ListNode* next;
    static bool debug; // ���� ��� �������

    ListNode(char d, ListNode* n = nullptr) : data(d), next(n) {
        if (debug) std::cout << "  ListNode ������: " << data << std::endl;
    }

    // ���������� new/delete ��� ListNode
    static void* operator new(size_t size) {
        if (debug) std::cout << "  �������� ������ ��� ListNode (" << size << " ����)" << std::endl;
        return ::operator new(size);
    }

    static void operator delete(void* ptr) {
        if (debug) std::cout << "  ����������� ������ ListNode" << std::endl;
        ::operator delete(ptr);
    }

    ~ListNode() {
        if (debug) std::cout << "  ListNode ���������: " << data << std::endl;
    }
};

bool ListNode::debug = true;

class SetList {
private:
    static int N, cnt;
    int n;
    char S;
    ListNode* head;
    static bool debug; // ���� ��� �������

public:
    SetList() : n(0), S('A' + (cnt++ % 26)), head(nullptr) {
        if (debug) std::cout << "SetList ������: " << S << " (����������� �� ���������)" << std::endl;
    }

    SetList(const SetList& other) : n(0), S('A' + (cnt++ % 26)), head(nullptr) {
        if (debug) std::cout << "SetList ������: " << S << " (����������� ����������� �� " << other.S << ")" << std::endl;
        ListNode* current = other.head;
        while (current) {
            add(current->data);
            current = current->next;
        }
    }

    SetList(const char* str) : n(0), S('A' + (cnt++ % 26)), head(nullptr) {
        if (debug) std::cout << "SetList ������: " << S << " (�� ������ \"" << (str ? str : "null") << "\")" << std::endl;
        if (str) {
            for (int i = 0; str[i]; i++) {
                if (str[i] >= 'A' && str[i] <= 'Z') {
                    add(str[i]);
                }
            }
        }
    }

    ~SetList() {
        if (debug) std::cout << "SetList ������������: " << S << std::endl;
        clear();
    }

    SetList& operator=(const SetList& other) {
        if (debug) std::cout << "SetList �������� ������������: " << S << " = " << other.S << std::endl;
        if (this != &other) {
            clear();
            ListNode* current = other.head;
            while (current) {
                add(current->data);
                current = current->next;
            }
        }
        return *this;
    }

    SetList operator&(const SetList& other) const {
        if (debug) std::cout << "SetList �������� &: " << S << " & " << other.S << std::endl;
        SetList result;
        ListNode* current = head;
        while (current) {
            if (other.contains(current->data)) {
                result.add(current->data);
            }
            current = current->next;
        }
        return result;
    }

    SetList operator|(const SetList& other) const {
        if (debug) std::cout << "SetList �������� |: " << S << " | " << other.S << std::endl;
        SetList result = *this;
        ListNode* current = other.head;
        while (current) {
            if (!result.contains(current->data)) {
                result.add(current->data);
            }
            current = current->next;
        }
        return result;
    }

    SetList operator~() const {
        if (debug) std::cout << "SetList �������� ~: ~" << S << std::endl;
        SetList result;
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!contains(c)) {
                result.add(c);
            }
        }
        return result;
    }

    void Show() const {
        std::cout << S << " = {";
        ListNode* current = head;
        while (current) {
            std::cout << current->data;
            if (current->next) std::cout << ", ";
            current = current->next;
        }
        std::cout << "}" << std::endl;
    }

    int power() const {
        if (debug) std::cout << "SetList power(): " << S << " = " << n << std::endl;
        return n;
    }

    // ����������� ����� ��� ������ ��������
    static void ResetCounter() {
        if (debug) std::cout << "SetList: ������� �������" << std::endl;
        cnt = 0;
    }

    // ���������� new/delete ��� SetList
    static void* operator new(size_t size) {
        if (debug) std::cout << "�������� ������ ��� SetList (" << size << " ����)" << std::endl;
        return ::operator new(size);
    }

    static void operator delete(void* ptr) {
        if (debug) std::cout << "����������� ������ SetList" << std::endl;
        ::operator delete(ptr);
    }

    // ����� ��� ���������/���������� �������
    static void SetDebug(bool state) {
        debug = state;
        ListNode::debug = state;
    }

private:
    void add(char c) {
        if (contains(c)) return;
        head = new ListNode(c, head);
        n++;
    }

    bool contains(char c) const {
        ListNode* current = head;
        while (current) {
            if (current->data == c) return true;
            current = current->next;
        }
        return false;
    }

    void clear() {
        while (head) {
            ListNode* temp = head;
            head = head->next;
            delete temp;
        }
        n = 0;
    }
};

int SetList::N = 26;
int SetList::cnt = 0;
bool SetList::debug = true;