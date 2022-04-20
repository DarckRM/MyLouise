package com.darcklh.louise.Service;

public interface CBIRService {

    /**
     * 三种数据空间距离检测算法
     * 闵可夫斯基距离
     * 直方图交点距离
     * 相对偏差距离
     * @param InHist
     * @param sampleHist
     * @return
     */
    public float minkowskiDist(double[] InHist, double[] sampleHist);
    public float histogramIntersectionDist(double[] InHist, double[] sampleHist);
    public float relativeDeviationDist(double[] InHist, double[] sampleHist);

}
