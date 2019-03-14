#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)


int k; //number of shades
int n; //matrix dimentions
float *matrix;

uchar4 RS_KERNEL ordered_dithering(uchar4 in, uint32_t x, uint32_t y) {

    /* DEBUG */
//	if (x == 0 && y == 0) {
//		rsDebug("DEBUG: matrix[0-3]:", matrix[0], matrix[1], matrix[2], matrix[3]);
//		rsDebug("DEBUG: n:", n);
//		rsDebug("DEBUG: k:", k);
//	}


    /* PROPER SCIRPT */
	uchar4 out = in;
	int levels[k];

    //set boundary for each level
	levels[0] = 0;
    for (int i = 1; i < k - 1; i++) {
    	levels[i] = (255 / (k - 1)) * i;
    }
    levels[k - 1] = 255;

    /* DEBUG */
//	if (x == 0 && y == 0) {
//		for (int i = 0; i < k; i++) {
//			rsDebug("DEBUG: level:", i, levels[i]);
//		}
//	}

	int dist_to_lower; //distance form color to lower boundary
	int dist_to_upper; //distance from color to upper boundary
	int first_boundary; //the nearest boundary
	int second_boundary; //the second nearest boundary
	int dist_to_first; //distance to the nearest boundary
	int dist_lower_to_upper; //distance between the two nearest boundaries

	//RED
	for (int i = 0; i < k - 1; i++) {
		//find the nearest colors
		if (levels[i] <= in.r && levels[i + 1] >= in.r) {
			dist_to_lower = in.r - levels[i];
			dist_to_upper = levels[i + 2] - in.r;
			dist_lower_to_upper = levels[i + 1] - levels[i];

			//find the nearest color
            if (dist_to_lower < dist_to_upper) {
            	//we are closer to first boundary
            	first_boundary = levels[i];
            	dist_to_first = dist_to_lower;

            } else {
            	//we are closer to second boundary
            	second_boundary = levels[i + 1];
            	dist_to_first = dist_to_upper;
            }

			float matrix_value = matrix[((y % n) * n) + (x % n)];

			float val = dist_to_first / dist_lower_to_upper;
			if (val < matrix_value) {
				out.r = first_boundary;
			} else {
				out.r = second_boundary;
			}

			break;
		}
	}

	//GREEN
	for (int i = 0; i < k - 1; i++) {
		//find the nearest colors
		if (levels[i] <= in.g && levels[i + 1] >= in.g) {
			dist_to_lower = in.g - levels[i];
			dist_to_upper = levels[i + 2] - in.g;
			dist_lower_to_upper = levels[i + 1] - levels[i];

			//find the nearest color
            if (dist_to_lower < dist_to_upper) {
            	//we are closer to first boundary
            	first_boundary = levels[i];
            	dist_to_first = dist_to_lower;

            } else {
            	//we are closer to second boundary
            	second_boundary = levels[i + 1];
            	dist_to_first = dist_to_upper;
            }

			float matrix_value = matrix[((y % n) * n) + (x % n)];

			float val = dist_to_first / dist_lower_to_upper;
			if (val < matrix_value) {
				out.g = first_boundary;
			} else {
				out.g = second_boundary;
			}

			break;
		}
	}

	//BLUE
	for (int i = 0; i < k - 1; i++) {
		//find the nearest colors
		if (levels[i] <= in.b && levels[i + 1] >= in.b) {
			dist_to_lower = in.b - levels[i];
			dist_to_upper = levels[i + 2] - in.b;
			dist_lower_to_upper = levels[i + 1] - levels[i];

			//find the nearest color
            if (dist_to_lower < dist_to_upper) {
            	//we are closer to first boundary
            	first_boundary = levels[i];
            	dist_to_first = dist_to_lower;

            } else {
            	//we are closer to second boundary
            	second_boundary = levels[i + 1];
            	dist_to_first = dist_to_upper;
            }

			float matrix_value = matrix[((y % n) * n) + (x % n)];

			float val = dist_to_first / dist_lower_to_upper;
			if (val < matrix_value) {
				out.b = first_boundary;
			} else {
				out.b = second_boundary;
			}

			break;
		}
	}

    return out;
}