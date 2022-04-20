package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Service.CBIRService;
import org.springframework.stereotype.Service;

@Service
public class CBIRServiceImpl implements CBIRService {

    @Override
    public float minkowskiDist(double[] InHist, double[] sampleHist) {
        float minkDist = 0;
        for (int i=0;i<InHist.length-1;i++)
            minkDist+=Math.pow((InHist[i]-sampleHist[i]),2);
        return (float) Math.sqrt(minkDist);
    }

    @Override
    public float histogramIntersectionDist(double[] InHist, double[] sampleHist) {
        float HIDist = 0;
        for (int i=0;i<InHist.length-1;i++)
            HIDist+=Math.min(InHist[i],sampleHist[i]);
        return HIDist;
    }

    @Override
    public float relativeDeviationDist(double[] InHist, double[] sampleHist) {
        float RDDist = 0;
        float Numerator=0;
        float Denom1=0;
        float Denom2=0;

        for (int i=0;i<InHist.length-1;i++)
        {
            Numerator+= Math.sqrt(Math.pow((InHist[i]-sampleHist[i]),2));
            Denom1+=Math.sqrt(Math.pow(InHist[i],2));
            Denom2+=Math.sqrt(Math.pow(sampleHist[i],2));
        }
        RDDist=2*Numerator/(Denom1+Denom2);
        return RDDist;
    }
}
