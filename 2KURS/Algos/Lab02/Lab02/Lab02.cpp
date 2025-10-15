#include <ctime>
#include <iostream>
#include <clocale>

// Выберите представление (раскомментируйте одну строку):
//#define USE_ARRAY
//#define USE_LIST
#define USE_WORD  
// #define USE_BOOL

#ifdef USE_ARRAY
#include "SetArray.h"
typedef SetArray SetType;
#elif defined(USE_LIST)
#include "SetList.h"
typedef SetList SetType;
#elif defined(USE_WORD)
#include "SetWord.h"
typedef SetWord SetType;
#elif defined(USE_BOOL)
#include "SetBool.h"
typedef SetBool SetType;
#endif

void experimentWithTracking() {
    std::cout << "\n=== ЭКСПЕРИМЕНТ С ОТСЛЕЖИВАНИЕМ ВЫЗОВОВ ===" << std::endl;

    // Включаем отладку
    SetType::SetDebug(true);

    // Создание множеств
    std::cout << "\n1. СОЗДАНИЕ МНОЖЕСТВ:" << std::endl;
    SetType A("ABCD");
    SetType B("BCDE");
    SetType C("CDEF");
    SetType D("DEFG");

    // Показ исходных данных
    std::cout << "\n2. ИСХОДНЫЕ МНОЖЕСТВА:" << std::endl;
    A.Show();
    B.Show();
    C.Show();
    D.Show();

    // Вычисление E по формуле из варианта 27: E = A & ~(B & C & D)
    std::cout << "\n3. ВЫЧИСЛЕНИЕ E = A & ~(B & C & D):" << std::endl;
    SetType E = A & ~(B & C & D);

    // Результат
    std::cout << "\n4. РЕЗУЛЬТАТ:" << std::endl;
    E.Show();

    // Проверка мощности
    std::cout << "\n5. МОЩНОСТИ МНОЖЕСТВ:" << std::endl;
    std::cout << "Мощность A: " << A.power() << std::endl;
    std::cout << "Мощность B: " << B.power() << std::endl;
    std::cout << "Мощность C: " << C.power() << std::endl;
    std::cout << "Мощность D: " << D.power() << std::endl;
    std::cout << "Мощность E: " << E.power() << std::endl;

    std::cout << "\n6. ЗАВЕРШЕНИЕ ЭКСПЕРИМЕНТА:" << std::endl;
}

void experimentWithTimeMeasurement() {
    std::cout << "\n=== ЭКСПЕРИМЕНТ С ИЗМЕРЕНИЕМ ВРЕМЕНИ ===" << std::endl;

    // Выключаем отладку для производительности
    SetType::SetDebug(false);

    // Сброс счетчика перед созданием новых множеств
    SetType::ResetCounter();

    SetType A("ABCD");
    SetType B("BCDE");
    SetType C("CDEF");
    SetType D("DEFG");

    // Измерение времени
    std::cout << "\nИЗМЕРЕНИЕ ВРЕМЕНИ:" << std::endl;
    const int REPEAT_COUNT = 100000;
    clock_t start = clock();

    for (int i = 0; i < REPEAT_COUNT; i++) {
        SetType temp = A & ~(B & C & D);
        // Явно уничтожаем временный объект
    }

    clock_t end = clock();
    double time = double(end - start) / CLOCKS_PER_SEC;
    std::cout << "Общее время: " << time << " секунд" << std::endl;
    std::cout << "Среднее время: " << time / REPEAT_COUNT << " секунд" << std::endl;
}

int main() {
    // Установка русской локали для корректного отображения
    setlocale(LC_ALL, "Russian");

    {
        std::cout << "=== ЛАБОРАТОРНАЯ РАБОТА ПО ТЕМЕ 'МНОЖЕСТВО КАК ОБЪЕКТ' ===" << std::endl;
        std::cout << "=== ВАРИАНТ 27: E = A & ~(B & C & D) ===" << std::endl;

        // Эксперимент с отслеживанием вызовов
        experimentWithTracking();

        // Эксперимент с измерением времени (без отслеживания вызовов)
        experimentWithTimeMeasurement();

        std::cout << "\n7. ЗАВЕРШЕНИЕ ПРОГРАММЫ:" << std::endl;
    } // Здесь уничтожаются все объекты

    std::cout << "\nНажмите Enter для выхода...";
    std::cin.get();
    return 0;
}