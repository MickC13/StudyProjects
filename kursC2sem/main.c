#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

typedef struct {
    char name[50];
    char manufacturer[50];
    float weight;
    int serialNumber;
    int resolutionX;
    int resolutionY;
    float price;
    int dimensions[2];
} TV;

typedef int (*CompareFunc)(const TV*, const TV*);
CompareFunc CompareFunctionList[7] = {
    NULL, NULL, NULL, NULL, NULL, NULL, NULL
};

typedef struct Node {
    TV tv;
    struct Node* next;
} Node;

typedef int (*FilterFunc)(const TV*, const char*);

void filter_menu(Node* head);
void filter_tvs(Node* head, FilterFunc filter, const char* query);

Node* load_from_file(const char* filename);
void save_to_file(const char* filename, Node* head);
void add_tv(Node** head);
void delete_tv(Node** head);
void display_tvs(Node* head);
void free_list(Node* head);
void print_help();
void clearConsole();
void edit_tv(Node* head);
void sort_menu(Node** head);
int choice(const char* question, int max);

void clearConsole() {
    #if defined(_WIN32) || defined(_WIN64)
        system("cls");
    #elif defined(__APPLE__) || defined(__linux__) || defined(__unix__)
        system("clear");
    #endif
}

int choice(const char* question, int max) {
    char buffer[100];
    int selected;

    while (1) {
        printf("%s", question);
        fgets(buffer, sizeof(buffer), stdin);

        if (sscanf(buffer, "%d", &selected) == 1) {
            if (selected >= 0 && selected <= max) {
                return selected;
            }
        }
        printf("Invalid input! Please enter number between 0 and %d\n", max);
    }
}

Node* load_from_file(const char* filename) {
    Node* head = NULL;
    char line[256];
    Node** current = &head;
    FILE* file = fopen(filename, "r");

    if (!file) return NULL;


    while (fgets(line, sizeof(line), file)) {
        Node* newNode = (Node*)malloc(sizeof(Node));
        TV* tv = &newNode->tv;

        if (sscanf(line, "%49[^,],%49[^,],%f,%d,%d,%d,%f,%d,%d",
            tv->name,
            tv->manufacturer,
            &tv->weight,
            &tv->serialNumber,
            &tv->resolutionX,
            &tv->resolutionY,
            &tv->price,
            &tv->dimensions[0],
            &tv->dimensions[1]) == 9) {

            newNode->next = NULL;
            *current = newNode;
            current = &(*current)->next;
        } else {
            free(newNode);
        }
    }

    fclose(file);
    return head;
}



void save_to_file(const char* filename, Node* head) {
    FILE* file = fopen(filename, "w");
    Node* current = head;

    if (file == NULL) {
        printf("Error saving file!\n");
        return;
    }

    while(current != NULL) {
        fprintf(file, "%s,%s,%.1f,%d,%d,%d,%.2f,%d,%d\n",
            current->tv.name,
            current->tv.manufacturer,
            current->tv.weight,
            current->tv.serialNumber,
            current->tv.resolutionX,
            current->tv.resolutionY,
            current->tv.price,
            current->tv.dimensions[0],
            current->tv.dimensions[1]);
        current = current->next;
    }

    fclose(file);
    printf("Data saved successfully to %s\n", filename);
}

