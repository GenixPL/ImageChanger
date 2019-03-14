#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)


int numberOfShades;
float thresholdValue;

uchar4 RS_KERNEL grey_levels(uchar4 in, uint32_t x, uint32_t y) {

	/* DEBUG */
//	if (x == 0 && y == 0) {
//		rsDebug("DEBUG:", numberOfShades);
//		rsDebug("DEBUG:", thresholdValue);
//	}


	/* PROPER SCRIPT */
	uchar4 out = in;

	int grey = (0.2989 * in.r + 0.5870 * in.g + 0.1140 * in.b);
	int levels[numberOfShades];

	//set boundary for each level
    levels[0] = 0;
    for (int i = 1; i < numberOfShades - 1; i++) {
        levels[i] = (255 / (numberOfShades - 1)) * i;
    }
    levels[numberOfShades - 1] = 255;

	/* DEBUG */
//	if (x == 0 && y ==0) {
//		for (int i = 0; i < k; i++) {
//			rsDebug("DEBUG: level:", i, levels[i]);
//		}
//	}


	int ci = levels[numberOfShades - 2];
	int ci1 = levels[numberOfShades - 1];

	for (int j = 0; j < numberOfShades - 1; j++) {
		if (grey >= levels[j] && grey <= levels[j + 1]) {
			ci = levels[j];
			ci1 = levels[j + 1];
		}
	}

	int ct = ci + thresholdValue * (ci1 - ci);

	if (grey < ct) {
		grey = ci;
	} else {
		grey = ci1;
	}

	out.r = grey;
	out.g = grey;
	out.b = grey;

    return out;
}