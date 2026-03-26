#include <iostream>
#include <unordered_set>
#include <vector>
#include <algorithm>
#include <cstdlib>
#include <ctime>

using namespace std;

class MyContainer {
private:
    unordered_set<int> dataSet;
    vector<int> seq;

public:
    auto begin() { return seq.begin(); }
    auto end() { return seq.end(); }
    auto begin() const { return seq.begin(); }
    auto end() const { return seq.end(); }

    void insert(int x) {
        if (dataSet.insert(x).second) {
            seq.push_back(x);
        }
    }

    void print() const {
        for (int x : seq)
            cout << x << " ";
        cout << endl;
    }

    void clear() {
        seq.clear();
        dataSet.clear();
    }

    // операции
    MyContainer Union(const MyContainer& other) const {
        MyContainer res = *this;
        for (int x : other)
            res.insert(x);
        return res;
    }

    MyContainer Intersection(const MyContainer& other) const {
        MyContainer res;
        for (int x : seq)
            if (other.dataSet.count(x))
                res.insert(x);
        return res;
    }

    MyContainer SymmetricDifference(const MyContainer& other) const {
        MyContainer res;

        for (int x : seq)
            if (!other.dataSet.count(x))
                res.insert(x);

        for (int x : other)
            if (!dataSet.count(x))
                res.insert(x);

        return res;
    }

    MyContainer Difference(const MyContainer& other) const {
        MyContainer res;
        for (int x : seq)
            if (!other.dataSet.count(x))
                res.insert(x);
        return res;
    }

    void Concat(const MyContainer& other) {
        for (int x : other.seq) {
            seq.push_back(x);
            dataSet.insert(x);
        }
    }

    void Erase(int l, int r) {
        if (seq.empty()) return;

        l = max(0, l);
        r = min((int)seq.size() - 1, r);
        if (l > r) return;

        seq.erase(seq.begin() + l, seq.begin() + r + 1);

        dataSet.clear();
        for (int x : seq)
            dataSet.insert(x);
    }
};


// 🔹 ручной ввод
void inputContainer(MyContainer& c, const string& name) {
    int n, x;
    cout << "Введите количество элементов для " << name << ": ";
    cin >> n;

    cout << "Введите элементы:\n";
    for (int i = 0; i < n; i++) {
        cin >> x;
        c.insert(x);
    }
}

//  случайная генерация
void randomContainer(MyContainer& c, const string& name) {
    int n = rand() % 6 + 3; // от 3 до 8 элементов
    cout << name << " (random): ";

    for (int i = 0; i < n; i++) {
        int x = rand() % 10; // числа 0-9
        c.insert(x);
    }
    c.print();
}


int main() {
    setlocale(LC_ALL, "Russian");
    srand(time(0));

    MyContainer A, B, C, D, E;

    int mode;
    cout << "Выберите режим:\n";
    cout << "1 - ручной ввод\n";
    cout << "2 - случайная генерация\n";
    cout << "Ваш выбор: ";
    cin >> mode;

    if (mode == 1) {
        inputContainer(A, "A");
        inputContainer(B, "B");
        inputContainer(C, "C");
        inputContainer(D, "D");
        inputContainer(E, "E");
    }
    else {
        randomContainer(A, "A");
        randomContainer(B, "B");
        randomContainer(C, "C");
        randomContainer(D, "D");
        randomContainer(E, "E");
    }

    cout << "\n=== Исходные множества ===" << endl;
    cout << "A = "; A.print();
    cout << "B = "; B.print();
    cout << "C = "; C.print();
    cout << "D = "; D.print();
    cout << "E = "; E.print();

    cout << "\n=== Вычисляем выражение ===" << endl;
    cout << "(A ∪ ((B ⊕ C) ∩ D)) ∩ E\n" << endl;

    auto step1 = B.SymmetricDifference(C);
    cout << "[1] B ⊕ C: ";
    step1.print();

    auto step2 = step1.Intersection(D);
    cout << "[2] (B ⊕ C) ∩ D: ";
    step2.print();

    auto step3 = A.Union(step2);
    cout << "[3] A ∪ ...: ";
    step3.print();

    auto result = step3.Intersection(E);
    cout << "[4] RESULT: ";
    result.print();

    cout << "\n=== Последовательность ===" << endl;

    result.Concat(A);
    cout << "[5] Concat: ";
    result.print();

    result.Erase(1, 2);
    cout << "[6] Erase(1,2): ";
    result.print();

    return 0;
}