void add_tv(Node** head) {
    Node* newNode = (Node*)malloc(sizeof(Node));
    char buffer[100];

    if (newNode == NULL) {
        printf("Memory allocation failed!\n");
        return;
    }

    printf("\n=== Add New TV ===\n");


    printf("Enter TV name: ");
    fgets(buffer, sizeof(buffer), stdin);
    buffer[strcspn(buffer, "\n")] = '\0';
    strncpy(newNode->tv.name, buffer, sizeof(newNode->tv.name));


    printf("Enter manufacturer: ");
    fgets(buffer, sizeof(buffer), stdin);
    buffer[strcspn(buffer, "\n")] = '\0';
    strncpy(newNode->tv.manufacturer, buffer, sizeof(newNode->tv.manufacturer));


    printf("Enter weight (kg): ");
    fgets(buffer, sizeof(buffer), stdin);
    sscanf(buffer, "%f", &newNode->tv.weight);


    printf("Enter horizontal resolution: ");
    fgets(buffer, sizeof(buffer), stdin);
    sscanf(buffer, "%d", &newNode->tv.resolutionX);

    printf("Enter vertical resolution: ");
    fgets(buffer, sizeof(buffer), stdin);
    sscanf(buffer, "%d", &newNode->tv.resolutionY);


    printf("Enter price (USD): ");
    fgets(buffer, sizeof(buffer), stdin);
    sscanf(buffer, "%f", &newNode->tv.price);


    printf("Enter width (cm): ");
    fgets(buffer, sizeof(buffer), stdin);
    sscanf(buffer, "%d", &newNode->tv.dimensions[0]);

    printf("Enter height (cm): ");
    fgets(buffer, sizeof(buffer), stdin);
    sscanf(buffer, "%d", &newNode->tv.dimensions[1]);


    printf("Enter serial number: ");
    fgets(buffer, sizeof(buffer), stdin);
    sscanf(buffer, "%d", &newNode->tv.serialNumber);

    newNode->next = *head;
    *head = newNode;

    printf("\nTV added successfully!\n");
}


void print_help() {
    clearConsole();
    printf("==== TV Database Help ====\n");
    printf("This program allows you to manage a database of TVs.\n\n");

    printf("Available commands:\n");
    printf("0. Help        - Show this help message\n");
    printf("1. Add TV      - Add new TV to database\n");
    printf("2. Delete TV   - Remove TV from database\n");
    printf("3. Display TVs - Show all TVs in database\n");
    printf("4. Exit        - Save data and exit program\n\n");

    printf("Data fields description:\n");
    printf("- Name         : TV model name (50 chars max)\n");
    printf("- Manufacturer : Brand name (50 chars max)\n");
    printf("- Weight       : Weight in kilograms (5.0-100.0)\n");
    printf("- Serial Number: Unique numeric identifier\n");
    printf("- Resolution   : Format: [width] [height] pixels\n");
    printf("- Price        : USD currency format\n");
    printf("- Dimensions   : Physical size in centimeters\n\n");

    printf("Data is automatically loaded from 'tvs.dat' on startup\n");
    printf("and saved when exiting the program.\n");


}

void display_tvs(Node* head) {
    int count = 1;
    Node* current;
    clearConsole();


    printf("\n%-4s %-20s %-15s %-8s %-12s %-10s %-15s %-12s\n",
           "#", "Name", "Manufacturer", "Weight", "Serial", "Resolution", "Price", "Dimensions");
    printf("----------------------------------------------------------------------------------------\n");


    current = head;
    while(current != NULL) {
        TV tv = current->tv;
        printf("%-4d %-20s %-15s %-8.1f %-10d %d, %-9d $%-10.2f %d, %d \n",
               count++,
               tv.name,
               tv.manufacturer,
               tv.weight,
               tv.serialNumber,
               tv.resolutionX,
               tv.resolutionY,
               tv.price,
               tv.dimensions[0],
               tv.dimensions[1]);
        current = current->next;
    }

    if(count == 1) {
        printf("\nNo TVs found in database!\n");
    }
}


void delete_tv(Node** head) {
    int position;
    char buffer[100];
    Node* current;
    Node* prev = NULL;
    int count = 1;

    display_tvs(*head);
    if(*head == NULL) return;


    while(1) {
        printf("\nEnter TV number to delete (0 to cancel): ");
        fgets(buffer, sizeof(buffer), stdin);
        if(sscanf(buffer, "%d", &position) == 1) break;
        printf("Invalid input! Please enter a number.\n");
    }

    if(position < 1) {
        printf("Operation cancelled.\n");
        return;
    }

    current = *head;


    while(current != NULL && count < position) {
        prev = current;
        current = current->next;
        count++;
    }

    if(current == NULL) {
        printf("Invalid position! Maximum available: %d\n", count-1);
        return;
    }


    if(prev == NULL) {
        *head = current->next;
    } else {
        prev->next = current->next;
    }

    free(current);
    printf("TV #%d deleted successfully!\n", position);
}



