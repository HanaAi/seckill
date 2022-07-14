package com.cq.seckill;

import java.util.ArrayList;
import java.util.Arrays;

public class subset {
    public static void main(String[] args){
        POINT A = new POINT(0.0, 0.0);
        POINT B = new POINT(2.0, 2.0);
        POINT C = new POINT(0.0, 2.0);
        POINT D = new POINT(1.0, 1.0);
        System.out.println(isInTriangle(A,B,C,D));
    }


    public static boolean isInTriangle(POINT A, POINT B, POINT C, POINT P) {
        /*利用叉乘法进行判断,假设P点就是M点*/
        double a = 0, b = 0, c = 0;

        POINT MA = new POINT(P.x - A.x,P.y - A.y);
        POINT MB = new POINT(P.x - B.x,P.y - B.y);
        POINT MC = new POINT(P.x - C.x,P.y - C.y);

        /*向量叉乘*/
        a = MA.x * MB.y - MA.y * MB.x;
        b = MB.x * MC.y - MB.y * MC.x;
        c = MC.x * MA.y - MC.y * MA.x;

        if((a <= 0 && b <= 0 && c <= 0)||
                (a > 0 && b > 0 && c > 0)){
            return true;
        }

        return false;
    }

    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     *
     * @param point1 double浮点型一维数组
     * @param point2 double浮点型一维数组
     * @param point3 double浮点型一维数组
     * @param point double浮点型一维数组
     * @return bool布尔型
     */
    public boolean judge (double[] point1, double[] point2, double[] point3, double[] point) {
        // write code here
        double a = 0, b = 0, c = 0;

        double[] MA = {point[0]-point1[0],point[1]-point1[1]};
        double[] MB = {point[0]-point2[0],point[1]-point2[1]};
        double[] MC = {point[0]-point3[0],point[1]-point3[1]};
        /*向量叉乘*/
        a = MA[0] * MB[1] - MA[1] * MB[0];
        b = MB[0] * MC[1] - MB[1] * MC[0];
        c = MC[0] * MA[1] - MC[1] * MA[0];

        if((a <= 0 && b <= 0 && c <= 0)||
                (a > 0 && b > 0 && c > 0)){
            return true;
        }

        return false;
    }


}

class POINT{
    double x;
    double y;
    public POINT(double x,double y){
        this.x = x;
        this.y = y;
    }
}

