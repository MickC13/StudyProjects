#include <iostream>
#include <set>
#include <vector>
#include <algorithm>
#include <iterator>
#include <chrono>
#include <fstream>
#include <cstdlib>

using namespace std;
using namespace chrono;

// ===== КОНТЕЙНЕР =====

class MyContainer {
private:
    set<int> base;                      // множество
    vector<set<int>::iterator> seq;     // последовательность (итераторы)

public:
    void insert(int x) {
        auto res = base.insert(x);
        if (res.second) {
            seq.push_back(res.first);
        }
    }

    void clear() {
        base.clear();
        seq.clear();
    }

    // получить отсортированные данные
    vector<int> getSorted() const {
        return vector<int>(base.begin(), base.end());
    }

    // ===== ОПЕРАЦИИ =====

    static MyContainer Union(const MyContainer& A, const MyContainer& B) {
        MyContainer res;

        vector<int> a = A.getSorted();
        vector<int> b = B.getSorted();
        vector<int> temp;

        set_union(a.begin(), a.end(), b.begin(), b.end(), back_inserter(temp));

        for (int x : temp)
            res.insert(x);

        return res;
    }

    static MyContainer Intersection(const MyContainer& A, const MyContainer& B) {
        MyContainer res;

        vector<int> a = A.getSorted();
        vector<int> b = B.getSorted();
        vector<int> temp;

        set_intersection(a.begin(), a.end(), b.begin(), b.end(), back_inserter(temp));

        for (int x : temp)
            res.insert(x);

        return res;
    }

    static MyContainer SymmetricDifference(const MyContainer& A, const MyContainer& B) {
        MyContainer res;

        vector<int> a = A.getSorted();
        vector<int> b = B.getSorted();
        vector<int> temp;

        set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), back_inserter(temp));

        for (int x : temp)
            res.insert(x);

        return res;
    }
};

// ===== ГЕНЕРАЦИЯ =====

void generate(MyContainer& c, int n) {
    for (int i = 0; i < n; i++)
        c.insert(rand() % (n * 10));
}

// ===== MAIN =====

int main() {
    setlocale(LC_ALL, "Russian");
    srand(time(0));

    string filepath = "C:\\Git\\abob\\2KURS\\Algos\\Lab07\\Lab07\\Lab07\\results.txt";

    // 🔥 ГАРАНТИРОВАННАЯ ОЧИСТКА ФАЙЛА
    {
        ofstream clearFile(filepath, ios::trunc);
    }

    // открытие для записи
    ofstream fout(filepath);

    if (!fout) {
        cout << "Ошибка открытия файла!\n";
        return 1;
    }

    // заголовок (удобно для Excel)
    fout << "n time(us)\n";

    for (int n = 10; n <= 200; n += 10) {

        int repeats = 20;
        double total = 0;

        for (int k = 0; k < repeats; k++) {

            MyContainer A, B, C, D, E;

            generate(A, n);
            generate(B, n);
            generate(C, n);
            generate(D, n);
            generate(E, n);

            auto t1 = high_resolution_clock::now();

            // ===== ВАРИАНТ 27 =====
            auto step1 = MyContainer::SymmetricDifference(B, C);
            auto step2 = MyContainer::Intersection(step1, D);
            auto step3 = MyContainer::Union(A, step2);
            auto result = MyContainer::Intersection(step3, E);

            auto t2 = high_resolution_clock::now();

            double dt = duration<double, micro>(t2 - t1).count();
            total += dt;
        }

        double avg = total / repeats;

        fout << n << " " << avg << endl;
        cout << "n=" << n << " time=" << avg << " us\n";
    }

    fout.close();

    cout << "\nГотово. Результаты обновлены в файле results.txt\n";

    return 0;
}