#include <iostream>
#include <set>
#include <list>
#include <vector>
#include <ctime>
#include <algorithm>
#include <cstdlib>
#include <cstring>

using namespace std;

const string alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
const int REPEAT_COUNT = 100000;


bool checkAlphabet(const string& input) {
    for (char c : input) {
        if (c < 'A' || c > 'Z') {
            return false;
        }
    }
    return true;
}

// Функция для генерации случайного множества символов
set<char> generateRandomSet(int minSize = 5, int maxSize = 20) {
    int size = rand() % (maxSize - minSize + 1) + minSize;
    set<char> result;

    while (result.size() < size) {
        char randomChar = 'A' + rand() % 26;
        result.insert(randomChar);
    }

    return result;
}

set<char> generateSet(const char* str) {
    set<char> result;
    while (*str) result.insert(*(str++));
    return result;
}

// Операции с массивами
vector<char> arrayOperation(const vector<char>& a, const vector<char>& b, const vector<char>& c, const vector<char>& d) {
    vector<char> result;

    for (char x : a) {
        bool foundInB = false;
        for (char y : b) {
            if (y == x) {
                foundInB = true;
                break;
            }
        }

        bool foundInC = false;
        for (char y : c) {
            if (y == x) {
                foundInC = true;
                break;
            }
        }

        bool foundInD = false;
        for (char y : d) {
            if (y == x) {
                foundInD = true;
                break;
            }
        }

        if (!(foundInB && foundInC && foundInD)) {
            result.push_back(x);
        }
    }

    return result;
}

// Операции со списками
list<char> listOperation(const list<char>& a, const list<char>& b, const list<char>& c, const list<char>& d) {
    list<char> result;

    for (char x : a) {
        bool foundInB = false;
        for (char y : b) {
            if (y == x) {
                foundInB = true;
                break;
            }
        }

        bool foundInC = false;
        for (char y : c) {
            if (y == x) {
                foundInC = true;
                break;
            }
        }

        bool foundInD = false;
        for (char y : d) {
            if (y == x) {
                foundInD = true;
                break;
            }
        }

        if (!(foundInB && foundInC && foundInD)) {
            result.push_back(x);
        }
    }

    return result;
}

// Операции с машинными словами
unsigned int wordOperation(unsigned int a, unsigned int b, unsigned int c, unsigned int d) {
    return a & ~(b & c & d);
}

// Операции с булевыми массивами
void bool26Operation(const bool a[26], const bool b[26], const bool c[26], const bool d[26], bool result[26]) {
    for (int i = 0; i < 26; i++) {
        result[i] = a[i] && !(b[i] && c[i] && d[i]);
    }
}

// Функции для преобразования результатов в set для вывода
set<char> arrayToSet(const vector<char>& arr) {
    return set<char>(arr.begin(), arr.end());
}

set<char> listToSet(const list<char>& lst) {
    return set<char>(lst.begin(), lst.end());
}

set<char> wordToSet(unsigned int word) {
    set<char> result;
    for (int i = 0; i < 26; i++) {
        if (word & (1 << i)) {
            result.insert('A' + i);
        }
    }

    return result;
}

set<char> bool26ToSet(const bool bits[26]) {
    set<char> result;
    for (int i = 0; i < 26; i++) {
        if (bits[i]) result.insert('A' + i);
    }
    return result;
}

void printSet(const set<char>& s) {
    if (s.empty()) {
        cout << "empty";
    }
    else {
        for (char x : s) {
            cout << x;
        }
    }
}

