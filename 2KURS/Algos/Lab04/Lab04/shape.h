//#pragma once

#define __SHAPE

#include <list>
#include "screen.h"
using std::list;
//==1. Поддержка экрана в форме матрицы символов ==
extern char screen[YMAX] [XMAX];	
enum color { black = '*', white = '.' };
void screen_init();
void screen_destroy();
bool on_screen(int a, int b); // проверка попадания точки на экран

void put_point(int a, int b);

void put_line(int x0, int y0, int x1, int y1);

void screen_clear();
void screen_refresh();
//== 2. Библиотека фигур ==
class shape {   // Виртуальный базовый класс «фигура»
public:
  static list<shape*> shapes;       // Список фигур (один на все фигуры!)
  shape( ) { shapes.push_back(this); } //Фигура присоединяется к списку
  virtual point north( ) const = 0;  //Точки для привязки
  virtual point south( ) const = 0;
  virtual point east( ) const = 0;
  virtual point west( ) const = 0;
  virtual point neast( ) const = 0;
  virtual point seast( ) const = 0;
  virtual point nwest( ) const = 0;
  virtual point swest( ) const = 0;
  virtual void draw( ) = 0;		//Рисование
  virtual void move(int, int) = 0;	//Перемещение
  virtual void resize(double) = 0;    	//Изменение размера
  virtual ~shape( ) { shapes.remove(this); } //Деструктор
};
void shape_refresh();   // Перерисовка всех фигур на экране


class rotatable : virtual public shape { 	//Фигуры, пригодные к повороту 
protected:
	enum class rotated { left, no, right };
	rotated state;           //Текущее состояние поворота
public:
	rotatable(rotated r = rotated::no) : state(r) { }
	virtual ~rotatable() {};
	virtual void rotate_left() { state = rotated::left; }
	//Повернуть влево
	
	virtual void rotate_right() { state = rotated::right; }
	//Повернуть вправо
};
class reflectable : virtual public shape { 	//Фигуры, пригодные к зеркальному отражению
protected:
	bool hor, vert;         //Текущее состояние отражения
public:
	reflectable(bool h = false, bool v = false) : hor(h), vert(v) { }
	void flip_horisontally() { hor = !hor; }		//Отразить горизонтально
	void flip_vertically() { vert = !vert; } 	//Отразить вертикально
};

class line : public shape {        // ==== Прямая линия ====
/* отрезок прямой ["w", "e"].
   north( ) определяет точку «выше центра отрезка и так далеко
   на север, как самая его северная точка», и т. п. */
protected:
	point w, e;
public:

  line(point a, point b) : w(a), e(b) { }; //Произвольная линия (по двум точкам)
  line(point a, int L) : w(point(a.x + L - 1, a.y)), e(a) {  }; //Горизонтальная линия
  point north( ) const { return point((w.x+e.x)/2, e.y<w.y? w.y : e.y); }
  point south( ) const { return point((w.x+e.x)/2, e.y<w.y? e.y : w.y); }
  point east( ) const { return point(e.x<w.x? w.x : e.x, (w.y+e.y)/2); }
  point west( ) const { return point(e.x<w.x? e.x : w.x, (w.y+e.y)/2); }
  point neast( ) const { return point(w.x<e.x? e.x : w.x, e.y<w.y? w.y : e.y); }
  point seast( ) const { return point(w.x<e.x? e.x : w.x, e.y<w.y? e.y : w.y); }
  point nwest( ) const { return point(w.x<e.x? w.x : e.x, e.y<w.y? w.y : e.y); }
  point swest( ) const { return point(w.x<e.x? w.x : e.x, e.y<w.y? e.y : w.y); }
  void move(int a, int b) 	{ w.x += a; w.y += b; e.x += a; e.y += b; }
  void draw( ) { put_line(w, e); }
  void resize(double d)                // Изменение длины линии в (d) раз
  { e.x = w.x + (e.x - w.x) * d; e.y = w.y + (e.y - w.y) * d; }
};
class rectangle : public rotatable {      // ==== Прямоугольник ====
/* nw ------ n ------ ne
   |		       |
   |		       |
   w	   c            e
   |		       |
   |		       |
   sw ------- s ------ se */
protected:
  point sw, ne;
public:
  rectangle(point a, point b) :  sw(a), ne(b) { }
  point north( ) const { return point((sw.x + ne.x) / 2, ne.y); }
  point south( ) const { return point((sw.x + ne.x) / 2, sw.y); }
  point east( ) const { return point(ne.x, (sw.y + ne.y) / 2); }
  point west( ) const { return point(sw.x, (sw.y + ne.y) / 2); }
  point neast( ) const { return ne; }
  point seast( ) const { return point(ne.x, sw.y); }
  point nwest( ) const { return point(sw.x, ne.y); }
  point swest( ) const { return sw; }
  void rotate_right( )           // Поворот вправо относительно se
	{ int w = ne.x - sw.x, h = ne.y - sw.y; // (учитывается масштаб по осям)
	  sw.x = ne.x - h * 2; ne.y = sw.y + w / 2;	}
  void rotate_left() // Поворот влево относительно sw
	{ int w = ne.x - sw.x, h = ne.y - sw.y; 
	  ne.x = sw.x + h * 2; ne.y = sw.y + w / 2; }
  void move(int a, int b)
	{ sw.x += a; sw.y += b; ne.x += a; ne.y += b; }
  void resize(int d) 
  { ne.x = sw.x + (ne.x - sw.x) * d; ne.y = sw.y + (ne.y - sw.y) * d; }
  void resize(double d)
	{
	  ne.x = sw.x + (ne.x - sw.x) * d;    ne.y = sw.y + (ne.y - sw.y) * d;
	}
  void draw( )
  { 
    put_line(nwest( ), ne);   put_line(ne, seast( ));
    put_line(seast( ), sw);   put_line(sw, nwest( ));
  }
};
void up(shape& p, const shape& q);

class myshape : public rectangle {   // Моя фигура ЯВЛЯЕТСЯ
	int w, h;			             //        прямоугольником
	line l_eye;    // левый глаз – моя фигура СОДЕРЖИТ линию
	line r_eye;   // правый глаз
	line mouth;  // рот
public:
	myshape(point, point);
	void draw();
	void move(int, int);
	void resize(double r) { rectangle::resize(r); rectangle::move(w * (1 - r) * 0.5, h * (1 - r) * 0.5); }
	void rotate_left() {}
	void rotate_right() {}
	point left_eye() {
		return point(l_eye.north().x, l_eye.west().y);
	}
	point right_eye() {
		return point(r_eye.north().x, r_eye.west().y);
	}
};

class h_circle : public rectangle, public reflectable {
public:
	h_circle(point a, int rd)
		: rectangle(point(a.x - rd, a.y), point(a.x + rd, a.y + rd * 0.7 + 1)) { }
	void draw();
	void flip_horisontally() { };   // Отразить горизонтально (пустая функция)
	void rotate_right() {}     // Повернуть вправо 
	void rotate_left() {}      // Повернуть влево
};

void down(shape& p, const shape& q);