void edit_tv(Node* head) {
    int position, choice;
    Node* current;
    char buffer[50];
    int success;
    int i;

    display_tvs(head);
    if (head == NULL) {
        printf("Database is empty!\n");
        return;
    }


    while (1) {
        printf("\nEnter TV number to edit (0 to cancel): ");
        fgets(buffer, sizeof(buffer), stdin);
        success = sscanf(buffer, "%d", &position);
        if (success == 1) break;
        printf("Invalid input! ");
    }

    if (position < 1) {
        printf("Operation cancelled.\n");
        return;
    }


    current = head;
    for (i = 1; current != NULL && i < position; i++) {
        current = current->next;
    }

    if (current == NULL) {
        printf("Invalid TV number!\n");
        return;
    }


    do {
        clearConsole();
        printf("\n=== Editing TV #%d ===\n", position);
        printf("  1. Name:          %s\n", current->tv.name);
        printf("  2. Manufacturer:  %s\n", current->tv.manufacturer);
        printf("  3. Weight:        %.1f kg\n", current->tv.weight);
        printf("  4. Resolution:    %dx%d px\n", current->tv.resolutionX, current->tv.resolutionY);
        printf("  5. Price:         $%.2f\n", current->tv.price);
        printf("  6. Dimensions:    %dx%d cm\n", current->tv.dimensions[0], current->tv.dimensions[1]);
        printf("  7. Serial Number: %d\n", current->tv.serialNumber);
        printf("  8. Finish Editing\n");
        printf("==============================\n");


        while (1) {
            printf("Select field (1-8): ");
            fgets(buffer, sizeof(buffer), stdin);
            if (sscanf(buffer, "%d", &choice) == 1 && choice >= 1 && choice <= 8) break;
            printf("Invalid choice! ");
        }

        switch (choice) {
            case 1:
                printf("New name: ");
                fgets(current->tv.name, sizeof(current->tv.name), stdin);
                current->tv.name[strcspn(current->tv.name, "\n")] = '\0';
                break;

            case 2:
                printf("New manufacturer: ");
                fgets(current->tv.manufacturer, sizeof(current->tv.manufacturer), stdin);
                current->tv.manufacturer[strcspn(current->tv.manufacturer, "\n")] = '\0';
                break;

            case 3:
                printf("New weight: ");
                fgets(buffer, sizeof(buffer), stdin);
                sscanf(buffer, "%f", &current->tv.weight);
                break;

            case 4:
                printf("Horizontal resolution: ");
                fgets(buffer, sizeof(buffer), stdin);
                sscanf(buffer, "%d", &current->tv.resolutionX);

                printf("Vertical resolution: ");
                fgets(buffer, sizeof(buffer), stdin);
                sscanf(buffer, "%d", &current->tv.resolutionY);
                break;

            case 5:
                printf("New price: ");
                fgets(buffer, sizeof(buffer), stdin);
                sscanf(buffer, "%f", &current->tv.price);
                break;

            case 6:
                printf("Width: ");
                fgets(buffer, sizeof(buffer), stdin);
                sscanf(buffer, "%d", &current->tv.dimensions[0]);

                printf("Height: ");
                fgets(buffer, sizeof(buffer), stdin);
                sscanf(buffer, "%d", &current->tv.dimensions[1]);
                break;

            case 7:
                printf("New serial: ");
                fgets(buffer, sizeof(buffer), stdin);
                sscanf(buffer, "%d", &current->tv.serialNumber);
                break;
        }

        if (choice != 8) {
            printf("\nChanges saved! Press Enter...");
            while (getchar() != '\n');

        }

    } while (choice != 8);
}


int cmpr_name(const TV* a, const TV* b) {
    return strcmp(a->name, b->name);
}

