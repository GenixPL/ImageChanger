#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)


float gamma;

uchar4 RS_KERNEL gamma_correction(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out = in;

	float r = 255 * powr(((float) out.r / 255), gamma);
    if (r < 0)
   	    out.r = 0;
    else if (r > 255)
    	out.r = 255;
    else
        out.r = trunc(r);

    float g = 255 * powr(((float) out.g / 255), gamma);
    if (g < 0)
        out.g = 0;
    else if (g > 255)
        out.g = 255;
    else
        out.g = trunc(g);

	float b = 255 * powr(((float) out.b / 255), gamma);
    if (b < 0)
        out.b = 0;
    else if (b > 255)
        out.b = 255;
    else
        out.b = trunc(b);

    return out;
}