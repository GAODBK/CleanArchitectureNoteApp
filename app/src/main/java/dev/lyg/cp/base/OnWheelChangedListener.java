package dev.lyg.cp.base;

import dev.lyg.cp.alert.WheelView;

/**
 * @author cncoderx
 */
public interface OnWheelChangedListener {
    void onChanged(WheelView view, int oldIndex, int newIndex);
}
