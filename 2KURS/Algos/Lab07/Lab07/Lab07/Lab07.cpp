#include <iostream>
#include <unordered_set>
#include <vector>
#include <algorithm>

using namespace std;

class MyContainer {
private:
    unordered_set<int> dataSet; // множество
    vector<int> seq;            // последовательность

public:
    // итераторы
    auto begin() { return seq.begin(); }
    auto end() { return seq.end(); }
    auto begin() const { return seq.begin(); }
    auto end() const { return seq.end(); }

    // вставка (как множество)
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

    int size() const {
        return seq.size();
    }

    // ОБЪЕДИНЕНИЕ
    MyContainer Union(const MyContainer& other) const {
        MyContainer res = *this;
        for (int x : other)
            res.insert(x);
        return res;
    }

    // ПЕРЕСЕЧЕНИЕ
    MyContainer Intersection(const MyContainer& other) const {
        MyContainer res;
        for (int x : seq)
            if (other.dataSet.count(x))
                res.insert(x);
        return res;
    }

    // XOR
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

    // РАЗНОСТЬ
    MyContainer Difference(const MyContainer& other) const {
        MyContainer res;
        for (int x : seq)
            if (!other.dataSet.count(x))
                res.insert(x);
        return res;
    }

    //  concat сохраняет повторы
    void Concat(const MyContainer& other) {
        for (int x : other.seq) {
            seq.push_back(x);        // добавляем ВСЕ
            dataSet.insert(x);       // множество обновляем
        }
    }

    // ERASE
    void Erase(int l, int r) {
        if (seq.empty()) return;

        l = max(0, l);
        r = min((int)seq.size() - 1, r);
        if (l > r) return;

        seq.erase(seq.begin() + l, seq.begin() + r + 1);

        // пересобираем множество
        dataSet.clear();
        for (int x : seq)
            dataSet.insert(x);
    }
};

int main() {
    setlocale(LC_ALL, "Russian");
    MyContainer A, B, C, D, E;

    for (int x : {1, 2, 3, 4}) A.insert(x);
    for (int x : {3, 4, 5, 6}) B.insert(x);
    for (int x : {4, 5, 6, 7}) C.insert(x);
    for (int x : {2, 3, 4, 8}) D.insert(x);
    for (int x : {2, 3, 4, 5}) E.insert(x);

    cout << "=== Исходные множества ===" << endl;
    cout << "A = "; A.print();
    cout << "B = "; B.print();
    cout << "C = "; C.print();
    cout << "D = "; D.print();
    cout << "E = "; E.print();

    cout << "\n=== Вычисляем выражение ===" << endl;
    cout << "(A ∪ ((B ⊕ C) ∩ D)) ∩ E\n" << endl;

    // ШАГ 1: B ⊕ C
    auto step1 = B.SymmetricDifference(C);
    cout << "[1] B ⊕ C (симметрическая разность): ";
    step1.print();

    // ШАГ 2: (B ⊕ C) ∩ D
    auto step2 = step1.Intersection(D);
    cout << "[2] (B ⊕ C) ∩ D (пересечение): ";
    step2.print();

    // ШАГ 3: A ∪ ...
    auto step3 = A.Union(step2);
    cout << "[3] A ∪ [результат] (объединение): ";
    step3.print();

    // ШАГ 4: ... ∩ E
    auto result = step3.Intersection(E);
    cout << "[4] Итог: (A ∪ ((B ⊕ C) ∩ D)) ∩ E = ";
    result.print();

    // Демонстрация последовательности
    cout << "\n=== Работа с последовательностью ===" << endl;

    result.Concat(A);
    cout << "[5] Concat(result, A) (добавление последовательности): ";
    result.print();

    result.Erase(1, 2);
    cout << "[6] Erase(1,2) (удаление по индексам): ";
    result.print();

    return 0;
}