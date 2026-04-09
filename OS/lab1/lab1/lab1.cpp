#include <windows.h>
#include <iostream>
#include <string>

using namespace std;

void listDrives() {
    DWORD drives = GetLogicalDrives();
    cout << "Доступные диски:\n";

    for (int i = 0; i < 26; i++) {
        if (drives & (1 << i)) {
            char letter = 'A' + i;
            cout << letter << ":\\\n";
        }
    }
}

void driveInfo() {
    string disk;
    cout << "Введите диск (например C:\\): ";
    cin >> disk;

    UINT type = GetDriveTypeA(disk.c_str());

    cout << "Тип диска: ";
    switch (type) {
    case DRIVE_FIXED: cout << "Жесткий диск\n"; break;
    case DRIVE_REMOVABLE: cout << "Съёмный диск\n"; break;
    case DRIVE_CDROM: cout << "CD/DVD\n"; break;
    default: cout << "Другой\n";
    }

    char volumeName[MAX_PATH]; 
    char fileSystem[MAX_PATH];
    DWORD serial, maxCompLen, flags;

    if (GetVolumeInformationA(
        disk.c_str(),
        volumeName, MAX_PATH,
        &serial,
        &maxCompLen,
        &flags,
        fileSystem, MAX_PATH)) {

        cout << "Метка тома: " << volumeName << endl;
        cout << "Файловая система: " << fileSystem << endl;
    }

    DWORD sectorsPerCluster, bytesPerSector, freeClusters, totalClusters;

    if (GetDiskFreeSpaceA(
        disk.c_str(),
        &sectorsPerCluster,
        &bytesPerSector,
        &freeClusters,
        &totalClusters)) {

        ULONGLONG freeSpace =
            (ULONGLONG)freeClusters * sectorsPerCluster * bytesPerSector;

        cout << "Free space (байт): " << freeSpace << endl;
    }
}

void createDir() {
    string path;
    cout << "Input path of new directory: ";
    cin >> path;

    if (CreateDirectoryA(path.c_str(), NULL))
        cout << "Каталог создан.\n";
    else
        cout << "Ошибка создания.\n";
}

void removeDir() {
    string path;
    cout << "Введите путь каталога: ";
    cin >> path;

    if (RemoveDirectoryA(path.c_str()))
        cout << "Каталог удален.\n";
    else
        cout << "Ошибка удаления.\n";
}

void createFileFunc() {
    string path;
    cout << "Введите путь нового файла: ";
    cin >> path;

    HANDLE hFile = CreateFileA(
        path.c_str(),
        GENERIC_WRITE,
        0,
        NULL,
        CREATE_NEW,
        FILE_ATTRIBUTE_NORMAL,
        NULL);

    if (hFile != INVALID_HANDLE_VALUE) {
        cout << "Файл создан.\n";
        CloseHandle(hFile);
    }
    else {
        cout << "Ошибка создания файла.\n";
    }
}

void copyFileFunc() {
    string src, dst;
    cout << "Исходный файл: ";
    cin >> src;
    cout << "Куда копировать (полный путь): ";
    cin >> dst;

    if (CopyFileA(src.c_str(), dst.c_str(), FALSE))
        cout << "Файл успешно скопирован.\n";
    else
        cout << "Ошибка копирования: " << GetLastError() << endl;
}   

void moveFileFunc() {
    string src, dst;
    cout << "Исходный файл: ";
    cin >> src;
    cout << "Куда переместить (полный путь): ";
    cin >> dst;

    if (MoveFileExA(src.c_str(), dst.c_str(),
        MOVEFILE_COPY_ALLOWED | MOVEFILE_REPLACE_EXISTING))
        cout << "Файл перемещен.\n";
    else
        cout << "Ошибка перемещения: " << GetLastError() << endl;
}

void fileAttributes() {
    string path;
    cout << "Введите путь файла: ";
    cin >> path;

    DWORD attrs = GetFileAttributesA(path.c_str());

    if (attrs == INVALID_FILE_ATTRIBUTES) {
        cout << "Ошибка.\n";
        return;
    }

    cout << "Атрибуты:\n";
    if (attrs & FILE_ATTRIBUTE_READONLY) cout << "READONLY\n";
    if (attrs & FILE_ATTRIBUTE_HIDDEN) cout << "HIDDEN\n";
    if (attrs & FILE_ATTRIBUTE_SYSTEM) cout << "SYSTEM\n";
    if (attrs & FILE_ATTRIBUTE_ARCHIVE) cout << "ARCHIVE\n";
}

void setReadOnly() {
    string path;
    cout << "Введите путь файла: ";
    cin >> path;

    DWORD attrs = GetFileAttributesA(path.c_str());
    if (attrs == INVALID_FILE_ATTRIBUTES) {
        cout << "Ошибка.\n";
        return;
    }

    cout << "1 - установить READONLY\n";
    cout << "2 - убрать READONLY\n";

    int choice;
    cin >> choice;

    if (choice == 1) {
        attrs |= FILE_ATTRIBUTE_READONLY;
    }
    else if (choice == 2) {
        attrs &= ~FILE_ATTRIBUTE_READONLY;
    }

    if (SetFileAttributesA(path.c_str(), attrs))
        cout << "Атрибут изменён.\n";
    else
        cout << "Ошибка изменения.\n";
}

int main() {
    setlocale(LC_CTYPE, "rus");
    int choice;

    do {
        cout << "\n=== МЕНЮ ===\n";
        cout << "1. Список дисков\n";
        cout << "2. Информация о диске\n";
        cout << "3. Создать каталог\n";
        cout << "4. Удалить каталог\n";
        cout << "5. Создать файл\n";
        cout << "6. Скопировать файл\n";
        cout << "7. Переместить файл\n";
        cout << "8. Атрибуты файла\n";
        cout << "9. Сделать файл только для чтения\n";
        cout << "0. Выход\n";
        cout << "Выбор: ";
        cin >> choice;

        switch (choice) {
        case 1: listDrives(); break;
        case 2: driveInfo(); break;
        case 3: createDir(); break;
        case 4: removeDir(); break;
        case 5: createFileFunc(); break;
        case 6: copyFileFunc(); break;
        case 7: moveFileFunc(); break;
        case 8: fileAttributes(); break;
        case 9: setReadOnly(); break;
        }

    } while (choice != 0);

    return 0;
}
