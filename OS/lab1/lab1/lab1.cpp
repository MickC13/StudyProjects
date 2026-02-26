#include <windows.h>
#include <iostream>
#include <string>

using namespace std;

void listDrives() {
    DWORD drives = GetLogicalDrives();
    cout << "Available Drives:\n";

    for (int i = 0; i < 26; i++) {
        if (drives & (1 << i)) {
            char letter = 'A' + i;
            cout << letter << ":\\\n";
        }
    }
}

void driveInfo() {
    string disk;
    cout << "Input drive (for examlpe C:\\): ";
    cin >> disk;

    UINT type = GetDriveTypeA(disk.c_str());

    cout << "type of Drive: ";
    switch (type) {
    case DRIVE_FIXED: cout << "Local drive\n"; break;
    case DRIVE_REMOVABLE: cout << "Removable drive\n"; break;
    case DRIVE_CDROM: cout << "CD/DVD\n"; break;
    default: cout << "Another\n";
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
        cout << "Directory created.\n";
    else
        cout << "Error of creation.\n";
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
    cout << "Куда копировать: ";
    cin >> dst;

    if (CopyFileA(src.c_str(), dst.c_str(), TRUE))
        cout << "Файл скопирован.\n";
    else
        cout << "Ошибка копирования.\n";
}

void moveFileFunc() {
    string src, dst;
    cout << "Исходный файл: ";
    cin >> src;
    cout << "Куда переместить: ";
    cin >> dst;

    if (MoveFileExA(src.c_str(), dst.c_str(), MOVEFILE_COPY_ALLOWED))
        cout << "Файл перемещен.\n";
    else
        cout << "Ошибка перемещения.\n";
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

    if (SetFileAttributesA(path.c_str(), attrs | FILE_ATTRIBUTE_READONLY))
        cout << "Атрибут READONLY установлен.\n";
    else
        cout << "Ошибка изменения атрибутов.\n";
}

int main() {
    int choice;

    do {
        cout << "\n=== МЕНЮ ===\n";
        cout << "1. List of drives\n";
        cout << "2. Info about drive\n";
        cout << "3. Create dir\n";
        cout << "4. Delete dir\n";
        cout << "5. Create file\n";
        cout << "6. Copy file\n";
        cout << "7. remove file\n";
        cout << "8. Attributes of file\n";
        cout << "9. Make file READONLY\n";
        cout << "0. Exit\n";
        cout << "Choice: ";
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
