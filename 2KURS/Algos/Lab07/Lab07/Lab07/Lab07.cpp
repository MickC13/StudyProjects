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
    void insert(int x) {
        if (dataSet.insert(x).second) {
            seq.push_back(x);
        }
    }

    void print() {
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
        for (int x : other.seq)
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

        // элементы из A, которых нет в B
        for (int x : seq)
            if (!other.dataSet.count(x))
                res.insert(x);

        // элементы из B, которых нет в A
        for (int x : other.seq)
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

    // CONCAT
    void Concat(const MyContainer& other) {
        for (int x : other.seq)
            insert(x);
    }

    // ERASE (удаление по позициям)
    void Erase(int l, int r) {
        vector<int> newSeq;
        for (int i = 0; i < seq.size(); i++) {
            if (i < l || i > r)
                newSeq.push_back(seq[i]);
        }
        seq = newSeq;

        dataSet.clear();
        for (int x : seq)
            dataSet.insert(x);
    }
};

int main() {
    MyContainer A, B, C, D, E;

    // пример данных
    for (int x : {1, 2, 3, 4}) A.insert(x);
    for (int x : {3, 4, 5, 6}) B.insert(x);
    for (int x : {4, 5, 6, 7}) C.insert(x);
    for (int x : {2, 3, 4, 8}) D.insert(x);
    for (int x : {2, 3, 4, 5}) E.insert(x);

    cout << "A: "; A.print();
    cout << "B: "; B.print();
    cout << "C: "; C.print();
    cout << "D: "; D.print();
    cout << "E: "; E.print();

    // 1. B ⊕ C
    auto step1 = B.SymmetricDifference(C);
    cout << "B ⊕ C: "; step1.print();

    // 2. (B ⊕ C) ∩ D
    auto step2 = step1.Intersection(D);
    cout << "(B ⊕ C) ∩ D: "; step2.print();

    // 3. A ∪ ...
    auto step3 = A.Union(step2);
    cout << "A ∪ ...: "; step3.print();

    // 4. итог: ∩ E
    auto result = step3.Intersection(E);
    cout << "RESULT: "; result.print();

    return 0;
}