package com.ludicrus.ludicrus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;

import com.ludicrus.ludicrus.R;

/**
 * Created by jpgarduno on 3/17/15.
 */
public class OrganizationLogoRenderer {

    public static Bitmap getMaskedOrganizationLogo(String base64, Context context)
    {
        try {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap orgLogo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            int width = orgLogo.getWidth();
            int height = orgLogo.getHeight();

            Bitmap teamMask = BitmapFactory.decodeResource(context.getResources(), R.drawable.team_mask);
            Bitmap teamMaskAlpha = BitmapFactory.decodeResource(context.getResources(), R.drawable.team_mask_alpha);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            Bitmap teamMaskResized = Bitmap.createScaledBitmap(teamMask, width, height, true);
            Bitmap teamMaskAlphaResized = Bitmap.createScaledBitmap(teamMaskAlpha, width, height, true);

            //Create alpha bitmap
            Bitmap alpha = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int[] alphaPix = new int[width * height];
            teamMaskAlphaResized.getPixels(alphaPix, 0, width, 0, 0, width, height);
            int count = width * height;
            for (int i = 0; i < count; ++i) {
                alphaPix[i] = alphaPix[i] << 8;
            }
            alpha.setPixels(alphaPix, 0, width, 0, 0, width, height);

            Bitmap result = Bitmap.createBitmap(teamMaskResized.getWidth(), teamMaskResized.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(result);
            c.drawBitmap(orgLogo, 0, 0, null);
            c.drawBitmap(teamMaskResized, 0, 0, paint);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            c.drawBitmap(alpha, 0, 0, paint);
            paint.setXfermode(null);

            return result;

        } catch (Exception e)
        {
            return null;
        }
    }
}
