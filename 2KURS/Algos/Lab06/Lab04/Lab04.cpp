// Lab04.cpp
#include <iostream>
#include <locale.h>
#include "shape.h"
#include "screen.h"

#include <stdexcept>
#include <string>

class ShapeException : public std::runtime_error {
public:
    ShapeException(const std::string& msg)
        : std::runtime_error(msg) {}
};

class InvalidShapeParameters : public ShapeException {
public:
    InvalidShapeParameters(const std::string& msg)
        : ShapeException(msg) {}
};

class ScreenOverflowException : public ShapeException {
public:
    ScreenOverflowException(const std::string& msg)
        : ShapeException(msg) {}
};
// Недостающие функции примыкания (влево и вправо)
void left(shape& p, const shape& q) {
    point w = q.west();
    point e = p.east();
    p.move(w.x - e.x - 1, w.y - e.y);
}

void right(shape& p, const shape& q) {
    point e = q.east();
    point w = p.west();
    p.move(e.x - w.x + 1, e.y - w.y);
}

// Прямоугольник с прямым крестом (фигура 1+10) – локальный класс,
// чтобы не конфликтовать с одноимённым классом в shape.cpp
namespace {
    class rectangle_cross : public rectangle {
        line vline;   // вертикальная линия через центр
        line hline;   // горизонтальная линия через центр
        
        // Обновляет линии после изменения размеров или поворота
        void update_lines() {
            point sw = swest();
            point ne = neast();
            int mid_x = (sw.x + ne.x) / 2;
            int mid_y = (sw.y + ne.y) / 2;
            vline = line(point(mid_x, sw.y), point(mid_x, ne.y));
            hline = line(point(sw.x, mid_y), point(ne.x, mid_y));
        }
        


    public:
        rectangle_cross(point a, point b) : rectangle(a, b), vline(point(0,0), point(0,0)),
            hline(point(0,0), point(0,0)) {
            if (!on_screen(a.x, a.y) || !on_screen(b.x, b.y))
                throw InvalidShapeParameters("Rectangle outside screen");

            if (a.x == b.x || a.y == b.y)
                throw InvalidShapeParameters("Zero width or height");

            update_lines();
        }


        void draw() override {
            rectangle::draw();   // контур прямоугольника
            vline.draw();        // вертикальная линия
            hline.draw();        // горизонтальная линия
        }

        void flip_left(){
            move(sw.x - ne.x -1, 0);
        }

        void flip_right() {
            move(-sw.x + ne.x+1, 0);
        }


        void move(int dx, int dy) override {
            point sw = swest();
            point ne = neast();

            if (!on_screen(sw.x + dx, sw.y + dy) ||
                !on_screen(ne.x + dx, ne.y + dy))
                throw ScreenOverflowException("Move outside screen");

            rectangle:: move(dx, dy);
            vline.move(dx, dy);
            hline.move(dx, dy);
        }

        void move_to(point p) {
            move(p.x - sw.x - (ne.x-sw.x)/2, p.y-sw.y -(ne.y - sw.y) / 2);
        }
        void resize(double r) override {
            rectangle::resize(r);
            update_lines();
        }

        void rotate_left () override {
            rectangle::rotate_left();
            update_lines();
        }

        void rotate_right() override {
            rectangle::rotate_right();
            update_lines();
        }

        // Поворот на 180° (вниз)
        void rotate_down() {
            rotate_left();
            rotate_left();
        }

    private:
        rectangle_cross(const rectangle_cross&) = delete;
        rectangle_cross& operator=(const rectangle_cross&) = delete;
    };
}

int main() {
    try {


        setlocale(LC_ALL, "Rus");
        screen_init();

        // == 1. Объявление набора фигур ==
        rectangle hat(point(0, 0), point(14, 5));
        line brim(point(20, 9), 17);
        myshape face(point(15, 10), point(27, 18));
        //h_circle beard(point(40, 10), 5);

        // Три прямоугольника с крестом для позиций 1, 10, 11
        rectangle_cross cross1(point(50, 30), point(56, 36));   // позиция 1 (галстук)
        rectangle_cross cross10(point(60, 30), point(62, 33));  // позиция 10 (левый глаз)
        rectangle_cross cross11(point(70, 30), point(72, 33));  // позиция 11 (правый глаз)

        shape_refresh();
        std::cout << "=== Generated... ===\n";
        std::cin.get(); // Смотрим исходный набор

        // == 2. Подготовка к сборке ==
        hat.rotate_right();
        brim.resize(2.0);
        face.resize(1.2);
        //beard.flip_vertically();
        //beard.resize(1.2);

        // Повороты добавленных фигур согласно заданию:
        // позиция 1 — вниз (180°)
        cross1.rotate_down();
        // позиция 10 — влево
       // cross10.rotate_left();
        // позиция 11 — вправо
        //cross11.rotate_right();

        // Небольшое изменение размеров для лучшего вида (необязательно)


        shape_refresh();
        std::cout << "=== Prepared... ===\n";
        std::cin.get(); // Смотрим результат поворотов

        // == 3. Сборка изображения ==
        up(brim, face);
        up(hat, brim);
        //down(beard, face);

        // Прикрепляем прямоугольники с крестом
        down(cross1, face);    // позиция 1 (галстук) — вниз от лица
        left(cross10, face);   // позиция 10 (левый глаз) — слева от лица
        right(cross11, face);  // позиция 11 (правый глаз) — справа от лица

        cross10.move_to(face.left_eye());
        cross11.move_to(face.right_eye());

        shape_refresh();
        std::cout << "=== Ready! ===\n";
        std::cin.get(); // Смотрим результат

        screen_destroy();
    }
    catch (const InvalidShapeParameters& e) {
        std::cout << "Ошибка параметров фигуры: "
            << e.what() << std::endl;
    }
    catch (const ScreenOverflowException& e) {
        std::cout << "Ошибка выхода за экран: "
            << e.what() << std::endl;
    }
    catch (const std::exception& e) {
        std::cout << "Другая ошибка: "
            << e.what() << std::endl;
    }
    return 0;
}