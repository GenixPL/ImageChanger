#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)

/* SOME USEFUL KNOWLEDGE*/
// >> (right shif) - move given number of bits to the right (111 >> 1 -> 011)
// << (left shift) - as above in other direction
// & (bitwise AND) - take bits from left and right and execute AND gate on them (0xFF make all bits 0 exept last ones)


/* Width of bitmap used to get proper location of (x,y) pixel in *pixels array (x + (y * bitmap_width)) and check bounds*/
int bitmap_width;

/* Height of bitmap used to check bounds */
int bitmap_height;

/* Array cosisting of all pixels from bitmap */
int *pixels;

/* Array of changes in coordinates (looking from anchor point of view) which should be used for given matrix
   e.g. [(x1) -1, (y1) -1, (x2) 0, (y2) -1, ...] means that for given (x, y) pixel we take following pixels into account
   (x1, y1):(x+(-1), y+(-1)), (x2, y2):(x+(0), (y+(-1)), ...
*/
int *changes_in_coordinates_to_consider;

/* Width of matrix used to bound computations in loop */
int matrix_width;

/* Height of matrix used to bound computations in loop */
int matrix_height;

/* Offset - value added to each color */
int offset;

/* Divisor - value by which we divide colors received during computations */
float divisor;

/* Array of values of weights assigned to given changes_in_coordinates_to_consider */
float *matrix;


uchar4 RS_KERNEL convolution(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out;
	out.r = 0;
	out.g = 0;
	out.b = 0;
	out.a = in.a;


	/* DEBUG SECTION */

//	if (x < 5 && y < 2) {
//		rsDebug("RS_TAG: values from in:", in);
//
//		int c = pixels[x + (y * bitmap_width)];
//	    uchar4 out;
//		out.r = (c >> 16) & 0xFF; //take second 8 bits
//		out.g = (c >> 8) & 0xFF; //take third 8 bits
//		out.b = (c >> 0) & 0xFF; //take last 8 bits
//		out.a = (c >> 24) & 0xFF; //take first 8 bits
//	    rsDebug("RS_TAG: values from pixels:", out);
//	    rsDebug("RS_TAG: ", 0);
//
//	    rsDebug("RS_TAG: first two values from changes:", changes_in_coordinates_to_consider[0], changes_in_coordinates_to_consider[1]);
//	    rsDebug("RS_TAG: ", 0);
//
//	    rsDebug("RS_TAG: first two values from matrix", matrix[0], matrix[1]);
//	    rsDebug("RS_TAG: ------------", 0);
//	}



	/* PROPER SCRIPT */
	int r = 0;
	int g = 0;
	int b = 0;

	for(int i = 0; i < matrix_height * matrix_width; i++) {
		//get current x
		int x_i = changes_in_coordinates_to_consider[i * 2] + x;
		//get current y
		int y_i = changes_in_coordinates_to_consider[(i * 2) + 1] + y;

		//check if current x and y are in bounds of bitmap
		if ((x_i < 0 || x_i > bitmap_width) || (y_i < 0 || y_i > bitmap_height)) {
			//are out of bounds - use (x, y) from in
			r += in.r * matrix[i];
			g += in.g * matrix[i];
			b += in.b * matrix[i];

		} else {
			//are in bounds - use (x_i, y_i)
			int px = pixels[x_i + (y_i * bitmap_width)];

			//get red color
			int r_col = (px >> 16) & 0xFF;
			r += r_col * matrix[i];

			//get green color
			int g_col = (px >> 8) & 0xFF;
			g += g_col * matrix[i];

			//get blue color
			int b_col = (px >> 0) & 0xFF;
			b += b_col * matrix[i];
		}
	}

	r = offset + trunc(r / divisor);
	g = offset + trunc(g / divisor);
	b = offset + trunc(b / divisor);

	if (r < 0)
		out.r = 0;
	else if (r > 255)
		out.r = 255;
	else
		out.r = r;

	if (g < 0)
		out.g = 0;
	else if (g > 255)
		out.g = 255;
	else
		out.g = g;

	if (b < 0)
		out.b = 0;
	else if (b > 255)
		out.b = 255;
	else
		out.b = b;

    return out;
}