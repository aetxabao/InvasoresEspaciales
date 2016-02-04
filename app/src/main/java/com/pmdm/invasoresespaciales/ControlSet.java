package com.pmdm.invasoresespaciales;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.SparseArray;

public class ControlSet {

    public RectF rectLeft, rectRigth, rectShoot;
    public SparseArray<PointF> activePointers;

    public void setLeftRect(RectF rect){
        rectLeft = rect;
    }

    public void setRightRect(RectF rect){
        rectRigth = rect;
    }

    public void setShootRect(RectF rect){
        rectShoot = rect;
    }

    public void setActivePointers(SparseArray<PointF> activePointers){
        this.activePointers = activePointers;
    }

    private boolean hasRectActivePoints(RectF rect){
        int key;
        PointF pointF;
        for(int i = 0; i < activePointers.size(); i++) {
            key = activePointers.keyAt(i);
            pointF = activePointers.get(key);
            if ((pointF!=null) && rect.contains(pointF.x,pointF.y)) return true;
        }
        return false;
    }

    public boolean isLeft(){
        return hasRectActivePoints(rectLeft) &&  !hasRectActivePoints(rectRigth);
    }

    public boolean isRight(){
        return !hasRectActivePoints(rectLeft) &&  hasRectActivePoints(rectRigth);
    }

    public boolean isShoot(){
        return hasRectActivePoints(rectShoot);
    }

}
