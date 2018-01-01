package com.ywl5320.player.base;



import com.ywl5320.player.util.BeanUtil;

import java.io.Serializable;

/**
 * Created by ywl5320 on 2017/9/4.
 */

public class BaseBean implements Serializable{

    public static final long serialVersionUID = -316172390920775219L;

    @Override
    public String toString() {
        return BeanUtil.bean2string(this);
    }

}
