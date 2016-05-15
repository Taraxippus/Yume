package com.taraxippus.yume.util;

import java.util.Random;

public class SimplexNoise_octave
{
    private static final long seed = 0;
    private static final int swapCount = 512;

    private final short perm[] = new short[512];
    private final short permMod12[] = new short[512];

    private static final Grad grad3[] =
            {
                    new Grad(1,1), new Grad(-1,1), new Grad(1,-1), new Grad(-1,-1),
                    new Grad(1,0), new Grad(-1,0), new Grad(1,0), new Grad(-1,0),
                    new Grad(0,1), new Grad(0,-1), new Grad(0,1), new Grad(0,-1)
            };

    private static final short p_supply[] =
            {
                    151,160,137,91,90,15,
                    131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
                    190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
                    88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
                    77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
                    102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
                    135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
                    5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
                    223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
                    129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
                    251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
                    49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
                    138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
            };

    public SimplexNoise_octave(long seed)
    {
        short[] p = p_supply.clone();

        if (seed == SimplexNoise_octave.seed)
        {
            Random rand = new Random();
            seed = rand.nextLong();
        }

        Random rand = new Random(seed);

        int swapFrom, swapTo;
        short temp;

        for (int i = 0; i < swapCount; i++)
        {
            swapFrom = rand.nextInt(p.length);
            swapTo = rand.nextInt(p.length);

            temp = p[swapFrom];
            p[swapFrom] = p[swapTo];
            p[swapTo] = temp;
        }


        for (int i = 0; i < 512; i++)
        {
            perm[i] = p[i & 255];
            permMod12[i] = (short) (perm[i] % 12);
        }
    }

    private static final float F2 = 0.5F * ((float)Math.sqrt(3.0F) - 1.0F);
    private static final float G2 = (3.0F - (float)Math.sqrt(3.0F)) / 6.0F;

    int xi;
    private int fastFloor(final float x)
    {
        xi = (int)x;
        return x < xi ? xi - 1 : xi;
    }

    private int fastFloor2(final float x)
    {
        int xi = (int)x;
        return x < xi ? xi - 1 : xi;
    }

    private float dot(Grad g, float x, float y)
    {
        return g.x * x + g.y * y;
    }


    float n, s, t, x0, y0, x1, y1, x2, y2, t0, t1, t2;
    int i, j, i1, j1, ii, jj, gi0, gi1, gi2;

    boolean inUse;

    public float noise(final float xin, final float yin)
    {
        if (inUse)
            return noise2(xin, yin);

        inUse = true;
        n = 0;
        s = (xin + yin) * F2;

        i = fastFloor(xin + s);
        j = fastFloor(yin + s);

        t = (i + j) * G2;
        x0 = xin - (i - t);
        y0 = yin - (j - t);

        if(x0 > y0)
        {
            i1 = 1;
            j1 = 0;
        }
        else
        {
            i1 = 0;
            j1 = 1;
        }

        x1 = x0 - i1 + G2;
        y1 = y0 - j1 + G2;
        x2 = x0 - 1.0F + 2.0F * G2;
        y2 = y0 - 1.0F + 2.0F * G2;

        ii = i & 255;
        jj = j & 255;
        gi0 = permMod12[ii + perm[jj]];
        gi1 = permMod12[ii + i1 + perm[jj + j1]];
        gi2 = permMod12[ii + 1 + perm[jj + 1]];

        t0 = 0.5F - x0 * x0 - y0 * y0;
        if (t0 >= 0)
        {
            n += t0 * t0 * t0 * t0 * (grad3[gi0].x * x0 + grad3[gi0].y * y0);
        }

        t1 = 0.5F - x1 * x1 - y1 * y1;
        if (t1 >= 0)
        {
            n += t1 * t1 * t1 * t1 * (grad3[gi1].x * x1 + grad3[gi1].y * y1);
        }

        t2 = 0.5F - x2 * x2 - y2 * y2;
        if (t2 >= 0)
        {
            n += t2 * t2 * t2 * t2 * (grad3[gi2].x * x2 + grad3[gi2].y * y2);
        }

        inUse = false;
        return 70F * n;
    }

    // 2D simplex noise
    public float noise2(final float xin, final float yin)
    {
        float n = 0;
        float s = (xin+yin)*F2;
        int i = fastFloor2(xin+s);
        int j = fastFloor2(yin+s);
        float t = (i+j)*G2;
        float x0 = xin-(i-t);
        float y0 = yin-(j-t);

        int i1, j1;
        if(x0>y0) {i1=1; j1=0;}
        else {i1=0; j1=1;}

        float x1 = x0 - i1 + G2;
        float y1 = y0 - j1 + G2;
        float x2 = x0 - 1.0F + 2.0F * G2;
        float y2 = y0 - 1.0F + 2.0F * G2;
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = permMod12[ii+perm[jj]];
        int gi1 = permMod12[ii+i1+perm[jj+j1]];
        int gi2 = permMod12[ii+1+perm[jj+1]];

        float t0 = 0.5F - x0 * x0 - y0 * y0;
        if (t0 >= 0)
        {
            t0 *= t0;
            n += t0 * t0 * dot(grad3[gi0], x0, y0);
        }

        float t1 = 0.5F - x1 * x1 - y1 * y1;
        if (t1 >= 0)
        {
            t1 *= t1;
            n += t1 * t1 * dot(grad3[gi1], x1, y1);
        }

        float t2 = 0.5F - x2 * x2 - y2 * y2;
        if (t2 >= 0)
        {
            t2 *= t2;
            n += t2 * t2 * dot(grad3[gi2], x2, y2);
        }

        return 70.0F * n;
    }
    
    private static class Grad
    {
        final float x, y;

        Grad(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
