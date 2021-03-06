package com.magicepg.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import entity.CoordinatesHolder;
import com.magicepg.wheel.layout.AbstractWheelLayoutManager;
import com.magicepg.wheel.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 19.02.2017
 */
public class BottomWheelRecyclerView extends AbstractWheelRecyclerView {

    private final Path gapPath;
    private final PointF bottomRayPosition;

    public interface OnBottomWheelSectorTapListener {
        void onRotateWheelByAngle(double rotationAngleInRad);
    }

    private OnBottomWheelSectorTapListener bottomWheelSectorTapListener;

    public BottomWheelRecyclerView(Context context) {
        this(context, null);
    }

    public BottomWheelRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomWheelRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bottomRayPosition = computeGapBottomRayPosition();
        gapPath = createGapClipPath();
    }

    public void setBottomWheelSectorTapListener(OnBottomWheelSectorTapListener bottomWheelSectorTapListener) {
        this.bottomWheelSectorTapListener = bottomWheelSectorTapListener;
    }

    @Override
    public void handleTapOnSectorView(View sectorViewToSelect) {
        bottomWheelSectorTapListener.onRotateWheelByAngle(computeWheelRotationForSector(sectorViewToSelect));
    }

    private double computeWheelRotationForSector(View sectorViewToSelect) {
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorViewToSelect);
        final double sectorViewBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);
        return getLayoutManager().getLayoutStartAngleInRad() - sectorViewBottomEdge;
    }

    @Override
    protected void doCutGapArea(Canvas canvas) {
        canvas.clipPath(gapPath);
    }

    @Override
    protected void drawGapLineRay(Canvas canvas) {
        final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();
        canvas.drawLine(
                circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y,
                bottomRayPosition.x, bottomRayPosition.y, gapRayDrawingPaint
        );
    }

    private Path createGapClipPath() {
        final Path res = new Path();
        final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();

        final int screenHeight = computationHelper.getComputedScreenDimensions().getHeight();

        res.moveTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.lineTo(bottomRayPosition.x, bottomRayPosition.y);
        res.lineTo(bottomRayPosition.x, screenHeight);
        res.lineTo(0, screenHeight);
        res.lineTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.close();

        return res;
    }

    private PointF computeGapBottomRayPosition() {
        final PointF pos = CoordinatesHolder.ofPolar(wheelConfig.getOuterRadius(),
                wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }

}
