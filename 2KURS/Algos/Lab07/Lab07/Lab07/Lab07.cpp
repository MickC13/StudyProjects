#include <iostream>
#include <vector>
#include <algorithm>
#include <cstdlib>
#include <ctime>

using namespace std;


// BST (дерево)

struct Node {
    int val;
    Node* left;
    Node* right;

    Node(int v) : val(v), left(nullptr), right(nullptr) {}
};


// КОНТЕЙНЕР

class MyContainer {
private:
    Node* root = nullptr;
    vector<int> seq;

    // ===== BST функции =====
    bool contains(Node* node, int x) const {
        if (!node) return false;
        if (x == node->val) return true;
        if (x < node->val) return contains(node->left, x);
        return contains(node->right, x);
    }

    Node* insertNode(Node* node, int x) {
        if (!node) return new Node(x);
        if (x < node->val)
            node->left = insertNode(node->left, x);
        else if (x > node->val)
            node->right = insertNode(node->right, x);
        return node;
    }

public:
    // ===== вставка (как множество) =====
    void insert(int x) {
        if (!contains(root, x)) {
            root = insertNode(root, x);
            seq.push_back(x);
        }
    }

    // ===== вывод =====
    void print() const {
        for (int x : seq)
            cout << x << " ";
        cout << endl;
    }

    // ===== CONCAT (последовательность!) =====
    void Concat(const MyContainer& other) {
        for (int x : other.seq) {
            seq.push_back(x);     // добавляем ВСЕ (с повторами)
            root = insertNode(root, x); // дерево обновляем
        }
    }

    // ===== ERASE (по индексам) =====
    void Erase(int l, int r) {
        if (seq.empty()) return;

        l = max(0, l);
        r = min((int)seq.size() - 1, r);
        if (l > r) return;

        seq.erase(seq.begin() + l, seq.begin() + r + 1);

        // пересборка дерева
        root = nullptr;
        for (int x : seq)
            root = insertNode(root, x);
    }

    // ===== EXCL (разность A \ B) =====
    MyContainer Excl(const MyContainer& other) const {
        MyContainer res;
        for (int x : seq)
            if (!other.contains(other.root, x))
                res.insert(x);
        return res;
    }

    // ===== UNION =====
    MyContainer Union(const MyContainer& other) const {
        MyContainer res = *this;
        for (int x : other.seq)
            res.insert(x);
        return res;
    }

    // ===== INTERSECTION =====
    MyContainer Intersection(const MyContainer& other) const {
        MyContainer res;
        for (int x : seq)
            if (other.contains(other.root, x))
                res.insert(x);
        return res;
    }

    // ===== XOR =====
    MyContainer SymmetricDifference(const MyContainer& other) const {
        MyContainer res;

        for (int x : seq)
            if (!other.contains(other.root, x))
                res.insert(x);

        for (int x : other.seq)
            if (!contains(root, x))
                res.insert(x);

        return res;
    }
};

// =====================
// ВВОД / ГЕНЕРАЦИЯ
// =====================
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

void randomContainer(MyContainer& c, const string& name) {
    int n = rand() % 6 + 3;
    cout << name << " (random): ";

    for (int i = 0; i < n; i++) {
        int x = rand() % 10;
        c.insert(x);
    }
    c.print();
}


// MAIN

int main() {
    setlocale(LC_ALL, "Russian");
    srand(time(0));

    MyContainer A, B, C, D, E;

    int mode;
    cout << "1 - ручной ввод\n2 - случайная генерация\nВыбор: ";
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

    cout << "\n===== ИСХОДНЫЕ МНОЖЕСТВА =====\n";
    cout << "A = "; A.print();
    cout << "B = "; B.print();
    cout << "C = "; C.print();
    cout << "D = "; D.print();
    cout << "E = "; E.print();

    cout << "\n===== ВЫЧИСЛЕНИЕ ВЫРАЖЕНИЯ =====\n";
    cout << "Формула: (A ∪ ((B ⊕ C) ∩ D)) ∩ E\n\n";

    // ШАГ 1
    auto step1 = B.SymmetricDifference(C);
    cout << "[1] Симметрическая разность (B ⊕ C): ";
    step1.print();

    // ШАГ 2
    auto step2 = step1.Intersection(D);
    cout << "[2] Пересечение ((B ⊕ C) ∩ D): ";
    step2.print();

    // ШАГ 3
    auto step3 = A.Union(step2);
    cout << "[3] Объединение (A ∪ ...): ";
    step3.print();

    // ШАГ 4
    auto result = step3.Intersection(E);
    cout << "[4] Пересечение с E ((...) ∩ E): ";
    result.print();

    cout << "\n>>> ИТОГ РЕЗУЛЬТАТА <<<\n";
    cout << "(A ∪ ((B ⊕ C) ∩ D)) ∩ E = ";
    result.print();

    // ===== ДОП. ОПЕРАЦИИ ВАРИАНТА =====
    cout << "\n===== ДОПОЛНИТЕЛЬНЫЕ ОПЕРАЦИИ =====\n";

    // EXCL
    auto excl = A.Excl(B);
    cout << "[EXCL] Разность A \\ B (исключение элементов B из A): ";
    excl.print();

    // CONCAT
    result.Concat(A);
    cout << "[CONCAT] Добавление последовательности A к результату: ";
    result.print();

    // ERASE
    result.Erase(1, 2);
    cout << "[ERASE] Удаление элементов с индексов [1..2]: ";
    result.print();

    return 0;
}