# CircleTickView
圆形刻度盘view

![view截图](https://github.com/389273716/CircleTickView/blob/master/app/GIF.gif)

# 中文说明：
本自定义view主要功能：

1. 可自定义起始时间以及最大时间，设置总格数，每格均分时间差。
1. 可自定义界面颜色字体大小，文本提示。
1. 单击触摸可触发刻度以及时间的变动动画效果，动画效果更自然，从上一次位置开始变更。触摸范围为大圆内到圆心距离大于1/2半径距离的坐标范围。触摸事件为action_move时不会触发动画。
1. 提供禁用触摸操作，以便特殊需求。
1. 提供是否清零设置（开启后设置时间等周边位置可清零），默认是0格，0格代表的是你设置的初始时间值。
1. 提供适用于自动倒计时模式下的方法，以便更好更新view的显示。
1. 提供时间以及刻度变化的监听。

# English description:
This custom view main functions:


1. Customizable starting time and maximum time, set the total number of lattice, each divided the time difference.
1. Customizable interface color font size, text prompt.
1. Click the touch can trigger changes in the scale and time animation, animation effects more natural, began to change from the last position.Touch the scope within the circle to the coordinates of the center distance is greater than 1/2 radius distance range.Touch events to action_move not trigger the animation.
1. Provide disable touch, so that special requirements.
1. Provide whether reset Settings (open after setting time and other peripheral location can reset), the default is 0, 0 case is representative of you to set the initial time of value.
1. The offer is applicable to automatic countdown mode method, in order to better update the view shows.
1. Provide listening time and scale changes.


# How To Use

```
 compile 'com.tc.circletickview:library:0.1.0'
```

```

     <com.tc.library.CircleTickView
        android:id="@+id/crpv_tick"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:paddingTop="40dp"
        app:animDuration="500"
        app:bottomText="Set Time"
        app:isCanResetZero="true"
        app:maxTime="1200000"
        app:startTime="300000"
        app:tickMaxCount="30"
        />
```

```
mCtvTime.setSelectTickCount(1, false);
        mCurrentTime = mCtvTime.getCurrentTime();
        mCtvTime.setOnTimeChangeListener(new CircleTickView.OnTimeChangeListener() {
            @Override
            public void onChange(long time, int tickCount) {
                mCurrentTime = time;
                LogUtil.e(TAG, mCurrentTime + "  mCurrentTime");

                }
            }
        });
```
