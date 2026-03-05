// Пополнение и использование библиотеки фигур
	//связь с ОС (пример для Visual C++2017)
#include <locale.h>
#include <iostream>
#include "screen.h"
#include "shape.h"
// ПРИМЕР ДОБАВКИ: дополнительный фрагмент – полуокружность
char screen[YMAX][XMAX]; 
list<shape*> shape::shapes;

void h_circle :: draw()   //Алгоритм Брезенхэма для окружностей
{  // (выдаются два сектора, указываемые значением reflected::vert)
    int x0 = (sw.x+ne.x)/2, y0 = vert ? ne.y : sw.y;
	int radius = (ne.x - sw.x)/2;
	int x = 0, y = radius, delta = 2 - 2 * radius, error = 0;
    while(y >= 0) { // Цикл рисования
	    if(vert) { put_point(x0 + x, y0 - y*0.7); put_point(x0 - x, y0 - y*0.7); }
		else { put_point(x0 + x, y0 + y*0.7); put_point(x0 - x, y0 + y*0.7); }
        error = 2 * (delta + y) - 1;
        if(delta < 0 && error <= 0) { ++x; delta += 2 * x + 1; continue; }
        error = 2 * (delta - x) - 1;
        if(delta > 0 && error > 0) { --y; delta += 1 - 2 * y; continue; }
        ++x; delta += 2 * (x - y);  --y;
	}
//	rectangle::draw();
}
// ПРИМЕР ДОБАВКИ: дополнительная функция присоединения…
void down(shape &p,  const shape &q)
{    point n = q.south( );
     point s = p.north( );
     p.move(n.x - s.x, n.y - s.y - 1); }

// Прямоугольник с крестом
class rectangle_cross : public rectangle {
	line diag1;
	line diag2;

public:
	rectangle_cross(point a, point b)
		: rectangle(a, b),
		diag1(a, b),
		diag2(point(a.x, b.y), point(b.x, a.y))
	{}

	void draw() override {
		rectangle::draw();
		diag1.draw();
		diag2.draw();
	}

	void move(int dx, int dy) override {
		rectangle::move(dx, dy);
		diag1.move(dx, dy);
		diag2.move(dx, dy);
	}

	void resize(double r) override {
		rectangle::resize(r);

		point sw = swest();
		point ne = neast();

		diag1 = line(sw, ne);
		diag2 = line(point(sw.x, ne.y), point(ne.x, sw.y));
	}

private:
	rectangle_cross(const rectangle_cross&) = delete;
	rectangle_cross& operator=(const rectangle_cross&) = delete;
};

myshape :: myshape(point a, point b)
	: rectangle(a, b),	//Инициализация базового класса
	  w(neast( ).x - swest( ).x + 1), // Инициализация данных
	  h(neast( ).y - swest( ).y + 1), // – строго в порядке объявления!
	  l_eye(point(swest( ).x + 2, swest( ).y + h * 3 / 4), 2),
	  r_eye(point(swest( ).x + w - 4, swest( ).y + h * 3 / 4), 2),
	  mouth(point(swest( ).x + 2, swest( ).y + h / 4), w - 4) 
{ }
void myshape :: draw( )
{
	 rectangle :: draw( );      //Контур лица (глаза и нос рисуются сами!) 
	 int a = (swest( ).x + neast( ).x) / 2;
	 int b = (swest( ).y + neast( ).y) / 2;
	 put_point(point(a, b));   // Нос – существует только на рисунке!
}
void myshape :: move(int a, int b)
{
	 rectangle :: move(a, b);
	 l_eye.move(a, b);  r_eye.move(a, b);
	 mouth.move(a, b);
}


void screen_init()
{
	for (auto y = 0; y < YMAX; ++y)
		for (auto& x : screen[y])  x = white;
}
void screen_destroy()
{
	for (auto y = 0; y < YMAX; ++y)
		for (auto& x : screen[y])  x = black;
}
bool on_screen(int a, int b) // проверка попадания точки на экран
{
	return 0 <= a && a < XMAX && 0 <= b && b < YMAX;
}
void put_point(int a, int b)
{
	if (on_screen(a, b)) screen[b][a] = black;
}
void put_line(int x0, int y0, int x1, int y1)
/* Алгоритм Брезенхэма для прямой:
рисование отрезка прямой от (x0, y0) до (x1, y1).
Уравнение прямой: b(x–x0) + a(y–y0) = 0.
Минимизируется величина abs(eps), где eps = 2*(b(x–x0)) + a(y–y0).  */
{
	int dx = 1;
	int a = x1 - x0;   if (a < 0) dx = -1, a = -a;
	int dy = 1;
	int b = y1 - y0;   if (b < 0) dy = -1, b = -b;
	int two_a = 2 * a;
	int two_b = 2 * b;
	int xcrit = -b + two_a;
	int eps = 0;
	for (;;) { //Формирование прямой линии по точкам
		put_point(x0, y0);
		if (x0 == x1 && y0 == y1) break;
		if (eps <= xcrit) x0 += dx, eps += two_b;
		if (eps >= a || a < b) y0 += dy, eps -= two_a;
	}
}
void screen_clear() { screen_init(); } //Очистка экрана
void screen_refresh() // Обновление экрана
{
	for (int y = YMAX - 1; 0 <= y; --y) { // с верхней строки до нижней
		for (auto x : screen[y])                 // от левого столбца до правого
			std::cout << x;
		std::cout << '\n';
	}
}

void shape_refresh()    // Перерисовка всех фигур на экране
{
	screen_clear();
	for (auto p : shape::shapes) p->draw(); //Динамическое связывание!!!
	screen_refresh();
}

void up(shape& p, const shape& q) // поместить фигуру p над фигурой q
{	//Это ОБЫЧНАЯ функция, не член класса! Динамическое связыва-ние!!
	point n = q.north();
	point s = p.south();
	p.move(n.x - s.x, n.y - s.y + 1);
}
