package com.darcklh.louise.Model.Louise;

import com.alibaba.fastjson.JSONObject;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Data
public class ProcessImage {

    @TableId
    private String hash_code;
    private String image_path;
    private String image_name;
    private String histogram_json;

    @TableField(exist = false)
    private double[][] histogram_info;

    @TableField(exist = false)
    private BufferedImage buffered_image;

    public ProcessImage(String imagePath, String imageName) throws IOException, NoSuchAlgorithmException {

        this.image_path = imagePath;
        this.image_name = imageName;
        this.buffered_image = readImage();
        this.hash_code = EncryptUtils.checkSumMD5(imagePath + "/" + imageName);
        this.histogram_info = getDifferentHistogram(this.buffered_image);
        this.buffered_image = null;

    }

    public ProcessImage(String image_path, String image_name, boolean is_init) {
        this.image_path = image_path;
        this.image_name = image_name;
    }

    public ProcessImage(String image_path, String image_name, String hash_code, String histogram_json) {
        this.image_path = image_path;
        this.image_name = image_name;
        this.hash_code = hash_code;
        this.histogram_json = histogram_json;
    }

    private BufferedImage readImage() throws SpecificException, IOException {

        BufferedImage bImage;
        File imageFile = new File(this.image_path + "/" + this.image_name);
        bImage = ImageIO.read(imageFile);
        // Check
        return bImage;
    }

    private double[][] getDifferentHistogram(BufferedImage inImage) {
        double [][] histgram = new double [3][256];
        int width = inImage.getWidth();//????????????
        int height = inImage.getHeight();//????????????
        int pix[] = new int [width*height];//????????????
        int r,g,b;//??????R???G???B??????
        pix = inImage.getRGB(0, 0, width, height, pix, 0, width);//????????????????????????????????????
        for(int i=0; i<width*height; i++) {
            r = pix[i]>>16 & 0xff; //??????R
            g = pix[i]>>8 & 0xff;
            b = pix[i] & 0xff;
            histgram[0][r] ++;
            histgram[1][g] ++;
            histgram[2][b] ++;
        }
        for(int j=0;j<256;j++)//???????????????????????????????????????????????????
        {
            for(int i=0;i<3;i++)
            {
                histgram[i][j]=histgram[i][j]/(width*height);
            }
        }
        return histgram;
    }
}
