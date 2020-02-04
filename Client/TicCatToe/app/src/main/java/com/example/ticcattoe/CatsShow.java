package com.example.ticcattoe;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Random;
import java.util.Stack;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.TOP;

public class CatsShow extends FrameLayout {
    Random r;
    private Context context;
    private boolean stopped;
    private Button exitButton;
    private Stack<ImageView> catsImages;
    private Stack<ImageView>  shownImages;
    private int imagesCount, imageWidth, imageHeight;
    private ViewGroup rootLayout;
    SoundPool sp;
    int catsSound, catsSound2, catsSound3;
    int catsSoundStreamID1, catsSoundStreamID2, catsSoundStreamID3;

    CatsShow(Context context, ViewGroup rootL) {
        super(context);
        this.catsImages = new Stack<ImageView>();
        this.shownImages = new Stack<ImageView>();
        this.context = context;
        this.imagesCount = 150;
        this.imageHeight=500;
        this.imageWidth=500;
        this.stopped = false;
        r= new Random();
        this.rootLayout = rootL;
        generateImages();
        generateCatsSound();
    }

    private void generateImages() {
        for (int i = 0; i < this.imagesCount  ; i++) {
            createImage(i);
            createExitButton();
        }
    }

    public void start() {
        startCatsSound();
        this.exitButton.setVisibility(VISIBLE);
        YoYo.with(Techniques.FadeIn).duration(1).repeat(0).playOn(this.exitButton);
        this.stopped = false;
        showImages();
    }

    public void stop() {
        stopCatsSound();
        YoYo.with(Techniques.SlideOutDown).duration(1800).repeat(0).playOn(this.exitButton);
        this.stopped =true;

        for(ImageView image : this.shownImages) {
            YoYo.with(Techniques.FadeOut).duration(2000).repeat(0).playOn(image);
        }
        this.shownImages = new Stack<ImageView>() ;
    }

    public void pause() {
        stopCatsSound();
    }

    public void resume() {
        startCatsSound();
    }

    public void killCats() {
        //Toast.makeText(this.context, "Cats killed...", Toast.LENGTH_LONG).show();
        for(ImageView image : this.catsImages) {
            this.rootLayout.removeView(image);
        }
        this.rootLayout.removeView(this.exitButton);
    }

    private int[] generateRandoms() {
        int left, right, top, bottom, imageResCode, animPos;
        int[] resources = {R.drawable.sweet_cat, R.drawable.sweet_cat_2, R.drawable.sweet_cat_3,
                R.drawable.sweet_cat_4,R.drawable.sweet_cat_5};
        left=  r.nextInt(500);
        right = r.nextInt(500);
        top = r.nextInt(1000)+100;
        bottom = r.nextInt(1000);
        imageResCode = resources[r.nextInt(5)];
        animPos = r.nextInt(15);
        int [] n = {left,right,top,bottom, imageResCode, animPos};
        return n;
    }

    private void showImages() {
        int i=0;
        for (final ImageView image: this.catsImages) {
            if(this.stopped) break;
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if(!stopped) {
                                int[] randoms = generateRandoms();
                                showImage(randoms, randoms[4], randoms[5], image);
                            }
                        }
                    },
                    200 * i
            );
            i++;
        }
    }
    private void createImage(int lGravity) {
        ImageView image = new ImageView(this.context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(500,
                500);
        image.setLayoutParams(params);
        if(lGravity%2 == 0 )  params.gravity = TOP;
        else  params.gravity = BOTTOM;
        image.setVisibility(INVISIBLE);
        this.rootLayout.addView(image);
        this.catsImages.push(image);
    }

    private void showImage(int[] margins, int resourceCode, int animPos, ImageView image) {
        int marginLeft = margins[0],marginRight = margins[1], marginTop = margins[2], marginBottom = margins[3];
        Techniques[] anims = {Techniques.Bounce, Techniques.SlideInUp, Techniques.DropOut, Techniques.SlideInDown, Techniques.RubberBand,
                Techniques.FadeIn, Techniques.Wave, Techniques.ZoomInRight, Techniques.ZoomIn, Techniques.Wave,Techniques.Pulse
                ,Techniques.Wobble, Techniques.Tada, Techniques.FlipInX,Techniques.Landing};
        Techniques anim = anims[animPos];
        image.setImageResource(resourceCode);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(500,
                500);
        params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        image.setLayoutParams(params);
        image.setForegroundGravity(CENTER);
        image.setVisibility(VISIBLE);
        YoYo.with(anim)
                .duration(1000)
                .repeat(1)
                .playOn(image);
        this.shownImages.push(image);
    }

    private void generateCatsSound() {
        //phase 1 - check which sdk the user has
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            sp = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();
        } else {
            sp = new SoundPool(100, AudioManager.STREAM_MUSIC, 1);

        }

        catsSound = sp.load(this.context, R.raw.angry_cat_sounds, 1);
        catsSound2 = sp.load(this.context, R.raw.cat_meow_audio_clip, 1);
        catsSound3 = sp.load(this.context, R.raw.cat_sound_3, 1);
    }

    private void startCatsSound() {
        this.catsSoundStreamID1 = sp.play(catsSound, 1, 1, 0, -1, 1);
        this.catsSoundStreamID2 = sp.play(catsSound2, 1, 1, 0, -1, 1);
        this.catsSoundStreamID3 = sp.play(this.catsSound3, 1, 1, 1, -1, 1);
    }

    private void stopCatsSound() {
        sp.stop(catsSoundStreamID1);
        sp.stop(catsSoundStreamID2);
        sp.stop(catsSoundStreamID3);
    }
    private void createExitButton() {
        Button exitButton = new Button(this.context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(700,
                300);
        params.setMargins(0,0,0,50);
        exitButton.setText("Stop cats show");
        exitButton.setId(R.id.stopCatsShowBtn);
        exitButton.setVisibility(INVISIBLE);
        exitButton.setAllCaps(false);
        params.gravity=BOTTOM|CENTER;
        exitButton.setTextColor(Color.WHITE);
        exitButton.setTextSize(18);
        exitButton.setLayoutParams(params);
        exitButton.setBackgroundResource(R.drawable.btn_background_2);
        exitButton.setOnClickListener((HomePage)this.context);
        this.rootLayout.addView(exitButton);
        this.exitButton = exitButton;
    }
}
