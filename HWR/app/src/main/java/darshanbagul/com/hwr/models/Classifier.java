package darshanbagul.com.hwr.models;

/**
 * Created by darshanbagul on 14/07/17.
 */

public interface Classifier {
    String name();

    Classification recognize(final float[] pixels);
}