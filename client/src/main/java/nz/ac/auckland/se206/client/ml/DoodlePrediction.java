package nz.ac.auckland.se206.client.ml;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.client.util.ImageUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for querying the DL model to get the predictions. Code partially
 * adapted from https://github.com/deepjavalibrary/djl-demo.
 */
public class DoodlePrediction {

  private static final Logger logger = LoggerFactory.getLogger(DoodlePrediction.class);

  /**
   * Prints the top K predictions of a given image under test.
   *
   * @param args BMP file to predict and the number of top K predictions to print.
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model or image cannot be found on the file system.
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  public static void main(final String[] args)
      throws ModelException, IOException, TranslateException, URISyntaxException {
    if (args.length != 2) {
      throw new IllegalArgumentException(
          "You are not providing the correct arguments. You need to provide the path of the file"
              + " and the number of top K predictions to print.");
    }

    printPredictions(
        new DoodlePrediction().getPredictions(new File(args[0]), Integer.parseInt(args[1])));
  }

  /**
   * Prints the predictions class name and confidence level.
   *
   * @param predictions The list of predictions to print.
   */
  public static void printPredictions(final List<Classifications.Classification> predictions) {
    final StringBuilder sb = new StringBuilder();

    int i = 1;
    for (final Classifications.Classification classification : predictions) {
      sb.append("TOP ")
          .append(i)
          .append(" : ")
          .append(classification.getClassName())
          .append(" : ")
          .append(String.format("%.2f%%", 100 * classification.getProbability()));
      logger.info(sb.toString());
      // Clear the string builder for the next prediction.
      sb.setLength(0);
      i++;
    }
  }

  private final ZooModel<Image, Classifications> model;

  /**
   * Constructs the doodle prediction model by loading it from a file.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public DoodlePrediction() throws ModelException, IOException, URISyntaxException {
    final ImageClassificationTranslator translator =
        ImageClassificationTranslator.builder()
            .addTransform(new ToTensor())
            .optFlag(Image.Flag.GRAYSCALE)
            .optApplySoftmax(true)
            .build();

    // Fix model not being found by using resource lookups
    final URL resource = DoodlePrediction.class.getResource("/ml/doodle_mobilenet.zip");
    if (resource == null) {
      throw new IOException("Model not found");
    }
    final Path path = new File(resource.toURI()).toPath();

    final Criteria<Image, Classifications> criteria =
        Criteria.builder()
            .setTypes(Image.class, Classifications.class)
            // This will not work if the application runs from a JAR.
            .optModelPath(path)
            //.optModelPath("src/main/resources/ml/doodle_mobilenet.zip")
            .optOption("mapLocation", "true")
            .optTranslator(translator)
            .build();

    this.model = ModelZoo.loadModel(criteria);
  }

  /**
   * Predicts the categories of the input image, returning the top K predictions.
   *
   * @param bufImg BufferedImage file to classify.
   * @param k The number of classes to return.
   * @return List of classification results and their confidence level.
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  public List<Classifications.Classification> getPredictions(BufferedImage bufImg, final int k)
      throws TranslateException {
    // The model requires a black background and white foreground.
    bufImg = ImageUtils.invertBlackAndWhite(bufImg);

    // The model requires the image to be 65x65 pixels.
    bufImg =
        Scalr.resize(
            bufImg, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 65, 65, Scalr.OP_ANTIALIAS);

    final Classifications classifications =
        this.model.newPredictor().predict(new BufferedImageFactory().fromImage(bufImg));

    return classifications.topK(k);
  }

  /**
   * Predicts the categories of the input image, returning the top K predictions.
   *
   * @param image BMP image file to classify.
   * @param k The number of classes to return.
   * @return List of classification results and their confidence level.
   * @throws IOException If the image is not found on the filesystem.
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  public List<Classifications.Classification> getPredictions(final File image, final int k)
      throws IOException, TranslateException {
    if (!image.exists()) {
      throw new FileNotFoundException("The file " + image.getAbsolutePath() + " does not exist");
    }

    return this.getPredictions(ImageIO.read(image), k);
  }
}
