#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)


float brightness;

uchar4 RS_KERNEL brightness_correction(uchar4 in, uint32_t x, uint32_t y) {
    uchar4 out = in;

	float r = in.r + brightness;
	if (r < 0)
		out.r = 0;
	else if (r > 255)
		out.r = 255;
	else
        out.r = trunc(r);

    float g = in.g + brightness;
    if (g < 0)
    	out.g = 0;
    else if (g > 255)
        out.g = 255;
    else
        out.g = trunc(g);

    float b = in.b + brightness;
    if (b < 0)
        out.b = 0;
    else if (b > 255)
    	out.b = 255;
    else
        out.b = trunc(b);

    return out;
}