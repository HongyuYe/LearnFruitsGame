package example.com.gameapp.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class CelebrityManager {
    private String assetPath;
    private String[] imageNames;
    private AssetManager assetManager;

    public CelebrityManager(AssetManager assetManager, String assetPath){
        this.assetManager = assetManager;
        this.assetPath = assetPath;
        try {
            imageNames = assetManager.list(assetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap get(int i) {
        try {
            InputStream stream = assetManager.open(assetPath + "/" + imageNames[i]);
            return BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName(int i) {
        return imageNames[i].replace(".jpg", "");
    }

    public int count() {
        return imageNames.length;
    }
}
