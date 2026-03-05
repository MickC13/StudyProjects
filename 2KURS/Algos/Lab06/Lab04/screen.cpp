#include "screen.h"
void put_line(point a, point b)
{
	put_line(a.x, a.y, b.x, b.y);
}
void put_point(point p) { put_point(p.x, p.y); }