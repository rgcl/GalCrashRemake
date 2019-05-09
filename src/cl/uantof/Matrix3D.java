package cl.uantof;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

class Matrix3D {
    float xx = 1.0F;
    float xy;
    float xz;
    float xo;
    float yx;
    float yy = 1.0F;
    float yz;
    float yo;
    float zx;
    float zy;
    float zz = 1.0F;
    float zo;

    Matrix3D() {
    }

    void scale(float var1) {
        this.xx *= var1;
        this.xy *= var1;
        this.xz *= var1;
        this.xo *= var1;
        this.yx *= var1;
        this.yy *= var1;
        this.yz *= var1;
        this.yo *= var1;
        this.zx *= var1;
        this.zy *= var1;
        this.zz *= var1;
        this.zo *= var1;
    }

    void scale(float var1, float var2, float var3) {
        this.xx *= var1;
        this.xy *= var1;
        this.xz *= var1;
        this.xo *= var1;
        this.yx *= var2;
        this.yy *= var2;
        this.yz *= var2;
        this.yo *= var2;
        this.zx *= var3;
        this.zy *= var3;
        this.zz *= var3;
        this.zo *= var3;
    }

    void translate(float var1, float var2, float var3) {
        this.xo += var1;
        this.yo += var2;
        this.zo += var3;
    }

    void yrot(double var1) {
        var1 *= 0.017453292500000002D;
        double var3 = Math.cos(var1);
        double var5 = Math.sin(var1);
        float var7 = (float)((double)this.xx * var3 + (double)this.zx * var5);
        float var8 = (float)((double)this.xy * var3 + (double)this.zy * var5);
        float var9 = (float)((double)this.xz * var3 + (double)this.zz * var5);
        float var10 = (float)((double)this.xo * var3 + (double)this.zo * var5);
        float var11 = (float)((double)this.zx * var3 - (double)this.xx * var5);
        float var12 = (float)((double)this.zy * var3 - (double)this.xy * var5);
        float var13 = (float)((double)this.zz * var3 - (double)this.xz * var5);
        float var14 = (float)((double)this.zo * var3 - (double)this.xo * var5);
        this.xo = var10;
        this.xx = var7;
        this.xy = var8;
        this.xz = var9;
        this.zo = var14;
        this.zx = var11;
        this.zy = var12;
        this.zz = var13;
    }

    void xrot(double var1) {
        var1 *= 0.017453292500000002D;
        double var3 = Math.cos(var1);
        double var5 = Math.sin(var1);
        float var7 = (float)((double)this.yx * var3 + (double)this.zx * var5);
        float var8 = (float)((double)this.yy * var3 + (double)this.zy * var5);
        float var9 = (float)((double)this.yz * var3 + (double)this.zz * var5);
        float var10 = (float)((double)this.yo * var3 + (double)this.zo * var5);
        float var11 = (float)((double)this.zx * var3 - (double)this.yx * var5);
        float var12 = (float)((double)this.zy * var3 - (double)this.yy * var5);
        float var13 = (float)((double)this.zz * var3 - (double)this.yz * var5);
        float var14 = (float)((double)this.zo * var3 - (double)this.yo * var5);
        this.yo = var10;
        this.yx = var7;
        this.yy = var8;
        this.yz = var9;
        this.zo = var14;
        this.zx = var11;
        this.zy = var12;
        this.zz = var13;
    }

    void zrot(double var1) {
        var1 *= 0.017453292500000002D;
        double var3 = Math.cos(var1);
        double var5 = Math.sin(var1);
        float var7 = (float)((double)this.yx * var3 + (double)this.xx * var5);
        float var8 = (float)((double)this.yy * var3 + (double)this.xy * var5);
        float var9 = (float)((double)this.yz * var3 + (double)this.xz * var5);
        float var10 = (float)((double)this.yo * var3 + (double)this.xo * var5);
        float var11 = (float)((double)this.xx * var3 - (double)this.yx * var5);
        float var12 = (float)((double)this.xy * var3 - (double)this.yy * var5);
        float var13 = (float)((double)this.xz * var3 - (double)this.yz * var5);
        float var14 = (float)((double)this.xo * var3 - (double)this.yo * var5);
        this.yo = var10;
        this.yx = var7;
        this.yy = var8;
        this.yz = var9;
        this.xo = var14;
        this.xx = var11;
        this.xy = var12;
        this.xz = var13;
    }

    void mult(Matrix3D var1) {
        float var2 = this.xx * var1.xx + this.yx * var1.xy + this.zx * var1.xz;
        float var3 = this.xy * var1.xx + this.yy * var1.xy + this.zy * var1.xz;
        float var4 = this.xz * var1.xx + this.yz * var1.xy + this.zz * var1.xz;
        float var5 = this.xo * var1.xx + this.yo * var1.xy + this.zo * var1.xz + var1.xo;
        float var6 = this.xx * var1.yx + this.yx * var1.yy + this.zx * var1.yz;
        float var7 = this.xy * var1.yx + this.yy * var1.yy + this.zy * var1.yz;
        float var8 = this.xz * var1.yx + this.yz * var1.yy + this.zz * var1.yz;
        float var9 = this.xo * var1.yx + this.yo * var1.yy + this.zo * var1.yz + var1.yo;
        float var10 = this.xx * var1.zx + this.yx * var1.zy + this.zx * var1.zz;
        float var11 = this.xy * var1.zx + this.yy * var1.zy + this.zy * var1.zz;
        float var12 = this.xz * var1.zx + this.yz * var1.zy + this.zz * var1.zz;
        float var13 = this.xo * var1.zx + this.yo * var1.zy + this.zo * var1.zz + var1.zo;
        this.xx = var2;
        this.xy = var3;
        this.xz = var4;
        this.xo = var5;
        this.yx = var6;
        this.yy = var7;
        this.yz = var8;
        this.yo = var9;
        this.zx = var10;
        this.zy = var11;
        this.zz = var12;
        this.zo = var13;
    }

    void unit() {
        this.xo = 0.0F;
        this.xx = 1.0F;
        this.xy = 0.0F;
        this.xz = 0.0F;
        this.yo = 0.0F;
        this.yx = 0.0F;
        this.yy = 1.0F;
        this.yz = 0.0F;
        this.zo = 0.0F;
        this.zx = 0.0F;
        this.zy = 0.0F;
        this.zz = 1.0F;
    }

    void transform(float[] var1, int[] var2, int var3) {
        float var4 = this.xx;
        float var5 = this.xy;
        float var6 = this.xz;
        float var7 = this.xo;
        float var8 = this.yx;
        float var9 = this.yy;
        float var10 = this.yz;
        float var11 = this.yo;
        float var12 = this.zx;
        float var13 = this.zy;
        float var14 = this.zz;
        float var15 = this.zo;
        int var16 = var3 * 3;

        while(true) {
            var16 -= 3;
            if (var16 < 0) {
                return;
            }

            float var17 = var1[var16];
            float var18 = var1[var16 + 1];
            float var19 = var1[var16 + 2];
            var2[var16] = (int)(var17 * var4 + var18 * var5 + var19 * var6 + var7);
            var2[var16 + 1] = (int)(var17 * var8 + var18 * var9 + var19 * var10 + var11);
            var2[var16 + 2] = (int)(var17 * var12 + var18 * var13 + var19 * var14 + var15);
        }
    }

    public String toString() {
        return "[" + this.xo + "," + this.xx + "," + this.xy + "," + this.xz + ";" + this.yo + "," + this.yx + "," + this.yy + "," + this.yz + ";" + this.zo + "," + this.zx + "," + this.zy + "," + this.zz + "]";
    }
}
