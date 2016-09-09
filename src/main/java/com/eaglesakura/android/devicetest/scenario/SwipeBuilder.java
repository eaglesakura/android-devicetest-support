package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.android.device.display.DisplayInfo;
import com.eaglesakura.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * スワイプ処理を行う
 */
public class SwipeBuilder {
    List<Vector2> mSwipePositions = new ArrayList<>();

    /**
     * 次の位置を指定する
     */
    public SwipeBuilder nextUV(double u, double v) {
        DisplayInfo info = new DisplayInfo(ScenarioContext.getContext());
        nextPosition(u * info.getWidthPixel(), v * info.getHeightPixel());
        return this;
    }

    /**
     * 次のスワイプ位置を指定する
     */
    public SwipeBuilder nextPosition(double x, double y) {
        mSwipePositions.add(new Vector2((float) x, (float) y));
        return this;
    }

    public void execute() {
        List<Vector2> positions = new ArrayList<>(mSwipePositions);

        Vector2 from = positions.remove(0);
        while (!positions.isEmpty()) {
            Vector2 to = positions.remove(0);
            double length = from.length(to);
            ScenarioContext.sDevice.swipe((int) from.x, (int) from.y, (int) to.x, (int) to.y, Math.max(5, (int) (length / 40)));
            from = to;
        }
    }

    public static SwipeBuilder fromUV(double u, double v) {
        DisplayInfo info = new DisplayInfo(ScenarioContext.getContext());
        return fromPosition(u * info.getWidthPixel(), v * info.getHeightPixel());
    }

    public static SwipeBuilder fromPosition(double x, double y) {
        SwipeBuilder builder = new SwipeBuilder();
        builder.nextPosition(x, y);
        return builder;
    }
}
