/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;

/**
 * 通过处理步数通知的类的接口实现.
 * 这些类可以通过计步器。
 * @author nEdAy
 */
public interface StepListener {
    public void onStep();
    public void passValue();
}

