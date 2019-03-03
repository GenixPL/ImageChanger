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

/* Array of values of ranks assigned to given changes_in_coordinates_to_consider */
float *divisors;


uchar4 RS_KERNEL blur(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out = in;

	/* DEBUG SECION */

	//check using console if everything in import (*pixels) works properly
	//if (x < 10 && y < 2) {
    //    rsDebug("RS_TAG: values from function:", in);

	//	  int c = pixels[x + (y * bitmap_width)];
	//    uchar4 out;
    //    out.r = (c >> 16) & 0xFF; //take second 8 bits
    //    out.g = (c >> 8) & 0xFF; //take third 8 bits
    //    out.b = (c >> 0) & 0xFF; //take last 8 bits
    //    out.a = (c >> 24) & 0xFF; //take first 8 bits
	//    rsDebug("RS_TAG: values from array:", out);
	//}



	/* PROPER SCRIPT */

	for(int i = 0; i < matrix_height * matrix_width; i++) {
		//get current x
		int x_i = changes_in_coordinates_to_consider[i * 2] + x;
		//get current y
		int y_i = changes_in_coordinates_to_consider[(i * 2) + 1] + y;

		//check if current x and y are in bounds of bitmap
		if ((x < 0 || x > bitmap_width)){
			;
		}
	}

    return out;
}