#include <windows.h>
#include <iostream>

using namespace std;

LPVOID reservedMemory = NULL;
LPVOID allocatedMemory = NULL;

SIZE_T regionSize = 4096;

void showSystemInfo() {
    SYSTEM_INFO si;
    GetSystemInfo(&si);

    cout << "\n--- SYSTEM INFO ---\n";
    cout << "Page size: " << si.dwPageSize << endl;
    cout << "Min app address: " << si.lpMinimumApplicationAddress << endl;
    cout << "Max app address: " << si.lpMaximumApplicationAddress << endl;
    cout << "Number of processors: " << si.dwNumberOfProcessors << endl;
}

void showMemoryStatus() {
    MEMORYSTATUS ms;
    GlobalMemoryStatus(&ms);

    cout << "\n--- MEMORY STATUS ---\n";
    cout << "Memory load: " << ms.dwMemoryLoad << " %" << endl;
    cout << "Total phys: " << ms.dwTotalPhys / 1024 / 1024 << " MB\n";
    cout << "Avail phys: " << ms.dwAvailPhys / 1024 / 1024 << " MB\n";
}

void queryMemory() {
    LPVOID addr;

    cout << "Введите адрес (hex): ";
    cin >> addr;

    MEMORY_BASIC_INFORMATION mbi;

    if (VirtualQuery(addr, &mbi, sizeof(mbi))) {

        cout << "\n--- MEMORY INFO ---\n";

        cout << "Base address: " << mbi.BaseAddress << endl;
        cout << "Region size: " << mbi.RegionSize << endl;
        cout << "State: " << mbi.State << endl;
        cout << "Protect: " << mbi.Protect << endl;
    }
    else {
        cout << "Ошибка VirtualQuery\n";
    }
}

void reserveMemoryAuto() {

    reservedMemory = VirtualAlloc(
        NULL,
        regionSize,
        MEM_RESERVE,
        PAGE_NOACCESS
    );

    if (reservedMemory)
        cout << "Память зарезервирована: "
        << reservedMemory << endl;
    else
        cout << "Ошибка MEM_RESERVE\n";
}

void reserveMemoryManual() {

    LPVOID addr;

    cout << "Введите адрес (hex): ";
    cin >> addr;

    reservedMemory = VirtualAlloc(
        addr,
        regionSize,
        MEM_RESERVE,
        PAGE_NOACCESS
    );

    if (reservedMemory)
        cout << "Память зарезервирована: "
        << reservedMemory << endl;
    else
        cout << "Ошибка MEM_RESERVE\n";
}

void commitMemory() {

    if (!reservedMemory) {

        cout << "Сначала выполните reserve\n";
        return;
    }

    allocatedMemory = VirtualAlloc(
        reservedMemory,
        regionSize,
        MEM_COMMIT,
        PAGE_READWRITE
    );

    if (allocatedMemory)
        cout << "Commit выполнен: "
        << allocatedMemory << endl;
    else
        cout << "Ошибка MEM_COMMIT\n";
}

void reserveCommitAuto() {

    allocatedMemory = VirtualAlloc(
        NULL,
        regionSize,
        MEM_RESERVE | MEM_COMMIT,
        PAGE_READWRITE
    );

    if (allocatedMemory)
        cout << "Память выделена: "
        << allocatedMemory << endl;
    else
        cout << "Ошибка VirtualAlloc\n";
}

void reserveCommitManual() {

    LPVOID addr;

    cout << "Введите адрес (hex): ";
    cin >> addr;

    allocatedMemory = VirtualAlloc(
        addr,
        regionSize,
        MEM_RESERVE | MEM_COMMIT,
        PAGE_READWRITE
    );

    if (allocatedMemory)
        cout << "Память выделена: "
        << allocatedMemory << endl;
    else
        cout << "Ошибка VirtualAlloc\n";
}

void freeMemory() {

    if (allocatedMemory) {

        VirtualFree(
            allocatedMemory,
            0,
            MEM_RELEASE
        );

        allocatedMemory = NULL;
    }

    if (reservedMemory) {

        VirtualFree(
            reservedMemory,
            0,
            MEM_RELEASE
        );

        reservedMemory = NULL;
    }

    cout << "Память освобождена\n";
}

void writeMemory() {

    if (!allocatedMemory) {

        cout << "Сначала выделите память!\n";
        return;
    }

    int value;

    cout << "Введите число: ";
    cin >> value;

    *((int*)allocatedMemory) = value;

    cout << "Записано по адресу "
        << allocatedMemory << endl;
}

void changeProtection() {

    if (!allocatedMemory) {

        cout << "Сначала выделите память!\n";
        return;
    }

    DWORD oldProtect;

    if (VirtualProtect(
        allocatedMemory,
        regionSize,
        PAGE_READONLY,
        &oldProtect
    )) {

        cout << "Защита изменена на READONLY\n";
    }
    else {

        cout << "Ошибка VirtualProtect\n";
    }
}

int main() {

    setlocale(LC_ALL, "Rus");

    int choice;

    while (true) {

        cout << "\n=== MENU ===\n";

        cout << "1. Информация о системе\n";
        cout << "2. Статус памяти\n";
        cout << "3. VirtualQuery\n";

        cout << "4. MEM_RESERVE (авто)\n";
        cout << "5. MEM_RESERVE (ручной)\n";

        cout << "6. MEM_COMMIT\n";

        cout << "7. Reserve + Commit (авто)\n";
        cout << "8. Reserve + Commit (ручной)\n";

        cout << "9. Освободить память\n";
        cout << "10. Записать в память\n";
        cout << "11. Изменить защиту\n";

        cout << "0. Выход\n";

        cout << "Выбор: ";

        cin >> choice;

        switch (choice) {

        case 1:
            showSystemInfo();
            break;

        case 2:
            showMemoryStatus();
            break;

        case 3:
            queryMemory();
            break;

        case 4:
            reserveMemoryAuto();
            break;

        case 5:
            reserveMemoryManual();
            break;

        case 6:
            commitMemory();
            break;

        case 7:
            reserveCommitAuto();
            break;

        case 8:
            reserveCommitManual();
            break;

        case 9:
            freeMemory();
            break;

        case 10:
            writeMemory();
            break;

        case 11:
            changeProtection();
            break;

        case 0:
            return 0;

        default:
            cout << "Ошибка выбора\n";
        }
    }
}
