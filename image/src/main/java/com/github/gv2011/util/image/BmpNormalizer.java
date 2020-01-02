package com.github.gv2011.util.image;

import static com.github.gv2011.util.Unit.DPI;
import static com.github.gv2011.util.Unit.PERCENT;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.github.gv2011.util.ex.ThrowingSupplier;

public class BmpNormalizer {

  private final double resolutionDotsPerMeter = 600 * DPI;
  private final double aspectTolerance = 1 * PERCENT;

  public void normalize(
    final ThrowingSupplier<InputStream> in, final CommonImageType inType,
    final double widthMeter, final double heightMeter,
    final OutputStream out
  ) {
    final BufferedImage img = callWithCloseable(in, ImageIO::createImageInputStream, iis->{
      final ImageReader reader = ImageIO.getImageReadersByFormatName(inType.formatName()).next();
      reader.setInput(iis);
      return reader.read(0);
    });

    checkAspectRatio(img, widthMeter, heightMeter);

    final int outWidthDots = (int) (widthMeter * resolutionDotsPerMeter);
    final int outHeightDots = (int) (heightMeter * resolutionDotsPerMeter);

    final BufferedImage scaled = new BufferedImage(outWidthDots, outHeightDots, BufferedImage.TYPE_BYTE_GRAY);
    scaled.createGraphics().drawImage(img, 0, 0, outWidthDots, outHeightDots, null);

    call(()->ImageIO.write(scaled, CommonImageType.BMP.formatName(), out));
  }

  private void checkAspectRatio(final BufferedImage img, final double widthMeter, final double heightMeter) {
    final double expected = heightMeter / widthMeter;
    final double max = expected * (1+aspectTolerance);
    final double min = expected * (1-aspectTolerance);
    final double actual = ((double) img.getHeight()) / (double) img.getWidth();
    verify(actual<max, ()->img.getWidth() +" x "+ img.getHeight());
    verify(actual>min, ()->img.getWidth() +" x "+ img.getHeight());
  }

}
