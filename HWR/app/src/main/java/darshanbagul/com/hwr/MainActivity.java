package darshanbagul.com.hwr;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import darshanbagul.com.hwr.models.Classification;
import darshanbagul.com.hwr.models.Classifier;
import darshanbagul.com.hwr.models.TensorflowClassifier;
import darshanbagul.com.hwr.views.DrawModel;
import darshanbagul.com.hwr.views.DrawView;

/**
 * Created by darshanbagul on 14/07/17.
 */

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private static final int PIXEL_WIDTH = 28;

    // ui elements
    private Button clearBtn, classBtn;
    private TextView resText;
//    private List<Classifier> mClassifiers = new ArrayList<>();
    private Classifier mClassifier;
    // views
    private DrawModel drawModel;
    private DrawView drawView;
    private PointF mTmpPiont = new PointF();

    private float mLastX;
    private float mLastY;

    @Override
    // In the onCreate() method, you perform basic application startup logic that should happen
    //only once for the entire life of the activity.
    protected void onCreate(Bundle savedInstanceState) {
        //initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get drawing view from XML (where the finger writes the number)
        drawView = (DrawView) findViewById(R.id.draw);
        //get the model object
        drawModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);

        //init the view with the model object
        drawView.setModel(drawModel);
        // give it a touch listener to activate when the user taps
        drawView.setOnTouchListener(this);

        //clear button
        //clear the drawing when the user taps
        clearBtn = (Button) findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);

        //class button
        //when tapped, this performs classification on the drawn image
        classBtn = (Button) findViewById(R.id.btn_class);
        classBtn.setOnClickListener(this);

        // res text
        //this is the text that shows the output of the classification
        resText = (TextView) findViewById(R.id.tfRes);

        // tensorflow
        //load up our saved model to perform inference from local storage
        loadModel();
    }

    //the activity lifecycle

    @Override
    //OnResume() is called when the user resumes his Activity which he left a while ago,
    // //say he presses home button and then comes back to app, onResume() is called.
    protected void onResume() {
        drawView.onResume();
        super.onResume();
    }

    @Override
    //OnPause() is called when the user receives an event like a call or a text message,
    // //when onPause() is called the Activity may be partially or completely hidden.
    protected void onPause() {
        drawView.onPause();
        super.onPause();
    }
    //creates a model object in memory using the saved tensorflow protobuf model file
    //which contains all the learned weights
    private void loadModel() {
        //The Runnable interface is another way in which you can implement multi-threading other than extending the
        // //Thread class due to the fact that Java allows you to extend only one class. Runnable is just an interface,
        // //which provides the method run.
        // //Threads are implementations and use Runnable to call the method run().
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //add 2 classifiers to our classifier arraylist
                    //the tensorflow classifier and the keras classifier
//                    mClassifiers.add(
//                            TensorflowClassifier.create(getAssets(), "TensorFlow",
//                                    "opt_mnist_convnet.pb", "labels.txt", PIXEL_WIDTH,
//                                    "input", "output", true));
                    mClassifier = TensorflowClassifier.create(getAssets(), "Keras",
                            "opt_mnist_convnet.pb", "labels.txt", PIXEL_WIDTH,
                            "conv2d_1_input", "dense_2/Softmax", false);
                } catch (final Exception e) {
                    //if they aren't found, throw an error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        //when the user clicks something
        if (view.getId() == R.id.btn_clear) {
            //if its the clear button
            //clear the drawing
            drawModel.clear();
            drawView.reset();
            drawView.invalidate();
            //empty the text view
            resText.setText("");
        } else if (view.getId() == R.id.btn_class) {
            //if the user clicks the classify button
            //get the pixel data and store it in an array
            float pixels[] = drawView.getPixelData();

            //init an empty string to fill with the classification output
            String text = "";
            //for each classifier in our array
//            for (Classifier classifier : mClassifiers) {
                //perform classification on the image
                final Classification res = mClassifier.recognize(pixels);
                //if it can't classify, output a question mark
                if (res.getLabel() == null) {
                    text += mClassifier.name() + ": ?\n";
                } else {
                    //else output its name
                    text += String.format("%s: %s, %f\n", mClassifier.name(), res.getLabel(),
                            res.getConf());
                }
//            }
            resText.setText(text);
        }
    }

    @Override
    //this method detects which direction a user is moving
    //their finger and draws a line accordingly in that
    //direction
    public boolean onTouch(View v, MotionEvent event) {
        //get the action and store it as an int
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        //actions have predefined ints, lets match
        //to detect, if the user has touched, which direction the users finger is
        //moving, and if they've stopped moving

        //if touched
        if (action == MotionEvent.ACTION_DOWN) {
            //begin drawing line
            processTouchDown(event);
            return true;
            //draw line in every direction the user moves
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;
            //if finger is lifted, stop drawing
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    //draw line down

    private void processTouchDown(MotionEvent event) {
        //calculate the x, y coordinates where the user has touched
        mLastX = event.getX();
        mLastY = event.getY();
        //user them to calcualte the position
        drawView.calcPos(mLastX, mLastY, mTmpPiont);
        //store them in memory to draw a line between the
        //difference in positions
        float lastConvX = mTmpPiont.x;
        float lastConvY = mTmpPiont.y;
        //and begin the line drawing
        drawModel.startLine(lastConvX, lastConvY);
    }

    //the main drawing function
    //it actually stores all the drawing positions
    //into the drawmodel object
    //we actually render the drawing from that object
    //in the drawrenderer class
    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        drawView.calcPos(x, y, mTmpPiont);
        float newConvX = mTmpPiont.x;
        float newConvY = mTmpPiont.y;
        drawModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        drawView.invalidate();
    }

    private void processTouchUp() {
        drawModel.endLine();
    }
}