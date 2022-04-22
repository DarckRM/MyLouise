package com.darcklh.louise.Model.Louise;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.darcklh.louise.Model.SpecificException;
import com.darcklh.louise.Utils.EncryptUtils;
import lombok.Data;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

@Data
public class ProcessImage {

    @TableId
    private String hash_code;
    private String image_path;
    private String image_name;
    private double[] histogram_info;

    @TableField(exist = false)
    private BufferedImage buffered_image;

    public ProcessImage(String imagePath, String imageName) throws IOException, NoSuchAlgorithmException {

        this.image_path = imagePath;
        this.image_name = imageName;
        this.buffered_image = readImage(imagePath);
        this.hash_code = EncryptUtils.checkSumMD5(imagePath);
        this.histogram_info = getNormalizedHistogram(this.buffered_image);

    }

    public ProcessImage(String image_path, String image_name, boolean is_init) {
        this.image_path = image_path;
        this.image_name = image_name;
    }

    private BufferedImage readImage(String imagePath) throws SpecificException, IOException {

        BufferedImage bImage;
        File imageFile = new File(this.image_path);
        bImage = ImageIO.read(imageFile);
        // Check
        return bImage;
    }

    private double[] getNormalizedHistogram(BufferedImage inImage) {

        float[] hsb;
        int argb, r, g, b;
        int HSBInt[] = new int[inImage.getWidth() * inImage.getHeight()];
        int c = 0;
        double[] NormHisto;

        for (int i = 0; i < inImage.getWidth(); i++) {
            for (int j = 0; j < inImage.getHeight(); j++) {

                argb = inImage.getRGB(i, j);
                //Extract R, G, B individually & negate
                b = ((argb) & 0xFF);
                g = ((argb >> 8) & 0xFF);
                r = ((argb >> 16) & 0xFF);
                hsb = Color.RGBtoHSB(r, g, b, null);
                HSBInt[c++] = quantizeHSB1(hsb);
            }
        }

        NormHisto = this.createHistogram(HSBInt);
        return NormHisto;
    }

    private double[] createHistogram(int[] hsb) {

        double max, min;
        int[] histogram;
        double[] normalizedHistogram;

        int noOfbins=256;

        histogram = new int[noOfbins];
        normalizedHistogram= new double[noOfbins];
        for (int i = 0; i < hsb.length; i++) {
            histogram[(hsb[i]& 0x00000003)*(4*16) +((hsb[i]& 0x0000000C)>>2)*16 + (hsb[i]&0x000000F0)>>4]++;
        }
        min= (double)histogram[0]/(256);
        max= (double)histogram[0]/(256);
        if(histogram !=null && noOfbins > 0 )
        {
            for (int i = 0; i < histogram.length; i++)
            {
                normalizedHistogram[i] = (double)histogram[i]/(256);

                if (normalizedHistogram[i] > max)
                    max = normalizedHistogram[i];
                if (normalizedHistogram[i] < min)
                    min = normalizedHistogram[i];
            }
        }
        return normalizedHistogram;
    }

    private int quantizeHSB1(float[] hsb) {
        // float[] hsbNew = new float[3];
        int h = Float.floatToIntBits((hsb[0] * 15f) / 4194303f);
        int s = Float.floatToIntBits((hsb[1] * 3f) / 4194303f);
        int b = Float.floatToIntBits((hsb[2] * 3f) / 4194303f);

        h = h & 0x007fffff;
        h = (h >> 19) & 0xF;

        s = s & 0x007fffff;
        s = (s >> 21) & 0x3;

        b = b & 0x007fffff;
        b = (b >> 21) & 0x3;

        int single = 0;

        single += h & 0xF;
        single = (single << 2);
        single += s & 0x3;
        single = (single << 2);
        single += b & 0x3;

        return single;
    }
}
