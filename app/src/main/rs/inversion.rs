#pragma version(1)
#pragma rs java_package_name(com.genix.imagechanger)

uchar4 RS_KERNEL inversion(uchar4 in, uint32_t x, uint32_t y) {
    uchar4 out = in;
    out.r = 255 - in.r;
    out.g = 255 - in.g;
    out.b = 255 - in.b;

    return out;
}