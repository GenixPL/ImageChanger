#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)


float contrast_level;

uchar4 RS_KERNEL contrast_enhancement(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out = in;

	float r = in.r * contrast_level;
    if (r < 0)
   	    out.r = 0;
    else if (r > 255)
    	out.r = 255;
    else
        out.r = trunc(r);

    float g = in.g * contrast_level;
    if (g < 0)
        out.g = 0;
    else if (g > 255)
        out.g = 255;
    else
        out.g = trunc(g);

	float b = in.b * contrast_level;
    if (b < 0)
        out.b = 0;
    else if (b > 255)
        out.b = 255;
    else
        out.b = trunc(b);

    return out;
}