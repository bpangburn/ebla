package com.greatmindsworking.EBLA;


import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

class MyIIOImage extends IIOImage {

    private static Raster createDummyRaster() {
        Raster ras =
            Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                                           1, 1, 1, new Point(0, 0));
        return ras;
    }

    public MyIIOImage(RenderedImage image,
                      List thumbnails,
                      IIOMetadata metadata) {
        super(createDummyRaster(), null, null);

        if (image == null) {
            throw new IllegalArgumentException("image == null!");
        }
        this.image = image;
        this.raster = null;
        this.thumbnails = thumbnails;
        this.metadata = metadata;
    }
}

public class ImageIOWorkaround {

    public static boolean write(RenderedImage im,
                                String formatName,
                                ImageOutputStream output)
        throws IIOException {
        if (im == null) {
            throw new IllegalArgumentException("im == null!");
        }
        if (formatName == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        if (output == null) {
            throw new IllegalArgumentException("output == null!");
        }

        ImageWriter writer = null;
        ImageTypeSpecifier type =
            ImageTypeSpecifier.createFromRenderedImage(im);
        Iterator iter =
            ImageIO.getImageWriters(type, formatName);
        if (iter.hasNext()) {
            writer = (ImageWriter)iter.next();
        }
        if (writer == null) {
            return false;
        }

        writer.setOutput(output);
        writer.write(new MyIIOImage(im, null, null));

        return true;
    }

    public static boolean write(RenderedImage im,
                                String formatName,
                                File output)
        throws IIOException {
        ImageOutputStream stream = null;
        try {
            stream = ImageIO.createImageOutputStream(output);
        } catch (IOException e) {
            throw new IIOException("Can't create output stream!", e);
        }

        return write(im, formatName, stream);
    }

    public static boolean write(RenderedImage im,
                                String formatName,
                                OutputStream output)
        throws IIOException {
        ImageOutputStream stream = null;
        try {
            stream = ImageIO.createImageOutputStream(output);
        } catch (IOException e) {
            throw new IIOException("Can't create output stream!", e);
        }

        return write(im, formatName, stream);
    }
}
