package com.ludicrus.ludicrus.helpers.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.ludicrus.ludicrus.util.EnumTypeface;

/**
 * Created by jpgarduno on 3/18/15.
 */
public class AssetsHelper {

    public static Typeface getTypeFace(Context context, int typeface)
    {
        Typeface quickTypeFace;

        switch (typeface)
        {
            case EnumTypeface.LOBSTER:
                quickTypeFace = Typeface.createFromAsset(context.getAssets(),"fonts/LobsterTwo-Regular.ttf");
                break;
            case EnumTypeface.QUICKSAND:
                quickTypeFace = Typeface.createFromAsset(context.getAssets(),"fonts/Quicksand-Regular.ttf");
                break;
            default:
                quickTypeFace = Typeface.createFromAsset(context.getAssets(),"fonts/Quicksand-Regular.ttf");
                break;
        }
        return quickTypeFace;
    }
}
