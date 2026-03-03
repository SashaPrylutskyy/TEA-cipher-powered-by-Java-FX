package com.op.teacipherfx.service;

public class TeaAlgorithm {
    private static final int DELTA = 0x9E3779B9;
    private static final int NUM_ROUNDS = 32;

    public static void encipherBlock(int[] v, int[] key) {
        int v0 = v[0], v1 = v[1], sum = 0;
        for (int i = 0; i < NUM_ROUNDS; i++) {
            sum += DELTA;
            v0 += ((v1 << 4) + key[0]) ^ (v1 + sum) ^ ((v1 >>> 5) + key[1]);
            v1 += ((v0 << 4) + key[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + key[3]);
        }
        v[0] = v0;
        v[1] = v1;
    }

    public static void decipherBlock(int[] v, int[] key) {
        int v0 = v[0], v1 = v[1], sum = DELTA * NUM_ROUNDS;
        for (int i = 0; i < NUM_ROUNDS; i++) {
            v1 -= ((v0 << 4) + key[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + key[3]);
            v0 -= ((v1 << 4) + key[0]) ^ (v1 + sum) ^ ((v1 >>> 5) + key[1]);
            sum -= DELTA;
        }
        v[0] = v0;
        v[1] = v1;
    }
}