int cmpr_price(const TV* a, const TV* b) {
    if (a->price > b->price) return 1;
    if (a->price < b->price) return -1;
    return 0;
}

int cmpr_resolution(const TV* a, const TV* b) {
    int a_res = a->resolutionX * a->resolutionY;
    int b_res = b->resolutionX * b->resolutionY;
    return a_res - b_res;
}

int cmpr_weight(const TV* a, const TV* b) {
    if (a->weight > b->weight) return 1;
    if (a->weight < b->weight) return -1;
    return 0;
}
int cmpr_serial(const TV* a, const TV* b) {
    if (a->serialNumber > b->serialNumber) return 1;
    if (a->serialNumber < b->serialNumber) return -1;
    return 0;
}

int cmpr_manufacturer(const TV* a, const TV* b) {
    return strcmp(a->manufacturer, b->manufacturer);
}


void sort_tvs(Node** head, CompareFunc cmp) {
        int swapped;
        Node *ptr1, *lptr = NULL;
    if (*head == NULL || cmp == NULL) return;




    do {
        swapped = 0;
        ptr1 = *head;

        while (ptr1->next != lptr) {
            if (cmp(&ptr1->tv, &ptr1->next->tv) > 0) {
                TV temp = ptr1->tv;
                ptr1->tv = ptr1->next->tv;
                ptr1->next->tv = temp;
                swapped = 1;
            }
            ptr1 = ptr1->next;
        }
        lptr = ptr1;
    } while (swapped);
}


void sort_menu(Node** head) {
    int choice;
    char buffer[10];
    CompareFunc cmp;
    clearConsole();
    printf("\n=== Sort by criteria ===\n");
    printf("1. By name\n");
    printf("2. By price\n");
    printf("3. By resolution\n");
    printf("4. By weight\n");
    printf("5. By serial number\n");
    printf("6. By manufacturer\n");
    printf("7. Cancel\n");


    do {
        printf("Choose criteria (1-7): ");
        fgets(buffer, sizeof(buffer), stdin);
    } while (sscanf(buffer, "%d", &choice) != 1 || choice < 1 || choice > 7);

    cmp = NULL;
    switch (choice) {
        case 1: cmp = cmpr_name;         break;
        case 2: cmp = cmpr_price;        break;
        case 3: cmp = cmpr_resolution;   break;
        case 4: cmp = cmpr_weight;       break;
        case 5: cmp = cmpr_serial;       break;
        case 6: cmp = cmpr_manufacturer; break;
        case 7: return;
    }

    if (cmp) {
        sort_tvs(head, cmp);
        printf("\nSorting completed successfully!\n");
        printf("Press Enter to continue...");
        while(getchar() != '\n');
        getchar();
    }
}

/* Функции-фильтры */
int filter_by_name(const TV* tv, const char* query) {
    return strstr(tv->name, query) != NULL;
}

int filter_by_manufacturer(const TV* tv, const char* query) {
    return strstr(tv->manufacturer, query) != NULL;
}

int filter_by_serial(const TV* tv, const char* query) {
    int target;
    return (sscanf(query, "%d", &target) == 1) && (tv->serialNumber == target);
}

int filter_by_price_range(const TV* tv, const char* query) {
    float min, max;
    return (sscanf(query, "%f-%f", &min, &max) == 2) &&
          (tv->price >= min && tv->price <= max);
}

int filter_by_weight_range(const TV* tv, const char* query) {
    float min, max;
    return (sscanf(query, "%f-%f", &min, &max) == 2) &&
          (tv->weight >= min && tv->weight <= max);
}

int filter_by_resolution(const TV* tv, const char* query) {
    int w, h;
    return (sscanf(query, "%dx%d", &w, &h) == 2) &&
          (tv->resolutionX == w && tv->resolutionY == h);
}

int filter_by_dimensions(const TV* tv, const char* query) {
    int w, h;
    return (sscanf(query, "%dx%d", &w, &h) == 2) &&
          (tv->dimensions[0] == w && tv->dimensions[1] == h);
}

