package com.fh.lib;

import android.view.InputDevice;
import android.view.InputDevice.MotionRange;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* compiled from: SDLActivity */
class SDLJoystickHandler_API12 extends SDLJoystickHandler {
    private ArrayList<SDLJoystick> mJoysticks = new ArrayList();

    /* compiled from: SDLActivity */
    class RangeComparator implements Comparator<MotionRange> {
        RangeComparator() {
        }

        public int compare(MotionRange arg0, MotionRange arg1) {
            return arg0.getAxis() - arg1.getAxis();
        }
    }

    /* compiled from: SDLActivity */
    class SDLJoystick {
        public ArrayList<MotionRange> axes;
        public int device_id;
        public ArrayList<MotionRange> hats;
        public String name;

        SDLJoystick() {
        }
    }

    public void pollInputDevices() {
        int i;
        int[] deviceIds = InputDevice.getDeviceIds();
        for (i = deviceIds.length - 1; i > -1; i--) {
            if (getJoystick(deviceIds[i]) == null) {
                SDLJoystick joystick = new SDLJoystick();
                InputDevice joystickDevice = InputDevice.getDevice(deviceIds[i]);
                if ((joystickDevice.getSources() & 16) != 0) {
                    joystick.device_id = deviceIds[i];
                    joystick.name = joystickDevice.getName();
                    joystick.axes = new ArrayList();
                    joystick.hats = new ArrayList();
                    List<MotionRange> ranges = joystickDevice.getMotionRanges();
                    Collections.sort(ranges, new RangeComparator());
                    for (MotionRange range : ranges) {
                        if ((range.getSource() & 16) != 0) {
                            if (range.getAxis() == 15 || range.getAxis() == 16) {
                                joystick.hats.add(range);
                            } else {
                                joystick.axes.add(range);
                            }
                        }
                    }
                    this.mJoysticks.add(joystick);
                    SDLActivity.nativeAddJoystick(joystick.device_id, joystick.name, 0, -1, joystick.axes.size(), joystick.hats.size() / 2, 0);
                }
            }
        }
        ArrayList<Integer> removedDevices = new ArrayList();
        for (i = 0; i < this.mJoysticks.size(); i++) {
            int device_id = ((SDLJoystick) this.mJoysticks.get(i)).device_id;
            int j = 0;
            while (j < deviceIds.length && device_id != deviceIds[j]) {
                j++;
            }
            if (j == deviceIds.length) {
                removedDevices.add(Integer.valueOf(device_id));
            }
        }
        for (i = 0; i < removedDevices.size(); i++) {
            device_id = ((Integer) removedDevices.get(i)).intValue();
            SDLActivity.nativeRemoveJoystick(device_id);
            for (j = 0; j < this.mJoysticks.size(); j++) {
                if (((SDLJoystick) this.mJoysticks.get(j)).device_id == device_id) {
                    this.mJoysticks.remove(j);
                    break;
                }
            }
        }
    }

    protected SDLJoystick getJoystick(int device_id) {
        for (int i = 0; i < this.mJoysticks.size(); i++) {
            if (((SDLJoystick) this.mJoysticks.get(i)).device_id == device_id) {
                return (SDLJoystick) this.mJoysticks.get(i);
            }
        }
        return null;
    }

    public boolean handleMotionEvent(MotionEvent event) {
        if ((event.getSource() & 16777232) != 0) {
            int actionPointerIndex = event.getActionIndex();
            switch (event.getActionMasked()) {
                case 2:
                    SDLJoystick joystick = getJoystick(event.getDeviceId());
                    if (joystick != null) {
                        int i;
                        for (i = 0; i < joystick.axes.size(); i++) {
                            MotionRange range = (MotionRange) joystick.axes.get(i);
                            SDLActivity.onNativeJoy(joystick.device_id, i, (((event.getAxisValue(range.getAxis(), actionPointerIndex) - range.getMin()) / range.getRange()) * 2.0f) - 1.0f);
                        }
                        for (i = 0; i < joystick.hats.size(); i += 2) {
                            SDLActivity.onNativeHat(joystick.device_id, i / 2, Math.round(event.getAxisValue(((MotionRange) joystick.hats.get(i)).getAxis(), actionPointerIndex)), Math.round(event.getAxisValue(((MotionRange) joystick.hats.get(i + 1)).getAxis(), actionPointerIndex)));
                        }
                        break;
                    }
                    break;
            }
        }
        return true;
    }
}
