// package unsed;
//
// import rule.Conf;
//
// public abstract class BitDevice implements Conf {
//
// private final int t;
//
// private final int i;
//
// private final double r;
//
// private final double k;
//
// private final double b;
//
// private final String u;
//
// private final String m;
//
// public BitDevice(int t, int i) {
// // need to optimization
// // this.t = t;
// // this.i = i;
// // switch (t) {
// // case 0:
// // switch (i) {
// // case 1:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "Normal In";
// // break;
// //
// // case 2:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "COUNT";
// // break;
// //
// // case 3:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "Low to High Latch";
// // break;
// //
// // case 4:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "High to Low Latch";
// // break;
// //
// // case 5:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "Frequency";
// // break;
// //
// // default:
// // k = 0;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "OFF";
// // break;
// // }
// // break;
// // case 1:
// // switch (i) {
// // case 1:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "Normal Out";
// // break;
// //
// // case 2:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "PWM Frequency Out";
// // break;
// //
// // case 3:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "Maichong Count Out";
// // break;
// //
// // case 4:
// // k = 1;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "Lazy Out";
// // break;
// //
// // default:
// // k = 0;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "OFF";
// // break;
// // }
// // break;
// // default:
// // k = 0;
// // b = 0;
// // r = 1;
// // u = "";
// // m = "STATUS";
// // break;
// // }
// }
//
// @Override
// public double getK() {
// return k;
// }
//
// @Override
// public double getB() {
// return b;
// }
//
// @Override
// public int getI() {
// return i;
// }
//
// @Override
// public double getR() {
// return r;
// }
//
// @Override
// public String getU() {
// return r != 1 ? r + u : u;
// }
//
// @Override
// public int getT() {
// return t;
// }
//
// @Override
// public String getM() {
// return m;
// }
// }