/* Основная функция фильтрации */
void filter_tvs(Node* head, FilterFunc filter, const char* query) {
    int count;
    Node* current;
    clearConsole();
    printf("\n=== Результаты фильтрации ===\n");

    count = 0;
    current = head;
    while(current != NULL) {
        if(filter(&current->tv, query)) {
            printf("%d. %s\n", ++count, current->tv.name);
            printf("   Производитель: %s\n", current->tv.manufacturer);
            printf("   Цена: $%.2f\n", current->tv.price);
            printf("   Вес: %.1f кг\n", current->tv.weight);
            printf("   Разрешение: %dx%d\n",
                  current->tv.resolutionX, current->tv.resolutionY);
            printf("   Габариты: %dx%d см\n",
                  current->tv.dimensions[0], current->tv.dimensions[1]);
            printf("   Серийный: %d\n", current->tv.serialNumber);
            printf("-----------------------------\n");
        }
        current = current->next;
    }

    if(count == 0) {
        printf("Нет совпадений с критериями!\n");
    }
    else {
        printf("Найдено элементов: %d\n", count);
    }
}

void free_list(Node* head) {
    Node* current = head;
    while(current != NULL) {
        Node* next = current->next;
        free(current);
        current = next;
    }
}


/* Меню фильтрации */
void filter_menu(Node* head) {
    int choice;
    char query[100];
    FilterFunc filter;
    clearConsole();
    printf("\n=== Фильтрация по критериям ===\n");
    printf("1. По названию\n");
    printf("2. По производителю\n");
    printf("3. По серийному номеру\n");
    printf("4. По диапазону цен\n");
    printf("5. По диапазону веса\n");
    printf("6. По разрешению\n");
    printf("7. По габаритам\n");
    printf("8. Выход\n");

     filter= NULL;

    /* Ввод критерия фильтрации */
    do {
        printf("Выберите критерий (1-8): ");
        fgets(query, sizeof(query), stdin);
    } while(sscanf(query, "%d", &choice) != 1 || choice < 1 || choice > 8);

    if(choice == 8) return;

    /* Ввод параметров фильтрации */
    printf("Введите параметры фильтра: ");
    fgets(query, sizeof(query), stdin);
    query[strcspn(query, "\n")] = '\0';

    /* Выбор функции фильтрации */
    switch(choice) {
        case 1: filter = filter_by_name; break;
        case 2: filter = filter_by_manufacturer; break;
        case 3: filter = filter_by_serial; break;
        case 4: filter = filter_by_price_range; break;
        case 5: filter = filter_by_weight_range; break;
        case 6: filter = filter_by_resolution; break;
        case 7: filter = filter_by_dimensions; break;
    }

    if(filter) {
        filter_tvs(head, filter, query);
        printf("\nНажмите Enter для продолжения...");
        while(getchar() != '\n');

    }
}


void menu(Node** head, const char* filename) {
    int choice_val;
    do {
        clearConsole();
        printf("==== TV Manager ====\n");
        printf("1. Add TV\n2. Delete TV\n3. Edit TV\n4. List TVs\n");
        printf("5. Filter\n6. Sort\n7. Help\n8. Exit\n");
        printf("=====================\nChoice: ");

        choice_val = choice("", 8);

        switch (choice_val) {
            case 1: add_tv(head); break;
            case 2: delete_tv(head); break;
            case 3: edit_tv(*head); break;
            case 4: display_tvs(*head); break;
            case 5: filter_menu(*head); break;
            case 6: sort_menu(head); break;
            case 7: print_help(); break;
            case 8:
                save_to_file(filename, *head);
                printf("Goodbye!\n");
                return;
            default: printf("Invalid choice!\n");
        }

        printf("\nPress Enter to return to menu...");
        while(getchar() != '\n');
    } while (1);
}

int main() {
    Node* head = load_from_file("tvs.dat");
    menu(&head, "tvs.dat");
    free_list(head);
    return 0;
}
