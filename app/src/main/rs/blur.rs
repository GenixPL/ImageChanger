#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)


int width;
int height;
int *pixels;

uchar4 RS_KERNEL blur(uchar4 in, uint32_t x, uint32_t y) {

	/* DEBUG SECION */

	//check using console if everything in import (*pixels) works properly
	//if (x < 10 && y < 2) {
    //    rsDebug("RS_TAG: values from function:", in);

	//	  int c = pixels[x + (y * width)];
	//    uchar4 out;
    //    out.r = (c >> 16) & 0xFF;
    //    out.g = (c >> 8) & 0xFF;
    //    out.b = (c >> 0) & 0xFF;
    //    out.a = (c >> 24) & 0xFF;
	//    rsDebug("RS_TAG: values from array:", out);
	//}
	//return in


	/* PROPER SCRIPT */

	uchar4 out;
	out.a = in.a;

    return in; //change it
}