/*
 * eichong.com Inc.
 * Copyright (c) 2014-2018 All Rights Reserved.
 */
package com.ec.epcore.epdata;

/**
 * @author ${zhuhuiming}
 * @version $Id: RedisKey.java, v 0.1 2018年07月12日 14:13 Exp $
 */
public class RedisKey {

    //ep_status_gun_[headpk]: status prefix
    public static final String EP_PREFIX = "ep:";
    public static final String EP_PREFIX_STATUS = "status:";
    public static final String EP_PREFIX_ONLINE = "online:";
    public static final String EP_PREFIX_OFFLINE = "offline:";
    public static final String EP_PREFIX_ORDER= "order:";
    public static final String EP_PREFIX_CONSUME="consume:";
    public static final String EP_PREFIX_CARDFROZEAMT = "cardfrozeamt:";
    public static final String EP_PREFIX_CHARGEVENT = "chargeevent:";

    //桩状态
    public static final String EP_STATUS = EP_PREFIX+ EP_PREFIX_STATUS+"epCode_";//桩枪组合的时实状态数据：String

    //每个枪头 每天产生一个充电实时数据list 有效期7天的
    public static final String EP_ORDER_LIST = EP_PREFIX+ EP_PREFIX_ORDER+ "list_";//枪的充电实时数据 string 放入这个订单list里

    public static final String EP_ORDER_LIST_DATA = EP_PREFIX+ EP_PREFIX_ORDER+"list_data_";
    //上线，离线时间
    public static final String EP_ONLINE= EP_PREFIX+ EP_PREFIX_ONLINE+"epCode_";
    public static final String EP_OFFLINE = EP_PREFIX+ EP_PREFIX_OFFLINE+"epCode_";

    public static final String EP_PREVENT_REP_CONSUME_RECORD = EP_PREFIX+ EP_PREFIX_CONSUME+"serialNo_";
    public static final String EP_PREVENT_REP_CARDFROZEAMT = EP_PREFIX + EP_PREFIX_CARDFROZEAMT + "id_";
    public static final String EP_PREVENT_REP_CHARGEEVENT = EP_PREFIX + EP_PREFIX_CHARGEVENT + "id_";

}