int main() {
    srand(time(0));

    // Выбор способа ввода
    int choice;
    cout << "Choose input method:\n";
    cout << "1. Manual input\n";
    cout << "2. Random generation\n";
    cin >> choice;

    // Объявляем переменные для каждого типа данных
    vector<char> A_array, B_array, C_array, D_array;
    list<char> A_list, B_list, C_list, D_list;
    unsigned int A_word = 0, B_word = 0, C_word = 0, D_word = 0;
    bool A_bool26[26], B_bool26[26], C_bool26[26], D_bool26[26];

    if (choice == 1) {
        // Ручной ввод
        vector<string> set_names = { "A", "B", "C", "D" };
        for (int i = 0; i < 4; i++) {
            string input;
            while (true) {
                cout << "Enter set " << set_names[i] << " (as a string without spaces, uppercase letters A-Z only): ";
                cin >> input;
                if (checkAlphabet(input)) break;
                cout << "Error: Only uppercase English letters (A-Z) are allowed. Please try again.\n";
            }
            // Заполняем все структуры данных
            set<char> temp_set;
            for (char c : input) {
                char upper_c = toupper(c);
                if (upper_c >= 'A' && upper_c <= 'Z') {
                    temp_set.insert(upper_c);
                }
            }

            // Заполняем массив
            vector<char> temp_array(temp_set.begin(), temp_set.end());

            // Заполняем список
            list<char> temp_list(temp_set.begin(), temp_set.end());

            // Заполняем машинное слово
            unsigned int temp_word = 0;
            for (char c : temp_set) {
                temp_word |= (1 << (c - 'A'));
            }

            // Заполняем булевый массив
            bool temp_bool26[26] = { false };
            for (char c : temp_set) {
                temp_bool26[c - 'A'] = true;
            }

            // Сохраняем результаты
            if (i == 0) {
                A_array = temp_array;
                A_list = temp_list;
                A_word = temp_word;
                memcpy(A_bool26, temp_bool26, sizeof(A_bool26));
            }
            else if (i == 1) {
                B_array = temp_array;
                B_list = temp_list;
                B_word = temp_word;
                memcpy(B_bool26, temp_bool26, sizeof(B_bool26));
            }
            else if (i == 2) {
                C_array = temp_array;
                C_list = temp_list;
                C_word = temp_word;
                memcpy(C_bool26, temp_bool26, sizeof(C_bool26));
            }
            else {
                D_array = temp_array;
                D_list = temp_list;
                D_word = temp_word;
                memcpy(D_bool26, temp_bool26, sizeof(D_bool26));
            }
        }
    }
    else {
        // Случайная генерация - генерируем одно множество и представляем его во всех форматах
        set<char> A_set = generateSet("BDEFHIKLMOPQSTV");
        set<char> B_set = generateSet("BCEFGHIJKNPRTVW");
        set<char> C_set = generateSet("BCDEFGHIKMORSTUVWXYZ");
        set<char> D_set = generateSet("ABDEFHIJLMNQRSTUVWXZ");

        // Преобразуем во все форматы
        A_array = vector<char>(A_set.begin(), A_set.end());
        B_array = vector<char>(B_set.begin(), B_set.end());
        C_array = vector<char>(C_set.begin(), C_set.end());
        D_array = vector<char>(D_set.begin(), D_set.end());

        A_list = list<char>(A_set.begin(), A_set.end());
        B_list = list<char>(B_set.begin(), B_set.end());
        C_list = list<char>(C_set.begin(), C_set.end());
        D_list = list<char>(D_set.begin(), D_set.end());

        for (char c : A_set) A_word |= (1 << (c - 'A'));
        for (char c : B_set) B_word |= (1 << (c - 'A'));
        for (char c : C_set) C_word |= (1 << (c - 'A'));
        for (char c : D_set) D_word |= (1 << (c - 'A'));

        for (int i = 0; i < 26; i++) {
            A_bool26[i] = (A_set.find('A' + i) != A_set.end());
            B_bool26[i] = (B_set.find('A' + i) != B_set.end());
            C_bool26[i] = (C_set.find('A' + i) != C_set.end());
            D_bool26[i] = (D_set.find('A' + i) != D_set.end());
        }

        // Вывод сгенерированных множеств
        cout << "Generated sets:\n";
        cout << "A = "; printSet(A_set); cout << endl;
        cout << "B = "; printSet(B_set); cout << endl;
        cout << "C = "; printSet(C_set); cout << endl;
        cout << "D = "; printSet(D_set); cout << endl;
    }

    // Для хранения результатов
    vector<char> E_array;
    list<char> E_list;
    unsigned int E_word = 0;
    bool E_bool26[26];

    // Вычисление E с использованием массивов
    clock_t start = clock();
    for (int i = 0; i < REPEAT_COUNT; i++) {
        E_array = arrayOperation(A_array, B_array, C_array, D_array);
    }
    clock_t end = clock();
    double array_time = double(end - start) / CLOCKS_PER_SEC / REPEAT_COUNT;

    // Вычисление E с использованием списков
    start = clock();
    for (int i = 0; i < REPEAT_COUNT; i++) {
        E_list = listOperation(A_list, B_list, C_list, D_list);
    }
    end = clock();
    double list_time = double(end - start) / CLOCKS_PER_SEC / REPEAT_COUNT;

    // Вычисление E с использованием машинных слов
    start = clock();
    for (int i = 0; i < REPEAT_COUNT * 100; i++) {
        E_word = wordOperation(A_word, B_word, C_word, D_word);
    }
    end = clock();
    double word_time = double(end - start) / CLOCKS_PER_SEC / REPEAT_COUNT / 100;

    // Вычисление E с использованием bool[26]
    start = clock();
    for (int i = 0; i < REPEAT_COUNT; i++) {
        bool26Operation(A_bool26, B_bool26, C_bool26, D_bool26, E_bool26);
    }
    end = clock();
    double bool26_time = double(end - start) / CLOCKS_PER_SEC / REPEAT_COUNT;

    // Преобразуем результаты обратно в set для вывода
    set<char> E_array_set = arrayToSet(E_array);
    set<char> E_list_set = listToSet(E_list);
    set<char> E_word_set = wordToSet(E_word);
    set<char> E_bool26_set = bool26ToSet(E_bool26);

    // Вывод результатов
    cout << "\nResult for character arrays:\n";
    cout << "E = "; printSet(E_array_set); cout << endl;
    cout << "Time: " << array_time << " seconds" << endl;

    cout << "Result for lists:\n";
    cout << "E = "; printSet(E_list_set); cout << endl;
    cout << "Time: " << list_time << " seconds" << endl;

    cout << "Result for machine words:\n";
    cout << "E = "; printSet(E_word_set); cout << endl;
    cout << "Time: " << word_time << " seconds" << endl;

    cout << "Result for bool[26]:\n";
    cout << "E = "; printSet(E_bool26_set); cout << endl;
    cout << "Time: " << bool26_time << " seconds" << endl;

    return 0;
}